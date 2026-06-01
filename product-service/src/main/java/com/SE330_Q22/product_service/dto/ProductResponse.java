package com.SE330_Q22.product_service.dto;

import com.SE330_Q22.product_service.entity.ProductStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


//@JsonPropertyOrder({
//        "id",
//        "skuCode",
//        "name",
//        "description",
//        "price",
//        "status",
//        "createdAt",
//        "updatedAt",
//})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductResponse
{
    private Long id;
    private String skuCode;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
