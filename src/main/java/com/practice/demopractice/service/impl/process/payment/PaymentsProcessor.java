package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentsProcessor {

        public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody);

        public TransactionType getType();


}

