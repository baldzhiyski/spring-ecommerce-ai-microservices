package org.baldzhiyski.order.repository;

import org.baldzhiyski.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Order> findAllByOrderByCreatedAtDesc();
}
