package com.SE330_Q22.inventory_service;

import com.SE330_Q22.inventory_service.dto.InventoryItemRequest;
import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.repository.InventoryRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InventoryControllerTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		inventoryRepository.deleteAll();
	}

	@Test
	void shouldCreateInventoryItem() throws Exception {
		InventoryItemRequest request = InventoryItemRequest.builder()
				.productId(1L)
				.skuCode("IPHONE_15")
				.quantityAvailable(10)
				.build();

		mockMvc.perform(post("/api/inventory")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		InventoryItem savedItem = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals("IPHONE_15", savedItem.getSkuCode());
		assertEquals(10, savedItem.getQuantityAvailable());
		assertEquals(0, savedItem.getQuantityReserved());
		assertEquals(0, savedItem.getQuantitySold());
	}

	@Test
	void shouldGetAllInventoryItems() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 10));
		inventoryRepository.save(createInventoryItem("SAMSUNG_S24", 5));

		mockMvc.perform(get("/api/inventory"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void shouldGetInventoryItemBySkuCode() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 10));

		mockMvc.perform(get("/api/inventory/sku/{skuCode}", "IPHONE_15"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.quantityAvailable").value(10))
				.andExpect(jsonPath("$.quantityReserved").value(0))
				.andExpect(jsonPath("$.quantitySold").value(0));
	}

	@Test
	void shouldReserveStock() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 10));

		mockMvc.perform(patch("/api/inventory/{skuCode}/reserve", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.quantityAvailable").value(8))
				.andExpect(jsonPath("$.quantityReserved").value(2))
				.andExpect(jsonPath("$.quantitySold").value(0));

		InventoryItem item = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(8, item.getQuantityAvailable());
		assertEquals(2, item.getQuantityReserved());
		assertEquals(0, item.getQuantitySold());
	}

	@Test
	void shouldNotReserveStockWhenQuantityIsGreaterThanAvailable() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 1));

		mockMvc.perform(patch("/api/inventory/{skuCode}/reserve", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().is5xxServerError());

		InventoryItem item = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(1, item.getQuantityAvailable());
		assertEquals(0, item.getQuantityReserved());
		assertEquals(0, item.getQuantitySold());
	}

	@Test
	void shouldConfirmStock() throws Exception {
		InventoryItem item = createInventoryItem("IPHONE_15", 8);
		item.setQuantityReserved(2);
		inventoryRepository.save(item);

		mockMvc.perform(patch("/api/inventory/{skuCode}/confirm", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.quantityAvailable").value(8))
				.andExpect(jsonPath("$.quantityReserved").value(0))
				.andExpect(jsonPath("$.quantitySold").value(2));

		InventoryItem savedItem = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(8, savedItem.getQuantityAvailable());
		assertEquals(0, savedItem.getQuantityReserved());
		assertEquals(2, savedItem.getQuantitySold());
	}

	@Test
	void shouldNotConfirmStockWhenReservedQuantityIsNotEnough() throws Exception {
		InventoryItem item = createInventoryItem("IPHONE_15", 8);
		item.setQuantityReserved(1);
		inventoryRepository.save(item);

		mockMvc.perform(patch("/api/inventory/{skuCode}/confirm", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().isBadRequest());

		InventoryItem savedItem = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(8, savedItem.getQuantityAvailable());
		assertEquals(1, savedItem.getQuantityReserved());
		assertEquals(0, savedItem.getQuantitySold());
	}

	@Test
	void shouldReleaseStock() throws Exception {
		InventoryItem item = createInventoryItem("IPHONE_15", 8);
		item.setQuantityReserved(2);
		inventoryRepository.save(item);

		mockMvc.perform(patch("/api/inventory/{skuCode}/release", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.quantityAvailable").value(10))
				.andExpect(jsonPath("$.quantityReserved").value(0))
				.andExpect(jsonPath("$.quantitySold").value(0));

		InventoryItem savedItem = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(10, savedItem.getQuantityAvailable());
		assertEquals(0, savedItem.getQuantityReserved());
		assertEquals(0, savedItem.getQuantitySold());
	}

	@Test
	void shouldNotReleaseStockWhenReservedQuantityIsNotEnough() throws Exception {
		InventoryItem item = createInventoryItem("IPHONE_15", 8);
		item.setQuantityReserved(1);
		inventoryRepository.save(item);

		mockMvc.perform(patch("/api/inventory/{skuCode}/release", "IPHONE_15")
						.param("quantity", "2"))
				.andExpect(status().isBadRequest());

		InventoryItem savedItem = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(8, savedItem.getQuantityAvailable());
		assertEquals(1, savedItem.getQuantityReserved());
		assertEquals(0, savedItem.getQuantitySold());
	}

	@Test
	void shouldAddStock() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 10));

		mockMvc.perform(patch("/api/inventory/{skuCode}/restock", "IPHONE_15")
						.param("quantity", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.skuCode").value("IPHONE_15"))
				.andExpect(jsonPath("$.quantityAvailable").value(15))
				.andExpect(jsonPath("$.quantityReserved").value(0))
				.andExpect(jsonPath("$.quantitySold").value(0));

		InventoryItem item = inventoryRepository.findBySkuCode("IPHONE_15")
				.orElseThrow();

		assertEquals(15, item.getQuantityAvailable());
		assertEquals(0, item.getQuantityReserved());
		assertEquals(0, item.getQuantitySold());
	}

	@Test
	void shouldReturnBadRequestWhenQuantityIsInvalid() throws Exception {
		inventoryRepository.save(createInventoryItem("IPHONE_15", 10));

		mockMvc.perform(patch("/api/inventory/{skuCode}/reserve", "IPHONE_15")
						.param("quantity", "0"))
				.andExpect(status().isBadRequest());
	}

	private InventoryItem createInventoryItem(String skuCode, Integer quantityAvailable) {
		LocalDateTime now = LocalDateTime.now();

		return InventoryItem.builder()
				.productId(1L)
				.skuCode(skuCode)
				.quantityAvailable(quantityAvailable)
				.quantityReserved(0)
				.quantitySold(0)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}