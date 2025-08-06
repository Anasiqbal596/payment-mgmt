package com.practice.demopractice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private String senderUsername;
    private String recipientUsername;
    private BigDecimal amount;
}
