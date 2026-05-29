package com.SE330_Q22.product_service.service;

import com.SE330_Q22.product_service.dto.ProductRequest;
import com.SE330_Q22.product_service.dto.ProductResponse;
import com.SE330_Q22.product_service.dto.UpdateProductRequest;
import com.SE330_Q22.product_service.entity.Product;
import com.SE330_Q22.product_service.entity.ProductStatus;
import com.SE330_Q22.product_service.mapper.ProductMapper;
import com.SE330_Q22.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
//@Transactional(readOnly = true)
public class ProductService
{
    public final ProductRepository productRepository;
    public final ProductMapper productMapper;

    public ProductResponse create(ProductRequest request)
    {
        Product product = productMapper.toEntity(request);
        product.setStatus(ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Product {} is saved", product.getId());

        return productMapper.toResponse(product);
    }

    public List<ProductResponse> getListProducts()
    {
        return productMapper.toResponseList(productRepository.findAll());
    }

    public ProductResponse getById(Long id)
    {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    public ProductResponse getBySkuCode(String skuCode) {
        Product product = productRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Product not found with skuCode: " + skuCode));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request)
    {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setUpdatedAt(LocalDateTime.now());
        if (request.getName() != null) product.setName(request.getName());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getSkuCode() != null) product.setSkuCode(request.getSkuCode());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        productRepository.save(product);

        log.info("Product {} is updated", product.getId());
        return productMapper.toResponse(product);
    }

    public void deleteProduct(Long id)
    {
        productRepository.deleteById(id);
        log.info("Product {} is deleted", id);
    }
}
