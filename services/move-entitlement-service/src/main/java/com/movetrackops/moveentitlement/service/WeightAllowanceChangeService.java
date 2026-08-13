package com.movetrackops.moveentitlement.service;

import com.movetrackops.moveentitlement.entity.ChangeControlRecord;
import com.movetrackops.moveentitlement.entity.Rank;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import com.movetrackops.moveentitlement.repository.ChangeControlRecordRepository;
import com.movetrackops.moveentitlement.repository.WeightAllowanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WeightAllowanceChangeService {

    private final WeightAllowanceRepository weightAllowanceRepository;
    private final ChangeControlRecordRepository changeControlRecordRepository;

    public WeightAllowanceChangeService(WeightAllowanceRepository weightAllowanceRepository, ChangeControlRecordRepository changeControlRecordRepository) {
        this.weightAllowanceRepository = weightAllowanceRepository;
        this.changeControlRecordRepository = changeControlRecordRepository;
    }

    public WeightAllowance updateWeightAllowance(Rank rank, int newMaxWeightLbs, String changedBy) {
        WeightAllowance allowance = weightAllowanceRepository.findByRank(rank)
                .orElseThrow(() -> new IllegalArgumentException("No weight allowance configured for rank: " + rank));

        int oldWeight = allowance.getMaxWeightLbs();

        ChangeControlRecord record = new ChangeControlRecord();
        record.setFieldChanged("weightAllowance.maxWeightLbs[" + rank + "]");
        record.setOldValue(String.valueOf(oldWeight));
        record.setNewValue(String.valueOf(newMaxWeightLbs));
        record.setChangedBy(changedBy);
        record.setChangedAt(LocalDateTime.now());
        changeControlRecordRepository.save(record);

        allowance.setMaxWeightLbs(newMaxWeightLbs);
        return weightAllowanceRepository.save(allowance);
    }
}