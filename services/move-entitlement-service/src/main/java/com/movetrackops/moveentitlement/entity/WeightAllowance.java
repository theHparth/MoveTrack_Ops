package com.movetrackops.moveentitlement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "weight_allowances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeightAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Rank rank;

    @Column(nullable = false)
    private Integer maxWeightLbs;
}