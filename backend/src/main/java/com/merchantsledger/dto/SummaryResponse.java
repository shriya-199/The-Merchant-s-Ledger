package com.merchantsledger.dto;

import java.math.BigDecimal;

public class SummaryResponse {
  private long customerCount;
  private long transactionCount;
  private BigDecimal totalBalance;

  public SummaryResponse(long customerCount, long transactionCount, BigDecimal totalBalance) {
    this.customerCount = customerCount;
    this.transactionCount = transactionCount;
    this.totalBalance = totalBalance;
  }

  public long getCustomerCount() {
    return customerCount;
  }

  public long getTransactionCount() {
    return transactionCount;
  }

  public BigDecimal getTotalBalance() {
    return totalBalance;
  }
}
