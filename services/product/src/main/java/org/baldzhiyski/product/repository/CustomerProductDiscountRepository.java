package org.baldzhiyski.product.repository;

import org.baldzhiyski.product.model.CustomerProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface CustomerProductDiscountRepository extends JpaRepository<CustomerProductDiscount, Integer> {

    @Modifying
    @Query("update CustomerProductDiscount d set d.active=false where d.active=true and d.endsAt is not null and d.endsAt < :now")
    int deactivateExpired(@Param("now") OffsetDateTime now);

    @Query("""
  select d from CustomerProductDiscount d
  where d.customerId = :customerId and d.productId = :productId and d.active = true
  """)
    Optional<CustomerProductDiscount> findActiveAny(@Param("customerId") Integer customerId,
                                                    @Param("productId") Integer productId);



    @Query("""
    select d from CustomerProductDiscount d
    where d.active = true
      and d.customerId = :customerId
      and d.productId  = :productId
      and (:now >= coalesce(d.startsAt, :now))
      and (:now <= coalesce(d.endsAt,   :now))
    """)
    Optional<CustomerProductDiscount> findActiveForNow(@Param("customerId") Integer customerId,
                                                       @Param("productId") Integer productId,
                                                       @Param("now") OffsetDateTime now);

}
