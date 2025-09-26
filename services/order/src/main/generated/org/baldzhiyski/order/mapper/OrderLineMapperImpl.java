package org.baldzhiyski.order.mapper;

import javax.annotation.processing.Generated;
import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.req.PurchaseRequest;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-26T14:43:54+0200",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class OrderLineMapperImpl implements OrderLineMapper {

    @Override
    public OrderLine toEntity(PurchaseRequest dto) {
        if ( dto == null ) {
            return null;
        }

        OrderLine orderLine = new OrderLine();

        orderLine.setProductId( dto.productId() );
        orderLine.setQuantity( dto.quantity() );

        return orderLine;
    }
}
