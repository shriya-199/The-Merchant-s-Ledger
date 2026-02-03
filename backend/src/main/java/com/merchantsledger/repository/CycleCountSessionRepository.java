package com.merchantsledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.CycleCountSession;

public interface CycleCountSessionRepository extends JpaRepository<CycleCountSession, Long> {
  List<CycleCountSession> findTop100ByTenantKeyOrderByCreatedAtDesc(String tenantKey);
  Optional<CycleCountSession> findByIdAndTenantKey(Long id, String tenantKey);
}
