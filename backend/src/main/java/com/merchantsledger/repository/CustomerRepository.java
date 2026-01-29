package com.merchantsledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
