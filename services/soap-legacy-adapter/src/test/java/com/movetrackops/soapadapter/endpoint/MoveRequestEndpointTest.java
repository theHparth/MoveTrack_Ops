package com.movetrackops.soapadapter.endpoint;

import com.movetrack.soap.moverequest.SubmitMoveRequest;
import com.movetrack.soap.moverequest.SubmitMoveRequestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoveRequestEndpointTest {

    @Mock
    private RestTemplate restTemplate;

    private MoveRequestEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpoint = new MoveRequestEndpoint(restTemplate);
    }

    @Test
    void submitMoveRequest_translatesAndReturnsRestResponse() throws Exception {
        SubmitMoveRequest request = new SubmitMoveRequest();
        request.setServiceMemberName("Jane Doe");
        request.setRank("E5");
        request.setOriginBase("Fort Bragg");
        request.setDestinationBase("Fort Hood");
        request.setMoveDate(DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(new GregorianCalendar(2026, 8, 1)));

        Map<String, Object> restResponse = new HashMap<>();
        restResponse.put("id", 42L);
        restResponse.put("entitlementLbs", 7000);
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(restResponse);

        SubmitMoveRequestResponse result = endpoint.submitMoveRequest(request);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getEntitlementLbs()).isEqualTo(7000);
    }
}
