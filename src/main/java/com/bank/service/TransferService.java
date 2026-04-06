package com.bank.service;

import com.bank.dto.TransferRequest;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.TransactionType;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransferService {

    private final AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    public String transfer(TransferRequest request) {
        if (request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }

        Account source = accountService.findAccountEntity(request.getSourceAccountNumber());
        Account target = accountService.findAccountEntity(request.getTargetAccountNumber());
        BigDecimal amount = request.getAmount();

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in source account");
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        accountService.createTransaction(source, TransactionType.TRANSFER_OUT, amount, request.getDescription());
        accountService.createTransaction(target, TransactionType.TRANSFER_IN, amount, request.getDescription());
        publishAuditEvent(source.getAccountNumber(), target.getAccountNumber(), amount);

        return "Transfer completed successfully";
    }

    @Async("bankTaskExecutor")
    public CompletableFuture<String> publishAuditEvent(String sourceAccount, String targetAccount, BigDecimal amount) {
        return CompletableFuture.completedFuture(
                "AUDIT::" + sourceAccount + "->" + targetAccount + "::" + amount.toPlainString()
        );
    }
}
