package org.baldzhiyski.order.service;

import org.baldzhiyski.order.model.req.OrderReq;

public interface OrderService {
    Integer createOrder(OrderReq order);
}
