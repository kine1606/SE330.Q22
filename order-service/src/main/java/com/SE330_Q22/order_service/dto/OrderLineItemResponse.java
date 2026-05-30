package com.SE330_Q22.order_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemResponse
{
    private Long productId;
    private String skuCode;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal lineTotal;
}