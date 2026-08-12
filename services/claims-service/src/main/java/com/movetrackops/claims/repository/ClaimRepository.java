package com.movetrackops.claims.repository;

import com.movetrackops.claims.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}