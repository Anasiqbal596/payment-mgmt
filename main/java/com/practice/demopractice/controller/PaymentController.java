package com.practice.demopractice.controller;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.dto.PaymentRequestDto;
import com.practice.demopractice.dto.ResponseDTO;
import com.practice.demopractice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    // Create payment
    @PostMapping
    public ResponseEntity<ResponseDTO> makePayment(@RequestBody InternalPaymentsRequestDTO dto) {
        ResponseDTO response = paymentService.makePayment(dto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<ResponseDTO> getAllPayments() {
        ResponseDTO response = paymentService.getAllPayments();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get single payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getPaymentById(@PathVariable String id) {
        ResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Update payment
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updatePayment(
            @PathVariable String id,
            @RequestBody InternalPaymentsRequestDTO dto) {
        ResponseDTO response = paymentService.updatePayment(id, dto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletePayment(@PathVariable String id) {
        ResponseDTO response = paymentService.deletePayment(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}