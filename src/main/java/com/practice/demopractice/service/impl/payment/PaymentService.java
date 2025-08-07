package com.practice.demopractice.service.impl.payment;

import com.practice.demopractice.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    ResponseDTO createPaymentRequest(InternalPaymentsRequestDTO dto);
    ResponseDTO getAllPayments();
    ResponseDTO getPaymentById(String id);
    ResponseDTO updatePayment(String id, InternalPaymentsRequestDTO dto);
    ResponseDTO deletePayment(String id);
    ResponseDTO getPaymentHistory(PaymentHistoryRequest request);
}