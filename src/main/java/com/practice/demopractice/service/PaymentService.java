package com.practice.demopractice.service;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    ResponseDTO makePayment(InternalPaymentsRequestDTO dto);

    ResponseDTO getAllPayments();
    ResponseDTO getPaymentById(String id);
    ResponseDTO updatePayment(String id, InternalPaymentsRequestDTO dto);
    ResponseDTO deletePayment(String id);
}
