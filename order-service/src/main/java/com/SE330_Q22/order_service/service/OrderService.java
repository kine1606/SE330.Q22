package com.SE330_Q22.order_service.service;

import com.SE330_Q22.order_service.client.InventoryClient;
import com.SE330_Q22.order_service.client.ProductClient;
import com.SE330_Q22.order_service.client.dto.ProductResponse;
import com.SE330_Q22.order_service.dto.OrderLineItemRequest;
import com.SE330_Q22.order_service.dto.OrderRequest;
import com.SE330_Q22.order_service.dto.OrderResponse;
import com.SE330_Q22.order_service.entity.Order;
import com.SE330_Q22.order_service.entity.OrderLineItem;
import com.SE330_Q22.order_service.mapper.OrderMapper;
import com.SE330_Q22.order_service.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.cloud.commons.config.DefaultsBindHandlerAdvisor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService
{
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;
    private final DefaultsBindHandlerAdvisor.MappingsProvider mappingsProvider;


    //POST /api/order
    public OrderResponse createOrder(OrderRequest orderRequest)
    {
        List<OrderLineItem> lineItems = new ArrayList<>();
        List<OrderLineItemRequest> reservedItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        try {
            for (OrderLineItemRequest itemRequest : orderRequest.getItems()) {
                // take information for lineItem
                ProductResponse productResponse = productClient.getProductBySkuCode(itemRequest.getSkuCode());
                log.info("product id {} is taken", productResponse.getId());
                //reserve Stock in inventory
                inventoryClient.reserveStock(itemRequest.getSkuCode(), itemRequest.getQuantity());

                log.info("reserved product id {}", productResponse.getId());

                reservedItems.add(itemRequest);

                OrderLineItem orderLineItem = CreateLineItem(productResponse, itemRequest);
                lineItems.add(orderLineItem);

                totalAmount = totalAmount.add(orderLineItem.getLineTotal());
            }

            Order order = Order.builder()
                    .orderNumber(generateOrderNumber())
//                    .userId(request.getUserId())
                    .status("PENDING")
                    .totalAmount(totalAmount)
                    .items(lineItems)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            log.info("Order {} is created", savedOrder.getOrderNumber());
            return orderMapper.toResponse(savedOrder);
        } catch (Exception ex) {
            for (OrderLineItemRequest reservedItem : reservedItems) {
                inventoryClient.releaseStock(reservedItem.getSkuCode(), reservedItem.getQuantity());
            }
            throw ex;
        }
    }

    // PATCH /api/order/{id}/order-success
    public OrderResponse markOrderSuccess(Long orderId) throws Exception
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if(!order.getStatus().equals("PENDING")) {
            log.info("Order {} is not pending", order.getOrderNumber());
            throw new BadRequestException("Order is not pending");
        }
        try
        {
            for (OrderLineItem item : order.getItems())
            {
                inventoryClient.confirmStock(item.getSkuCode(), item.getQuantity());
            }
            order.setStatus("SUCCESS");
            orderRepository.save(order);
            log.info("Order {} is confirmed", order.getOrderNumber());

            return orderMapper.toResponse(order);
        }
        catch (Exception ex)
        {
            log.error("Confirm stock failed for order {}", order.getOrderNumber(), ex);
            throw new RuntimeException("Cannot confirm stock for this order");
        }
    }

    //PATCH /api/order/{id}/order-failed
    public OrderResponse markOrderFail(Long orderId) throws Exception
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if(!order.getStatus().equals("PENDING")) {
            log.info("Order {} is not pending", order.getOrderNumber());
            throw new BadRequestException("Order is not pending");
        }
        try
        {
            for (OrderLineItem item : order.getItems())
            {
                inventoryClient.releaseStock(item.getSkuCode(), item.getQuantity());
            }
            order.setStatus("FAIL");
            orderRepository.save(order);
            log.info("Order {} is failed", order.getOrderNumber());

            return orderMapper.toResponse(order);
        }
        catch (Exception ex)
        {
            log.error("Release stock failed for order {}", order.getOrderNumber(), ex);
            throw new RuntimeException("Cannot confirm stock for this order");
        }
    }

    //GET /api/order
    public List<OrderResponse> getAllOrders() {
        return orderMapper.toResponseList(orderRepository.findAll());
    }

    //GET /api/order/{id}
    public OrderResponse getOrderById(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with " + orderId));
        return orderMapper.toResponse(order);
    }

    //GET api/order/user/{userId}
//    public List<OrderResponse> getOrdersByUserId(UUID userId) {
//        return orderMapper.toResponseList(orderRepository.findByUserId);
//    }

    private OrderLineItem CreateLineItem(ProductResponse productResponse,
                                              OrderLineItemRequest itemRequest)
    {
        BigDecimal lineTotal = productResponse.getPrice()
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        return OrderLineItem.builder()
            .productId(productResponse.getId())
            .skuCode(productResponse.getSkuCode())
            .productName(productResponse.getName())
            .price(productResponse.getPrice())
            .quantity(itemRequest.getQuantity())
            .lineTotal(lineTotal)
            .build();
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
