package com.practice.demopractice.service.impl;

import com.practice.demopractice.Repository.PaymentRepository;
import com.practice.demopractice.Repository.UserRepository;
import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PaymentRepository paymentRepo;

    @Autowired
    public PaymentServiceImpl(UserRepository userRepo, PaymentRepository paymentRepo) {
        this.userRepo = userRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional
    public ResponseDTO makePayment(InternalPaymentsRequestDTO dto) {
        Optional<User> userOptional = userRepo.findById(dto.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseDTO.builder().status("error").statusCode(HttpStatus.NOT_FOUND.value()).message("User not found with ID: " + dto.getUserId()).build();
        }
        User user = userOptional.get();

        try {
            TransactionType transactionType = TransactionType.valueOf(dto.getTransactionType().toUpperCase());

            Payment payment = Payment.builder().user(user).amount(dto.getAmount()).fee(dto.getFee()).transactionType(transactionType).createdDate(LocalDateTime.now()).status(PaymentStatus.PENDING).build();

            payment = paymentRepo.save(payment);

            // Simulate payment processing
            payment.setStatus(PaymentStatus.SUCCESS);
            payment = paymentRepo.save(payment);

            return ResponseDTO.builder().status("success").statusCode(HttpStatus.CREATED.value()).data(convertPaymentToMap(payment)).message("Payment processed successfully").build();

        } catch (IllegalArgumentException e) {
            return ResponseDTO.builder().status("error").statusCode(HttpStatus.BAD_REQUEST.value()).message("Invalid transaction type: " + dto.getTransactionType()).build();
        }
    }

    @Override
    public ResponseDTO getAllPayments() {
        List<Payment> payments = paymentRepo.findAll();
        if (payments.isEmpty()) {
            return ResponseDTO.builder().status("success").statusCode(HttpStatus.OK.value()).message("No payments found").build();
        }

        List<Map<String, Object>> paymentList = payments.stream().map(this::convertPaymentToMap).collect(Collectors.toList());

        return ResponseDTO.builder().status("success").statusCode(HttpStatus.OK.value()).data(paymentList).message("Found " + paymentList.size() + " payments").build();
    }

    @Override
    public ResponseDTO getPaymentById(String id) {
        try {
            Long paymentId = Long.parseLong(id);
            Optional<Payment> paymentOptional = paymentRepo.findById(paymentId);

            if (paymentOptional.isEmpty()) {
                return ResponseDTO.builder().status("error").statusCode(HttpStatus.NOT_FOUND.value()).message("Payment not found with ID: " + id).build();
            }

            return ResponseDTO.builder().status("success").statusCode(HttpStatus.OK.value()).data(convertPaymentToMap(paymentOptional.get())).message("Payment retrieved successfully").build();

        } catch (NumberFormatException e) {
            return ResponseDTO.builder().status("error").statusCode(HttpStatus.BAD_REQUEST.value()).message("Invalid payment ID format").build();
        }
    }

    @Override
    @Transactional
    public ResponseDTO updatePayment(String id, InternalPaymentsRequestDTO dto) {
        try {
            Long paymentId = Long.parseLong(id);
            Optional<Payment> paymentOptional = paymentRepo.findById(paymentId);

            if (paymentOptional.isEmpty()) {
                return ResponseDTO.builder().status("error").statusCode(HttpStatus.NOT_FOUND.value()).message("Payment not found with ID: " + id).build();
            }

            Payment payment = paymentOptional.get();

            try {
                // Update payment fields
                payment.setAmount(dto.getAmount());
                payment.setFee(dto.getFee());
                payment.setTransactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()));

                if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                    payment.setStatus(PaymentStatus.valueOf(dto.getStatus().toUpperCase()));
                }

                payment = paymentRepo.save(payment);

                return ResponseDTO.builder().status("success").statusCode(HttpStatus.OK.value()).data(convertPaymentToMap(payment)).message("Payment updated successfully").build();

            } catch (IllegalArgumentException e) {
                return ResponseDTO.builder().status("error").statusCode(HttpStatus.BAD_REQUEST.value()).message("Invalid enum value: " + e.getMessage()).build();
            }

        } catch (NumberFormatException e) {
            return ResponseDTO.builder().status("error").statusCode(HttpStatus.BAD_REQUEST.value()).message("Invalid payment ID format").build();
        }
    }

    @Override
    @Transactional
    public ResponseDTO deletePayment(String id) {
        try {
            Long paymentId = Long.parseLong(id);

            if (!paymentRepo.existsById(paymentId)) {
                return ResponseDTO.builder().status("error").statusCode(HttpStatus.NOT_FOUND.value()).message("Payment not found with ID: " + id).build();
            }

            paymentRepo.deleteById(paymentId);

            return ResponseDTO.builder().status("success").statusCode(HttpStatus.OK.value()).message("Payment deleted successfully").build();

        } catch (NumberFormatException e) {
            return ResponseDTO.builder().status("error").statusCode(HttpStatus.BAD_REQUEST.value()).message("Invalid payment ID format").build();
        }
    }

    private Map<String, Object> convertPaymentToMap(Payment payment) {
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("id", payment.getId());
        paymentMap.put("userId", payment.getUser().getId());
        paymentMap.put("amount", payment.getAmount());
        paymentMap.put("fee", payment.getFee());
        paymentMap.put("transactionType", payment.getTransactionType().name());
        paymentMap.put("status", payment.getStatus().name());
        paymentMap.put("createdDate", payment.getCreatedDate());
        return paymentMap;
    }
}