package com.movetrackops.moveentitlement.service;

import com.movetrackops.moveentitlement.config.RabbitMQConfig;
import com.movetrackops.moveentitlement.entity.MoveRequest;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import com.movetrackops.moveentitlement.repository.MoveRequestRepository;
import com.movetrackops.moveentitlement.repository.WeightAllowanceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EntitlementCalculationService {

    private final MoveRequestRepository moveRequestRepository;
    private final WeightAllowanceRepository weightAllowanceRepository;
    private final RabbitTemplate rabbitTemplate;

    public EntitlementCalculationService(MoveRequestRepository moveRequestRepository,
                                          WeightAllowanceRepository weightAllowanceRepository,
                                          RabbitTemplate rabbitTemplate) {
        this.moveRequestRepository = moveRequestRepository;
        this.weightAllowanceRepository = weightAllowanceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public MoveRequest submitMoveRequest(MoveRequest request) {
        MoveRequest saved = moveRequestRepository.save(request);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "move.created", saved);
        return saved;
    }

    public int calculateEntitlement(Long moveRequestId) {
        MoveRequest request = moveRequestRepository.findById(moveRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Move request not found: " + moveRequestId));

        WeightAllowance allowance = weightAllowanceRepository.findByRank(request.getRank())
                .orElseThrow(() -> new IllegalStateException("No weight allowance configured for rank: " + request.getRank()));

        return allowance.getMaxWeightLbs();
    }
}