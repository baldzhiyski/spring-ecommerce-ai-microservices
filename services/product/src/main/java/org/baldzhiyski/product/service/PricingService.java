package org.baldzhiyski.product.service;

import org.baldzhiyski.product.model.JSONResponse;
import org.baldzhiyski.product.model.Product;
import org.baldzhiyski.product.model.req.CustomerDiscountUpsertReq;

import java.math.BigDecimal;

public interface PricingService {
    BigDecimal effectiveDiscount(String customerId, Product product);
    BigDecimal finalPrice(BigDecimal price, BigDecimal discount);
    JSONResponse upsert(CustomerDiscountUpsertReq req);
    JSONResponse purgeExpired();
}
