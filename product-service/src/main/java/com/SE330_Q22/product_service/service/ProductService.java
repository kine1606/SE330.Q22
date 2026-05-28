package com.SE330_Q22.product_service.service;

import com.SE330_Q22.product_service.dto.ProductRequest;
import com.SE330_Q22.product_service.dto.ProductResponse;
import com.SE330_Q22.product_service.entity.Product;
import com.SE330_Q22.product_service.entity.ProductStatus;
import com.SE330_Q22.product_service.mapper.ProductMapper;
import com.SE330_Q22.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService
{
    public final ProductRepository productRepository;
    public final ProductMapper productMapper;

    public ProductResponse create(ProductRequest request)
    {
        log.info("Request skuCode = {}", request.getSkuCode());
        log.info("Request name = {}", request.getName());
        log.info("Request price = {}", request.getPrice());

        Product product = productMapper.toEntity(request);

        log.info("Product skuCode = {}", product.getSkuCode());
        log.info("Product name = {}", product.getName());
        log.info("Product price = {}", product.getPrice());

        product.setStatus(ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Product {} is saved", product.getId());
        return productMapper.toResponse(product);
    }
}
