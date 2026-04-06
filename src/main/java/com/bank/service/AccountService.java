package com.bank.service;

import com.bank.dto.AccountRequest;
import com.bank.dto.AccountResponse;
import com.bank.dto.DashboardResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.BankTransaction;
import com.bank.model.Customer;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.util.AccountNumberGenerator;
import com.bank.util.MapperUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                          CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
    }

    public AccountResponse createAccount(AccountRequest request) {
        Customer customer = customerService.findCustomerEntity(request.getCustomerId());
        Account account = new Account(
                AccountNumberGenerator.generate(),
                request.getAccountType(),
                request.getOpeningBalance(),
                LocalDateTime.now(),
                customer
        );
        customer.getAccounts().add(account);

        Account savedAccount = accountRepository.save(account);
        createTransaction(savedAccount, TransactionType.DEPOSIT, request.getOpeningBalance(), "Opening balance");
        return MapperUtil.toAccountResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Account::getCreatedAt).reversed())
                .map(MapperUtil::toAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber) {
        return MapperUtil.toAccountResponse(findAccountEntity(accountNumber));
    }

    public Account findAccountEntity(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for number " + accountNumber));
    }

    @Transactional(readOnly = true)
    public DashboardResponse buildDashboard() {
        List<Account> accounts = accountRepository.findAll();
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> balanceByAccountType = accounts.stream()
                .collect(Collectors.groupingBy(account -> account.getAccountType().name(),
                        Collectors.mapping(Account::getBalance,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        List<String> alerts = accounts.stream()
                .filter(account -> account.getBalance().compareTo(new BigDecimal("500.00")) < 0)
                .map(account -> account.getAccountNumber() + " has low balance")
                .collect(Collectors.collectingAndThen(Collectors.toList(), ArrayList::new));

        return new DashboardResponse(
                customerService.getAllCustomers().size(),
                accounts.size(),
                totalBalance,
                balanceByAccountType,
                alerts
        );
    }

    public void createTransaction(Account account, TransactionType type, BigDecimal amount, String description) {
        BankTransaction transaction = new BankTransaction(type, amount, description, LocalDateTime.now(), account);
        account.getTransactions().add(transaction);
        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<BankTransaction> getRecentTransactions(String accountNumber) {
        return transactionRepository.findTop5ByAccountAccountNumberOrderByTransactionTimeDesc(accountNumber);
    }
}
