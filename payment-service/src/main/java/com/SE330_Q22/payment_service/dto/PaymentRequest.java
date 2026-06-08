package com.SE330_Q22.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest
{
//    @NotNull(message = "Order id is required")
    private Long orderId;
//    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
