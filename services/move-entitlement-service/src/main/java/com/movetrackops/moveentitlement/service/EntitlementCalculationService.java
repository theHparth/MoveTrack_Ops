package com.movetrackops.moveentitlement.service;

import com.movetrackops.moveentitlement.entity.MoveRequest;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import com.movetrackops.moveentitlement.repository.MoveRequestRepository;
import com.movetrackops.moveentitlement.repository.WeightAllowanceRepository;
import org.springframework.stereotype.Service;

@Service
public class EntitlementCalculationService {

    private final MoveRequestRepository moveRequestRepository;
    private final WeightAllowanceRepository weightAllowanceRepository;

    public EntitlementCalculationService(MoveRequestRepository moveRequestRepository, WeightAllowanceRepository weightAllowanceRepository) {
        this.moveRequestRepository = moveRequestRepository;
        this.weightAllowanceRepository = weightAllowanceRepository;
    }

    public MoveRequest submitMoveRequest(MoveRequest request) {
        return moveRequestRepository.save(request);
    }

    public int calculateEntitlement(Long moveRequestId) {
        MoveRequest request = moveRequestRepository.findById(moveRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Move request not found: " + moveRequestId));

        WeightAllowance allowance = weightAllowanceRepository.findByRank(request.getRank())
                .orElseThrow(() -> new IllegalStateException("No weight allowance configured for rank: " + request.getRank()));

        return allowance.getMaxWeightLbs();
    }
}