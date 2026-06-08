package com.SE330_Q22.payment_service.service;

import com.SE330_Q22.payment_service.client.*;
import com.SE330_Q22.payment_service.client.dto.OrderResponse;
import com.SE330_Q22.payment_service.config.MomoProperties;
import com.SE330_Q22.payment_service.dto.PaymentRequest;
import com.SE330_Q22.payment_service.dto.PaymentResponse;
import com.SE330_Q22.payment_service.dto.momo.HmacUtil;
import com.SE330_Q22.payment_service.dto.momo.MomoCreateRequest;
import com.SE330_Q22.payment_service.dto.momo.MomoCreateResponse;
import com.SE330_Q22.payment_service.entity.Payment;
import com.SE330_Q22.payment_service.factory.PaymentFactory;
import com.SE330_Q22.payment_service.factory.PaymentProcessor;
import com.SE330_Q22.payment_service.mapper.PaymentMapper;
import com.SE330_Q22.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService
{
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;
    private final MomoProperties momoProperties;
    private final MomoClient momoClient;
    // POST /api/payment
    public PaymentResponse createPayment(PaymentRequest request) {
        OrderResponse order = orderClient.getOrderById(request.getOrderId());

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order is not pending");
        }

        if ("MOMO".equalsIgnoreCase(request.getPaymentMethod())) {
            return createMomoPayment(order, request);
        }

        PaymentProcessor processor = paymentFactory.getProcessor(request.getPaymentMethod());

        boolean success = processor.pay(order.getId(), order.getTotalAmount());

        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(success ? "SUCCESS" : "FAILED")
                .transactionId(UUID.randomUUID().toString())
                .paidAt(success ? now : null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        if (success) {
            orderClient.markOrderSuccess(order.getId());
        } else {
            orderClient.markOrderFailed(order.getId());
        }

        return paymentMapper.toResponse(savedPayment);
    }
    private PaymentResponse createMomoPayment(OrderResponse order, PaymentRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .amount(order.getTotalAmount())
                .paymentMethod("MOMO")
                .status("PENDING")
                .transactionId("MOMO-" + UUID.randomUUID())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        String requestId = savedPayment.getTransactionId();
        String momoOrderId = "ORDER-" + order.getId() + "-PAYMENT-" + savedPayment.getId();
        String orderInfo = "Pay order " + order.getOrderNumber();
        String extraData = "";
        Long amount = order.getTotalAmount().longValue();

        String rawSignature =
                "accessKey=" + momoProperties.getAccessKey().trim() +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + momoProperties.getIpnUrl().trim() +
                        "&orderId=" + momoOrderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + momoProperties.getPartnerCode().trim() +
                        "&redirectUrl=" + momoProperties.getRedirectUrl().trim() +
                        "&requestId=" + requestId +
                        "&requestType=" + momoProperties.getRequestType().trim();

        String signature = HmacUtil.hmacSha256(
                rawSignature,
                momoProperties.getSecretKey().trim()
        );
        log.info("Raw signature: {}", rawSignature);
        log.info("Signature: {}", signature);
        MomoCreateRequest momoRequest = MomoCreateRequest.builder()
                .partnerCode(momoProperties.getPartnerCode())
                .partnerName("SE330 Shop")
                .storeId("SE330_STORE")
                .requestId(requestId)
                .amount(amount)
                .orderId(momoOrderId)
                .orderInfo(orderInfo)
                .redirectUrl(momoProperties.getRedirectUrl())
                .ipnUrl(momoProperties.getIpnUrl())
                .lang("vi")
                .requestType(momoProperties.getRequestType())
                .autoCapture(true)
                .extraData(extraData)
                .signature(signature)
                .build();

        MomoCreateResponse momoResponse = momoClient.createPayment(momoRequest);

        if (momoResponse == null || momoResponse.getResultCode() == null) {
            savedPayment.setStatus("FAILED");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            throw new RuntimeException("MoMo payment response is invalid");
        }

        if (momoResponse.getResultCode() != 0) {
            savedPayment.setStatus("FAILED");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            throw new RuntimeException("MoMo create payment failed: " + momoResponse.getMessage());
        }

        savedPayment.setPayUrl(momoResponse.getPayUrl());
        savedPayment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(savedPayment);
        return mapToResponse(updatedPayment);
    }
    private PaymentResponse mapToResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setPayUrl(payment.getPayUrl());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());

        return response;
    }
}
