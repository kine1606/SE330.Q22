package com.SE330_Q22.order_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
//    private ProductStatus status;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
