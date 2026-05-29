package com.SE330_Q22.product_service;

import com.SE330_Q22.product_service.dto.ProductRequest;
import com.SE330_Q22.product_service.entity.Product;
import com.SE330_Q22.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerTestcontainersTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest request = ProductRequest.builder()
				.skuCode("IPHONE_15")
				.name("iPhone 15")
				.description("Apple iPhone 15 128GB")
				.price(BigDecimal.valueOf(22000000))
				.build();

		mockMvc.perform(post("api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.name").value("iPhone 15"))
				.andExpect(jsonPath("$.description").value("Apple iPhone 15 128GB"))
				.andExpect(jsonPath("$.price").value(BigDecimal.valueOf(22000000)));
	}

	@Test
	void shouldGetAllProducts() throws Exception
	{
		productRepository.save(createProduct("IPHONE_15", "iPhone 15"));
		productRepository.save(createProduct("SAMSUNG_S24", "Samsung S24"));

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void shouldGetProductById() throws Exception
	{
		Product savedProduct = productRepository.save(
				createProduct("IPHONE_15", "iPhone 15")
		);

		mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedProduct.getId().toString()))
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.name").value("iPhone 15"));
	}

	@Test
	void shouldGetProductBySkuCode() throws Exception {
		productRepository.save(createProduct("IPHONE_15", "iPhone 15"));

		mockMvc.perform(get("/api/products/sku/{skuCode}", "IPHONE_15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.name").value("iPhone 15"));
	}

	@Test
	void shouldUpdateProduct() throws Exception {
		Product savedProduct = productRepository.save(
				createProduct("IPHONE_15", "iPhone 15")
		);

		ProductRequest updateRequest = ProductRequest.builder()
				.skuCode("IPHONE_15_UPDATED")
				.name("iPhone 15 Updated")
				.description("Updated description")
				.price(BigDecimal.valueOf(21000000))
				.build();

		mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedProduct.getId().toString()))
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15_UPDATED"))
				.andExpect(jsonPath("$.name").value("iPhone 15 Updated"))
				.andExpect(jsonPath("$.description").value("Updated description"))
				.andExpect(jsonPath("$.price").value(21000000))
				.andExpect(jsonPath("$.stockQuantity").value(20));
	}

	@Test
	void shouldDeleteProduct() throws Exception {
		Product savedProduct = productRepository.save(
				createProduct("IPHONE_15", "iPhone 15")
		);

		mockMvc.perform(delete("/api/products/{id}", savedProduct.getId()))
				.andExpect(status().isNoContent());

		assertFalse(productRepository.existsById(savedProduct.getId()));
	}

	@Test
	void shouldReturnBadRequestWhenCreateProductWithInvalidBody() throws Exception {
		ProductRequest invalidRequest = ProductRequest.builder()
				.skuCode("")
				.name("")
				.description("Invalid product")
				.price(BigDecimal.valueOf(-1000))
				.build();

		mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	private Product createProduct(String skuCode, String name) {
		return Product.builder()
				.skuCode(skuCode)
				.name(name)
				.description(name + " description")
				.price(BigDecimal.valueOf(1000000))
//				.status("ACTIVE")
				.build();
	}
}