package com.SE330_Q22.product_service.controller;

import com.SE330_Q22.product_service.dto.ProductRequest;
import com.SE330_Q22.product_service.dto.ProductResponse;
import com.SE330_Q22.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController
{
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest productRequest)
    {
        return productService.create(productRequest);
    }
}
