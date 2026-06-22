package com.SE330_Q22.product_service;

import com.SE330_Q22.product_service.entity.Product;
import com.SE330_Q22.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    Product.builder().name("Laptop ASUS ROG Strix").description("Laptop Gaming siêu mượt, card RTX 4060").price(new BigDecimal("25000000")).build(),
                    Product.builder().name("MacBook Pro M3").description("Apple Laptop mạnh mẽ dành cho Creator").price(new BigDecimal("35000000")).build(),
                    Product.builder().name("iPhone 15 Pro Max").description("Điện thoại Apple hot nhất năm").price(new BigDecimal("29000000")).build(),
                    Product.builder().name("Samsung Galaxy S24 Ultra").description("Đỉnh cao Android có tích hợp AI").price(new BigDecimal("24000000")).build(),
                    Product.builder().name("Bàn phím cơ Keychron K2").description("Gõ êm, nhôm nguyên khối").price(new BigDecimal("2500000")).build(),
                    Product.builder().name("Chuột Logitech MX Master 3S").description("Tuyệt phẩm chuột công thái học").price(new BigDecimal("2200000")).build()
            ));
            System.out.println("✅ Đã tạo 6 sản phẩm mẫu cho Product Service!");
        }
    }
}
