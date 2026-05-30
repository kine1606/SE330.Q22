package com.SE330_Q22.order_service.client.dto;

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
    //    private ItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}