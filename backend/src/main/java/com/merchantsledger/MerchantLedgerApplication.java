package com.merchantsledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MerchantLedgerApplication {
  public static void main(String[] args) {
    SpringApplication.run(MerchantLedgerApplication.class, args);
  }
}
