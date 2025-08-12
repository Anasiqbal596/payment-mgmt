package com.practice.demopractice.dto;

import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal fee;            // use BigDecimal for money
    private BigDecimal updatedAmount;
    //private int fee;
    private TransactionType transactionType;
    private Long payerId;
    private Long recipientId;
    private LocalDateTime createdDate;
    private PaymentStatus status;
}