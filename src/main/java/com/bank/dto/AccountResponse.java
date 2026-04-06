package com.bank.dto;

import com.bank.model.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private List<TransactionResponse> recentTransactions;

    public AccountResponse(Long id, String accountNumber, AccountType accountType, BigDecimal balance,
                           LocalDateTime createdAt, List<TransactionResponse> recentTransactions) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
        this.recentTransactions = Collections.unmodifiableList(new ArrayList<>(recentTransactions));
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }
}
