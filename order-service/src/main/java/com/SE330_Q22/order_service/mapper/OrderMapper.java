package com.SE330_Q22.order_service.mapper;

import com.SE330_Q22.order_service.dto.OrderRequest;
import com.SE330_Q22.order_service.dto.OrderResponse;
import com.SE330_Q22.order_service.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface OrderMapper
{
    Order toEntity(OrderRequest orderRequest);

    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);
}
