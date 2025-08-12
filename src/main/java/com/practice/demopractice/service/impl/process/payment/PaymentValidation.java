package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface PaymentValidation  {
     void validateFee(InternalPaymentsRequestDTO dto);
}
