package com.SE330_Q22.payment_service.client;

import com.SE330_Q22.payment_service.client.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${order.service.url}")
public interface OrderClient
{
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderById(@PathVariable("orderId") Long orderId);

    @PatchMapping("/api/orders/{orderId}/order-failed")
    OrderResponse markOrderFailed(@PathVariable("orderId") Long orderId);

    @PatchMapping("/api/orders/{orderId}/order-success")
    OrderResponse markOrderSuccess(@PathVariable("orderId") Long orderId);
}
