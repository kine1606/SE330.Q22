package com.SE330_Q22.payment_service.factory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MomoPaymentProcessor implements PaymentProcessor {

    @Override
    public String getPaymentMethod() {
        return "MOMO";
    }

    @Override
    public boolean pay(Long orderId, BigDecimal amount) {
        // Demo trước: giả lập thanh toán MoMo thành công
        return true;
    }
}