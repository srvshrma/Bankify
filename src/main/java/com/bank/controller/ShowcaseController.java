package com.bank.controller;

import com.bank.dto.ApiResponse;
import com.bank.dto.Java8FeatureResponse;
import com.bank.service.Java8ShowcaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/showcase")
public class ShowcaseController {

    private final Java8ShowcaseService java8ShowcaseService;

    public ShowcaseController(Java8ShowcaseService java8ShowcaseService) {
        this.java8ShowcaseService = java8ShowcaseService;
    }

    @GetMapping("/java8")
    public ResponseEntity<ApiResponse<Java8FeatureResponse>> showcase() {
        return ResponseEntity.ok(new ApiResponse<>("Java 8 showcase generated", java8ShowcaseService.generateShowcase()));
    }
}
