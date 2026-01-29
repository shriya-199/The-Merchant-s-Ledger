package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.AuditLogResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.AuditService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditService auditService;

  public AuditController(AuditService auditService) {
    this.auditService = auditService;
  }

  @GetMapping
  public List<AuditLogResponse> list(@AuthenticationPrincipal User user) {
    return auditService.list(user);
  }
}
