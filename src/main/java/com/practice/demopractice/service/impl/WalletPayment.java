package com.practice.demopractice.service.impl;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.PaymentsProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WalletPayment implements PaymentsProcessor {

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        return ResponseEntity.ok("Processed Wallet Payment");
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WALLET; // Ensure this matches your enum
    }
}
