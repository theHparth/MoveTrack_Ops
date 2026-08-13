package com.movetrackops.moveentitlement.dto;

import com.movetrackops.moveentitlement.entity.Rank;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MoveRequestDto {

    @NotBlank
    private String serviceMemberName;

    @NotNull
    private Rank rank;

    @NotBlank
    private String originBase;

    @NotBlank
    private String destinationBase;

    @NotNull
    @FutureOrPresent
    private LocalDate moveDate;
}