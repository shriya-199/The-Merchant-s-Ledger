package com.merchantsledger.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchantsledger.dto.StockItemResponse;
import com.merchantsledger.dto.StockMovementResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.InventoryService;

@RestController
@RequestMapping("/api/exports")
public class ExportController {
  private final InventoryService inventoryService;

  public ExportController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping(value = "/stock.csv", produces = "text/csv")
  public ResponseEntity<byte[]> stockCsv(@AuthenticationPrincipal User user) {
    List<StockItemResponse> stock = inventoryService.listStock(user, null);
    StringBuilder builder = new StringBuilder();
    builder.append("Warehouse,Product,SKU,Quantity\n");
    for (StockItemResponse item : stock) {
      builder.append(item.getWarehouseName()).append(',')
          .append(item.getProductName()).append(',')
          .append(item.getSku()).append(',')
          .append(item.getQuantity()).append('\n');
    }
    byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(bytes);
  }

  @GetMapping(value = "/movements.csv", produces = "text/csv")
  public ResponseEntity<byte[]> movementsCsv(@AuthenticationPrincipal User user) {
    List<StockMovementResponse> movements = inventoryService.listMovements(user);
    StringBuilder builder = new StringBuilder();
    builder.append("Type,Product,FromWarehouse,ToWarehouse,Quantity,TransactionId,ReferenceType,ReferenceId,ReasonCode,Note\n");
    for (StockMovementResponse move : movements) {
      builder.append(move.getType()).append(',')
          .append(move.getProductName()).append(',')
          .append(move.getFromWarehouseName() == null ? "" : move.getFromWarehouseName()).append(',')
          .append(move.getToWarehouseName() == null ? "" : move.getToWarehouseName()).append(',')
          .append(move.getQuantity()).append(',')
          .append(move.getTransactionId() == null ? "" : move.getTransactionId()).append(',')
          .append(move.getReferenceType() == null ? "" : move.getReferenceType()).append(',')
          .append(move.getReferenceId() == null ? "" : move.getReferenceId()).append(',')
          .append(move.getReasonCode() == null ? "" : move.getReasonCode()).append(',')
          .append(move.getReferenceNote() == null ? "" : move.getReferenceNote())
          .append('\n');
    }
    byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movements.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(bytes);
  }
}
