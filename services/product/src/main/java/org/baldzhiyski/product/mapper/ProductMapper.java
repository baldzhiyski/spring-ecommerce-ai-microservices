package org.baldzhiyski.product.mapper;

import org.baldzhiyski.product.model.Product;
import org.baldzhiyski.product.model.req.CreateProductReq;
import org.baldzhiyski.product.model.res.ProductRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "categoryReq", target = "category")
    Product toEntity(CreateProductReq req);

    ProductRes toDto(Product product);
}
