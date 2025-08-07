package com.practice.demopractice.controller;

import com.practice.demopractice.dto.InternalPaymentsRequestDTO;
import com.practice.demopractice.service.impl.process.payment.factory.PaymentFactory;
import com.practice.demopractice.service.impl.process.payment.PaymentsProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.practice.demopractice.util.ApplicationConstant.APIPaths.PROCESS;


@RestController
@RequestMapping("/pay")
public class ProcessPaymentController {

    @Autowired
    private PaymentFactory paymentFactory;

    /**
     * Make Payments do payments through Factory Design Pattern
     * @param request
     * @param dto
     * @return
     */
    @PostMapping(PROCESS)
    public ResponseEntity<Object> processPayment(HttpServletRequest request, @RequestBody InternalPaymentsRequestDTO dto) {
        //based on transaction type get payment implementation
        PaymentsProcessor processor = paymentFactory.getPaymentMethodType(dto.getTransactionType());
        //call the specific payment
        return processor.processPayment(request, dto);
    }


}

