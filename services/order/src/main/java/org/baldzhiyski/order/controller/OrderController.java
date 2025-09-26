package org.baldzhiyski.order.controller;

import lombok.AllArgsConstructor;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Integer> createOrder(@RequestBody OrderReq order) {
        return ResponseEntity.ok(this.orderService.createOrder(order));
    }

}
