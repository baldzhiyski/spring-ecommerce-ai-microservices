package org.baldzhiyski.payment.controller;

import org.baldzhiyski.payment.payment.res.PaymentRes;
import org.baldzhiyski.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<PaymentRes> getPaymentForOrderRef(@RequestParam String orderRef) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForCustomer(orderRef));
    }
}
