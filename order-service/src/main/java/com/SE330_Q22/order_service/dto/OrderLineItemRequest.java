package com.SE330_Q22.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItemRequest
{

//    @NotBlank(message = "SKU code is required")
    private String skuCode;

//    @NotNull(message = "Quantity is required")
//    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
