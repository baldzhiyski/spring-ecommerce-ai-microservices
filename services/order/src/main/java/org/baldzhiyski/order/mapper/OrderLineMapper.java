package org.baldzhiyski.order.mapper;

import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.req.PurchaseRequest;
import org.mapstruct.Mapper;

import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface OrderLineMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderLine toEntity(PurchaseRequest dto);
}
