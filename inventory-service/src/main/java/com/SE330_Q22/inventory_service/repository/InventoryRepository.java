package com.SE330_Q22.inventory_service.repository;

import com.SE330_Q22.inventory_service.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long>
{
    Optional<InventoryItem> findBySkuCode(String skuCode);

    boolean existsBySkuCode(String skuCode);
}
