package com.SE330_Q22.inventory_service.mapper;

import com.SE330_Q22.inventory_service.dto.InventoryItemRequest;
import com.SE330_Q22.inventory_service.dto.InventoryResponse;
import com.SE330_Q22.inventory_service.entity.InventoryItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper
{
    InventoryItem toEntity(InventoryItemRequest request);
    InventoryResponse toResponse(InventoryItem inventoryItem);
    List<InventoryResponse> toResponseList(List<InventoryItem> inventoryItems);
}
