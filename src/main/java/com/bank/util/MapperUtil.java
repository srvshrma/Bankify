package com.bank.util;

import com.bank.dto.AccountResponse;
import com.bank.dto.CustomerResponse;
import com.bank.dto.TransactionResponse;
import com.bank.model.Account;
import com.bank.model.BankTransaction;
import com.bank.model.Customer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class MapperUtil {

    private MapperUtil() {
    }

    public static CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getJoinedOn(),
                customer.getAccounts()
                        .stream()
                        .sorted(Comparator.comparing(Account::getCreatedAt))
                        .map(MapperUtil::toAccountResponse)
                        .collect(Collectors.toList())
        );
    }

    public static AccountResponse toAccountResponse(Account account) {
        List<TransactionResponse> recentTransactions = account.getTransactions()
                .stream()
                .sorted(Comparator.comparing(BankTransaction::getTransactionTime).reversed())
                .limit(5)
                .map(MapperUtil::toTransactionResponse)
                .collect(Collectors.toList());

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt(),
                recentTransactions
        );
    }

    public static TransactionResponse toTransactionResponse(BankTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionTime()
        );
    }
}
