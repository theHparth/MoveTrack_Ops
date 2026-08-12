package com.movetrackops.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClaimRequest {

    @NotNull
    private Long moveRequestId;

    @NotBlank
    private String claimantName;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal claimedAmount;
}