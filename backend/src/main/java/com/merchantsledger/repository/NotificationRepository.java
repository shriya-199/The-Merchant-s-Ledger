package com.merchantsledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findTop50ByTenantKeyOrderByCreatedAtDesc(String tenantKey);
}
