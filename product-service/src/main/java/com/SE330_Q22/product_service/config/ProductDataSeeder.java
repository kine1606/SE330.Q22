package com.SE330_Q22.product_service.config;

import com.SE330_Q22.product_service.entity.Product;
import com.SE330_Q22.product_service.entity.ProductStatus;
import com.SE330_Q22.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        List<Product> products = List.of(
                Product.builder()
                        .skuCode("IPHONE_15")
                        .name("iPhone 15")
                        .description("Apple iPhone 15 128GB")
                        .price(BigDecimal.valueOf(22000000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                Product.builder()
                        .skuCode("IPHONE_15_PRO")
                        .name("iPhone 15 Pro")
                        .description("Apple iPhone 15 Pro 256GB")
                        .price(BigDecimal.valueOf(29000000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                Product.builder()
                        .skuCode("SAMSUNG_S24")
                        .name("Samsung Galaxy S24")
                        .description("Samsung Galaxy S24 256GB")
                        .price(BigDecimal.valueOf(21000000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                Product.builder()
                        .skuCode("MACBOOK_AIR_M2")
                        .name("MacBook Air M2")
                        .description("Apple MacBook Air M2 13 inch")
                        .price(BigDecimal.valueOf(25000000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                Product.builder()
                        .skuCode("AIRPODS_PRO_2")
                        .name("AirPods Pro 2")
                        .description("Apple AirPods Pro 2nd Generation")
                        .price(BigDecimal.valueOf(5500000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),

                Product.builder()
                        .skuCode("IPAD_AIR_5")
                        .name("iPad Air 5")
                        .description("Apple iPad Air 5 64GB WiFi")
                        .price(BigDecimal.valueOf(14500000))
                        .status(ProductStatus.ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        productRepository.saveAll(products);
    }
}