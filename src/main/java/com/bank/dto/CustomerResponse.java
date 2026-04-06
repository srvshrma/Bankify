package com.bank.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerResponse {

    private Long id;
    private String fullName;
    private String email;
    private LocalDate joinedOn;
    private List<AccountResponse> accounts;

    public CustomerResponse(Long id, String fullName, String email, LocalDate joinedOn, List<AccountResponse> accounts) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.joinedOn = joinedOn;
        this.accounts = Collections.unmodifiableList(new ArrayList<>(accounts));
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getJoinedOn() {
        return joinedOn;
    }

    public List<AccountResponse> getAccounts() {
        return accounts;
    }
}
