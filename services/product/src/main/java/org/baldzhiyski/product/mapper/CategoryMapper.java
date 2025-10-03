package org.baldzhiyski.product.mapper;

import org.baldzhiyski.product.model.Category;
import org.baldzhiyski.product.model.req.CategoryReq;
import org.baldzhiyski.product.model.res.CategoryRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true) // ignore if creating new categories
    Category toEntity(CategoryReq req);

    CategoryRes  toDto(Category category);
}