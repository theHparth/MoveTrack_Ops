package com.movetrackops.moveentitlement.service;

import com.movetrackops.moveentitlement.entity.ChangeControlRecord;
import com.movetrackops.moveentitlement.entity.Rank;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import com.movetrackops.moveentitlement.repository.ChangeControlRecordRepository;
import com.movetrackops.moveentitlement.repository.WeightAllowanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeightAllowanceChangeServiceTest {

    @Mock
    private WeightAllowanceRepository weightAllowanceRepository;

    @Mock
    private ChangeControlRecordRepository changeControlRecordRepository;

    private WeightAllowanceChangeService changeService;

    @BeforeEach
    void setUp() {
        changeService = new WeightAllowanceChangeService(weightAllowanceRepository, changeControlRecordRepository);
    }

    @Test
    void updateWeightAllowance_logsChangeAndUpdatesValue() {
        WeightAllowance allowance = new WeightAllowance();
        allowance.setRank(Rank.E5);
        allowance.setMaxWeightLbs(7000);

        when(weightAllowanceRepository.findByRank(Rank.E5)).thenReturn(Optional.of(allowance));
        when(weightAllowanceRepository.save(any(WeightAllowance.class))).thenAnswer(inv -> inv.getArgument(0));

        WeightAllowance result = changeService.updateWeightAllowance(Rank.E5, 7500, "admin");

        assertThat(result.getMaxWeightLbs()).isEqualTo(7500);

        ArgumentCaptor<ChangeControlRecord> captor = ArgumentCaptor.forClass(ChangeControlRecord.class);
        verify(changeControlRecordRepository).save(captor.capture());
        assertThat(captor.getValue().getOldValue()).isEqualTo("7000");
        assertThat(captor.getValue().getNewValue()).isEqualTo("7500");
    }
}