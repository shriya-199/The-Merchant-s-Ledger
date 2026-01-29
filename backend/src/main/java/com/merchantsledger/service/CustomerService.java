package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.CustomerRequest;
import com.merchantsledger.dto.CustomerResponse;
import com.merchantsledger.entity.Customer;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.CustomerRepository;

@Service
public class CustomerService {
  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public List<CustomerResponse> getAll() {
    return customerRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public CustomerResponse getById(Long id) {
    return toResponse(findEntity(id));
  }

  public CustomerResponse create(CustomerRequest request) {
    Customer customer = new Customer();
    customer.setName(request.getName());
    customer.setEmail(request.getEmail());
    customer.setPhone(request.getPhone());
    customer.setAddress(request.getAddress());
    return toResponse(customerRepository.save(customer));
  }

  public CustomerResponse update(Long id, CustomerRequest request) {
    Customer customer = findEntity(id);
    customer.setName(request.getName());
    customer.setEmail(request.getEmail());
    customer.setPhone(request.getPhone());
    customer.setAddress(request.getAddress());
    return toResponse(customerRepository.save(customer));
  }

  public void delete(Long id) {
    Customer customer = findEntity(id);
    customerRepository.delete(customer);
  }

  private Customer findEntity(Long id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found"));
  }

  private CustomerResponse toResponse(Customer customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getAddress(),
        customer.getBalance(),
        customer.getCreatedAt()
    );
  }
}
