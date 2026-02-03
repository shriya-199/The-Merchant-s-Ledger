package com.merchantsledger.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.merchantsledger.dto.CycleCountLineRequest;
import com.merchantsledger.dto.CycleCountLineResponse;
import com.merchantsledger.dto.CycleCountSessionRequest;
import com.merchantsledger.dto.CycleCountSessionResponse;
import com.merchantsledger.dto.StockMovementRequest;
import com.merchantsledger.entity.CycleCountLine;
import com.merchantsledger.entity.CycleCountSession;
import com.merchantsledger.entity.CycleCountStatus;
import com.merchantsledger.entity.MovementType;
import com.merchantsledger.entity.Product;
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.StockItem;
import com.merchantsledger.entity.User;
import com.merchantsledger.entity.Warehouse;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.ForbiddenException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.CycleCountLineRepository;
import com.merchantsledger.repository.CycleCountSessionRepository;
import com.merchantsledger.repository.ProductRepository;
import com.merchantsledger.repository.StockItemRepository;
import com.merchantsledger.repository.WarehouseLocationRepository;
import com.merchantsledger.repository.WarehouseRepository;

@Service
public class ReconciliationService {
  private final CycleCountSessionRepository cycleCountSessionRepository;
  private final CycleCountLineRepository cycleCountLineRepository;
  private final WarehouseRepository warehouseRepository;
  private final ProductRepository productRepository;
  private final StockItemRepository stockItemRepository;
  private final WarehouseLocationRepository warehouseLocationRepository;
  private final InventoryService inventoryService;
  private final AuditService auditService;

  public ReconciliationService(CycleCountSessionRepository cycleCountSessionRepository,
                               CycleCountLineRepository cycleCountLineRepository,
                               WarehouseRepository warehouseRepository,
                               ProductRepository productRepository,
                               StockItemRepository stockItemRepository,
                               WarehouseLocationRepository warehouseLocationRepository,
                               InventoryService inventoryService,
                               AuditService auditService) {
    this.cycleCountSessionRepository = cycleCountSessionRepository;
    this.cycleCountLineRepository = cycleCountLineRepository;
    this.warehouseRepository = warehouseRepository;
    this.productRepository = productRepository;
    this.stockItemRepository = stockItemRepository;
    this.warehouseLocationRepository = warehouseLocationRepository;
    this.inventoryService = inventoryService;
    this.auditService = auditService;
  }

  public List<CycleCountSessionResponse> listSessions(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return cycleCountSessionRepository.findTop100ByTenantKeyOrderByCreatedAtDesc(tenantKey).stream()
        .map(this::toSessionResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public CycleCountSessionResponse createSession(User user, CycleCountSessionRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
        .orElseThrow(() -> new NotFoundException("Warehouse not found"));
    if (!warehouse.getTenantKey().equals(tenantKey)) {
      throw new NotFoundException("Warehouse not found");
    }

    CycleCountSession session = new CycleCountSession();
    session.setWarehouse(warehouse);
    session.setTenantKey(tenantKey);
    session.setName(request.getName().trim());
    session.setCreatedBy(displayName(user));
    session.setStatus(CycleCountStatus.OPEN);

    CycleCountSession saved = cycleCountSessionRepository.save(session);
    auditService.log(user, "RECON_SESSION_CREATE", "CycleCountSession", String.valueOf(saved.getId()), saved.getName());
    return toSessionResponse(saved);
  }

  @Transactional
  public CycleCountSessionResponse addLine(User user, Long sessionId, CycleCountLineRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    CycleCountSession session = cycleCountSessionRepository.findByIdAndTenantKey(sessionId, tenantKey)
        .orElseThrow(() -> new NotFoundException("Cycle count session not found"));
    if (session.getStatus() != CycleCountStatus.OPEN) {
      throw new BadRequestException("Only OPEN sessions can accept cycle count lines");
    }

    Product product = productRepository.findByIdAndTenantKey(request.getProductId(), tenantKey)
        .orElseThrow(() -> new NotFoundException("Product not found"));

    String sourceLocation = normalizeLocationCode(request.getSourceLocation());
    if (sourceLocation != null) {
      boolean exists = warehouseLocationRepository.findByWarehouseIdAndTenantKeyAndLocationCode(
          session.getWarehouse().getId(),
          tenantKey,
          sourceLocation
      ).isPresent();
      if (!exists) {
        throw new BadRequestException("Unknown source location in selected warehouse");
      }
    }

    StockItem stockItem = stockItemRepository.findByWarehouseIdAndProductId(session.getWarehouse().getId(), product.getId())
        .orElse(null);
    long expectedQty = stockItem == null ? 0 : stockItem.getQuantity();
    long countedQty = request.getCountedQty();

    CycleCountLine line = new CycleCountLine();
    line.setSession(session);
    line.setProduct(product);
    line.setExpectedQty(expectedQty);
    line.setCountedQty(countedQty);
    line.setVariance(countedQty - expectedQty);
    line.setSourceLocation(sourceLocation);
    line.setReasonCode(normalize(request.getReasonCode()));
    cycleCountLineRepository.save(line);

    auditService.log(user, "RECON_LINE_ADD", "CycleCountSession", String.valueOf(session.getId()), product.getSku());
    return toSessionResponse(session);
  }

  @Transactional
  public CycleCountSessionResponse submit(User user, Long sessionId) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    CycleCountSession session = cycleCountSessionRepository.findByIdAndTenantKey(sessionId, tenantKey)
        .orElseThrow(() -> new NotFoundException("Cycle count session not found"));
    if (session.getStatus() != CycleCountStatus.OPEN) {
      throw new BadRequestException("Only OPEN sessions can be submitted");
    }
    if (cycleCountLineRepository.findBySessionIdOrderByCreatedAtAsc(session.getId()).isEmpty()) {
      throw new BadRequestException("Add at least one line before submission");
    }
    session.setStatus(CycleCountStatus.SUBMITTED);
    session.setSubmittedBy(displayName(user));
    session.setSubmittedAt(Instant.now());
    cycleCountSessionRepository.save(session);
    auditService.log(user, "RECON_SUBMIT", "CycleCountSession", String.valueOf(session.getId()), session.getName());
    return toSessionResponse(session);
  }

  @Transactional
  public CycleCountSessionResponse approve(User user, Long sessionId) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    CycleCountSession session = cycleCountSessionRepository.findByIdAndTenantKey(sessionId, tenantKey)
        .orElseThrow(() -> new NotFoundException("Cycle count session not found"));
    if (session.getStatus() != CycleCountStatus.SUBMITTED) {
      throw new BadRequestException("Only SUBMITTED sessions can be approved");
    }
    if (displayName(user).equals(session.getCreatedBy())) {
      throw new ForbiddenException("Two-person rule: creator cannot approve the same cycle count");
    }
    if (!hasAnyRole(user,
        RoleName.SYSTEM_ADMIN,
        RoleName.MERCHANT_ADMIN,
        RoleName.WAREHOUSE_MANAGER,
        RoleName.INVENTORY_AUDITOR,
        RoleName.ADMIN,
        RoleName.MANAGER)) {
      throw new ForbiddenException("Approval requires manager or auditor level role");
    }

    List<CycleCountLine> lines = cycleCountLineRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
    for (CycleCountLine line : lines) {
      if (line.getVariance() == 0) {
        continue;
      }
      StockMovementRequest adjustRequest = new StockMovementRequest();
      adjustRequest.setType(MovementType.ADJUST);
      adjustRequest.setProductId(line.getProduct().getId());
      adjustRequest.setFromWarehouseId(session.getWarehouse().getId());
      adjustRequest.setQuantity(Math.abs(line.getVariance()));
      adjustRequest.setAdjustmentDirection(line.getVariance() > 0 ? "INCREASE" : "DECREASE");
      adjustRequest.setReasonCode(line.getReasonCode() == null ? "CYCLE_COUNT" : line.getReasonCode());
      adjustRequest.setReferenceType("CYCLE_COUNT");
      adjustRequest.setReferenceId(String.valueOf(session.getId()));
      adjustRequest.setSourceLocation(line.getSourceLocation());
      adjustRequest.setTransactionId(UUID.randomUUID().toString());
      adjustRequest.setCorrelationId("CYCLE-" + session.getId());
      adjustRequest.setIdempotencyKey("CYCLE-" + session.getId() + "-LINE-" + line.getId());
      adjustRequest.setPerformedVia("RECONCILIATION");
      adjustRequest.setReferenceNote("Cycle count approval: " + session.getName());
      inventoryService.recordMovement(user, adjustRequest);
    }

    session.setStatus(CycleCountStatus.APPROVED);
    session.setApprovedBy(displayName(user));
    session.setApprovedAt(Instant.now());
    cycleCountSessionRepository.save(session);
    auditService.log(user, "RECON_APPROVE", "CycleCountSession", String.valueOf(session.getId()), session.getName());
    return toSessionResponse(session);
  }

  private CycleCountSessionResponse toSessionResponse(CycleCountSession session) {
    List<CycleCountLineResponse> lines = cycleCountLineRepository.findBySessionIdOrderByCreatedAtAsc(session.getId())
        .stream()
        .map(this::toLineResponse)
        .collect(Collectors.toList());

    return new CycleCountSessionResponse(
        session.getId(),
        session.getName(),
        session.getWarehouse().getId(),
        session.getWarehouse().getName(),
        session.getStatus(),
        session.getCreatedBy(),
        session.getSubmittedBy(),
        session.getApprovedBy(),
        session.getCreatedAt(),
        session.getSubmittedAt(),
        session.getApprovedAt(),
        lines
    );
  }

  private CycleCountLineResponse toLineResponse(CycleCountLine line) {
    return new CycleCountLineResponse(
        line.getId(),
        line.getProduct().getId(),
        line.getProduct().getName(),
        line.getProduct().getSku(),
        line.getExpectedQty(),
        line.getCountedQty(),
        line.getVariance(),
        line.getSourceLocation(),
        line.getReasonCode(),
        line.getCreatedAt()
    );
  }

  private String displayName(User user) {
    return user.getFullName() + " <" + user.getEmail() + ">";
  }

  private boolean hasAnyRole(User user, RoleName... roles) {
    for (RoleName role : roles) {
      boolean hasRole = user.getRoles().stream().anyMatch(r -> r.getName() == role);
      if (hasRole) {
        return true;
      }
    }
    return false;
  }

  private String normalizeLocationCode(String locationCode) {
    if (locationCode == null || locationCode.isBlank()) {
      return null;
    }
    String[] segments = locationCode.split("/");
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

  private String normalize(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
