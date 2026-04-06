package com.bank.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiError {

    private final String message;
    private final List<String> details;
    private final LocalDateTime timestamp;

    public ApiError(String message, List<String> details, LocalDateTime timestamp) {
        this.message = message;
        this.details = Collections.unmodifiableList(new ArrayList<>(details));
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
