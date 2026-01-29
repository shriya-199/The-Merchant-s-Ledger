package com.merchantsledger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merchantsledger.dto.SummaryResponse;
import com.merchantsledger.service.SummaryService;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
  private final SummaryService summaryService;

  public SummaryController(SummaryService summaryService) {
    this.summaryService = summaryService;
  }

  @GetMapping
  public SummaryResponse summary() {
    return summaryService.getSummary();
  }
}
