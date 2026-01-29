package com.merchantsledger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchantsledger.dto.AnalyticsResponse;
import com.merchantsledger.entity.User;
import com.merchantsledger.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping
  public AnalyticsResponse analytics(@AuthenticationPrincipal User user) {
    return analyticsService.build(user);
  }
}
