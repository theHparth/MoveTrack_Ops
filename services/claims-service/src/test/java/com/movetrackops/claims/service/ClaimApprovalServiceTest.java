package com.movetrackops.claims.service;

import com.movetrackops.claims.config.ClaimsApprovalProperties;
import com.movetrackops.claims.entity.Claim;
import com.movetrackops.claims.entity.ClaimStatus;
import com.movetrackops.claims.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimApprovalServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    private ClaimApprovalService approvalService;

    @BeforeEach
    void setUp() {
        ClaimsApprovalProperties properties = new ClaimsApprovalProperties();
        properties.setSupervisorThreshold(new BigDecimal("1000.00"));
        approvalService = new ClaimApprovalService(claimRepository, properties);
    }

        private Claim claimWith(Long id, ClaimStatus status, String amount) {
        Claim claim = new Claim();
        claim.setId(id);
        claim.setStatus(status);
        claim.setClaimedAmount(new BigDecimal(amount));
        when(claimRepository.findById(id)).thenReturn(Optional.of(claim));
        return claim;
    }

    private void stubSaveReturnsInput() {
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void adjusterRejects_setsRejected() {
        claimWith(1L, ClaimStatus.ADJUSTER_REVIEW, "500.00");
        stubSaveReturnsInput();
        Claim result = approvalService.recordAdjusterDecision(1L, false);
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.REJECTED);
    }

    @Test
    void adjusterApproves_underThreshold_setsApproved() {
        claimWith(2L, ClaimStatus.ADJUSTER_REVIEW, "500.00");
        stubSaveReturnsInput();
        Claim result = approvalService.recordAdjusterDecision(2L, true);
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.APPROVED);
    }

    @Test
    void adjusterApproves_overThreshold_setsPendingSupervisor() {
        claimWith(3L, ClaimStatus.ADJUSTER_REVIEW, "1500.00");
        stubSaveReturnsInput();
        Claim result = approvalService.recordAdjusterDecision(3L, true);
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.PENDING_SUPERVISOR_APPROVAL);
    }

    @Test
    void adjusterDecision_wrongStatus_throws() {
        claimWith(4L, ClaimStatus.APPROVED, "500.00");
        assertThatThrownBy(() -> approvalService.recordAdjusterDecision(4L, true))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void supervisorApproves_setsApproved() {
        claimWith(5L, ClaimStatus.PENDING_SUPERVISOR_APPROVAL, "1500.00");
        stubSaveReturnsInput();
        Claim result = approvalService.recordSupervisorDecision(5L, true);
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.APPROVED);
    }
}