package com.movetrackops.claims.web;

import com.movetrackops.claims.entity.Claim;
import com.movetrackops.claims.entity.ClaimStatus;
import com.movetrackops.claims.service.ClaimApprovalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimController.class)
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimApprovalService approvalService;

    @Test
    void fileClaim_returnsCreatedClaim() throws Exception {
        Claim saved = new Claim();
        saved.setId(10L);
        saved.setStatus(ClaimStatus.ADJUSTER_REVIEW);
        saved.setClaimedAmount(new BigDecimal("500.00"));
        saved.setFiledDate(LocalDate.now());

        when(approvalService.submitForAdjusterReview(any(Claim.class))).thenReturn(saved);

        String requestBody = """
                {
                  "moveRequestId": 1,
                  "claimantName": "Jane Doe",
                  "description": "Broken sofa",
                  "claimedAmount": 500.00
                }
                """;

        mockMvc.perform(post("/api/claims")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("ADJUSTER_REVIEW"));
    }

    @Test
    void fileClaim_missingClaimantName_returns400() throws Exception {
        String requestBody = """
                {
                  "moveRequestId": 1,
                  "description": "Broken sofa",
                  "claimedAmount": 500.00
                }
                """;

        mockMvc.perform(post("/api/claims")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getClaim_returnsClaim() throws Exception {
        Claim claim = new Claim();
        claim.setId(20L);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setClaimedAmount(new BigDecimal("300.00"));

        when(approvalService.getClaim(20L)).thenReturn(claim);

        mockMvc.perform(get("/api/claims/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}