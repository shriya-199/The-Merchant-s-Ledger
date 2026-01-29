package com.merchantsledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.StockItem;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
  Optional<StockItem> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
  List<StockItem> findByWarehouseId(Long warehouseId);
  List<StockItem> findByTenantKey(String tenantKey);
}
