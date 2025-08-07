package com.practice.demopractice.service.impl.process.payment.factory;

import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.impl.process.payment.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        TransactionType type =null;
        try {
            type = TransactionType.valueOf(transactionTypeStr.toUpperCase());
            if(type){
                return strategyMap.get(type);
            }
            else if (type.equals(peerToPeer)) {
                return strategyMap.get(type);
            } else if (type.equals(internationalPayment)) {
                return strategyMap.get(type);
            }else if (type.equals(walletPayment))
            {
                return strategyMap.get(type);
            }
        }catch (IllegalArgumentException e){
                e.printStackTrace();
                return strategyMap.get(type);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return strategyMap.get(type);
    }
}
