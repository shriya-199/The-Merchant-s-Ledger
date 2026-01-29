package com.merchantsledger.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.SummaryResponse;
import com.merchantsledger.repository.CustomerRepository;
import com.merchantsledger.repository.LedgerEntryRepository;

@Service
public class SummaryService {
  private final CustomerRepository customerRepository;
  private final LedgerEntryRepository ledgerEntryRepository;

  public SummaryService(CustomerRepository customerRepository, LedgerEntryRepository ledgerEntryRepository) {
    this.customerRepository = customerRepository;
    this.ledgerEntryRepository = ledgerEntryRepository;
  }

  public SummaryResponse getSummary() {
    long customers = customerRepository.count();
    long transactions = ledgerEntryRepository.count();
    BigDecimal totalBalance = customerRepository.findAll().stream()
        .map(customer -> customer.getBalance())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new SummaryResponse(customers, transactions, totalBalance);
  }
}
