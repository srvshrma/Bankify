package com.bank.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardResponse {

    private long totalCustomers;
    private long totalAccounts;
    private BigDecimal totalBalance;
    private Map<String, BigDecimal> balanceByAccountType;
    private List<String> alerts;

    public DashboardResponse(long totalCustomers, long totalAccounts, BigDecimal totalBalance,
                             Map<String, BigDecimal> balanceByAccountType, List<String> alerts) {
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.totalBalance = totalBalance;
        this.balanceByAccountType = Collections.unmodifiableMap(new LinkedHashMap<>(balanceByAccountType));
        this.alerts = Collections.unmodifiableList(new ArrayList<>(alerts));
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public Map<String, BigDecimal> getBalanceByAccountType() {
        return balanceByAccountType;
    }

    public List<String> getAlerts() {
        return alerts;
    }
}
