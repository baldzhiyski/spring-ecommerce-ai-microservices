package org.baldzhiyski.order.repository;

import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Order> findAllByOrderByCreatedAtDesc();

    Optional<Order> findByReference(String reference);

    @Modifying
    @Query("update Order o set o.status = :status where o.reference = :ref")
    int updateStatusByReference(@Param("ref") String ref, @Param("status") OrderStatus status);
}
