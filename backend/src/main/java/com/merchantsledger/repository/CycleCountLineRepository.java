package com.merchantsledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.CycleCountLine;

public interface CycleCountLineRepository extends JpaRepository<CycleCountLine, Long> {
  List<CycleCountLine> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
