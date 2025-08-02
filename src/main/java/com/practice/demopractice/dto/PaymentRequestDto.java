package com.practice.demopractice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


import java.time.LocalDateTime;

@Data
@Builder
public class PaymentRequestDto {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private int fee;
    private String transactionType;
    private String status;
    private LocalDateTime createdDate;
}
