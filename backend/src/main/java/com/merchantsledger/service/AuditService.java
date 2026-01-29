package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.AuditLogResponse;
import com.merchantsledger.entity.AuditLog;
import com.merchantsledger.entity.User;
import com.merchantsledger.repository.AuditLogRepository;

@Service
public class AuditService {
  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void log(User actor, String action, String entityType, String entityId, String metadata) {
    AuditLog log = new AuditLog();
    log.setAction(action);
    log.setActorEmail(actor != null ? actor.getEmail() : "system");
    log.setActorUserId(actor != null ? actor.getId() : null);
    log.setEntityType(entityType);
    log.setEntityId(entityId);
    log.setMetadata(metadata);
    log.setTenantKey(TenantResolver.resolveTenantKey(actor));
    auditLogRepository.save(log);
  }

  public List<AuditLogResponse> list(User actor) {
    String tenantKey = TenantResolver.resolveTenantKey(actor);
    return auditLogRepository.findTop50ByTenantKeyOrderByCreatedAtDesc(tenantKey).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private AuditLogResponse toResponse(AuditLog log) {
    return new AuditLogResponse(
        log.getId(),
        log.getAction(),
        log.getActorEmail(),
        log.getEntityType(),
        log.getEntityId(),
        log.getMetadata(),
        log.getCreatedAt()
    );
  }
}
