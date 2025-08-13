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
public class WalletPayment implements PaymentsProcessor, PaymentValidation {

    private final PaymentRepository paymentRepository;
    private final FeeConfig feeConfig;

    public WalletPayment(PaymentRepository paymentRepository, FeeConfig feeConfig) {
        this.paymentRepository = paymentRepository;
        this.feeConfig = feeConfig;
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        log.info("Starting Wallet payment processing for UserId: {}, RecipientId: {}, Amount: {}",
                requestBody.getUserId(), requestBody.getRecipientId(), requestBody.getAmount());

        try {
            // Validation
            validateFee(requestBody);

            // Fee calculation
            BigDecimal feeRate = feeConfig.getDomestic(); // Could be feeConfig.getWallet() if configured separately
            log.debug("Using fee rate: {}", feeRate);

            BigDecimal fee = requestBody.getAmount()
                    .multiply(feeRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                    .setScale(6, RoundingMode.HALF_UP);

            BigDecimal updatedAmount = requestBody.getAmount()
                    .subtract(fee)
                    .setScale(6, RoundingMode.HALF_UP);

            log.debug("Calculated fee: {}, Updated amount after fee: {}", fee, updatedAmount);

            // Create payment object
            Payment payment = new Payment();
            payment.setAmount(requestBody.getAmount());
            payment.setFee(fee);
            payment.setUpdatedAmount(updatedAmount);
            payment.setTransactionType(TransactionType.WALLET);
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

            log.info("Wallet payment processed successfully for UserId: {} -> RecipientId: {}",
                    requestBody.getUserId(), requestBody.getRecipientId());
            return ResponseEntity.ok("Processed Wallet Payment: " + payment);

        } catch (Exception e) {
            log.error("Error processing Wallet payment for UserId: {}, Reason: {}",
                    requestBody.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Payment processing failed: " + e.getMessage());
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WALLET;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        log.info("Validating Wallet payment request for UserId: {}, Amount: {}", dto.getUserId(), dto.getAmount());

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: Invalid amount for UserId: {}", dto.getUserId());
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (dto.getRecipientId() == null) {
            log.warn("Validation failed: Missing recipient ID for UserId: {}", dto.getUserId());
            throw new IllegalArgumentException("Recipient ID must be provided.");
        }
        if (dto.getUserId() == null) {
            log.warn("Validation failed: Missing user ID.");
            throw new IllegalArgumentException("User ID must be provided.");
        }
        if (feeConfig.getDomestic() == null) { // Could be feeConfig.getWallet() if separated
            log.error("Validation failed: Domestic fee rate not configured.");
            throw new IllegalStateException("Domestic fee rate not configured in application.properties");
        }

        log.info("Validation passed for Wallet payment request.");
    }
}
