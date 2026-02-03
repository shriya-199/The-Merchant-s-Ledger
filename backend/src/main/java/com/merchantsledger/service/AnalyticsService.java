package com.merchantsledger.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.AnalyticsResponse;
import com.merchantsledger.dto.AnalyticsResponse.DailyCount;
import com.merchantsledger.entity.MovementType;
import com.merchantsledger.entity.StockMovement;
import com.merchantsledger.entity.User;
import com.merchantsledger.repository.StockItemRepository;
import com.merchantsledger.repository.StockMovementRepository;

@Service
public class AnalyticsService {
  private final StockMovementRepository stockMovementRepository;
  private final StockItemRepository stockItemRepository;

  public AnalyticsService(StockMovementRepository stockMovementRepository, StockItemRepository stockItemRepository) {
    this.stockMovementRepository = stockMovementRepository;
    this.stockItemRepository = stockItemRepository;
  }

  public AnalyticsResponse build(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    long total = stockMovementRepository.countByTenantKey(tenantKey);
    long inbound = stockMovementRepository.countByTenantKeyAndTypeIn(
        tenantKey,
        List.of(MovementType.RECEIVE, MovementType.RETURN, MovementType.RELEASE, MovementType.INBOUND));
    long outbound = stockMovementRepository.countByTenantKeyAndTypeIn(
        tenantKey,
        List.of(MovementType.SHIP, MovementType.RESERVE, MovementType.DAMAGE, MovementType.OUTBOUND));
    long transfer = stockMovementRepository.countByTenantKeyAndType(tenantKey, MovementType.TRANSFER);

    long lowStock = stockItemRepository.findByTenantKey(tenantKey).stream()
        .filter(item -> item.getQuantity() <= item.getProduct().getReorderLevel())
        .count();

    List<StockMovement> recent = stockMovementRepository.findTop50ByTenantKeyOrderByCreatedAtDesc(tenantKey);
    Map<LocalDate, Long> daily = new LinkedHashMap<>();
    LocalDate today = LocalDate.now();
    for (int i = 6; i >= 0; i--) {
      daily.put(today.minusDays(i), 0L);
    }
    for (StockMovement movement : recent) {
      LocalDate day = movement.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
      if (daily.containsKey(day)) {
        daily.put(day, daily.get(day) + 1);
      }
    }

    List<DailyCount> dailyCounts = new ArrayList<>();
    for (Map.Entry<LocalDate, Long> entry : daily.entrySet()) {
      dailyCounts.add(new DailyCount(entry.getKey().toString(), entry.getValue()));
    }

    return new AnalyticsResponse(total, inbound, outbound, transfer, lowStock, dailyCounts);
  }
}
