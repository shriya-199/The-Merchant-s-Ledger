package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.InventorySummaryResponse;
import com.merchantsledger.dto.LowStockResponse;
import com.merchantsledger.dto.StockItemResponse;
import com.merchantsledger.dto.StockMovementRequest;
import com.merchantsledger.dto.StockMovementResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping("/stock")
  public List<StockItemResponse> listStock(@AuthenticationPrincipal User user, @RequestParam(required = false) Long warehouseId) {
    return inventoryService.listStock(user, warehouseId);
  }

  @GetMapping("/movements")
  public List<StockMovementResponse> listMovements(@AuthenticationPrincipal User user) {
    return inventoryService.listMovements(user);
  }

  @PostMapping("/movements")
  public StockMovementResponse record(@AuthenticationPrincipal User user, @Valid @RequestBody StockMovementRequest request) {
    return inventoryService.recordMovement(user, request);
  }

  @GetMapping("/summary")
  public InventorySummaryResponse summary(@AuthenticationPrincipal User user) {
    return inventoryService.getSummary(user);
  }

  @GetMapping("/low-stock")
  public List<LowStockResponse> lowStock(@AuthenticationPrincipal User user) {
    return inventoryService.getLowStock(user);
  }
}
