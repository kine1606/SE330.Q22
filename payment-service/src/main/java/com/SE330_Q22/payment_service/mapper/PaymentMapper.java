package com.SE330_Q22.payment_service.mapper;

import com.SE330_Q22.payment_service.dto.PaymentRequest;
import com.SE330_Q22.payment_service.dto.PaymentResponse;
import com.SE330_Q22.payment_service.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface PaymentMapper
{
    Payment toEntity(PaymentRequest request);
    PaymentResponse toResponse(Payment payment);
}
