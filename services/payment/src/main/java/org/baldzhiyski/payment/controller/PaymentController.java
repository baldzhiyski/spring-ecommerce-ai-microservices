package org.baldzhiyski.payment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.baldzhiyski.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private PaymentService paymentService;


    @PostMapping
    public ResponseEntity<Integer> createPayment(@RequestBody @Valid PaymentReq payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
}
