package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.dto.CustomerRequest;
import com.bank.dto.CustomerResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomerShouldPersistAndReturnMappedResponse() {
        CustomerRequest request = new CustomerRequest();
        request.setFullName("Riya Sen");
        request.setEmail("riya@bank.com");

        when(customerRepository.findByEmail("riya@bank.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        CustomerResponse response = customerService.createCustomer(request);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertEquals("Riya Sen", response.getFullName());
        assertEquals("riya@bank.com", response.getEmail());
        assertEquals(10L, response.getId());
        assertEquals(LocalDate.now(), savedCustomer.getJoinedOn());
        assertTrue(response.getAccounts().isEmpty());
    }

    @Test
    void createCustomerShouldRejectDuplicateEmail() {
        CustomerRequest request = new CustomerRequest();
        request.setFullName("Riya Sen");
        request.setEmail("riya@bank.com");

        when(customerRepository.findByEmail("riya@bank.com"))
                .thenReturn(Optional.of(new Customer("Existing", "riya@bank.com", LocalDate.now())));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(request));

        assertEquals("Customer email already exists", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void findCustomerEntityShouldThrowWhenMissing() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerService.findCustomerEntity(99L));

        assertEquals("Customer not found for id 99", exception.getMessage());
    }

    @Test
    void getAllCustomersShouldMapRepositoryResults() {
        Customer customer = new Customer("Aarav Mehta", "aarav@bank.com", LocalDate.of(2024, 1, 15));
        customer.setId(1L);
        customer.setAccounts(Collections.emptyList());
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        CustomerResponse response = customerService.getAllCustomers().get(0);

        assertEquals(1L, response.getId());
        assertEquals("Aarav Mehta", response.getFullName());
        assertTrue(response.getAccounts().isEmpty());
    }
}
