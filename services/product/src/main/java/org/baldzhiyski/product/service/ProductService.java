package org.baldzhiyski.product.service;

import org.baldzhiyski.product.model.req.BuyProductReq;
import org.baldzhiyski.product.model.req.CreateProductReq;
import org.baldzhiyski.product.model.res.ProductPurchasedResp;
import org.baldzhiyski.product.model.res.ProductRes;

import java.util.List;

public interface ProductService {
    Integer addProduct(CreateProductReq product);
    ProductRes getProduct(Integer productId);

    List<ProductRes> findAllProducts();

    List<ProductRes> findAllProductsByIds(List<Integer> ids);
}
