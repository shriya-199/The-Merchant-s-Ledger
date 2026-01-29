package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.merchantsledger.dto.InventorySummaryResponse;
import com.merchantsledger.dto.LowStockResponse;
import com.merchantsledger.dto.StockItemResponse;
import com.merchantsledger.dto.StockMovementRequest;
import com.merchantsledger.dto.StockMovementResponse;
import com.merchantsledger.entity.MovementType;
import com.merchantsledger.entity.Product;
import com.merchantsledger.entity.StockItem;
import com.merchantsledger.entity.StockMovement;
import com.merchantsledger.entity.User;
import com.merchantsledger.entity.Warehouse;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.ProductRepository;
import com.merchantsledger.repository.StockItemRepository;
import com.merchantsledger.repository.StockMovementRepository;
import com.merchantsledger.repository.WarehouseRepository;

@Service
public class InventoryService {
  private final StockItemRepository stockItemRepository;
  private final StockMovementRepository stockMovementRepository;
  private final WarehouseRepository warehouseRepository;
  private final ProductRepository productRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final AuditService auditService;
  private final NotificationService notificationService;

  public InventoryService(StockItemRepository stockItemRepository,
                          StockMovementRepository stockMovementRepository,
                          WarehouseRepository warehouseRepository,
                          ProductRepository productRepository,
                          SimpMessagingTemplate messagingTemplate,
                          AuditService auditService,
                          NotificationService notificationService) {
    this.stockItemRepository = stockItemRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.warehouseRepository = warehouseRepository;
    this.productRepository = productRepository;
    this.messagingTemplate = messagingTemplate;
    this.auditService = auditService;
    this.notificationService = notificationService;
  }

  public List<StockItemResponse> listStock(User user, Long warehouseId) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    List<StockItem> items = warehouseId == null
        ? stockItemRepository.findByTenantKey(tenantKey)
        : stockItemRepository.findByWarehouseId(warehouseId).stream()
            .filter(item -> item.getTenantKey().equals(tenantKey))
            .collect(Collectors.toList());

    return items.stream().map(this::toStockResponse).collect(Collectors.toList());
  }

  public List<StockMovementResponse> listMovements(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return stockMovementRepository.findTop50ByTenantKeyOrderByCreatedAtDesc(tenantKey).stream()
        .map(this::toMovementResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public StockMovementResponse recordMovement(User user, StockMovementRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Product product = productRepository.findByIdAndTenantKey(request.getProductId(), tenantKey)
        .orElseThrow(() -> new NotFoundException("Product not found"));

    Warehouse fromWarehouse = null;
    Warehouse toWarehouse = null;

    if (request.getFromWarehouseId() != null) {
      fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
          .orElseThrow(() -> new NotFoundException("From warehouse not found"));
      if (!fromWarehouse.getTenantKey().equals(tenantKey)) {
        throw new NotFoundException("From warehouse not found");
      }
    }

    if (request.getToWarehouseId() != null) {
      toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
          .orElseThrow(() -> new NotFoundException("To warehouse not found"));
      if (!toWarehouse.getTenantKey().equals(tenantKey)) {
        throw new NotFoundException("To warehouse not found");
      }
    }

    if (request.getType() == MovementType.INBOUND && toWarehouse == null) {
      throw new BadRequestException("Inbound movement requires destination warehouse");
    }
    if (request.getType() == MovementType.OUTBOUND && fromWarehouse == null) {
      throw new BadRequestException("Outbound movement requires source warehouse");
    }
    if (request.getType() == MovementType.TRANSFER && (fromWarehouse == null || toWarehouse == null)) {
      throw new BadRequestException("Transfer requires both source and destination warehouses");
    }

    if (request.getType() == MovementType.INBOUND) {
      adjustStock(toWarehouse, product, request.getQuantity(), tenantKey);
    } else if (request.getType() == MovementType.OUTBOUND) {
      adjustStock(fromWarehouse, product, -request.getQuantity(), tenantKey);
    } else {
      adjustStock(fromWarehouse, product, -request.getQuantity(), tenantKey);
      adjustStock(toWarehouse, product, request.getQuantity(), tenantKey);
    }

    StockMovement movement = new StockMovement();
    movement.setType(request.getType());
    movement.setProduct(product);
    movement.setFromWarehouse(fromWarehouse);
    movement.setToWarehouse(toWarehouse);
    movement.setQuantity(request.getQuantity());
    movement.setReferenceNote(request.getReferenceNote());
    movement.setTenantKey(tenantKey);

    StockMovement saved = stockMovementRepository.save(movement);
    StockMovementResponse response = toMovementResponse(saved);

    messagingTemplate.convertAndSend("/topic/stock", response);
    auditService.log(user, "STOCK_MOVEMENT", "StockMovement", String.valueOf(saved.getId()), saved.getType().name());
    checkLowStockAlerts(user, product, fromWarehouse, toWarehouse);

    return response;
  }

  public InventorySummaryResponse getSummary(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    long warehouses = warehouseRepository.findByTenantKey(tenantKey).size();
    long products = productRepository.findByTenantKey(tenantKey).size();
    long totalUnits = stockItemRepository.findByTenantKey(tenantKey).stream()
        .mapToLong(StockItem::getQuantity)
        .sum();
    return new InventorySummaryResponse(warehouses, products, totalUnits);
  }

  public List<LowStockResponse> getLowStock(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return stockItemRepository.findByTenantKey(tenantKey).stream()
        .filter(item -> item.getQuantity() <= item.getProduct().getReorderLevel())
        .map(item -> new LowStockResponse(
            item.getWarehouse().getId(),
            item.getWarehouse().getName(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getSku(),
            item.getQuantity(),
            item.getProduct().getReorderLevel()
        ))
        .collect(Collectors.toList());
  }

  private void adjustStock(Warehouse warehouse, Product product, long delta, String tenantKey) {
    StockItem item = stockItemRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
        .orElseGet(() -> {
          StockItem created = new StockItem();
          created.setWarehouse(warehouse);
          created.setProduct(product);
          created.setQuantity(0);
          created.setTenantKey(tenantKey);
          return created;
        });

    long updated = item.getQuantity() + delta;
    if (updated < 0) {
      throw new BadRequestException("Insufficient stock in warehouse " + warehouse.getName());
    }
    item.setQuantity(updated);
    item.setTenantKey(tenantKey);
    stockItemRepository.save(item);
  }

  private void checkLowStockAlerts(User user, Product product, Warehouse fromWarehouse, Warehouse toWarehouse) {
    if (fromWarehouse != null) {
      StockItem item = stockItemRepository.findByWarehouseIdAndProductId(fromWarehouse.getId(), product.getId())
          .orElse(null);
      if (item != null && item.getQuantity() <= product.getReorderLevel()) {
        notificationService.notify(user, "Low stock alert", product.getName() + " below reorder level at " + fromWarehouse.getName(), "warning");
      }
    }
    if (toWarehouse != null) {
      StockItem item = stockItemRepository.findByWarehouseIdAndProductId(toWarehouse.getId(), product.getId())
          .orElse(null);
      if (item != null && item.getQuantity() <= product.getReorderLevel()) {
        notificationService.notify(user, "Low stock alert", product.getName() + " below reorder level at " + toWarehouse.getName(), "warning");
      }
    }
  }

  private StockItemResponse toStockResponse(StockItem item) {
    return new StockItemResponse(
        item.getId(),
        item.getWarehouse().getId(),
        item.getWarehouse().getName(),
        item.getProduct().getId(),
        item.getProduct().getName(),
        item.getProduct().getSku(),
        item.getQuantity(),
        item.getUpdatedAt()
    );
  }

  private StockMovementResponse toMovementResponse(StockMovement movement) {
    return new StockMovementResponse(
        movement.getId(),
        movement.getType(),
        movement.getProduct().getId(),
        movement.getProduct().getName(),
        movement.getFromWarehouse() != null ? movement.getFromWarehouse().getId() : null,
        movement.getFromWarehouse() != null ? movement.getFromWarehouse().getName() : null,
        movement.getToWarehouse() != null ? movement.getToWarehouse().getId() : null,
        movement.getToWarehouse() != null ? movement.getToWarehouse().getName() : null,
        movement.getQuantity(),
        movement.getReferenceNote(),
        movement.getCreatedAt()
    );
  }
}
