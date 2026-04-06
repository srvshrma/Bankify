package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.dto.AccountRequest;
import com.bank.dto.CustomerResponse;
import com.bank.dto.DashboardResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.BankTransaction;
import com.bank.model.Customer;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountShouldPersistAccountAndOpeningTransaction() {
        Customer customer = new Customer("Aarav Mehta", "aarav@bank.com", LocalDate.of(2024, 1, 15));
        customer.setId(1L);

        AccountRequest request = new AccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);
        request.setOpeningBalance(new BigDecimal("1500.00"));

        when(customerService.findCustomerEntity(1L)).thenReturn(customer);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });
        when(transactionRepository.save(any(BankTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        com.bank.dto.AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertEquals(7L, response.getId());
        assertEquals(AccountType.SAVINGS, response.getAccountType());
        assertEquals(new BigDecimal("1500.00"), response.getBalance());
        assertEquals(customer, savedAccount.getCustomer());
        assertTrue(savedAccount.getAccountNumber().startsWith("BNK"));
        assertEquals(1, savedAccount.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, savedAccount.getTransactions().get(0).getTransactionType());
    }

    @Test
    void buildDashboardShouldAggregateBalancesAndAlerts() {
        Account lowBalanceAccount = createAccount("BNK-LOW", AccountType.CURRENT, new BigDecimal("300.00"),
                LocalDateTime.of(2024, 4, 1, 10, 0));
        Account savingsAccount = createAccount("BNK-SAVE", AccountType.SAVINGS, new BigDecimal("1200.00"),
                LocalDateTime.of(2024, 4, 2, 10, 0));
        Account fixedDepositAccount = createAccount("BNK-FD", AccountType.FIXED_DEPOSIT, new BigDecimal("4500.00"),
                LocalDateTime.of(2024, 4, 3, 10, 0));

        when(accountRepository.findAll()).thenReturn(Arrays.asList(lowBalanceAccount, savingsAccount, fixedDepositAccount));
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(
                new CustomerResponse(1L, "A", "a@bank.com", LocalDate.now(), Collections.emptyList()),
                new CustomerResponse(2L, "B", "b@bank.com", LocalDate.now(), Collections.emptyList())
        ));

        DashboardResponse response = accountService.buildDashboard();

        assertEquals(2L, response.getTotalCustomers());
        assertEquals(3L, response.getTotalAccounts());
        assertEquals(new BigDecimal("6000.00"), response.getTotalBalance());
        assertEquals(new BigDecimal("300.00"), response.getBalanceByAccountType().get("CURRENT"));
        assertEquals(new BigDecimal("1200.00"), response.getBalanceByAccountType().get("SAVINGS"));
        assertEquals(new BigDecimal("4500.00"), response.getBalanceByAccountType().get("FIXED_DEPOSIT"));
        assertEquals(1, response.getAlerts().size());
        assertTrue(response.getAlerts().get(0).contains("BNK-LOW"));
    }

    @Test
    void findAccountEntityShouldThrowWhenMissing() {
        when(accountRepository.findByAccountNumber("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.findAccountEntity("missing"));

        assertEquals("Account not found for number missing", exception.getMessage());
    }

    private Account createAccount(String accountNumber, AccountType accountType, BigDecimal balance,
                                  LocalDateTime createdAt) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setCreatedAt(createdAt);
        return account;
    }
}
