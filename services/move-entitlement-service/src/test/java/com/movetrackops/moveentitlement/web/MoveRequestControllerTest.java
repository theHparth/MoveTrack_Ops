package com.movetrackops.moveentitlement.web;

import com.movetrackops.moveentitlement.entity.MoveRequest;
import com.movetrackops.moveentitlement.entity.Rank;
import com.movetrackops.moveentitlement.service.EntitlementCalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MoveRequestController.class)
class MoveRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntitlementCalculationService calculationService;

    @Test
    void submitMoveRequest_returnsEntitlement() throws Exception {
        MoveRequest saved = new MoveRequest();
        saved.setId(1L);
        saved.setRank(Rank.E5);

        when(calculationService.submitMoveRequest(any(MoveRequest.class))).thenReturn(saved);
        when(calculationService.calculateEntitlement(1L)).thenReturn(7000);

        String requestBody = """
                {
                  "serviceMemberName": "Jane Doe",
                  "rank": "E5",
                  "originBase": "Fort Bragg",
                  "destinationBase": "Fort Hood",
                  "moveDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/move-requests")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitlementLbs").value(7000));
    }

    @Test
    void submitMoveRequest_missingServiceMemberName_returns400() throws Exception {
        String requestBody = """
                {
                  "rank": "E5",
                  "originBase": "Fort Bragg",
                  "destinationBase": "Fort Hood",
                  "moveDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/move-requests")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}