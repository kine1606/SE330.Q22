package com.SE330_Q22.inventory_service.service;

import com.SE330_Q22.inventory_service.dto.InventoryItemRequest;
import com.SE330_Q22.inventory_service.dto.InventoryResponse;
import com.SE330_Q22.inventory_service.entity.InventoryItem;
import com.SE330_Q22.inventory_service.mapper.InventoryItemMapper;
import com.SE330_Q22.inventory_service.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService
{
    private final InventoryRepository inventoryRepository;
    private final InventoryItemMapper inventoryItemMapper;


    //HELPER
    private InventoryItem getItemBySkuCode(String skuCode)
    {
        return inventoryRepository.findBySkuCode((skuCode))
                .orElseThrow(()-> new EntityNotFoundException("Inventory Item not found with skuCode " + skuCode));
    }
    private void validateQuantity(Integer quantity)
    {
        if(quantity == null || quantity <= 0)
        {
            throw new RuntimeException("Quantity must be greater than 0");
        }
    }
    private boolean isNotEnough(Integer quantity, Integer targetQuantity)
    {
        return quantity < targetQuantity;
    }
    //==========================================================--===================================================

//    POST /api/inventory
    public void createInventoryItem(InventoryItemRequest request)
    {
        if (inventoryRepository.existsBySkuCode(request.getSkuCode())) {
            throw new RuntimeException("Inventory item already exists with skuCode " + request.getSkuCode());
        }
        InventoryItem inventoryItem = inventoryItemMapper.toEntity(request);
        inventoryItem.setCreatedAt(LocalDateTime.now());
        inventoryItem.setUpdatedAt(LocalDateTime.now());
        inventoryItem.setQuantityReserved(0);
        inventoryItem.setQuantitySold(0);

        inventoryRepository.save(inventoryItem);
        log.info("Inventory item {} is created",  inventoryItem.getId());
    }

    //    GET  /api/inventory
    public List<InventoryResponse> getListInventoryItem()
    {
        return  inventoryItemMapper.toResponseList(inventoryRepository.findAll());
    }

    //    GET  /api/inventory/{id}
    public InventoryResponse getById(Long id)
    {
        InventoryItem inventoryItem = inventoryRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Inventory Item not found with id " + id));
        return  inventoryItemMapper.toResponse(inventoryItem);
    }

    //GET  /api/inventory/sku/{skuCode}
    public InventoryResponse getBySkuCode(String skuCode)
    {
        return  inventoryItemMapper.toResponse(getItemBySkuCode(skuCode));
    }

    //PATCH /api/inventory/{skuCode}/reserve?quantity=?
    // reserve item when order
    @Transactional
    public InventoryResponse reserveStock(String skuCode, Integer quantity)
    {
        validateQuantity(quantity);;
        InventoryItem item = getItemBySkuCode(skuCode);
        if (isNotEnough(item.getQuantityAvailable(), quantity))
        {
            log.info("Not enough stock for skuCode: {}. Available: {}, requested: {}",
                    item.getSkuCode(), item.getQuantityAvailable(), quantity);
            throw new RuntimeException("Not enough available items to reserve");
        }
        item.setQuantityAvailable(item.getQuantityAvailable() - quantity);
        item.setQuantityReserved(quantity + item.getQuantityReserved());
        item.setUpdatedAt(LocalDateTime.now());

        InventoryItem savedItem = inventoryRepository.save(item);

        log.info("Reserved {} items for skuCode {}", quantity, item.getSkuCode());
        return inventoryItemMapper.toResponse(savedItem);
    }

    //PATCH /api/inventory/{skuCode}/confirm?quantity=?
    // confirm when payment ok or order success
    @Transactional
    public InventoryResponse confirmStock(String skuCode, Integer quantity)
    {
        validateQuantity(quantity);
        InventoryItem item = getItemBySkuCode(skuCode);
        if (isNotEnough(item.getQuantityReserved(), quantity))
        {
            throw new RuntimeException("Not enough reserved items to sold");
        }

        item.setQuantityReserved(item.getQuantityReserved() - quantity);
        item.setQuantitySold(item.getQuantitySold() + quantity);
        item.setUpdatedAt(LocalDateTime.now());

        InventoryItem savedItem = inventoryRepository.save(item);

        log.info("Sold {} items for skuCode: {}", quantity, skuCode);
        return  inventoryItemMapper.toResponse(savedItem);
    }

    //PATCH /api/inventory/{skuCode}/release?quantity=?
    // when fail payment  or cancel order
    @Transactional
    public InventoryResponse releaseStock(String skuCode, Integer quantity)
    {
        validateQuantity(quantity);
        InventoryItem item = getItemBySkuCode(skuCode);
        if (isNotEnough(item.getQuantityReserved(), quantity))
        {
            throw new RuntimeException("Not enough reserved items to release");
        }

        item.setQuantityReserved(item.getQuantityReserved() - quantity);
        item.setQuantityAvailable(item.getQuantityAvailable() + quantity);
        item.setUpdatedAt(LocalDateTime.now());
        InventoryItem savedItem = inventoryRepository.save(item);

        log.info("Released {} items for skuCode: {}", quantity, skuCode);
        return  inventoryItemMapper.toResponse(savedItem);
    }

    //PATCH /api/inventory/{skuCode}/add?quantity=?
    public InventoryResponse restock(String skuCode, Integer quantity)
    {
        InventoryItem item = getItemBySkuCode(skuCode);
        validateQuantity(quantity);

        item.setQuantityAvailable(item.getQuantityAvailable() + quantity);
        item.setUpdatedAt(LocalDateTime.now());
        InventoryItem savedItem = inventoryRepository.save(item);
        log.info("Added {} items for skuCode: {}", quantity, skuCode);

        return  inventoryItemMapper.toResponse(savedItem);
    }
}
