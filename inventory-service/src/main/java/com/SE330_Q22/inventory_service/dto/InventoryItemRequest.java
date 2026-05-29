package com.SE330_Q22.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemRequest
{
//    @NotBlank(message = "SKU code is required")
    private Long productId;
//    @NotBlank(message = "SKU code is required")
    private String skuCode;
    private Integer quantityAvailable;

//    private Integer quantityReserved;
//    private Integer quantitySold;
}
