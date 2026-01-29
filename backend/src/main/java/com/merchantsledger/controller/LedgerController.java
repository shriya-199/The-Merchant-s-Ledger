package com.merchantsledger.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.LedgerEntryRequest;
import com.merchantsledger.dto.LedgerEntryResponse;
import com.merchantsledger.service.LedgerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {
  private final LedgerService ledgerService;

  public LedgerController(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

  @GetMapping
  public List<LedgerEntryResponse> list(@RequestParam(required = false) Long customerId) {
    return ledgerService.listRecent(customerId);
  }

  @PostMapping
  public LedgerEntryResponse create(@Valid @RequestBody LedgerEntryRequest request) {
    return ledgerService.create(request);
  }
}
