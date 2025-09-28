package org.baldzhiyski.payment.repository;

import org.baldzhiyski.payment.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentRef(String paymentRef);

    @Modifying
    @Query(value = """
INSERT INTO payment (payment_ref, order_ref, amount, payment_status, created_at, updated_at, success_email_sent)
VALUES (:paymentRef, :orderRef, :amount, :status, now(), now(), false)
ON CONFLICT (order_ref) DO UPDATE
SET payment_ref = EXCLUDED.payment_ref,
    amount = EXCLUDED.amount,
    payment_status = EXCLUDED.payment_status,
    updated_at = now()
""", nativeQuery = true)
    int upsertByOrder(@Param("paymentRef") String paymentRef,
                      @Param("orderRef")   String orderRef,
                      @Param("amount") BigDecimal amount,
                      @Param("status")     String status);

    @Modifying
    @Query("""
update Payment p
   set p.successEmailSent = true
 where p.orderRef = :orderRef
   and p.paymentStatus = 'SUCCEEDED'
   and p.successEmailSent = false
""")
    int markSuccessEmailSentOnce(@Param("orderRef") String orderRef);

}
