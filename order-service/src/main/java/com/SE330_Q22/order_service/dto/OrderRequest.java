package com.SE330_Q22.order_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

//    @NotNull(message = "User id is required")
//    private Long userId;

//    @Valid
//    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderLineItemRequest> items;
}
