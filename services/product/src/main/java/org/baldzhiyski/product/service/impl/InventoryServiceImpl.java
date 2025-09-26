package org.baldzhiyski.product.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.baldzhiyski.product.exception.ProductPurchaseException;
import org.baldzhiyski.product.model.Product;
import org.baldzhiyski.product.model.ProductReservation;
import org.baldzhiyski.product.model.req.BuyProductReq;
import org.baldzhiyski.product.model.req.ReserveCommand;
import org.baldzhiyski.product.model.res.ProductPurchasedResp;
import org.baldzhiyski.product.model.res.ReserveResponse;
import org.baldzhiyski.product.repository.ProductRepository;
import org.baldzhiyski.product.repository.ProductReservationRepository;
import org.baldzhiyski.product.service.InventoryService;
import org.baldzhiyski.product.service.PricingService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Inventory orchestration for the 2-phase flow:
 *  1) RESERVE: create PENDING reservations (no stock decrement), return priced lines
 *  2) CONFIRM: on payment success, decrement stock and mark reservations CONFIRMED
 *  3) CANCEL : on failure/rollback/timeout, mark reservations CANCELED (no stock change)
 *
 * Concurrency:
 *  - We pessimistically lock product rows before checking availability.
 *  - Availability = onHand - sum(PENDING reservations not expired).
 *  - Idempotency is achieved by upserting PENDING reservations per (orderRef, productId).
 */
@Service
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepo;
    private final ProductReservationRepository resRepo;
    private final PricingService pricing;

    /**
     * RESERVE step: validates availability and writes/refreshes PENDING reservations with a TTL.
     * Does NOT decrement stock. Returns pricing preview for the requested items.
     *
     * @param cmd order reference, customer id, and requested items
     * @return orderRef, TTL, and priced items (unit price/discount per line)
     */
    @Override
    @Transactional
    public ReserveResponse reserve(ReserveCommand cmd) {
        // === 0) Timing window for reservation validity ===
        final OffsetDateTime now = OffsetDateTime.now();
        final OffsetDateTime expiresAt = now.plusMinutes(10); // TODO: externalize as config

        // === 1) Aggregate requested quantities per product (collapse duplicates) ===
        //  want[productId] = total requested quantity for that product
        Map<Integer, Integer> want = cmd.items().stream()
                .collect(Collectors.groupingBy(
                        BuyProductReq::id,
                        Collectors.summingInt(BuyProductReq::quantity)
                ));

        if (want.isEmpty()) {
            // Nothing to reserve; return empty pricing with a TTL
            return new ReserveResponse(cmd.orderRef(), expiresAt, List.of());
        }

        // === 2) Lock product rows to serialize availability checks & avoid races ===
        //  We fetch and index by id for quick lookups.
        Map<Integer, Product> productsById = productRepo.findAllForUpdateByIdIn(want.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // === 3) Validate availability: available = onHand - sum(pending, not expired) ===
        //  If any product is short, fail the reservation atomically.
        for (var e : want.entrySet()) {
            Integer productId = e.getKey();
            int requestedQty = e.getValue();

            Product p = productsById.get(productId);
            if (p == null) {
                throw new EntityNotFoundException("Product " + productId + " not found");
            }

            int pending = resRepo.sumPending(productId, now); // only PENDING and not expired
            int onHand = (int) Math.floor(
                    Optional.of(p.getAvailableQuantity()).orElse(0d)
            );
            int available = onHand - pending;

            if (requestedQty > available) {
                throw new ProductPurchaseException(
                        "Insufficient stock for %d (want %d, available %d)"
                                .formatted(productId, requestedQty, available)
                );
            }
        }

        // === 4) Upsert PENDING reservations for this orderRef ===
        //  Strategy: remove any previous PENDING for the same orderRef, then insert current snapshot.
        //  This makes RESERVE idempotent and retry-safe for the same orderRef.
        resRepo.deleteAll(resRepo.findPendingForUpdate(cmd.orderRef()));

        List<ProductReservation> pendingRows = want.entrySet().stream()
                .map(e -> ProductReservation.builder()
                        .orderRef(cmd.orderRef())
                        .customerId(cmd.customerId())
                        .productId(e.getKey())
                        .quantity(e.getValue())
                        .status("PENDING")
                        .expiresAt(expiresAt)
                        .build())
                .toList();

        resRepo.saveAll(pendingRows);

        // === 5) Build pricing preview (no stock changes yet) ===
        //  We price per line in the original request (not the collapsed map) so the caller
        //  can keep a 1:1 relationship with their requested items.
        List<ProductPurchasedResp> priced = cmd.items().stream()
                .map(i -> {
                    Product p = productsById.get(i.id());
                    var discount = pricing.effectiveDiscount(cmd.customerId(), p);
                    var finalUnit = pricing.finalPrice(p.getPrice(), discount);
                    return ProductPurchasedResp.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .description(p.getDescription())
                            .price(p.getPrice())
                            .discount(discount)
                            .finalUnitPrice(finalUnit)
                            .boughtQuantity(i.quantity())
                            .build();
                })
                .toList();

        // === 6) Return reservation metadata + priced lines ===
        return new ReserveResponse(cmd.orderRef(), expiresAt, priced);
    }

    /**
     * CONFIRM step: on payment success, make the reservation permanent by decrementing stock
     * and marking reservations CONFIRMED. Idempotent: calling twice does nothing after the first.
     *
     * @param orderRef correlation id for the reservation (usually the order reference)
     */
    @Override
    @Transactional
    public void confirm(String orderRef) {
        // === 1) Lock all PENDING reservations for this orderRef ===
        List<ProductReservation> pending = resRepo.findPendingForUpdate(orderRef);
        if (pending.isEmpty()) {
            // Nothing to confirm (already confirmed/canceled/expired or never reserved) — idempotent success
            return;
        }

        // === 2) Aggregate quantities per productId from the reservations ===
        Map<Integer, Integer> qtyByProduct = pending.stream()
                .collect(Collectors.groupingBy(
                        ProductReservation::getProductId,
                        Collectors.summingInt(ProductReservation::getQuantity)
                ));

        // === 3) Lock the corresponding product rows and index them ===
        Map<Integer, Product> productsById = productRepo.findAllForUpdateByIdIn(qtyByProduct.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // === 4) Apply the permanent decrement (availability already validated during RESERVE) ===
        qtyByProduct.forEach((productId, qty) -> {
            Product p = productsById.get(productId);
            if (p == null) {
                // Should not happen unless the product was deleted; fail explicitly
                throw new EntityNotFoundException("Product not found during confirm: " + productId);
            }
            p.setAvailableQuantity(p.getAvailableQuantity() - qty);
        });

        // === 5) Persist the updated products in one batch ===
        productRepo.saveAll(productsById.values());

        // === 6) Flip reservations from PENDING -> CONFIRMED (idempotent-safe) ===
        resRepo.markConfirmed(orderRef, OffsetDateTime.now());
    }

    /**
     * CANCEL step: on payment failure/rollback/timeout, release the reservation by
     * marking rows CANCELED. No stock change occurs because we never decremented.
     *
     * @param orderRef correlation id for the reservation (usually the order reference)
     */
    @Override
    @Transactional
    public void cancel(String orderRef) {
        // Mark PENDING -> CANCELED (idempotent; does nothing if already not PENDING)
        resRepo.markCanceled(orderRef, OffsetDateTime.now());
    }

}
