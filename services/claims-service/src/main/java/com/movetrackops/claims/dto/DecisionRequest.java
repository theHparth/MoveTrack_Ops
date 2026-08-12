package com.movetrackops.claims.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionRequest {
    @NotNull
    private Boolean approved;
}