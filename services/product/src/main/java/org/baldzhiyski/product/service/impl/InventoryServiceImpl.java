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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final ProductRepository productRepo;
    private final ProductReservationRepository resRepo;
    private final PricingService pricing;

    @Override
    public ReserveResponse reserve(ReserveCommand cmd) {
        var now = OffsetDateTime.now();
        var ttl = now.plusMinutes(10);

        // sum quantities per product
        Map<Integer,Integer> want = cmd.items().stream()
                .collect(Collectors.groupingBy(BuyProductReq::id, Collectors.summingInt(BuyProductReq::quantity)));

        // lock products
        var products = productRepo.findAllForUpdateByIdIn(want.keySet())
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        // check availability: available = onhand - pending
        for (var e : want.entrySet()) {
            var p = products.get(e.getKey());
            if (p == null) throw new EntityNotFoundException("Product " + e.getKey() + " not found");
            int pending = resRepo.sumPending(p.getId(), now);
            int available = (int)Math.floor(Optional.of(p.getAvailableQuantity()).orElse(0d)) - pending;
            if (e.getValue() > available) {
                throw new ProductPurchaseException("Insufficient stock for %d (want %d, available %d)".formatted(p.getId(), e.getValue(), available));
            }
        }

        // create/replace PENDING reservations (idempotent per orderRef+product)
        var pending = want.entrySet().stream().map(e ->
                ProductReservation.builder()
                        .orderRef(cmd.orderRef())
                        .customerId(cmd.customerId())
                        .productId(e.getKey())
                        .quantity(e.getValue())
                        .status("PENDING")
                        .expiresAt(ttl)
                        .build()
        ).toList();
        // simplest: delete old PENDING for this ref then save
        resRepo.deleteAll(resRepo.findPendingForUpdate(cmd.orderRef()));
        resRepo.saveAll(pending);

        // pricing preview (no decrement yet)
        var priced = cmd.items().stream().map(i -> {
            var p = products.get(i.id());
            var d = pricing.effectiveDiscount(cmd.customerId(), p);
            var unit = pricing.finalPrice(p.getPrice(), d);
            return ProductPurchasedResp.builder()
                    .id(p.getId()).name(p.getName()).description(p.getDescription())
                    .price(p.getPrice()).discount(d).finalUnitPrice(unit)
                    .boughtQuantity(i.quantity()).build();
        }).toList();

        return new ReserveResponse(cmd.orderRef(), ttl, priced);
    }

    @Override
    @Transactional
    public void confirm(String orderRef) {
        // 1) lock pending reservations for this order
        var pending = resRepo.findPendingForUpdate(orderRef);
        if (pending.isEmpty()) {
            return; // idempotent
        }

        // 2) group total qty per product
        var qtyByPid = pending.stream()
                .collect(Collectors.groupingBy(
                        ProductReservation::getProductId,
                        Collectors.summingInt(ProductReservation::getQuantity)
                ));

        // 3) lock products and index by id
        var lockedProducts = productRepo.findAllForUpdateByIdIn(qtyByPid.keySet());
        var byId = lockedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4) decrement stock (no re-check needed; availability was checked at reserve)
        qtyByPid.forEach((pid, qty) -> {
            var p = byId.get(pid);
            if (p == null) {
                throw new EntityNotFoundException("Product not found during confirm: " + pid);
            }
            p.setAvailableQuantity(p.getAvailableQuantity() - qty);
        });

        // 5) persist updates — pass Iterable<Product>, not Map
        productRepo.saveAll(byId.values());

        // 6) mark reservations as CONFIRMED (idempotent-safe)
        resRepo.markConfirmed(orderRef, OffsetDateTime.now());
    }


    @Transactional
    public void cancel(String orderRef) {
        // simply mark canceled; no stock change because we never decremented
        resRepo.markCanceled(orderRef, OffsetDateTime.now());
    }
}
