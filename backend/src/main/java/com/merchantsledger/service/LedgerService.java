package com.merchantsledger.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.merchantsledger.dto.LedgerEntryRequest;
import com.merchantsledger.dto.LedgerEntryResponse;
import com.merchantsledger.entity.Customer;
import com.merchantsledger.entity.LedgerEntry;
import com.merchantsledger.entity.User;
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

  public List<LedgerEntryResponse> listRecent(User user, Long customerId) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    List<LedgerEntry> entries;
    if (customerId == null) {
      entries = ledgerEntryRepository.findTop50ByTenantKeyOrderByCreatedAtDesc(tenantKey);
      if (entries.isEmpty()) {
        entries = ledgerEntryRepository.findTop20ByOrderByCreatedAtDesc();
      }
    } else {
      entries = ledgerEntryRepository.findByTenantKeyAndCustomerIdOrderByCreatedAtDesc(tenantKey, customerId);
      if (entries.isEmpty()) {
        entries = ledgerEntryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
      }
    }

    return entries.stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public LedgerEntryResponse create(User user, LedgerEntryRequest request) {
    String tenantKey = TenantResolver.resolveTenantKey(user);
    if (!isBlank(request.getIdempotencyKey())) {
      LedgerEntry existing = ledgerEntryRepository.findByTenantKeyAndIdempotencyKey(tenantKey, request.getIdempotencyKey())
          .orElse(null);
      if (existing != null) {
        return toResponse(existing);
      }
    }

    Customer customer = customerRepository.findById(request.getCustomerId())
        .orElseThrow(() -> new NotFoundException("Customer not found"));

    LedgerEntry entry = new LedgerEntry();
    entry.setCustomer(customer);
    entry.setType(request.getType());
    entry.setAmount(request.getAmount());
    entry.setTransactionId(isBlank(request.getTransactionId()) ? UUID.randomUUID().toString() : request.getTransactionId().trim());
    entry.setIdempotencyKey(normalize(request.getIdempotencyKey()));
    entry.setCorrelationId(isBlank(request.getCorrelationId()) ? entry.getTransactionId() : request.getCorrelationId().trim());
    entry.setReferenceType(normalize(request.getReferenceType()));
    entry.setReferenceId(normalize(request.getReferenceId()));
    entry.setRelatedMovementId(request.getRelatedMovementId());
    entry.setTenantKey(tenantKey);
    entry.setDescription(request.getDescription());

    LedgerEntry saved = ledgerEntryRepository.save(entry);

    BigDecimal balance = customer.getBalance();
    if (request.getType().isCredit()) {
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
        entry.getTransactionId(),
        entry.getIdempotencyKey(),
        entry.getCorrelationId(),
        entry.getReferenceType(),
        entry.getReferenceId(),
        entry.getRelatedMovementId(),
        entry.getDescription(),
        entry.getCreatedAt()
    );
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private String normalize(String value) {
    if (isBlank(value)) {
      return null;
    }
    return value.trim();
  }
}
