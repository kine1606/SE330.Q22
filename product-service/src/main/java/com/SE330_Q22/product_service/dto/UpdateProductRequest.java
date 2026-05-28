package com.SE330_Q22.product_service.dto;

import com.SE330_Q22.product_service.entity.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest
{
    private String skuCode;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductStatus status;
}
