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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.practice.demopractice.util.ApplicationConstant.APIPaths.PROCESS;

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
        if (dto == null) {
            return ResponseEntity.badRequest().body("Request body is required");
        }

        TransactionType type;
        try {
            if (dto.getTransactionType() == null) {
                return ResponseEntity.badRequest().body("transactionType is required");
            }
            type = TransactionType.valueOf(dto.getTransactionType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid transactionType: " + dto.getTransactionType());
        }

        PaymentsProcessor processor = paymentFactory.getPaymentMethodType(type);
        if (processor == null) {
            return ResponseEntity.badRequest().body("No processor found for type: " + type);
        }

        try {
            return processor.processPayment(request, dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * GET: Fetch payments by transaction type and userId
     */
    @GetMapping(value = "/{transactionType}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPaymentInfo(@PathVariable String transactionType,
                                                 @PathVariable Long userId) {
        TransactionType type;
        try {
            type = TransactionType.valueOf(transactionType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid transaction type: " + transactionType);
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        List<Payment> payments = paymentRepository.findByUser_IdAndTransactionType(userId, type);

        // return DTOs instead of entities to avoid lazy-loading issues and leaking internals
        List<PaymentResponseDto> dtos = payments.stream().map(this::toDto).collect(Collectors.toList());
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
