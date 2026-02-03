package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.CycleCountLineRequest;
import com.merchantsledger.dto.CycleCountSessionRequest;
import com.merchantsledger.dto.CycleCountSessionResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.ReconciliationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {
  private final ReconciliationService reconciliationService;

  public ReconciliationController(ReconciliationService reconciliationService) {
    this.reconciliationService = reconciliationService;
  }

  @GetMapping("/sessions")
  public List<CycleCountSessionResponse> listSessions(@AuthenticationPrincipal User user) {
    return reconciliationService.listSessions(user);
  }

  @PostMapping("/sessions")
  public CycleCountSessionResponse createSession(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody CycleCountSessionRequest request) {
    return reconciliationService.createSession(user, request);
  }

  @PostMapping("/sessions/{id}/lines")
  public CycleCountSessionResponse addLine(@AuthenticationPrincipal User user,
                                           @PathVariable Long id,
                                           @Valid @RequestBody CycleCountLineRequest request) {
    return reconciliationService.addLine(user, id, request);
  }

  @PostMapping("/sessions/{id}/submit")
  public CycleCountSessionResponse submit(@AuthenticationPrincipal User user, @PathVariable Long id) {
    return reconciliationService.submit(user, id);
  }

  @PostMapping("/sessions/{id}/approve")
  public CycleCountSessionResponse approve(@AuthenticationPrincipal User user, @PathVariable Long id) {
    return reconciliationService.approve(user, id);
  }
}
