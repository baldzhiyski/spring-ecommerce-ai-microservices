package org.baldzhiyski.order.mapper;

import org.baldzhiyski.order.model.Order;
import org.baldzhiyski.order.model.req.OrderReq;
import org.baldzhiyski.order.model.res.OrderRes;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {OrderLineMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderReq req);


    // For reading
    @Mapping(source = "orderLines", target = "lines")
    OrderRes toRes(Order order);


}