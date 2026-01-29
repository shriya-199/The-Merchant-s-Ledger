package com.merchantsledger.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.NotificationResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  public List<NotificationResponse> list(@AuthenticationPrincipal User user) {
    return notificationService.list(user);
  }

  @PostMapping("/{id}/read")
  public void markRead(@AuthenticationPrincipal User user, @PathVariable Long id) {
    notificationService.markRead(user, id);
  }
}
