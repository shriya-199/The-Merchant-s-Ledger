package com.merchantsledger.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.StockItem;
import com.merchantsledger.entity.StockMovement;
import com.merchantsledger.entity.User;
import com.merchantsledger.entity.Warehouse;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.ForbiddenException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.ProductRepository;
import com.merchantsledger.repository.StockItemRepository;
import com.merchantsledger.repository.StockMovementRepository;
import com.merchantsledger.repository.WarehouseRepository;
import com.merchantsledger.repository.WarehouseLocationRepository;

@Service
public class InventoryService {
  private final StockItemRepository stockItemRepository;
  private final StockMovementRepository stockMovementRepository;
  private final WarehouseRepository warehouseRepository;
  private final ProductRepository productRepository;
  private final WarehouseLocationRepository warehouseLocationRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final AuditService auditService;
  private final NotificationService notificationService;

  public InventoryService(StockItemRepository stockItemRepository,
                          StockMovementRepository stockMovementRepository,
                          WarehouseRepository warehouseRepository,
                          ProductRepository productRepository,
                          WarehouseLocationRepository warehouseLocationRepository,
                          SimpMessagingTemplate messagingTemplate,
                          AuditService auditService,
                          NotificationService notificationService) {
    this.stockItemRepository = stockItemRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.warehouseRepository = warehouseRepository;
    this.productRepository = productRepository;
    this.warehouseLocationRepository = warehouseLocationRepository;
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
    MovementType normalizedType = normalizeMovementType(request.getType());
    if (!isBlank(request.getIdempotencyKey())) {
      StockMovement existing = stockMovementRepository
          .findByTenantKeyAndIdempotencyKey(tenantKey, request.getIdempotencyKey())
          .orElse(null);
      if (existing != null) {
        return toMovementResponse(existing);
      }
    }

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

    validateWarehouses(request.getType(), fromWarehouse, toWarehouse);
    validateRoleForMovement(user, normalizedType);
    validateHierarchyLocation(user, fromWarehouse, request.getSourceLocation(), "source");
    validateHierarchyLocation(user, toWarehouse, request.getDestinationLocation(), "destination");

    if (normalizedType == MovementType.TRANSFER && fromWarehouse.getId().equals(toWarehouse.getId())) {
      throw new BadRequestException("Transfer source and destination must be different");
    }

    if (normalizedType == MovementType.RECEIVE || normalizedType == MovementType.RETURN) {
      adjustStock(toWarehouse, product, request.getQuantity(), tenantKey);
    } else if (normalizedType == MovementType.SHIP
        || normalizedType == MovementType.RESERVE
        || normalizedType == MovementType.DAMAGE) {
      adjustStock(fromWarehouse, product, -request.getQuantity(), tenantKey);
    } else if (normalizedType == MovementType.RELEASE) {
      adjustStock(fromWarehouse, product, request.getQuantity(), tenantKey);
    } else if (normalizedType == MovementType.TRANSFER) {
      adjustStock(fromWarehouse, product, -request.getQuantity(), tenantKey);
      adjustStock(toWarehouse, product, request.getQuantity(), tenantKey);
    } else if (normalizedType == MovementType.ADJUST) {
      Warehouse targetWarehouse = fromWarehouse != null ? fromWarehouse : toWarehouse;
      String direction = request.getAdjustmentDirection() == null
          ? "INCREASE"
          : request.getAdjustmentDirection().trim().toUpperCase(Locale.ROOT);
      if (!direction.equals("INCREASE") && !direction.equals("DECREASE")) {
        throw new BadRequestException("Adjustment direction must be INCREASE or DECREASE");
      }
      long delta = direction.equals("INCREASE") ? request.getQuantity() : -request.getQuantity();
      adjustStock(targetWarehouse, product, delta, tenantKey);
    }

    StockMovement movement = new StockMovement();
    movement.setType(normalizedType);
    movement.setProduct(product);
    movement.setFromWarehouse(fromWarehouse);
    movement.setToWarehouse(toWarehouse);
    movement.setQuantity(request.getQuantity());
    movement.setTransactionId(
        isBlank(request.getTransactionId()) ? UUID.randomUUID().toString() : request.getTransactionId().trim());
    movement.setIdempotencyKey(normalize(request.getIdempotencyKey()));
    movement.setCorrelationId(isBlank(request.getCorrelationId()) ? movement.getTransactionId() : request.getCorrelationId().trim());
    movement.setReferenceType(normalize(request.getReferenceType()));
    movement.setReferenceId(normalize(request.getReferenceId()));
    movement.setReasonCode(normalize(request.getReasonCode()));
    movement.setSourceLocation(isBlank(request.getSourceLocation()) ? null : normalizeLocationCode(request.getSourceLocation()));
    movement.setDestinationLocation(isBlank(request.getDestinationLocation()) ? null : normalizeLocationCode(request.getDestinationLocation()));
    movement.setPerformedBy(user.getFullName() + " <" + user.getEmail() + ">");
    movement.setPerformedVia(isBlank(request.getPerformedVia()) ? "API" : request.getPerformedVia().trim());
    movement.setMetadataJson(normalize(request.getMetadataJson()));
    movement.setReferenceNote(request.getReferenceNote());
    movement.setTenantKey(tenantKey);

    StockMovement saved = stockMovementRepository.save(movement);
    StockMovementResponse response = toMovementResponse(saved);

    messagingTemplate.convertAndSend("/topic/stock", response);
    auditService.log(user, "STOCK_MOVEMENT", "StockMovement", String.valueOf(saved.getId()),
        saved.getType().name() + " tx=" + saved.getTransactionId());
    checkLowStockAlerts(user, product, touchedWarehouses(fromWarehouse, toWarehouse));

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

  private void checkLowStockAlerts(User user, Product product, List<Warehouse> warehouses) {
    for (Warehouse warehouse : warehouses) {
      if (warehouse == null) {
        continue;
      }
      StockItem item = stockItemRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
          .orElse(null);
      if (item != null && item.getQuantity() <= product.getReorderLevel()) {
        notificationService.notify(
            user,
            "Low stock alert",
            product.getName() + " below reorder level at " + warehouse.getName(),
            "warning");
      }
    }
  }

  private List<Warehouse> touchedWarehouses(Warehouse fromWarehouse, Warehouse toWarehouse) {
    List<Warehouse> warehouses = new ArrayList<>();
    if (fromWarehouse != null) {
      warehouses.add(fromWarehouse);
    }
    if (toWarehouse != null && (fromWarehouse == null || !fromWarehouse.getId().equals(toWarehouse.getId()))) {
      warehouses.add(toWarehouse);
    }
    return warehouses;
  }

  private void validateWarehouses(MovementType type, Warehouse fromWarehouse, Warehouse toWarehouse) {
    MovementType normalizedType = normalizeMovementType(type);
    if ((normalizedType == MovementType.RECEIVE || normalizedType == MovementType.RETURN) && toWarehouse == null) {
      throw new BadRequestException(normalizedType.name() + " requires destination warehouse");
    }
    if ((normalizedType == MovementType.SHIP || normalizedType == MovementType.RESERVE || normalizedType == MovementType.RELEASE
        || normalizedType == MovementType.DAMAGE) && fromWarehouse == null) {
      throw new BadRequestException(normalizedType.name() + " requires source warehouse");
    }
    if (normalizedType == MovementType.TRANSFER && (fromWarehouse == null || toWarehouse == null)) {
      throw new BadRequestException("TRANSFER requires both source and destination warehouses");
    }
    if (normalizedType == MovementType.ADJUST && fromWarehouse == null && toWarehouse == null) {
      throw new BadRequestException("ADJUST requires at least one warehouse");
    }
  }

  private void validateRoleForMovement(User user, MovementType type) {
    if (type == MovementType.ADJUST && !hasAnyRole(
        user,
        RoleName.SYSTEM_ADMIN,
        RoleName.MERCHANT_ADMIN,
        RoleName.WAREHOUSE_MANAGER,
        RoleName.INVENTORY_AUDITOR,
        RoleName.ADMIN,
        RoleName.MANAGER
    )) {
      throw new ForbiddenException("You are not allowed to perform stock adjustments");
    }

    if ((type == MovementType.SHIP || type == MovementType.RESERVE || type == MovementType.RELEASE)
        && !hasAnyRole(
        user,
        RoleName.SYSTEM_ADMIN,
        RoleName.MERCHANT_ADMIN,
        RoleName.MERCHANT_OPERATIONS,
        RoleName.WAREHOUSE_MANAGER,
        RoleName.PICKER_PACKER,
        RoleName.ADMIN,
        RoleName.MANAGER,
        RoleName.STAFF
    )) {
      throw new ForbiddenException("You do not have shipping/reservation permission");
    }

    if ((type == MovementType.RECEIVE || type == MovementType.RETURN)
        && !hasAnyRole(
        user,
        RoleName.SYSTEM_ADMIN,
        RoleName.MERCHANT_ADMIN,
        RoleName.MERCHANT_OPERATIONS,
        RoleName.WAREHOUSE_MANAGER,
        RoleName.RECEIVER_GRN_OPERATOR,
        RoleName.INVENTORY_AUDITOR,
        RoleName.ADMIN,
        RoleName.MANAGER,
        RoleName.STAFF
    )) {
      throw new ForbiddenException("You do not have receiving permission");
    }
  }

  private void validateHierarchyLocation(User user, Warehouse warehouse, String locationCode, String side) {
    if (warehouse == null || isBlank(locationCode)) {
      return;
    }
    String tenantKey = TenantResolver.resolveTenantKey(user);
    String normalized = normalizeLocationCode(locationCode);
    boolean exists = warehouseLocationRepository.findByWarehouseIdAndTenantKeyAndLocationCode(
        warehouse.getId(),
        tenantKey,
        normalized
    ).isPresent();
    if (!exists) {
      throw new BadRequestException("Unknown " + side + " location for warehouse " + warehouse.getName() + ": " + normalized);
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private String normalize(String value) {
    if (isBlank(value)) {
      return null;
    }
    return value.trim();
  }

  private String normalizeLocationCode(String value) {
    if (value == null || value.isBlank()) {
      return value;
    }
    String[] segments = value.split("/");
    if (segments.length != 5) {
      throw new BadRequestException("Location must follow Zone/Aisle/Rack/Shelf/Bin");
    }
    return String.join("/",
        segments[0].trim().toUpperCase(Locale.ROOT),
        segments[1].trim().toUpperCase(Locale.ROOT),
        segments[2].trim().toUpperCase(Locale.ROOT),
        segments[3].trim().toUpperCase(Locale.ROOT),
        segments[4].trim().toUpperCase(Locale.ROOT));
  }

  private boolean hasAnyRole(User user, RoleName... roles) {
    for (RoleName role : roles) {
      boolean found = user.getRoles().stream().anyMatch(r -> r.getName() == role);
      if (found) {
        return true;
      }
    }
    return false;
  }

  private MovementType normalizeMovementType(MovementType type) {
    if (type == MovementType.INBOUND) {
      return MovementType.RECEIVE;
    }
    if (type == MovementType.OUTBOUND) {
      return MovementType.SHIP;
    }
    return type;
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
        movement.getTransactionId(),
        movement.getIdempotencyKey(),
        movement.getCorrelationId(),
        movement.getReferenceType(),
        movement.getReferenceId(),
        movement.getReasonCode(),
        movement.getSourceLocation(),
        movement.getDestinationLocation(),
        movement.getPerformedBy(),
        movement.getPerformedVia(),
        movement.getMetadataJson(),
        movement.getReferenceNote(),
        movement.getCreatedAt()
    );
  }
}
