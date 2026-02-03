package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.WarehouseRequest;
import com.merchantsledger.dto.WarehouseLocationRequest;
import com.merchantsledger.dto.WarehouseLocationResponse;
import com.merchantsledger.dto.WarehouseResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.WarehouseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
  private final WarehouseService warehouseService;

  public WarehouseController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  @GetMapping
  public List<WarehouseResponse> list(@AuthenticationPrincipal User user) {
    return warehouseService.list(user);
  }

  @PostMapping
  public WarehouseResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody WarehouseRequest request) {
    return warehouseService.create(user, request);
  }

  @PutMapping("/{id}")
  public WarehouseResponse update(@AuthenticationPrincipal User user, @PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
    return warehouseService.update(user, id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
    warehouseService.delete(user, id);
  }

  @GetMapping("/{id}/locations")
  public List<WarehouseLocationResponse> listLocations(@AuthenticationPrincipal User user, @PathVariable Long id) {
    return warehouseService.listLocations(user, id);
  }

  @PostMapping("/{id}/locations")
  public WarehouseLocationResponse createLocation(@AuthenticationPrincipal User user,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody WarehouseLocationRequest request) {
    return warehouseService.createLocation(user, id, request);
  }
}
