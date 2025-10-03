package org.baldzhiyski.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.res.OrderRes;
import org.baldzhiyski.order.model.res.PaymentCheckoutRes;
import org.baldzhiyski.order.model.res.ProductInfo;
import org.baldzhiyski.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<PaymentCheckoutRes> createOrder(@RequestBody @Valid OrderReq order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderRes>> findAll(
            @RequestParam(value = "customerId", required = false) String customerId
    ) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(orderService.findAllByCustomerId(customerId));
        }
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRes> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/product-info/{orderId}")
    public ResponseEntity<List<ProductInfo>> findAllProductsIdsByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.findAllProductsIdsForCurrentOrder(orderId));
    }
}
