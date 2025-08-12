package com.practice.demopractice.entity;

import com.practice.demopractice.service.impl.process.payment.PeerToPeer;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value; // ✅


import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "fees")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeConfig {

    @Value("${wallet}")
    private BigDecimal wallet;

    @Value("${domestic}")
    private BigDecimal domestic;

    @Value("${international}")
    private BigDecimal international;

    @Value("${peerToPeer}")
    private BigDecimal peerToPeer;

    public BigDecimal getWallet() { return wallet; }
    public BigDecimal getDomestic() { return domestic; }
    public BigDecimal getInternational() { return international; }
    public BigDecimal getPeerToPeer() { return peerToPeer; }
}

