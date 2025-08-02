package com.practice.demopractice.service;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InternationalPayment implements PaymentsProcessor {

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        return new ResponseEntity<>("InternationalPayment Implementation", HttpStatus.OK);
    }

    public TransactionType getType() {
        return TransactionType.INTERNATIONAL;
    }
}
