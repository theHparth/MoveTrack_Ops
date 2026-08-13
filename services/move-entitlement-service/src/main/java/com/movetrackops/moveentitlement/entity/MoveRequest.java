package com.movetrackops.moveentitlement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "move_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceMemberName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rank rank;

    @Column(nullable = false)
    private String originBase;

    @Column(nullable = false)
    private String destinationBase;

    @Column(nullable = false)
    private LocalDate moveDate;
}