package com.movetrackops.moveentitlement.service;

import com.movetrackops.moveentitlement.entity.MoveRequest;
import com.movetrackops.moveentitlement.entity.Rank;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import com.movetrackops.moveentitlement.repository.MoveRequestRepository;
import com.movetrackops.moveentitlement.repository.WeightAllowanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntitlementCalculationServiceTest {

    @Mock
    private MoveRequestRepository moveRequestRepository;

    @Mock
    private WeightAllowanceRepository weightAllowanceRepository;

    private EntitlementCalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new EntitlementCalculationService(moveRequestRepository, weightAllowanceRepository);
    }

    @Test
    void calculateEntitlement_returnsWeightAllowanceForRank() {
        MoveRequest request = new MoveRequest();
        request.setId(1L);
        request.setRank(Rank.E5);

        WeightAllowance allowance = new WeightAllowance();
        allowance.setRank(Rank.E5);
        allowance.setMaxWeightLbs(7000);

        when(moveRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(weightAllowanceRepository.findByRank(Rank.E5)).thenReturn(Optional.of(allowance));

        assertThat(calculationService.calculateEntitlement(1L)).isEqualTo(7000);
    }

    @Test
    void calculateEntitlement_moveRequestNotFound_throws() {
        when(moveRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calculationService.calculateEntitlement(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}