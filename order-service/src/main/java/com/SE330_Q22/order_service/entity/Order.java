package com.SE330_Q22.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;
//    @Column(nullable = false)
    @Column(nullable = false)
    private Long userId;

    //    @Column(nullable = false)
    private String status;

//    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderLineItem> items;

    //    @CreatedDate
//    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //    @LastModifiedDate
//    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
