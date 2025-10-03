package org.baldzhiyski.payment.service;

import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.res.PaymentRes;

public interface PaymentService {

    PaymentRes getAllPaymentsForCustomer(String orderRef);
}
