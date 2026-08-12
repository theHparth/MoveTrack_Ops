package com.movetrackops.claims.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "claims.approval")
@Getter
@Setter
public class ClaimsApprovalProperties {
    private BigDecimal supervisorThreshold;
}