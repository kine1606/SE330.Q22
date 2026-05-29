package com.SE330_Q22.inventory_service.entity;

import com.SE330_Q22.inventory_service.dto.ItemStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "product_id", nullable = false)
    private Long productId;

    //    @Column(name = "sku_code", nullable = false)
    private String skuCode;

    //    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    //    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved;

    //    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

//    private ItemStatus status;
//    @CreatedDate
//    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

//    @LastModifiedDate
//    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
