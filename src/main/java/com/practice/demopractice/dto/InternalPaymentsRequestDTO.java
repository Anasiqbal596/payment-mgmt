package com.practice.demopractice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalPaymentsRequestDTO {
    private Long userId;
    private Long recipientId;
    private BigDecimal amount;
    private int fee;
    private String transactionType;
    private String status;

}


