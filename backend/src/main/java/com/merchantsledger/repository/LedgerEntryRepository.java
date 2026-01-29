package com.merchantsledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
  List<LedgerEntry> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
  List<LedgerEntry> findTop20ByOrderByCreatedAtDesc();
}
