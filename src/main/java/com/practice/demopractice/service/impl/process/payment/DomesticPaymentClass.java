package com.practice.demopractice.service.impl.process.payment;

import com.practice.demopractice.Repository.PaymentRepository;
//import com.practice.demopractice.config.FeeConfig;
import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.entity.FeeConfig;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class DomesticPaymentClass implements PaymentsProcessor, PaymentValidation {

    private final PaymentRepository paymentRepository;
    private final FeeConfig feeConfig;

    public DomesticPaymentClass(PaymentRepository paymentRepository, FeeConfig feeConfig) {
        this.paymentRepository = paymentRepository;
        this.feeConfig = feeConfig;
    }

    @Override
    public ResponseEntity<Object> processPayment(HttpServletRequest request, InternalPaymentsRequestDTO requestBody) {
        // Validation
        validateFee(requestBody);

        // Fee calculation
        BigDecimal feeRate = feeConfig.getDomestic(); // e.g., 0.05 means 0.05%
        BigDecimal fee = requestBody.getAmount()
                .multiply(feeRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal updatedAmount = requestBody.getAmount()
                .subtract(fee)
                .setScale(6, RoundingMode.HALF_UP);

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

        // Save to DB
        paymentRepository.save(payment);

        return ResponseEntity.ok("Processed Domestic Payment"+payment);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.DOMESTIC;
    }

    @Override
    public void validateFee(InternalPaymentsRequestDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (dto.getRecipientId() == null) {
            throw new IllegalArgumentException("Recipient ID must be provided.");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID must be provided.");
        }
        if (feeConfig.getDomestic() == null) {
            throw new IllegalStateException("Domestic fee rate not configured in application.properties");
        }
    }
}
