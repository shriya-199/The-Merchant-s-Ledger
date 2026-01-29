package com.merchantsledger.dto;

import java.util.List;

public class AnalyticsResponse {
  private long totalMovements;
  private long inboundCount;
  private long outboundCount;
  private long transferCount;
  private long lowStockCount;
  private List<DailyCount> dailyMovements;

  public AnalyticsResponse(long totalMovements, long inboundCount, long outboundCount,
                           long transferCount, long lowStockCount, List<DailyCount> dailyMovements) {
    this.totalMovements = totalMovements;
    this.inboundCount = inboundCount;
    this.outboundCount = outboundCount;
    this.transferCount = transferCount;
    this.lowStockCount = lowStockCount;
    this.dailyMovements = dailyMovements;
  }

  public long getTotalMovements() {
    return totalMovements;
  }

  public long getInboundCount() {
    return inboundCount;
  }

  public long getOutboundCount() {
    return outboundCount;
  }

  public long getTransferCount() {
    return transferCount;
  }

  public long getLowStockCount() {
    return lowStockCount;
  }

  public List<DailyCount> getDailyMovements() {
    return dailyMovements;
  }

  public static class DailyCount {
    private String day;
    private long count;

    public DailyCount(String day, long count) {
      this.day = day;
      this.count = count;
    }

    public String getDay() {
      return day;
    }

    public long getCount() {
      return count;
    }
  }
}
