package com.merchantsledger.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.MovementType;
import com.merchantsledger.entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
  List<StockMovement> findTop50ByOrderByCreatedAtDesc();
  List<StockMovement> findTop50ByTenantKeyOrderByCreatedAtDesc(String tenantKey);
  Optional<StockMovement> findByTenantKeyAndIdempotencyKey(String tenantKey, String idempotencyKey);
  long countByTenantKey(String tenantKey);
  long countByTenantKeyAndType(String tenantKey, MovementType type);
  long countByTenantKeyAndTypeIn(String tenantKey, Collection<MovementType> types);
}
