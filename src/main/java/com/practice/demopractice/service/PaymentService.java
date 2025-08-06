package com.practice.demopractice.service;

import com.practice.demopractice.dto.*;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface PaymentService {
    ResponseDTO makePayment(InternalPaymentsRequestDTO dto);
    ResponseDTO getAllPayments();
    ResponseDTO getPaymentById(String id);
    ResponseDTO updatePayment(String id, InternalPaymentsRequestDTO dto);
    ResponseDTO deletePayment(String id);
    ResponseDTO getPaymentHistory(PaymentHistoryRequest request);
}