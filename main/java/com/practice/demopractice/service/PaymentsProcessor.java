package com.practice.demopractice.service;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PaymentsProcessor {

        ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody);

        TransactionType getType(); // ✅ must be implemented
    }
