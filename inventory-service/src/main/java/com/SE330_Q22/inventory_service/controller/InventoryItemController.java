package com.SE330_Q22.inventory_service.controller;

import com.SE330_Q22.inventory_service.dto.InventoryItemRequest;
import com.SE330_Q22.inventory_service.dto.InventoryResponse;
import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.mapper.InventoryItemMapper;
import com.SE330_Q22.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryItemController
{
    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getListInventoryItems()
    {
        return inventoryService.getListInventoryItem();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse getInventoryItemById(@PathVariable Long id)
    {
        return inventoryService.getById(id);
    }

    @GetMapping("/sku/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse getInventoryItemBySkuCode(@PathVariable String skuCode)
    {
        return inventoryService.getBySkuCode(skuCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createInventoryItem(@RequestBody InventoryItemRequest request)
    {
        inventoryService.createInventoryItem(request);
    }

    @PatchMapping("/{skuCode}/reserve")
    //PATCH /api/inventory/{skuCode}/reserve?quantity=
    public InventoryResponse reserveItem(@PathVariable String skuCode, @RequestParam Integer quantity)
    {
        return inventoryService.reserveStock(skuCode, quantity);
    }
    @PatchMapping("/{skuCode}/confirm")
    //PATCH /api/inventory/{skuCode}/confirm?quantity=
    public InventoryResponse confirmItem(@PathVariable String skuCode, @RequestParam Integer quantity)
    {
        return inventoryService.confirmStock(skuCode, quantity);
    }
    @PatchMapping("/{skuCode}/release")
    //PATCH /api/inventory/{skuCode}/release?quantity=
    public InventoryResponse releaseItem(@PathVariable String skuCode, @RequestParam Integer quantity)
    {
        return inventoryService.releaseStock(skuCode, quantity);
    }
    @PatchMapping("/{skuCode}/restock")
    //PATCH /api/inventory/{skuCode}/restock?quantity=
    public InventoryResponse restockItem(@PathVariable String skuCode, @RequestParam Integer quantity)
    {
        return inventoryService.restock(skuCode, quantity);
    }
}
