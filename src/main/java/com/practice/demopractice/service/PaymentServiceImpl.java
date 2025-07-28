package com.practice.demopractice.service;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentServiceImpl {

    @Autowired
    private Map<String, PaymentsProcessor> paymentsProcessorMap;

    public ResponseEntity<Object> makePayment(String paymentType, HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        PaymentsProcessor processor = paymentsProcessorMap.get(paymentType);

        if (processor == null) {
            return ResponseEntity.badRequest().body("Invalid payment type: " + paymentType);
        }

        return processor.processPayment(request, requestBody);
    }
}