package org.baldzhiyski.payment.service.impl;

import lombok.AllArgsConstructor;
import org.baldzhiyski.payment.config.StripeProps;
import org.baldzhiyski.payment.events.PaymentEventPublisher;
import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.PaymentStatus;
import org.baldzhiyski.payment.repository.PaymentRepository;
import org.baldzhiyski.payment.service.StripeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class StripeServiceImpl implements StripeService {
    private final PaymentRepository repo;
    private final PaymentEventPublisher publisher;
    private final StripeProps stripeProps; // for currency default

    @Transactional
    public void handleCheckoutCompleted(com.stripe.model.checkout.Session session) {
        // PaymentIntent id is the real payment reference
        String paymentRef = session.getPaymentIntent();
        if (paymentRef == null) return; // safety

        String orderRef = valueOr(session.getMetadata().get("orderRef"), "UNKNOWN");
        String email    = session.getCustomerEmail();
        String customerFullName = session.getMetadata().get("customerFullName");
        long amountMin  = session.getAmountTotal() != null ? session.getAmountTotal() : 0L;
        BigDecimal amount = BigDecimal.valueOf(amountMin).movePointLeft(2);
        String currency = stripeProps.currency();

        // Upsert by paymentRef
        Payment p = repo.findByPaymentRef(paymentRef).orElseGet(Payment::new);
        boolean created = (p.getId() == null);

        p.setPaymentRef(paymentRef);
        p.setOrderRef(orderRef);
        p.setAmount(amount);
        p.setPaymentStatus(PaymentStatus.SUCCEEDED);

        repo.save(p);

        // Publish after commit; idempotent (SUCCEEDED is final)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                publisher.paymentSucceeded(orderRef, paymentRef, amountMin, currency,customerFullName, email);
            }
        });
    }

    @Override
    @Transactional
    public void handlePaymentFailed(com.stripe.model.PaymentIntent pi) {
        String paymentRef = pi.getId();
        String orderRef   = valueOr(pi.getMetadata().get("orderRef"), "UNKNOWN");
        String email      = firstNonNull(pi.getReceiptEmail(), pi.getMetadata().get("customerEmail"));
        String customerFullName = pi.getMetadata().get("customerFullName");
        long amountMin    = pi.getAmount() != null ? pi.getAmount() : 0L;
        BigDecimal amount = BigDecimal.valueOf(amountMin).movePointLeft(2);
        String currency   = stripeProps.currency();
        String reason     = pi.getLastPaymentError() != null ? pi.getLastPaymentError().getMessage() : "unknown";

        Payment p = repo.findByPaymentRef(paymentRef).orElseGet(Payment::new);
        boolean created = (p.getId() == null);

        p.setPaymentRef(paymentRef);
        p.setOrderRef(orderRef);
        p.setAmount(amount);

        // If we already marked SUCCEEDED, don't downgrade
        if (p.getPaymentStatus() != PaymentStatus.SUCCEEDED) {
            p.setPaymentStatus(PaymentStatus.FAILED);
        }

        repo.save(p);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                // Only publish failed if it's actually failed (not already succeeded)
                if (p.getPaymentStatus() == PaymentStatus.FAILED) {
                    publisher.paymentFailed(orderRef, paymentRef, amountMin, currency,customerFullName, email, reason);
                }
            }
        });
    }

    private static String valueOr(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
    private static String firstNonNull(String a, String b) { return (a != null && !a.isBlank()) ? a : b; }
}
