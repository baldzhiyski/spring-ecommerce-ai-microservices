package org.baldzhiyski.order.service;

import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.res.OrderRes;

import java.util.List;

public interface OrderService {
    Integer createOrder(OrderReq orderReq);
    List<OrderRes> findAll();
    List<OrderRes> findAllByCustomerId(String customerId);
    OrderRes findById(Integer id);
}
