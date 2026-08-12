package com.movetrackops.moveentitlement.repository;

import com.movetrackops.moveentitlement.entity.MoveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoveRequestRepository extends JpaRepository<MoveRequest, Long> {
}