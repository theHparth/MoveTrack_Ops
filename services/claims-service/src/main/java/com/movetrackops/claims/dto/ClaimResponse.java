package com.movetrackops.claims.dto;

import com.movetrackops.claims.entity.ClaimStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ClaimResponse {
    private Long id;
    private Long moveRequestId;
    private String claimantName;
    private String description;
    private BigDecimal claimedAmount;
    private ClaimStatus status;
    private LocalDate filedDate;
}