package com.merchantsledger.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.WarehouseRequest;
import com.merchantsledger.dto.WarehouseLocationRequest;
import com.merchantsledger.dto.WarehouseLocationResponse;
import com.merchantsledger.dto.WarehouseResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.entity.Warehouse;
import com.merchantsledger.entity.WarehouseLocation;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.WarehouseRepository;
import com.merchantsledger.repository.WarehouseLocationRepository;

@Service
public class WarehouseService {
  private final WarehouseRepository warehouseRepository;
  private final WarehouseLocationRepository warehouseLocationRepository;
  private final AuditService auditService;

  public WarehouseService(WarehouseRepository warehouseRepository,
                          WarehouseLocationRepository warehouseLocationRepository,
                          AuditService auditService) {
    this.warehouseRepository = warehouseRepository;
    this.warehouseLocationRepository = warehouseLocationRepository;
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

  public List<WarehouseLocationResponse> listLocations(User user, Long warehouseId) {
    Warehouse warehouse = findTenantWarehouse(user, warehouseId);
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return warehouseLocationRepository.findByWarehouseIdAndTenantKeyOrderByZoneAscAisleAscRackAscShelfAscBinAsc(
            warehouse.getId(),
            tenantKey
        )
        .stream()
        .map(this::toLocationResponse)
        .collect(Collectors.toList());
  }

  public WarehouseLocationResponse createLocation(User user, Long warehouseId, WarehouseLocationRequest request) {
    Warehouse warehouse = findTenantWarehouse(user, warehouseId);
    String tenantKey = TenantResolver.resolveTenantKey(user);
    String locationCode = buildLocationCode(request.getZone(), request.getAisle(), request.getRack(), request.getShelf(), request.getBin());
    if (warehouseLocationRepository.findByWarehouseIdAndTenantKeyAndLocationCode(warehouse.getId(), tenantKey, locationCode).isPresent()) {
      throw new BadRequestException("Location already exists in this warehouse");
    }

    WarehouseLocation location = new WarehouseLocation();
    location.setWarehouse(warehouse);
    location.setZone(normalizeSegment(request.getZone()));
    location.setAisle(normalizeSegment(request.getAisle()));
    location.setRack(normalizeSegment(request.getRack()));
    location.setShelf(normalizeSegment(request.getShelf()));
    location.setBin(normalizeSegment(request.getBin()));
    location.setLocationCode(locationCode);
    location.setTenantKey(tenantKey);

    WarehouseLocation saved = warehouseLocationRepository.save(location);
    auditService.log(user, "WAREHOUSE_LOCATION_CREATE", "WarehouseLocation", String.valueOf(saved.getId()), saved.getLocationCode());
    return toLocationResponse(saved);
  }

  public WarehouseLocation resolveWarehouseLocation(User user, Warehouse warehouse, String locationCode) {
    if (locationCode == null || locationCode.isBlank() || warehouse == null) {
      return null;
    }
    String tenantKey = TenantResolver.resolveTenantKey(user);
    String normalized = normalizeLocationCode(locationCode);
    return warehouseLocationRepository.findByWarehouseIdAndTenantKeyAndLocationCode(warehouse.getId(), tenantKey, normalized)
        .orElse(null);
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

  private WarehouseLocationResponse toLocationResponse(WarehouseLocation location) {
    return new WarehouseLocationResponse(
        location.getId(),
        location.getWarehouse().getId(),
        location.getWarehouse().getName(),
        location.getZone(),
        location.getAisle(),
        location.getRack(),
        location.getShelf(),
        location.getBin(),
        location.getLocationCode(),
        location.getCreatedAt()
    );
  }

  private Warehouse findTenantWarehouse(User user, Long warehouseId) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Warehouse warehouse = warehouseRepository.findById(warehouseId)
        .orElseThrow(() -> new NotFoundException("Warehouse not found"));
    if (!warehouse.getTenantKey().equals(tenantKey)) {
      throw new NotFoundException("Warehouse not found");
    }
    return warehouse;
  }

  private String buildLocationCode(String zone, String aisle, String rack, String shelf, String bin) {
    return String.join("/",
        normalizeSegment(zone),
        normalizeSegment(aisle),
        normalizeSegment(rack),
        normalizeSegment(shelf),
        normalizeSegment(bin)
    );
  }

  private String normalizeLocationCode(String locationCode) {
    String[] segments = locationCode.split("/");
    if (segments.length != 5) {
      throw new BadRequestException("Location must follow Zone/Aisle/Rack/Shelf/Bin");
    }
    return String.join("/",
        normalizeSegment(segments[0]),
        normalizeSegment(segments[1]),
        normalizeSegment(segments[2]),
        normalizeSegment(segments[3]),
        normalizeSegment(segments[4]));
  }

  private String normalizeSegment(String segment) {
    if (segment == null || segment.isBlank()) {
      throw new BadRequestException("Location hierarchy segment cannot be blank");
    }
    return segment.trim().toUpperCase(Locale.ROOT);
  }
}
