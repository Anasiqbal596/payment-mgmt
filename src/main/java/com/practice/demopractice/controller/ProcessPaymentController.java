package com.practice.demopractice.controller;

import com.practice.demopractice.Repository.PaymentRepository;
import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.PaymentResponseDto;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.impl.process.payment.PaymentsProcessor;
import com.practice.demopractice.service.impl.process.payment.factory.PaymentFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.practice.demopractice.util.ApplicationConstant.APIPaths.PROCESS;

@Slf4j
@RestController
@RequestMapping("/pay")
public class ProcessPaymentController {

    private final PaymentFactory paymentFactory;
    private final PaymentRepository paymentRepository;

    public ProcessPaymentController(PaymentFactory paymentFactory,
                                    PaymentRepository paymentRepository) {
        this.paymentFactory = paymentFactory;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Make Payments (factory-based)
     */
    @PostMapping(value = PROCESS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processPayment(HttpServletRequest request,
                                                 @Valid @RequestBody InternalPaymentsRequestDTO dto) {
        log.info("Received payment request: {}", dto);

        if (dto == null) {
            log.warn("Request body is missing.");
            return ResponseEntity.badRequest().body("Request body is required");
        }

        TransactionType type;
        try {
            if (dto.getTransactionType() == null) {
                log.warn("transactionType is missing in request: {}", dto);
                return ResponseEntity.badRequest().body("transactionType is required");
            }
            type = TransactionType.valueOf(dto.getTransactionType().toUpperCase());
            log.debug("Parsed transactionType: {}", type);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid transactionType: {}", dto.getTransactionType(), ex);
            return ResponseEntity.badRequest().body("Invalid transactionType: " + dto.getTransactionType());
        }

        PaymentsProcessor processor = paymentFactory.getPaymentMethodType(type);
        if (processor == null) {
            log.error("No processor found for type: {}", type);
            return ResponseEntity.badRequest().body("No processor found for type: " + type);
        }

        try {
            log.info("Processing payment using processor: {}", processor.getClass().getSimpleName());
            ResponseEntity<Object> response = processor.processPayment(request, dto);
            log.info("Payment processed successfully for userId={} and type={}", dto.getUserId(), type);
            return response;
        } catch (Exception e) {
            log.error("Failed to process payment for userId={} and type={}: {}", dto.getUserId(), type, e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * GET: Fetch payments by transaction type and userId
     */
    @GetMapping(value = "/{transactionType}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPaymentInfo(@PathVariable String transactionType,
                                                 @PathVariable Long userId) {
        log.info("Fetching payment info for userId={} and transactionType={}", userId, transactionType);

        TransactionType type;
        try {
            type = TransactionType.valueOf(transactionType.toUpperCase());
            log.debug("Parsed transactionType: {}", type);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid transaction type: {}", transactionType, ex);
            return ResponseEntity.badRequest().body("Invalid transaction type: " + transactionType);
        }

        if (userId == null) {
            log.warn("userId is null in getPaymentInfo request");
            return ResponseEntity.badRequest().body("userId is required");
        }

        List<Payment> payments = paymentRepository.findByUser_IdAndTransactionType(userId, type);
        log.info("Found {} payments for userId={} and type={}", payments.size(), userId, type);

        List<PaymentResponseDto> dtos = payments.stream().map(this::toDto).collect(Collectors.toList());
        log.debug("Mapped payments to DTO list: {}", dtos);
        return ResponseEntity.ok(dtos);
    }

    // Simple mapper — adapt PaymentResponseDto to your actual DTO implementation
    private PaymentResponseDto toDto(Payment p) {
        return PaymentResponseDto.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .fee(p.getFee())
                .updatedAmount(p.getUpdatedAmount())
                .transactionType(p.getTransactionType())
                .payerId(p.getUser() != null ? p.getUser().getId() : null)
                .recipientId(p.getRecipient() != null ? p.getRecipient().getId() : null)
                .createdDate(p.getCreatedDate())
                .status(p.getStatus())
                .build();
    }
}
