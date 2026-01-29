package com.merchantsledger.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.merchantsledger.dto.CustomerRequest;
import com.merchantsledger.dto.CustomerResponse;
import com.merchantsledger.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping
  public List<CustomerResponse> list() {
    return customerService.getAll();
  }

  @GetMapping("/{id}")
  public CustomerResponse get(@PathVariable Long id) {
    return customerService.getById(id);
  }

  @PostMapping
  public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
    return customerService.create(request);
  }

  @PutMapping("/{id}")
  public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
    return customerService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    customerService.delete(id);
  }
}
