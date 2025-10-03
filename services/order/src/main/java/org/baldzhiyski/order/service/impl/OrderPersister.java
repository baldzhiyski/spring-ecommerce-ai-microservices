package org.baldzhiyski.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.OrderStatus;
import org.baldzhiyski.order.repository.OrderLineRepository;
import org.baldzhiyski.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 1) A persister that ALWAYS commits the order + lines
@Service
@RequiredArgsConstructor
public class OrderPersister {
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order persistCommitted(Order order, List<OrderLine> lines) {
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Order saved = orderRepository.saveAndFlush(order);

        lines.forEach(l -> l.setOrder(saved));
        orderLineRepository.saveAllAndFlush(lines);

        return saved; // committed when this method returns
    }
}
