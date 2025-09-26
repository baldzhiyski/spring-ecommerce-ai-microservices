package org.baldzhiyski.product.service;

import org.baldzhiyski.product.model.req.BuyProductReq;
import org.baldzhiyski.product.model.req.CreateProductReq;
import org.baldzhiyski.product.model.res.ProductPurchasedResp;
import org.baldzhiyski.product.model.res.ProductRes;

import java.util.List;

public interface ProductService {
    Integer addProduct(CreateProductReq product);

    List<ProductPurchasedResp> purchaseProducts(List<BuyProductReq> request, String customerId);

    ProductRes getProduct(Integer productId);

    List<ProductRes> findAllProducts();
}
