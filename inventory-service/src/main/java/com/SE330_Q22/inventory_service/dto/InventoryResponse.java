package com.SE330_Q22.inventory_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse
{
    private Long id;
    private Long productId;
    private String skuCode;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer quantitySold;
//    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
