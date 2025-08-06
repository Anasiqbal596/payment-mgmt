package com.practice.demopractice.controller;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.PaymentHistoryRequest;
import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.practice.demopractice.util.APIPaths.MY_HISTORY;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseDTO> makePayment(@RequestBody InternalPaymentsRequestDTO dto) {
        var res = paymentService.makePayment(dto);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll() {
        var res = paymentService.getAllPayments();
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable String id) {
        var res = paymentService.getPaymentById(id);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(
            @PathVariable String id,
            @RequestBody InternalPaymentsRequestDTO dto) {
        var res = paymentService.updatePayment(id, dto);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable String id) {
        var res = paymentService.deletePayment(id);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    @GetMapping(MY_HISTORY)
    public ResponseEntity<ResponseDTO> history(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.builder()
                            .status("error").statusCode(400)
                            .message("Start must be before end").build());
        }

        var req = new PaymentHistoryRequest();
        req.setStartDate(startDate);
        req.setEndDate(endDate);
        req.setTransactionType(transactionType);
        req.setStatus(status);
        req.setPage(page);
        req.setSize(size);

        var res = paymentService.getPaymentHistory(req);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }
}
