package com.SE330_Q22.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_line_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItem
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // productId, skuCode, productName and price is snapshot when create order.
    private Long productId;

//    @Column(nullable = false)
    private String skuCode;

    private String productName;
//    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

//    @Column(nullable = false)
    private Integer quantity;

//    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;
}
