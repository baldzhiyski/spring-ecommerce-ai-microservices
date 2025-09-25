package org.baldzhiyski.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.baldzhiyski.product.model.JSONResponse;
import org.baldzhiyski.product.model.req.CustomerDiscountUpsertReq;
import org.baldzhiyski.product.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
@Validated
public class PricingController {

    private final PricingService discountService;

    /**
     * Upsert a per-customer discount for a product.
     * POST /pricing/customer-discounts
     */
    @PostMapping("/customer-discounts")
    public ResponseEntity<JSONResponse> upsertCustomerDiscount(
            @RequestBody @Valid CustomerDiscountUpsertReq req
    ) {
        var res = discountService.upsert(req);
        return ResponseEntity.ok(res);
    }

    /**
     * Delete all expired customer-product discounts (ends_at < now).
     * DELETE /pricing/customer-discounts/expired
     */
    @DeleteMapping("/customer-discounts/expired")
    public ResponseEntity<JSONResponse> deleteExpiredDiscounts() {
        var res = discountService.purgeExpired();
        return ResponseEntity.ok(res);
    }
}
