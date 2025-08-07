package com.practice.demopractice.service.impl.payment;

import com.practice.demopractice.Repository.PaymentRepository;
import com.practice.demopractice.Repository.UserRepository;
import com.practice.demopractice.dto.*;
import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.entity.User;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Fixed import

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.practice.demopractice.util.ApplicationConstant.ResponseCode.FAIL;
import static com.practice.demopractice.util.ApplicationConstant.ResponseCode.SUCCESS;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepo;
    private final PaymentRepository paymentRepo;

    @Autowired
    public PaymentServiceImpl(UserRepository userRepo, PaymentRepository paymentRepo) {
        this.userRepo = userRepo;
        this.paymentRepo = paymentRepo;
    }

    private ResponseDTO buildSuccess(String message, HttpStatus status, Object data) {
        return ResponseDTO.builder()
                .status(SUCCESS)
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }

    private ResponseDTO buildError(String message, HttpStatus status) {
        return ResponseDTO.builder()
                .status(FAIL)
                .statusCode(status.value())
                .message(message)
                .build();
    }

    private PaymentResponseDto toDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .fee(payment.getFee())
                .transactionType(payment.getTransactionType())
                .payerId(payment.getUser().getId())
                .recipientId(payment.getRecipient().getId())
                .createdDate(payment.getCreatedDate())
                .status(payment.getStatus())
                .build();
    }

    @Override
    @Transactional
    public ResponseDTO createPaymentRequest(InternalPaymentsRequestDTO dto) {
        var userOpt = userRepo.findById(dto.getUserId());
        if (userOpt.isEmpty()) return buildError("User not found", HttpStatus.NOT_FOUND);

        var recOpt = userRepo.findById(dto.getRecipientId());
        if (recOpt.isEmpty()) return buildError("Recipient not found", HttpStatus.NOT_FOUND);

        try {
            TransactionType tx = TransactionType.valueOf(dto.getTransactionType().toUpperCase());
            Payment pay = Payment.builder()
                    .user(userOpt.get())
                    .recipient(recOpt.get())
                    .amount(dto.getAmount())
                    .fee(dto.getFee())
                    .transactionType(tx)
                    .status(PaymentStatus.PENDING)
                    .createdDate(LocalDateTime.now())
                    .build();


            pay.setStatus(PaymentStatus.SUCCESS);
            pay = paymentRepo.save(pay);
            return buildSuccess("Payment successful", HttpStatus.CREATED, toDto(pay));
        } catch (IllegalArgumentException e) {
            return buildError("Invalid transaction type", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseDTO getAllPayments() {
        var list = paymentRepo.findAll();
        if (list.isEmpty()) return buildSuccess("No payments", HttpStatus.OK, List.of());
        return buildSuccess("Found " + list.size(), HttpStatus.OK,
                list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @Override
    public ResponseDTO getPaymentById(String id) {
        try {
            var pid = Long.parseLong(id);
            return paymentRepo.findById(pid)
                    .map(p -> buildSuccess("OK", HttpStatus.OK, toDto(p)))
                    .orElse(buildError("Not found", HttpStatus.NOT_FOUND));
        } catch (NumberFormatException e) {
            return buildError("Bad ID", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ResponseDTO updatePayment(String id, InternalPaymentsRequestDTO dto) {
        try {
            var pid = Long.parseLong(id);
            var opt = paymentRepo.findById(pid);
            if (opt.isEmpty()) return buildError("Not found", HttpStatus.NOT_FOUND);

            var p = opt.get();
            p.setAmount(dto.getAmount());
            p.setFee(dto.getFee());
            p.setTransactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()));
            if (dto.getStatus() != null) p.setStatus(PaymentStatus.valueOf(dto.getStatus().toUpperCase()));
            if (dto.getRecipientId() != null) {
                userRepo.findById(dto.getRecipientId())
                        .ifPresentOrElse(p::setRecipient,
                                () -> {
                                    throw new IllegalArgumentException("Bad recipient");
                                });
            }
            return buildSuccess("Updated", HttpStatus.OK, toDto(paymentRepo.save(p)));
        } catch (Exception e) {
            return buildError("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ResponseDTO deletePayment(String id) {
        try {
            var pid = Long.parseLong(id);
            if (!paymentRepo.existsById(pid)) return buildError("Not found", HttpStatus.NOT_FOUND);
            paymentRepo.deleteById(pid);
            return buildSuccess("Deleted", HttpStatus.OK, null);
        } catch (NumberFormatException e) {
            return buildError("Bad ID", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = true) // Now using Spring's Transactional
    public ResponseDTO getPaymentHistory(PaymentHistoryRequest req) {
        // 1) Load authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // DEBUG: Print authenticated user info
        System.out.println("Authenticated User ID: " + user.getId());
        System.out.println("Authenticated Username: " + username);

        // 2) Build specification to get payments where user is either payer OR recipient
        Specification<Payment> spec = (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("user").get("id"), user.getId()),      // User is payer
                        cb.equal(root.get("recipient").get("id"), user.getId()) // User is recipient
                );

        // 3) Apply date filters
        if (req.getStartDate() != null) {
            LocalDateTime start = req.getStartDate().atStartOfDay();
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdDate"), start));
        }
        if (req.getEndDate() != null) {
            LocalDateTime end = req.getEndDate().atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdDate"), end));
        }

        // 4) Apply other filters
        if (req.getTransactionType() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("transactionType"), req.getTransactionType()));
        }
        if (req.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), req.getStatus()));
        }

        // 5) Create page request with sorting
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        Page<Payment> page = null;
        if(user.getRole().equals(UserRole.ADMIN)){
            page =    paymentRepo.findAll(pageable);
        }else{
            // 6) Execute query
            page = paymentRepo.findAll(spec, pageable);
        }

        // 7) Convert to DTOs
        List<PaymentResponseDto> dtos = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 8) Prepare pagination metadata
        Map<String, Object> meta = Map.of(
                "page", page.getNumber(),
                "size", page.getSize(),
                "total", page.getTotalElements(),
                "pages", page.getTotalPages()
        );

        return ResponseDTO.builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .message("Payment history retrieved")
                .data(dtos)
                .pageData(meta)
                .build();
    }
}