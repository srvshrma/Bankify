package com.bank.service;

import com.bank.dto.Java8FeatureResponse;
import com.bank.model.Account;
import com.bank.util.InterestCalculator;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Java8ShowcaseService {

    private final AccountService accountService;

    public Java8ShowcaseService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Java8FeatureResponse generateShowcase() {
        List<Account> accounts = accountService.getAllAccounts()
                .stream()
                .map(response -> accountService.findAccountEntity(response.getAccountNumber()))
                .collect(Collectors.toList());

        List<String> lambdaExamples = Arrays.asList(
                "Lambda sorted accounts: " + accounts.stream()
                        .map(Account::getAccountNumber)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.joining(", ")),
                "Method reference customer count: " + accounts.stream()
                        .map(Account::getCustomer)
                        .map(customer -> customer.getEmail().toLowerCase(Locale.ROOT))
                        .distinct()
                        .count()
        );

        Map<String, String> optionalResults = new LinkedHashMap<>();
        optionalResults.put("firstRichAccount",
                accounts.stream()
                        .filter(account -> account.getBalance().compareTo(new BigDecimal("3000")) > 0)
                        .findFirst()
                        .map(Account::getAccountNumber)
                        .orElse("No premium account"));
        optionalResults.put("firstCustomerEmail",
                accounts.stream()
                        .map(Account::getCustomer)
                        .findFirst()
                        .map(customer -> customer.getEmail())
                        .orElse("unknown@bank.local"));

        Map<String, Object> streamAnalytics = new LinkedHashMap<>();
        streamAnalytics.put("averageBalance",
                accounts.stream().map(Account::getBalance)
                        .collect(Collectors.averagingDouble(BigDecimal::doubleValue)));
        streamAnalytics.put("accountTypeDistribution",
                accounts.stream()
                        .collect(Collectors.groupingBy(account -> account.getAccountType().name(), Collectors.counting())));
        streamAnalytics.put("topBalances",
                accounts.stream()
                        .map(Account::getBalance)
                        .sorted((left, right) -> right.compareTo(left))
                        .limit(3)
                        .collect(Collectors.toList()));

        ZonedDateTime indiaTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime newYorkTime = indiaTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        Map<String, Object> timeApi = new LinkedHashMap<>();
        timeApi.put("indiaTime", indiaTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        timeApi.put("newYorkTime", newYorkTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        timeApi.put("hoursBetween", Duration.between(newYorkTime.toLocalDateTime(), indiaTime.toLocalDateTime()).abs().toHours());

        String encodedToken = Base64.getEncoder().encodeToString(
                ("BANK-COMPLIANCE-" + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8)
        );

        CompletableFuture<String> highValueAccounts = CompletableFuture.supplyAsync(() ->
                accounts.stream()
                        .filter(account -> account.getBalance().compareTo(new BigDecimal("5000")) > 0)
                        .map(Account::getAccountNumber)
                        .collect(Collectors.joining(", "))
        );

        CompletableFuture<String> dormantCheck = CompletableFuture.supplyAsync(() ->
                accounts.stream()
                        .filter(account -> account.getTransactions().size() <= 1)
                        .map(Account::getAccountNumber)
                        .collect(Collectors.joining(", "))
        );

        String highValueAccountNumbers = highValueAccounts.join();
        String dormantAccountNumbers = dormantCheck.join();

        List<String> asyncInsights = Arrays.asList(
                highValueAccountNumbers.isEmpty() ? "No high value accounts" : "High value accounts: " + highValueAccountNumbers,
                dormantAccountNumbers.isEmpty() ? "No dormant accounts" : "Dormant/new accounts: " + dormantAccountNumbers
        );

        InterestCalculator calculator = InterestCalculator.simpleInterest();
        BigDecimal sampleBalance = accounts.stream()
                .map(Account::getBalance)
                .findFirst()
                .orElse(new BigDecimal("1000"));

        return new Java8FeatureResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                lambdaExamples,
                optionalResults,
                streamAnalytics,
                timeApi,
                encodedToken,
                asyncInsights,
                calculator.describe(sampleBalance, new BigDecimal("0.04"))
        );
    }
}
