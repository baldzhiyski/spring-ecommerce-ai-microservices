package org.baldzhiyski.order.service;

import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.res.OrderRes;
import org.baldzhiyski.order.model.res.PaymentCheckoutRes;
import org.baldzhiyski.order.model.res.ProductInfo;

import java.util.List;

public interface OrderService {
    PaymentCheckoutRes createOrder(OrderReq orderReq);
    List<OrderRes> findAll();
    List<OrderRes> findAllByCustomerId(String customerId);
    OrderRes findById(Integer id);

    List<ProductInfo> findAllProductsIdsForCurrentOrder(Integer id);
}
