package com.practice.demopractice.service.impl.process.payment.factory;

import com.practice.demopractice.enums.TransactionType;
import com.practice.demopractice.service.impl.process.payment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.practice.demopractice.enums.TransactionType.*;
import static org.springframework.data.util.TypeUtils.type;

@Component
public class PaymentFactory {

        @Autowired
        DomesticPaymentClass domesticPaymentClass;
        @Autowired
        InternationalPayment internationalPayment;
        @Autowired
        PeerToPeer peerToPeer;
        @Autowired
        WalletPayment walletPayment;

//    private final Map<TransactionType, PaymentsProcessor> strategyMap = new HashMap<>();

//    public PaymentFactory(List<PaymentsProcessor> processorList) {
//        for (PaymentsProcessor processor : processorList) {
//            strategyMap.put(processor.getType(), processor);
//
//        }
//    }


    public PaymentsProcessor getPaymentMethodType(TransactionType transactionType) {

        try {
            TransactionType type =valueOf(transactionType.name().toUpperCase());
            if(type.equals(WALLET)){
                return walletPayment;
            }
            else if (type.equals(PEER)) {
                return peerToPeer;
            } else if (type.equals(DOMESTIC)) {
                return domesticPaymentClass;
            }else if (type.equals(INTERNATIONAL))
            {
                return internationalPayment;
            }
        }catch (IllegalArgumentException e){
                e.printStackTrace();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
