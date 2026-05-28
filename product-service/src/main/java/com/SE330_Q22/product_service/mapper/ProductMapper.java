package com.SE330_Q22.product_service.mapper;

import com.SE330_Q22.product_service.dto.ProductRequest;
import com.SE330_Q22.product_service.dto.ProductResponse;
import com.SE330_Q22.product_service.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper
{
    Product toEntity(ProductRequest request);
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

}
