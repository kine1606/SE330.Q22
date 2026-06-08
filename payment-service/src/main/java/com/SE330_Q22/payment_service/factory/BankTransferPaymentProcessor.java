package com.SE330_Q22.payment_service.factory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BankTransferPaymentProcessor implements PaymentProcessor
{
    @Override
    public String getPaymentMethod()
    {
        return "bank";
    }

    @Override
    public boolean pay(Long orderId, BigDecimal amount)
    {
        return false;
    }
}
