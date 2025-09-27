package org.baldzhiyski.payment.service;

import jakarta.validation.Valid;
import org.baldzhiyski.payment.payment.req.PaymentReq;

public interface PaymentService {
    Integer createPayment(@Valid PaymentReq payment);
}
