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
    private final StripeProps stripeProps;

    @Transactional
    @Override
    public void handleSuccess(String paymentRef, String orderRef, long amountMinor,
                              String currency, String fullName, String email,
                              String receiptUrl, String paymentMethod) {

        // 1) Persist SUCCEEDED + latest fields (idempotent on orderRef)
        var amount = BigDecimal.valueOf(amountMinor).movePointLeft(2);
        repo.upsertByOrder(paymentRef, orderRef, amount, PaymentStatus.SUCCEEDED.name());

        // 2) Flip the "sent" flag ONCE (only the first call returns 1)
        int flipped = repo.markSuccessEmailSentOnce(orderRef);
        if (flipped == 0) {
            // Email for this order was already sent → do nothing
            return;
        }

        // 3) Publish after commit (guarantees the row is durable before emailing)
        final String cur = (currency == null || currency.isBlank()) ? stripeProps.currency() : currency;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                publisher.paymentSucceeded(orderRef, paymentRef, amountMinor, cur, fullName, email, receiptUrl, paymentMethod);
            }
        });
    }

    @Transactional
    @Override
    public void handleFailure(String paymentRef, String orderRef, long amountMinor, String currency,
                              String fullName, String email, String reason) {
        BigDecimal amount = BigDecimal.valueOf(amountMinor).movePointLeft(2);
        Payment p = repo.findByPaymentRef(paymentRef).orElseGet(Payment::new);
        p.setPaymentRef(paymentRef);
        p.setOrderRef(orderRef);
        p.setAmount(amount);
        p.setPaymentStatus(PaymentStatus.FAILED);
        repo.save(p);

        String cur = or(currency, stripeProps.currency());
        String rsn = isBlank(reason) ? "payment_failed" : reason;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                publisher.paymentFailed(orderRef, paymentRef, amountMinor, cur, fullName, email, rsn);
            }
        });
    }

    private static String or(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
    private static boolean isBlank(String s){ return s == null || s.isBlank(); }
}

