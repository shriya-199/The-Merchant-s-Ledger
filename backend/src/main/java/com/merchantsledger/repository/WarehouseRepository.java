package com.merchantsledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
  List<Warehouse> findByTenantKey(String tenantKey);
}
