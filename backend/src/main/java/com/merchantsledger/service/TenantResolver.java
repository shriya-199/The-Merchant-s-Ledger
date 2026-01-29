package com.merchantsledger.service;

import com.merchantsledger.entity.User;

public class TenantResolver {
  private TenantResolver() {}

  public static String resolveTenantKey(User user) {
    if (user == null) {
      return "default";
    }
    String company = user.getCompanyName();
    if (company == null || company.isBlank()) {
      return "default";
    }
    return company.trim().toLowerCase();
  }
}
