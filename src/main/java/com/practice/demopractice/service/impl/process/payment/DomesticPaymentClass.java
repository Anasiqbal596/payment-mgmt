package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.Repository.PaymentRepository;
import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.entity.FeeConfig;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
//import com.practice.demopractice.repository.PaymentRepository;
//import com.practice.demopractice.config.FeeConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DomesticPaymentClass implements PaymentsProcessor, PaymentValidation {

    private final FeeConfig feeConfig;
    private final PaymentRepository paymentRepository;

    public DomesticPaymentClass(FeeConfig feeConfig, PaymentRepository paymentRepository) {
        this.feeConfig = feeConfig;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        log.info("Received Domestic Payment Request - User ID: {}, Recipient ID: {}, Amount: {}, TransactionType: {}",
                requestBody.getUserId(),
                requestBody.getRecipientId(),
                requestBody.getAmount(),
                requestBody.getTransactionType());

        // Validation
        validateFee(requestBody);

        // Fee calculation
        BigDecimal feeRate = feeConfig.getDomestic();
        if (feeRate == null) {
            log.error("Domestic fee rate is missing in configuration.");
            return ResponseEntity.status(500).body("Fee configuration missing for domestic payments.");
        }
        log.debug("Using domestic fee rate: {}", feeRate);

        BigDecimal fee = requestBody.getAmount()
                .multiply(feeRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal updatedAmount = requestBody.getAmount()
                .subtract(fee)
                .setScale(6, RoundingMode.HALF_UP);

        log.info("Calculated Fee: {}, Updated Amount: {}", fee, updatedAmount);

        // Create payment object
        Payment payment = new Payment();
        payment.setAmount(requestBody.getAmount());
        payment.setFee(fee);
        payment.setUpdatedAmount(updatedAmount);
        payment.setTransactionType(TransactionType.DOMESTIC);
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

        log.debug("Prepared Payment Entity: Payer ID={}, Recipient ID={}, Status={}, Created={}",
                payer.getId(), recipient.getId(), payment.getStatus(), payment.getCreatedDate());

        // Save to DB
        try {
            paymentRepository.save(payment);
            log.info("Domestic Payment successfully saved with Amount: {} and Fee: {}", payment.getAmount(), payment.getFee());
        } catch (Exception e) {
            log.error("Error saving domestic payment to database: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to save payment: " + e.getMessage());
        }

        return ResponseEntity.ok("Processed Domestic Payment Successfully");
    }

    @Override
    public TransactionType getType() {
        return TransactionType.DOMESTIC;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        log.debug("Validating fee for amount: {}", dto.getAmount());
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid payment amount: {}", dto.getAmount());
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
