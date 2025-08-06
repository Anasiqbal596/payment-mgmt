package com.practice.demopractice.dto;

import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentHistoryRequest {
        private LocalDate startDate;
        private LocalDate endDate;
        private TransactionType transactionType;
        private PaymentStatus status;
        private int page = 0;
        private int size = 10;
}

