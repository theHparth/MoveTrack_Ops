package com.movetrackops.soapadapter.endpoint;

import com.movetrack.soap.moverequest.SubmitMoveRequest;
import com.movetrack.soap.moverequest.SubmitMoveRequestResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class MoveRequestEndpoint {

    private static final String NAMESPACE_URI = "http://movetrack.com/soap/moverequest";
    private static final String MOVE_ENTITLEMENT_URL = "http://move-entitlement-service:8082/api/move-requests";

    private final RestTemplate restTemplate;

    public MoveRequestEndpoint(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "submitMoveRequest")
    @ResponsePayload
    public SubmitMoveRequestResponse submitMoveRequest(@RequestPayload SubmitMoveRequest request) {
        Map<String, Object> dto = toRestDto(request);

        @SuppressWarnings("unchecked")
        Map<String, Object> restResponse = restTemplate.postForObject(MOVE_ENTITLEMENT_URL, dto, Map.class);

        return toSoapResponse(restResponse);
    }

    private Map<String, Object> toRestDto(SubmitMoveRequest request) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("serviceMemberName", request.getServiceMemberName());
        dto.put("rank", request.getRank());
        dto.put("originBase", request.getOriginBase());
        dto.put("destinationBase", request.getDestinationBase());
        dto.put("moveDate", request.getMoveDate().toXMLFormat());
        return dto;
    }

    private SubmitMoveRequestResponse toSoapResponse(Map<String, Object> restResponse) {
        SubmitMoveRequestResponse response = new SubmitMoveRequestResponse();
        response.setId(Long.parseLong(restResponse.get("id").toString()));
        response.setEntitlementLbs(Integer.parseInt(restResponse.get("entitlementLbs").toString()));
        return response;
    }
}
