package org.baldzhiyski.product.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.baldzhiyski.product.exception.ProductPurchaseException;
import org.baldzhiyski.product.mapper.ProductMapper;
import org.baldzhiyski.product.model.Product;
import org.baldzhiyski.product.model.req.BuyProductReq;
import org.baldzhiyski.product.model.req.CreateProductReq;
import org.baldzhiyski.product.model.res.ProductPurchasedResp;
import org.baldzhiyski.product.model.res.ProductRes;
import org.baldzhiyski.product.repository.ProductRepository;
import org.baldzhiyski.product.service.PricingService;
import org.baldzhiyski.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ProductServiceImpl  implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Integer addProduct(CreateProductReq product) {
        return this.productRepository.save(productMapper.toEntity(product)).getId();
    }

    @Override
    public ProductRes getProduct(Integer productId) {
        return this.productMapper.toDto(this.productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product with id %s was not found !",productId))));
    }

    @Override
    public List<ProductRes> findAllProducts() {
        return this.productRepository
                .findAll()
                .stream()
                .map(this.productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductRes> findAllProductsByIds(List<Integer> ids) {
        return this.productRepository.findAllById(ids)
                .stream()
                .map(this.productMapper::toDto)
                .toList();
    }
}
