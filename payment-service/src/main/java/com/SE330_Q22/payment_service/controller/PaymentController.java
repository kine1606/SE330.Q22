package com.SE330_Q22.payment_service.controller;

import com.SE330_Q22.payment_service.dto.PaymentRequest;
import com.SE330_Q22.payment_service.dto.PaymentResponse;
import com.SE330_Q22.payment_service.entity.Payment;
import com.SE330_Q22.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments" )
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@RequestBody PaymentRequest request)
    {
        return paymentService.createPayment(request);
    }
}
