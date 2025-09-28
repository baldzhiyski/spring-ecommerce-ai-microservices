package org.baldzhiyski.payment.repository;

import org.baldzhiyski.payment.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentRef(String paymentRef);

    @Modifying
    @Query(value = """
    insert into payment (
        payment_ref, order_ref, amount, payment_status,
        created_at, updated_at
    ) values (
        :paymentRef, :orderRef, :amount, :status,
        now(), now()
    )
    on conflict (payment_ref) do update
    set order_ref     = excluded.order_ref,
        amount        = excluded.amount,
        payment_status= excluded.payment_status,
        updated_at    = now()
    """, nativeQuery = true)
    void upsert(@Param("paymentRef") String paymentRef,
                @Param("orderRef") String orderRef,
                @Param("amount") java.math.BigDecimal amount,
                @Param("status") String status);
}
