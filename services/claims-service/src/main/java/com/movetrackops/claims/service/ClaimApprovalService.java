package com.movetrackops.claims.service;

import com.movetrackops.claims.config.ClaimsApprovalProperties;
import com.movetrackops.claims.entity.Claim;
import com.movetrackops.claims.entity.ClaimStatus;
import com.movetrackops.claims.repository.ClaimRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimApprovalService {

    private final ClaimRepository claimRepository;
    private final ClaimsApprovalProperties approvalProperties;

    public ClaimApprovalService(ClaimRepository claimRepository, ClaimsApprovalProperties approvalProperties) {
        this.claimRepository = claimRepository;
        this.approvalProperties = approvalProperties;
    }

    public Claim submitForAdjusterReview(Claim claim) {
        claim.setStatus(ClaimStatus.ADJUSTER_REVIEW);
        return claimRepository.save(claim);
    }

    public Claim recordAdjusterDecision(Long claimId, boolean approved) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));

        if (claim.getStatus() != ClaimStatus.ADJUSTER_REVIEW) {
            throw new IllegalStateException("Claim is not awaiting adjuster review: " + claimId);
        }

        if (!approved) {
            claim.setStatus(ClaimStatus.REJECTED);
            return claimRepository.save(claim);
        }

        if (claim.getClaimedAmount().compareTo(approvalProperties.getSupervisorThreshold()) > 0) {
            claim.setStatus(ClaimStatus.PENDING_SUPERVISOR_APPROVAL);
        } else {
            claim.setStatus(ClaimStatus.APPROVED);
        }
        return claimRepository.save(claim);
    }

    public Claim recordSupervisorDecision(Long claimId, boolean approved) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));

        if (claim.getStatus() != ClaimStatus.PENDING_SUPERVISOR_APPROVAL) {
            throw new IllegalStateException("Claim is not awaiting supervisor approval: " + claimId);
        }

        claim.setStatus(approved ? ClaimStatus.APPROVED : ClaimStatus.REJECTED);
        return claimRepository.save(claim);
    }
}