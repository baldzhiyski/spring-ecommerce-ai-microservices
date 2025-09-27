package org.baldzhiyski.payment.service;

public interface StripeService {
    void handlePaymentFailed(com.stripe.model.PaymentIntent pi);
    void handleCheckoutCompleted(com.stripe.model.checkout.Session session);
}
