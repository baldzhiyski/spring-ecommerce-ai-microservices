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
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl  implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PricingService pricingService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, PricingService pricingService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.pricingService = pricingService;
    }

    @Override
    public Integer addProduct(CreateProductReq product) {
        return this.productRepository.save(productMapper.toEntity(product)).getId();
    }


    @Override
    @Transactional
    public List<ProductPurchasedResp> purchaseProducts(List<BuyProductReq> request, String customerId) {
        // 1) collect IDs (preserve request order)
        List<Integer> idsInOrder = request.stream()
                .map(BuyProductReq::id)
                .filter(Objects::nonNull)
                .toList();

        if (idsInOrder.isEmpty()) return List.of();

        // 2) sum quantities per product (handle duplicates)
        Map<Integer, Integer> quantities = request.stream()
                .collect(Collectors.groupingBy(
                        BuyProductReq::id,
                        Collectors.summingInt(BuyProductReq::quantity)
                ));

        // 3) load & lock rows for update (pessimistic write to avoid race conditions)
        List<Product> locked = productRepository.findAllForUpdateByIdIn(quantities.keySet());

        // 4) ensure all requested ids exist
        Set<Integer> foundIds = locked.stream().map(Product::getId).collect(Collectors.toSet());
        List<Integer> missing = quantities.keySet().stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!missing.isEmpty()) {
            throw new EntityNotFoundException("Products not found: " + missing);
        }

        // 5) index by id
        Map<Integer, Product> byId = locked.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 6) validate stock & update in-memory
        for (Map.Entry<Integer, Integer> e : quantities.entrySet()) {
            Integer id = e.getKey();
            int qty = e.getValue();
            Product p = byId.get(id);

            double available = Optional.of(p.getAvailableQuantity()).orElse(0d);
            if (qty > available) {
                throw new ProductPurchaseException(
                        "Insufficient stock for product %d (requested %d, available %.0f)"
                                .formatted(id, qty, available)
                );
            }
            p.setAvailableQuantity(available - qty);
        }

        // 7) persist once (batch)
        productRepository.saveAll(locked);

        // TODO: publish domain events / outbox, charge payment, etc.


        // 8) build response (keep request order, collapse duplicates)
        return idsInOrder.stream()
                .distinct()
                .map(id -> {
                    Product p = byId.get(id);
                    var effDiscount = pricingService.effectiveDiscount(customerId, p);           // <--
                    var finalUnit  = pricingService.finalPrice(p.getPrice(), effDiscount);       // <--
                    return ProductPurchasedResp.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .description(p.getDescription())
                            .price(p.getPrice())
                            .discount(effDiscount)     // e.g., 0.150
                            .finalUnitPrice(finalUnit)          // price * (1 - discount)
                            .boughtQuantity(quantities.get(id))
                            .build();
                })
                .toList();
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
}
