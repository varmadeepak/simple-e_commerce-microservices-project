package com.microservicesproject.product_service.service;

import com.microservicesproject.product_service.dto.ProductRequest;
import com.microservicesproject.product_service.dto.ProductResponse;
import com.microservicesproject.product_service.model.Product;
import com.microservicesproject.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();
        log.info("Product created: {}", product.getDescription());
        productRepository.save(product);
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::maptToProductResponse)
                .toList();
    }
    private ProductResponse maptToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
