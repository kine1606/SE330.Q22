package com.SE330_Q22.product_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest
{
    @NotBlank(message = "SKU code is required")
    private String skuCode;
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    private BigDecimal price;
}
