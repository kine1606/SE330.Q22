package com.SE330_Q22.order_service.controller;

import com.SE330_Q22.order_service.dto.OrderRequest;
import com.SE330_Q22.order_service.dto.OrderResponse;
import com.SE330_Q22.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService orderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders()
    {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable Long id)
    {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByUserId(@PathVariable Long userId)
    {
        return orderService.getOrdersByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest,
                                     @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdStr)
    {
        Long userId = (userIdStr != null) ? Long.valueOf(userIdStr) : null;
        return orderService.createOrder(orderRequest, userId);
    }

    @PatchMapping("/{id}/order-failed")
    public OrderResponse markOrderFail(@PathVariable Long id) throws Exception
    {
        return orderService.markOrderFail(id);
    }
}
