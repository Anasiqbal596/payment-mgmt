package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.entity.FeeConfig;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentValidationImpl implements PaymentValidation, PaymentsProcessor {

    private final FeeConfig feeConfig;

    public PaymentValidationImpl(FeeConfig feeConfig) {
        this.feeConfig = feeConfig;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        log.info("Validating fee for TransactionType: {}, UserId: {}, Amount: {}",
                dto.getTransactionType(), dto.getUserId(), dto.getAmount());

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: Amount is invalid for UserId: {}", dto.getUserId());
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (dto.getTransactionType() == null || dto.getTransactionType().isBlank()) {
            log.warn("Validation failed: Transaction type is missing for UserId: {}", dto.getUserId());
            throw new IllegalArgumentException("Transaction type must be provided.");
        }

        TransactionType txType;
        try {
            txType = TransactionType.valueOf(dto.getTransactionType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid transaction type: {}", dto.getTransactionType(), e);
            throw new IllegalArgumentException("Unsupported transaction type: " + dto.getTransactionType());
        }

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
                log.error("Unsupported transaction type: {}", txType);
                throw new IllegalArgumentException("Unsupported transaction type: " + txType);
        }

        log.debug("Expected fee for {} transaction: {}", txType, expectedFee);
        log.info("Validation successful for UserId: {}", dto.getUserId());
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO dto) {
        log.info("Starting payment processing for TransactionType: {}, UserId: {}",
                dto.getTransactionType(), dto.getUserId());

        try {
            // Validate first
            validateFee(dto);

            // Placeholder process logic
            log.info("Payment of type {} processed successfully for UserId: {}", dto.getTransactionType(), dto.getUserId());
            return ResponseEntity.ok("Payment of type " + dto.getTransactionType() + " processed successfully.");
        } catch (Exception e) {
            log.error("Payment processing failed for UserId: {}", dto.getUserId(), e);
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }

    @Override
    public TransactionType getType() {
        return null; // You can update this if needed
    }
}
