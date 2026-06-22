package com.SE330_Q22.inventory_service;

import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
            inventoryRepository.saveAll(List.of(
                    InventoryItem.builder().productId(1L).quantity(50).build(),
                    InventoryItem.builder().productId(2L).quantity(20).build(),
                    InventoryItem.builder().productId(3L).quantity(100).build(),
                    InventoryItem.builder().productId(4L).quantity(75).build(),
                    InventoryItem.builder().productId(5L).quantity(200).build(),
                    InventoryItem.builder().productId(6L).quantity(150).build()
            ));
            System.out.println("✅ Đã tạo kho hàng mẫu cho Inventory Service!");
        }
    }
}
