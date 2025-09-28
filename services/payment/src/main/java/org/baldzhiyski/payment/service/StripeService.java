package org.baldzhiyski.payment.service;

public interface StripeService {
    void handleSuccess(String paymentRef, String orderRef, long amountMinor, String currency,
                       String fullName, String email, String receiptUrl, String paymentMethod);
    void handleFailure(String paymentRef, String orderRef, long amountMinor, String currency,
                       String fullName, String email, String reason);
}
