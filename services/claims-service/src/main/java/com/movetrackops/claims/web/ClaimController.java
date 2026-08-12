package com.movetrackops.claims.web;

import com.movetrackops.claims.dto.ClaimRequest;
import com.movetrackops.claims.dto.ClaimResponse;
import com.movetrackops.claims.dto.DecisionRequest;
import com.movetrackops.claims.entity.Claim;
import com.movetrackops.claims.service.ClaimApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimApprovalService approvalService;

    public ClaimController(ClaimApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> fileClaim(@Valid @RequestBody ClaimRequest request) {
        Claim claim = new Claim();
        claim.setMoveRequestId(request.getMoveRequestId());
        claim.setClaimantName(request.getClaimantName());
        claim.setDescription(request.getDescription());
        claim.setClaimedAmount(request.getClaimedAmount());
        claim.setFiledDate(LocalDate.now());
        return ResponseEntity.ok(toResponse(approvalService.submitForAdjusterReview(claim)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaim(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(approvalService.getClaim(id)));
    }

    @PostMapping("/{id}/adjuster-decision")
    public ResponseEntity<ClaimResponse> adjusterDecision(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(toResponse(approvalService.recordAdjusterDecision(id, request.getApproved())));
    }

    @PostMapping("/{id}/supervisor-decision")
    public ResponseEntity<ClaimResponse> supervisorDecision(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(toResponse(approvalService.recordSupervisorDecision(id, request.getApproved())));
    }

    private ClaimResponse toResponse(Claim claim) {
        ClaimResponse response = new ClaimResponse();
        response.setId(claim.getId());
        response.setMoveRequestId(claim.getMoveRequestId());
        response.setClaimantName(claim.getClaimantName());
        response.setDescription(claim.getDescription());
        response.setClaimedAmount(claim.getClaimedAmount());
        response.setStatus(claim.getStatus());
        response.setFiledDate(claim.getFiledDate());
        return response;
    }
}