package com.merchantsledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.WarehouseLocation;

public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, Long> {
  List<WarehouseLocation> findByWarehouseIdAndTenantKeyOrderByZoneAscAisleAscRackAscShelfAscBinAsc(
      Long warehouseId,
      String tenantKey
  );

  Optional<WarehouseLocation> findByWarehouseIdAndTenantKeyAndLocationCode(
      Long warehouseId,
      String tenantKey,
      String locationCode
  );
}
