package org.baldzhiyski.order.mapper;

import javax.annotation.processing.Generated;
import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.req.OrderReq;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-26T12:32:57+0200",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(OrderReq req) {
        if ( req == null ) {
            return null;
        }

        Order order = new Order();

        order.setReference( req.reference() );
        order.setPaymentMethod( req.paymentMethod() );
        order.setCustomerId( req.customerId() );

        return order;
    }
}
