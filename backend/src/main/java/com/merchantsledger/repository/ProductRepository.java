package com.merchantsledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findBySku(String sku);
  Optional<Product> findByBarcode(String barcode);
  List<Product> findByTenantKey(String tenantKey);
  Optional<Product> findByIdAndTenantKey(Long id, String tenantKey);
  Optional<Product> findBySkuAndTenantKey(String sku, String tenantKey);
}
