package com.bank.repository;

import com.bank.model.BankTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findTop5ByAccountAccountNumberOrderByTransactionTimeDesc(String accountNumber);
}
