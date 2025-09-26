package org.baldzhiyski.product.repository;


import jakarta.persistence.LockModeType;
import org.baldzhiyski.product.model.ProductReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {

    @Query("select coalesce(sum(r.quantity),0) from ProductReservation r " +
            "where r.productId=:pid and r.status='PENDING' and r.expiresAt> :now")
    int sumPending(@Param("pid") Integer productId, @Param("now") OffsetDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ProductReservation r where r.orderRef=:ref and r.status='PENDING'")
    List<ProductReservation> findPendingForUpdate(@Param("ref") String orderRef);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductReservation r set r.status='CONFIRMED', r.updatedAt=:now " +
            "where r.orderRef=:ref and r.status='PENDING'")
    int markConfirmed(@Param("ref") String orderRef, @Param("now") OffsetDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ProductReservation r set r.status='CANCELED', r.updatedAt=:now " +
            "where r.orderRef=:ref and r.status='PENDING'")
    int markCanceled(@Param("ref") String orderRef, @Param("now") OffsetDateTime now);
}