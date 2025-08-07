package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PaymentsProcessor {

        public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody);

        public TransactionType getType(); // ✅ must be implemented
}
