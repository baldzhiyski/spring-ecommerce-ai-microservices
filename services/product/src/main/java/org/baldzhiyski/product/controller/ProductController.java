package org.baldzhiyski.product.controller;

import org.baldzhiyski.product.model.req.BuyProductReq;
import org.baldzhiyski.product.model.req.CreateProductReq;
import org.baldzhiyski.product.model.res.ProductPurchasedResp;
import org.baldzhiyski.product.model.res.ProductRes;
import org.baldzhiyski.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Integer> addProduct(@RequestBody CreateProductReq product){
        Integer id = this.productService.addProduct(product);
        return ResponseEntity.ok(id);

    }

    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchasedResp>> purchaseProduct(@RequestBody List<BuyProductReq> request,
                                                                      @RequestParam String customerId){
        return ResponseEntity.ok(productService.purchaseProducts(request,customerId));

    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductRes> getProduct(@PathVariable("productId") Integer productId){
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductRes>> findAllProducts(){
        return  ResponseEntity.ok(productService.findAllProducts());
    }

}
