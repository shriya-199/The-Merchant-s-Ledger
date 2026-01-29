package com.merchantsledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
  List<AuditLog> findTop50ByTenantKeyOrderByCreatedAtDesc(String tenantKey);
}
