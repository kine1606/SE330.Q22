package com.SE330_Q22.order_service.client;

import com.SE330_Q22.order_service.client.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient
{
    @PatchMapping("/api/inventory/{skuCode}/reserve")
    InventoryResponse reserveStock(@PathVariable String skuCode, @RequestParam Integer quantity);

    @PatchMapping("/api/inventory/{skuCode}/release")
    InventoryResponse releaseStock(@PathVariable String skuCode, @RequestParam Integer quantity);

    @PatchMapping("/api/inventory/{skuCode}/confirm")
    InventoryResponse confirmStock(@PathVariable String skuCode, @RequestParam Integer quantity);
}
