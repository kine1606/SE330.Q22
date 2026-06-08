package com.SE330_Q22.payment_service.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    //    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String payUrl;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist()
    {
//        this.status = this.status == null ? PaymentStatus.PENDING : this.status;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        paidAt = LocalDateTime.now();
    }
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(name = "order_id", nullable = false)
//    private Long orderId;
//    @Column(name = "user_id", nullable = false)
//    private Long userId;
//    @Column(name = "amount")
//    private BigDecimal amount;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "payment_method", nullable = false)
//    private PaymentMethod paymentMethod;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "payment_status", nullable = false)
//    private PaymentStatus status;
//    @Column(name = "transaction_id", nullable = false)
//    private String transactionId;
//
//    private LocalDateTime paidAt;
//    @CreatedDate
//    @Column(name = "create_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//    @LastModifiedDate
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;


}
