package com.practice.demopractice.factory;

import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.PaymentsProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentFactory {
    private final Map<TransactionType, PaymentsProcessor> strategyMap = new HashMap<>();

    public PaymentFactory(List<PaymentsProcessor> processorList) {
        for (PaymentsProcessor processor : processorList) {
            strategyMap.put(processor.getType(), processor);
        }
    }

    public PaymentsProcessor getPaymentMethodType(String transactionTypeStr) {
        TransactionType type = TransactionType.valueOf(transactionTypeStr.toUpperCase());
        return strategyMap.get(type);
    }
}
