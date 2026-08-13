package com.movetrackops.moveentitlement.dto;

import com.movetrackops.moveentitlement.entity.Rank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MoveRequestResponse {
    private Long id;
    private String serviceMemberName;
    private Rank rank;
    private String originBase;
    private String destinationBase;
    private LocalDate moveDate;
    private Integer entitlementLbs;
}