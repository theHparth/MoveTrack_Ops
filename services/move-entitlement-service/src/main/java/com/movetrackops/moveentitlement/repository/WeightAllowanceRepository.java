package com.movetrackops.moveentitlement.repository;

import com.movetrackops.moveentitlement.entity.Rank;
import com.movetrackops.moveentitlement.entity.WeightAllowance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeightAllowanceRepository extends JpaRepository<WeightAllowance, Long> {
    Optional<WeightAllowance> findByRank(Rank rank);
}