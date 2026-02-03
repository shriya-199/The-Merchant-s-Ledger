package com.merchantsledger.entity;

public enum LedgerType {
  SALE_CREDIT(true),
  MANUAL_CREDIT(true),
  COD_SETTLEMENT(true),
  CHARGEBACK_RELEASE(true),
  ADJUSTMENT_CREDIT(true),
  PAYOUT_DEBIT(false),
  REFUND_DEBIT(false),
  COMMISSION_FEE(false),
  SHIPPING_FEE(false),
  STORAGE_FEE(false),
  CHARGEBACK_HOLD(false),
  ADJUSTMENT_DEBIT(false),
  CREDIT(true),
  DEBIT(false);

  private final boolean credit;

  LedgerType(boolean credit) {
    this.credit = credit;
  }

  public boolean isCredit() {
    return credit;
  }
}
