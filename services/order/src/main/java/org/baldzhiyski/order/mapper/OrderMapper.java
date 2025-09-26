package org.baldzhiyski.order.mapper;

import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.req.OrderReq;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderLines" , ignore = true)
    @Mapping(target = "totalAmount",ignore = true)
    Order toEntity(OrderReq req);

}