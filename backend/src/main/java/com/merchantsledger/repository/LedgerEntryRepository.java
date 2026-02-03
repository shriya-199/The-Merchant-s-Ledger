package com.merchantsledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
  List<LedgerEntry> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
  List<LedgerEntry> findTop20ByOrderByCreatedAtDesc();
  List<LedgerEntry> findByTenantKeyAndCustomerIdOrderByCreatedAtDesc(String tenantKey, Long customerId);
  List<LedgerEntry> findTop50ByTenantKeyOrderByCreatedAtDesc(String tenantKey);
  Optional<LedgerEntry> findByTenantKeyAndIdempotencyKey(String tenantKey, String idempotencyKey);
}
