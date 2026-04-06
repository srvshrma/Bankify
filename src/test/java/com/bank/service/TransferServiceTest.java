package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.dto.TransferRequest;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.TransactionType;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountService accountService;

    @Test
    void transferShouldMoveBalanceAndCreateTransactions() {
        TransferService transferService = spy(new TransferService(accountService));
        Account source = createAccount("SRC-1", new BigDecimal("2000.00"));
        Account target = createAccount("TGT-1", new BigDecimal("300.00"));
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("SRC-1");
        request.setTargetAccountNumber("TGT-1");
        request.setAmount(new BigDecimal("250.00"));
        request.setDescription("Invoice settlement");

        when(accountService.findAccountEntity("SRC-1")).thenReturn(source);
        when(accountService.findAccountEntity("TGT-1")).thenReturn(target);
        doReturn(CompletableFuture.completedFuture("audit"))
                .when(transferService).publishAuditEvent("SRC-1", "TGT-1", new BigDecimal("250.00"));

        String response = transferService.transfer(request);

        assertEquals("Transfer completed successfully", response);
        assertEquals(new BigDecimal("1750.00"), source.getBalance());
        assertEquals(new BigDecimal("550.00"), target.getBalance());
        verify(accountService).createTransaction(source, TransactionType.TRANSFER_OUT,
                new BigDecimal("250.00"), "Invoice settlement");
        verify(accountService).createTransaction(target, TransactionType.TRANSFER_IN,
                new BigDecimal("250.00"), "Invoice settlement");
    }

    @Test
    void transferShouldRejectSameSourceAndTargetAccount() {
        TransferService transferService = new TransferService(accountService);
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("BNK-1");
        request.setTargetAccountNumber("BNK-1");
        request.setAmount(new BigDecimal("10.00"));
        request.setDescription("Invalid transfer");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(request));

        assertEquals("Source and target accounts must be different", exception.getMessage());
    }

    @Test
    void transferShouldRejectWhenBalanceIsInsufficient() {
        TransferService transferService = new TransferService(accountService);
        Account source = createAccount("SRC-1", new BigDecimal("50.00"));
        Account target = createAccount("TGT-1", new BigDecimal("300.00"));
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("SRC-1");
        request.setTargetAccountNumber("TGT-1");
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Transfer");

        when(accountService.findAccountEntity("SRC-1")).thenReturn(source);
        when(accountService.findAccountEntity("TGT-1")).thenReturn(target);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(request));

        assertEquals("Insufficient balance in source account", exception.getMessage());
        verify(accountService, never()).createTransaction(source, TransactionType.TRANSFER_OUT,
                new BigDecimal("100.00"), "Transfer");
    }

    private Account createAccount(String accountNumber, BigDecimal balance) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        return account;
    }
}
