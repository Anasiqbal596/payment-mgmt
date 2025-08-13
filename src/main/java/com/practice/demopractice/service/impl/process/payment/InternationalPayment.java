package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.Repository.PaymentRepository;
import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.entity.FeeConfig;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
public class InternationalPayment implements PaymentsProcessor, PaymentValidation {

    private final PaymentRepository paymentRepository;
    private final FeeConfig feeConfig;

    public InternationalPayment(PaymentRepository paymentRepository, FeeConfig feeConfig) {
        this.paymentRepository = paymentRepository;
        this.feeConfig = feeConfig;
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        log.info("Starting INTERNATIONAL payment processing for User ID: {}, Recipient ID: {}, Amount: {}",
                requestBody.getUserId(), requestBody.getRecipientId(), requestBody.getAmount());

        try {
            // Validation
            validateFee(requestBody);
            log.debug("Validation successful for INTERNATIONAL payment: {}", requestBody);

            // Fee calculation
            BigDecimal feeRate = feeConfig.getInternational(); // Use correct fee for international
            log.debug("International fee rate from config: {}", feeRate);

            BigDecimal fee = requestBody.getAmount()
                    .multiply(feeRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                    .setScale(6, RoundingMode.HALF_UP);
            log.debug("Calculated fee: {}", fee);

            BigDecimal updatedAmount = requestBody.getAmount()
                    .subtract(fee)
                    .setScale(6, RoundingMode.HALF_UP);
            log.debug("Updated amount after fee deduction: {}", updatedAmount);

            // Create payment object
            Payment payment = new Payment();
            payment.setAmount(requestBody.getAmount());
            payment.setFee(fee);
            payment.setUpdatedAmount(updatedAmount);
            payment.setTransactionType(TransactionType.INTERNATIONAL);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCreatedDate(LocalDateTime.now());

            // Set payer
            User payer = new User();
            payer.setId(requestBody.getUserId());
            payment.setUser(payer);

            // Set recipient
            User recipient = new User();
            recipient.setId(requestBody.getRecipientId());
            payment.setRecipient(recipient);

            // Save to DB
            paymentRepository.save(payment);
            log.info("INTERNATIONAL payment saved successfully with ID: {}", payment.getId());

            return ResponseEntity.ok("Processed International Payment: " + payment);
        } catch (Exception e) {
            log.error("Error processing INTERNATIONAL payment for User ID: {}", requestBody.getUserId(), e);
            return ResponseEntity.status(500).body("Failed to process International payment: " + e.getMessage());
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INTERNATIONAL;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: Amount is invalid");
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (dto.getRecipientId() == null) {
            log.warn("Validation failed: Recipient ID is missing");
            throw new IllegalArgumentException("Recipient ID must be provided.");
        }
        if (dto.getUserId() == null) {
            log.warn("Validation failed: User ID is missing");
            throw new IllegalArgumentException("User ID must be provided.");
        }
        if (feeConfig.getInternational() == null) {
            log.error("International fee rate is not configured in application.properties");
            throw new IllegalStateException("International fee rate not configured in application.properties");
        }
    }
}
