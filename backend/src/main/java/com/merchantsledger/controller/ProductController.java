package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.ProductRequest;
import com.merchantsledger.dto.ProductResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public List<ProductResponse> list(@AuthenticationPrincipal User user) {
    return productService.list(user);
  }

  @GetMapping("/barcode/{barcode}")
  public ProductResponse byBarcode(@AuthenticationPrincipal User user, @PathVariable String barcode) {
    return productService.findByBarcode(user, barcode);
  }

  @PostMapping
  public ProductResponse create(@AuthenticationPrincipal User user, @Valid @RequestBody ProductRequest request) {
    return productService.create(user, request);
  }

  @PutMapping("/{id}")
  public ProductResponse update(@AuthenticationPrincipal User user, @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    return productService.update(user, id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
    productService.delete(user, id);
  }
}
