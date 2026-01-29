package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.ProductRequest;
import com.merchantsledger.dto.ProductResponse;
import com.merchantsledger.entity.Product;
import com.merchantsledger.entity.User;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final AuditService auditService;

  public ProductService(ProductRepository productRepository, AuditService auditService) {
    this.productRepository = productRepository;
    this.auditService = auditService;
  }

  public List<ProductResponse> list(User user) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    return productRepository.findByTenantKey(tenantKey).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public ProductResponse findByBarcode(User user, String barcode) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Product product = productRepository.findByBarcode(barcode)
        .filter(found -> tenantKey.equals(found.getTenantKey()))
        .orElseThrow(() -> new NotFoundException("Product not found"));
    return toResponse(product);
  }

  public ProductResponse create(User user, ProductRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    productRepository.findBySkuAndTenantKey(request.getSku(), tenantKey).ifPresent(existing -> {
      throw new BadRequestException("SKU already exists");
    });

    Product product = new Product();
    product.setName(request.getName());
    product.setSku(request.getSku());
    product.setBarcode(request.getBarcode());
    product.setCategory(request.getCategory());
    product.setUnit(request.getUnit());
    product.setReorderLevel(request.getReorderLevel());
    product.setTenantKey(tenantKey);
    Product saved = productRepository.save(product);
    auditService.log(user, "PRODUCT_CREATE", "Product", String.valueOf(saved.getId()), saved.getSku());
    return toResponse(saved);
  }

  public ProductResponse update(User user, Long id, ProductRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Product product = productRepository.findByIdAndTenantKey(id, tenantKey)
        .orElseThrow(() -> new NotFoundException("Product not found"));
    product.setName(request.getName());
    product.setSku(request.getSku());
    product.setBarcode(request.getBarcode());
    product.setCategory(request.getCategory());
    product.setUnit(request.getUnit());
    product.setReorderLevel(request.getReorderLevel());
    Product saved = productRepository.save(product);
    auditService.log(user, "PRODUCT_UPDATE", "Product", String.valueOf(saved.getId()), saved.getSku());
    return toResponse(saved);
  }

  public void delete(User user, Long id) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    Product product = productRepository.findByIdAndTenantKey(id, tenantKey)
        .orElseThrow(() -> new NotFoundException("Product not found"));
    productRepository.delete(product);
    auditService.log(user, "PRODUCT_DELETE", "Product", String.valueOf(product.getId()), product.getSku());
  }

  private ProductResponse toResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getSku(),
        product.getBarcode(),
        product.getCategory(),
        product.getUnit(),
        product.getReorderLevel(),
        product.getCreatedAt()
    );
  }
}
