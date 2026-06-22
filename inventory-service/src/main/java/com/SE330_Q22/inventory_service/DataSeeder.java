package com.SE330_Q22.inventory_service;

import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    public DataSeeder(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            inventoryRepository.saveAll(List.of(
                    InventoryItem.builder().productId(1L).skuCode("SKU01").quantityAvailable(50).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build(),
                    InventoryItem.builder().productId(2L).skuCode("SKU02").quantityAvailable(20).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build(),
                    InventoryItem.builder().productId(3L).skuCode("SKU03").quantityAvailable(100).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build(),
                    InventoryItem.builder().productId(4L).skuCode("SKU04").quantityAvailable(75).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build(),
                    InventoryItem.builder().productId(5L).skuCode("SKU05").quantityAvailable(200).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build(),
                    InventoryItem.builder().productId(6L).skuCode("SKU06").quantityAvailable(150).quantityReserved(0).quantitySold(0).createdAt(now).updatedAt(now).build()
            ));
            System.out.println("✅ Đã tạo kho hàng mẫu cho Inventory Service!");
        }
    }
}
