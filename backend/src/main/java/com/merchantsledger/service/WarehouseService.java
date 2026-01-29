package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.WarehouseRequest;
import com.merchantsledger.dto.WarehouseResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.entity.Warehouse;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.WarehouseRepository;

@Service
public class WarehouseService {
  private final WarehouseRepository warehouseRepository;
  private final AuditService auditService;

  public WarehouseService(WarehouseRepository warehouseRepository, AuditService auditService) {
    this.warehouseRepository = warehouseRepository;
    this.auditService = auditService;
  }

  public List<WarehouseResponse> list(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return warehouseRepository.findByTenantKey(tenantKey).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public WarehouseResponse create(User user, WarehouseRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Warehouse warehouse = new Warehouse();
    warehouse.setName(request.getName());
    warehouse.setCode(request.getCode());
    warehouse.setLocation(request.getLocation());
    warehouse.setTenantKey(tenantKey);
    Warehouse saved = warehouseRepository.save(warehouse);
    auditService.log(user, "WAREHOUSE_CREATE", "Warehouse", String.valueOf(saved.getId()), saved.getName());
    return toResponse(saved);
  }

  public WarehouseResponse update(User user, Long id, WarehouseRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Warehouse warehouse = warehouseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Warehouse not found"));
    if (!warehouse.getTenantKey().equals(tenantKey)) {
      throw new NotFoundException("Warehouse not found");
    }
    warehouse.setName(request.getName());
    warehouse.setCode(request.getCode());
    warehouse.setLocation(request.getLocation());
    Warehouse saved = warehouseRepository.save(warehouse);
    auditService.log(user, "WAREHOUSE_UPDATE", "Warehouse", String.valueOf(saved.getId()), saved.getName());
    return toResponse(saved);
  }

  public void delete(User user, Long id) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Warehouse warehouse = warehouseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Warehouse not found"));
    if (!warehouse.getTenantKey().equals(tenantKey)) {
      throw new NotFoundException("Warehouse not found");
    }
    warehouseRepository.delete(warehouse);
    auditService.log(user, "WAREHOUSE_DELETE", "Warehouse", String.valueOf(warehouse.getId()), warehouse.getName());
  }

  private WarehouseResponse toResponse(Warehouse warehouse) {
    return new WarehouseResponse(
        warehouse.getId(),
        warehouse.getName(),
        warehouse.getCode(),
        warehouse.getLocation(),
        warehouse.getCreatedAt()
    );
  }
}
