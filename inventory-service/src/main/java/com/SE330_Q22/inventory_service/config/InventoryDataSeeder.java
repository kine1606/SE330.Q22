package com.SE330_Q22.inventory_service.config;

import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InventoryDataSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) {
        if (inventoryRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        List<InventoryItem> inventoryItems = List.of(
                InventoryItem.builder()
                        .productId(1L)
                        .skuCode("IPHONE_15")
                        .quantityAvailable(10)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                InventoryItem.builder()
                        .productId(2L)
                        .skuCode("IPHONE_15_PRO")
                        .quantityAvailable(8)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                InventoryItem.builder()
                        .productId(3L)
                        .skuCode("SAMSUNG_S24")
                        .quantityAvailable(15)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                InventoryItem.builder()
                        .productId(4L)
                        .skuCode("MACBOOK_AIR_M2")
                        .quantityAvailable(5)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                InventoryItem.builder()
                        .productId(5L)
                        .skuCode("AIRPODS_PRO_2")
                        .quantityAvailable(20)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                InventoryItem.builder()
                        .productId(6L)
                        .skuCode("IPAD_AIR_5")
                        .quantityAvailable(12)
                        .quantityReserved(0)
                        .quantitySold(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        inventoryRepository.saveAll(inventoryItems);
    }
}