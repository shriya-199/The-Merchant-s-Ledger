package com.merchantsledger.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.merchantsledger.dto.LedgerEntryRequest;
import com.merchantsledger.dto.LedgerEntryResponse;
import com.merchantsledger.entity.Customer;
import com.merchantsledger.entity.LedgerEntry;
import com.merchantsledger.entity.LedgerType;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.CustomerRepository;
import com.merchantsledger.repository.LedgerEntryRepository;

@Service
public class LedgerService {
  private final LedgerEntryRepository ledgerEntryRepository;
  private final CustomerRepository customerRepository;

  public LedgerService(LedgerEntryRepository ledgerEntryRepository, CustomerRepository customerRepository) {
    this.ledgerEntryRepository = ledgerEntryRepository;
    this.customerRepository = customerRepository;
  }

  public List<LedgerEntryResponse> listRecent(Long customerId) {
    List<LedgerEntry> entries = customerId == null
        ? ledgerEntryRepository.findTop20ByOrderByCreatedAtDesc()
        : ledgerEntryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

    return entries.stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public LedgerEntryResponse create(LedgerEntryRequest request) {
    Customer customer = customerRepository.findById(request.getCustomerId())
        .orElseThrow(() -> new NotFoundException("Customer not found"));

    LedgerEntry entry = new LedgerEntry();
    entry.setCustomer(customer);
    entry.setType(request.getType());
    entry.setAmount(request.getAmount());
    entry.setDescription(request.getDescription());

    LedgerEntry saved = ledgerEntryRepository.save(entry);

    BigDecimal balance = customer.getBalance();
    if (request.getType() == LedgerType.CREDIT) {
      balance = balance.add(request.getAmount());
    } else {
      balance = balance.subtract(request.getAmount());
    }
    customer.setBalance(balance);
    customerRepository.save(customer);

    return toResponse(saved);
  }

  private LedgerEntryResponse toResponse(LedgerEntry entry) {
    return new LedgerEntryResponse(
        entry.getId(),
        entry.getCustomer().getId(),
        entry.getCustomer().getName(),
        entry.getType(),
        entry.getAmount(),
        entry.getDescription(),
        entry.getCreatedAt()
    );
  }
}
