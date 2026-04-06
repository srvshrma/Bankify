package com.bank.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Java8FeatureResponse {

    private String generatedAt;
    private List<String> lambdas;
    private Map<String, String> optionalResults;
    private Map<String, Object> streamAnalytics;
    private Map<String, Object> timeApi;
    private String encodedComplianceToken;
    private List<String> asyncInsights;
    private String defaultMethodComputation;

    public Java8FeatureResponse(String generatedAt, List<String> lambdas, Map<String, String> optionalResults,
                                Map<String, Object> streamAnalytics, Map<String, Object> timeApi,
                                String encodedComplianceToken, List<String> asyncInsights,
                                String defaultMethodComputation) {
        this.generatedAt = generatedAt;
        this.lambdas = Collections.unmodifiableList(new ArrayList<>(lambdas));
        this.optionalResults = Collections.unmodifiableMap(new LinkedHashMap<>(optionalResults));
        this.streamAnalytics = Collections.unmodifiableMap(new LinkedHashMap<>(streamAnalytics));
        this.timeApi = Collections.unmodifiableMap(new LinkedHashMap<>(timeApi));
        this.encodedComplianceToken = encodedComplianceToken;
        this.asyncInsights = Collections.unmodifiableList(new ArrayList<>(asyncInsights));
        this.defaultMethodComputation = defaultMethodComputation;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public List<String> getLambdas() {
        return lambdas;
    }

    public Map<String, String> getOptionalResults() {
        return optionalResults;
    }

    public Map<String, Object> getStreamAnalytics() {
        return streamAnalytics;
    }

    public Map<String, Object> getTimeApi() {
        return timeApi;
    }

    public String getEncodedComplianceToken() {
        return encodedComplianceToken;
    }

    public List<String> getAsyncInsights() {
        return asyncInsights;
    }

    public String getDefaultMethodComputation() {
        return defaultMethodComputation;
    }
}
