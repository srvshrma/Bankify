package com.bank.service;

import com.bank.dto.CustomerRequest;
import com.bank.dto.CustomerResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;
import com.bank.util.MapperUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(request.getEmail());
        existingCustomer.ifPresent(customer -> {
            throw new IllegalArgumentException("Customer email already exists");
        });

        Customer customer = new Customer(request.getFullName(), request.getEmail(), LocalDate.now());
        return MapperUtil.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(MapperUtil::toCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return MapperUtil.toCustomerResponse(findCustomerEntity(customerId));
    }

    public Customer findCustomerEntity(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for id " + customerId));
    }
}
