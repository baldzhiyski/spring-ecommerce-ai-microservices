package org.baldzhiyski.product.service.impl;

import jakarta.transaction.Transactional;
import org.baldzhiyski.product.model.CustomerProductDiscount;
import org.baldzhiyski.product.model.JSONResponse;
import org.baldzhiyski.product.model.Product;
import org.baldzhiyski.product.model.req.CustomerDiscountUpsertReq;
import org.baldzhiyski.product.repository.CustomerProductDiscountRepository;
import org.baldzhiyski.product.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Service
public class PriceServiceImpl implements PricingService {
    private final CustomerProductDiscountRepository customerProductDiscountRepository;

    public PriceServiceImpl(CustomerProductDiscountRepository customerProductDiscountRepository) {
        this.customerProductDiscountRepository = customerProductDiscountRepository;
    }

    @Override
    public BigDecimal effectiveDiscount(String customerId, Product product) {
        BigDecimal base = product.getDiscount() == null ? BigDecimal.ZERO : product.getDiscount();
        if (customerId == null) return base;

        return customerProductDiscountRepository.findActiveForNow(customerId, product.getId(), OffsetDateTime.now())
                .map(CustomerProductDiscount::getDiscount)
                .filter(d -> d.compareTo(base) > 0) // override only if better
                .orElse(base);
    }

    @Override
    public BigDecimal finalPrice(BigDecimal price, BigDecimal discount) {
        if (price == null) return null;
        BigDecimal d = discount == null ? BigDecimal.ZERO : discount;
        return price.multiply(BigDecimal.ONE.subtract(d)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public JSONResponse upsert(CustomerDiscountUpsertReq req) {
        // Basic window sanity check
        if (req.startsAt() != null && req.endsAt() != null && req.endsAt().isBefore(req.startsAt())) {
            throw new IllegalArgumentException("endsAt must be >= startsAt");
        }

        var now = OffsetDateTime.now();

        // Prefer an active-for-now row; otherwise any active row for this (customer, product)
        var existing = customerProductDiscountRepository.findActiveForNow(req.customerId(), req.productId(), now)
                .or(() -> customerProductDiscountRepository.findActiveAny(req.customerId(), req.productId()));

        var active = (req.active() == null) ? Boolean.TRUE : req.active();

        CustomerProductDiscount d = existing.orElseGet(CustomerProductDiscount::new);
        boolean created = (d.getId() == null);

        d.setCustomerId(req.customerId());
        d.setProductId(req.productId());
        // normalize scale to 3 decimals to match NUMERIC(4,3)
        d.setDiscount(req.discount() == null ? null : req.discount().setScale(3, RoundingMode.HALF_UP));
        d.setStartsAt(req.startsAt());
        d.setEndsAt(req.endsAt());
        d.setActive(active);

        var saved = customerProductDiscountRepository.save(d);

        return JSONResponse.builder()
                .status("OK")
                .message((created ? "Created" : "Updated")
                        + " customer discount: id=" + saved.getId()
                        + ", customerId=" + saved.getCustomerId()
                        + ", productId=" + saved.getProductId()
                        + ", discount=" + saved.getDiscount())
                .build();
    }

    @Override
    public JSONResponse purgeExpired() {
        int deleted = customerProductDiscountRepository.deactivateExpired(OffsetDateTime.now());
        return JSONResponse.builder()
                .status("OK")
                .message("Deactivated expired discounts: " + deleted)
                .build();
    }
}
