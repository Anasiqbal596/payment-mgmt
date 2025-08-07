package com.practice.demopractice.controller;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.PaymentHistoryRequest;
import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.enums.PaymentStatus;
import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.impl.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.practice.demopractice.util.ApplicationConstant.APIPaths.ALL_HISTORY;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;//TODO,FixME


    @PostMapping
    public ResponseEntity<ResponseDTO> createPaymentRequest(@RequestBody InternalPaymentsRequestDTO dto) {
        var res = paymentService.createPaymentRequest(dto);
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

    @GetMapping(ALL_HISTORY)
    public ResponseEntity<ResponseDTO> history(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal UserDetails userDetails)// used for fetch latest  transaction
    {

        if (startDate != null && endDate != null && startDate.isAfter(endDate))
        {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.builder()
                            .status("error").statusCode(400)
                            .message("Start must be before end data").build());
        }
        // Using var:
        //var req = new PaymentHistoryRequest();  In Java (from version 10 onward), var is just a shorthand for declaring a local variable with type inference.
        PaymentHistoryRequest paymentHistoryRequest = new PaymentHistoryRequest();
        paymentHistoryRequest.setStartDate(startDate);
        paymentHistoryRequest.setEndDate(endDate);
        paymentHistoryRequest.setTransactionType(transactionType);
        paymentHistoryRequest.setStatus(status);
        paymentHistoryRequest.setPage(page);
        paymentHistoryRequest.setSize(size);

        ResponseDTO res = paymentService.getPaymentHistory(paymentHistoryRequest);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }




}
