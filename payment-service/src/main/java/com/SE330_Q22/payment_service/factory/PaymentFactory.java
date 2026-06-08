package com.SE330_Q22.payment_service.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentFactory
{
    private final Map<String, PaymentProcessor> processorMap;
    public PaymentFactory(List<PaymentProcessor> processors)
    {
        this.processorMap = processors.stream()
                .collect(Collectors.toMap(
                        PaymentProcessor::getPaymentMethod,
                        processor -> processor
                ));
    }

    public PaymentProcessor getProcessor(String paymentMethod) {
        PaymentProcessor processor = processorMap.get(paymentMethod);

        if (processor == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        return processor;
    }
}
