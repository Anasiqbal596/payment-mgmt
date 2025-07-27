package com.practice.demopractice.service.impl;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.PaymentsProcessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class PeerToPeer implements PaymentsProcessor {

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        return new ResponseEntity<>("PeerToPeer", HttpStatus.OK);
    }

    public TransactionType getType() {
        return TransactionType.PEER;
    }
}
