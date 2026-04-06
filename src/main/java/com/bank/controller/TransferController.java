package com.bank.controller;

import com.bank.dto.ApiResponse;
import com.bank.dto.TransferRequest;
import com.bank.service.TransferService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(transferService.transfer(request), request.getDescription()));
    }
}
