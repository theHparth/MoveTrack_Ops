package com.movetrackops.moveentitlement.web;

import com.movetrackops.moveentitlement.dto.MoveRequestDto;
import com.movetrackops.moveentitlement.dto.MoveRequestResponse;
import com.movetrackops.moveentitlement.entity.MoveRequest;
import com.movetrackops.moveentitlement.service.EntitlementCalculationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/move-requests")
public class MoveRequestController {

    private final EntitlementCalculationService calculationService;

    public MoveRequestController(EntitlementCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping
    public ResponseEntity<MoveRequestResponse> submitMoveRequest(@Valid @RequestBody MoveRequestDto dto) {
        MoveRequest request = new MoveRequest();
        request.setServiceMemberName(dto.getServiceMemberName());
        request.setRank(dto.getRank());
        request.setOriginBase(dto.getOriginBase());
        request.setDestinationBase(dto.getDestinationBase());
        request.setMoveDate(dto.getMoveDate());

        MoveRequest saved = calculationService.submitMoveRequest(request);
        int entitlement = calculationService.calculateEntitlement(saved.getId());

        return ResponseEntity.ok(toResponse(saved, entitlement));
    }

    private MoveRequestResponse toResponse(MoveRequest request, int entitlement) {
        MoveRequestResponse response = new MoveRequestResponse();
        response.setId(request.getId());
        response.setServiceMemberName(request.getServiceMemberName());
        response.setRank(request.getRank());
        response.setOriginBase(request.getOriginBase());
        response.setDestinationBase(request.getDestinationBase());
        response.setMoveDate(request.getMoveDate());
        response.setEntitlementLbs(entitlement);
        return response;
    }
}