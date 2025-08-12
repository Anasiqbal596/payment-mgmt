package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
//import com.practice.demopractice.config.FeeConfig;
import com.practice.demopractice.entity.FeeConfig;
import com.practice.demopractice.enums.TransactionType;
//import com.practice.demopractice.service.PaymentValidation;
//import com.practice.demopractice.service.PaymentsProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentValidationImpl implements PaymentValidation, PaymentsProcessor {

    private final FeeConfig feeConfig;
    private final TransactionType type;

    // Constructor injection with type specified
    public PaymentValidationImpl(FeeConfig feeConfig) {
        this.feeConfig = feeConfig;
        this.type = TransactionType.WALLET;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        TransactionType txType = TransactionType.valueOf(dto.getTransactionType().toUpperCase());
        BigDecimal expectedFee;

        switch (txType) {
            case WALLET:
                expectedFee = dto.getAmount().multiply(feeConfig.getWallet());
                break;
            case DOMESTIC:
                expectedFee = dto.getAmount().multiply(feeConfig.getDomestic());
                break;
            case INTERNATIONAL:
                expectedFee = dto.getAmount().multiply(feeConfig.getInternational());
                break;
            case PEER:
                expectedFee = dto.getAmount().multiply(feeConfig.getPeerToPeer());
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + txType);
        }


    }

    @Override
    public TransactionType getType() {
        return this.type;
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO dto) {
        // Validate first
        validateFee(dto);

        // Process logic (this is just a placeholder — put your real logic here)
        return ResponseEntity.ok("Payment of type " + dto.getTransactionType() + " processed successfully.");
    }
}
