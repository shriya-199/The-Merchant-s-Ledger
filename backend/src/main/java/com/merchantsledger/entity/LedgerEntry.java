package com.merchantsledger.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LedgerType type;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  private String description;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public LedgerType getType() {
    return type;
  }

  public void setType(LedgerType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
