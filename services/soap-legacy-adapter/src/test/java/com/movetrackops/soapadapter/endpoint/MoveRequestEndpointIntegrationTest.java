package com.movetrackops.soapadapter.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@SpringBootTest
class MoveRequestEndpointIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private RestTemplate restTemplate;

    private MockWebServiceClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    void submitMoveRequest_returnsWellFormedSoapResponse() throws Exception {
        Map<String, Object> restResponse = new HashMap<>();
        restResponse.put("id", 42L);
        restResponse.put("entitlementLbs", 7000);
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(restResponse);

        StringSource requestPayload = new StringSource(
            "<submitMoveRequest xmlns='http://movetrack.com/soap/moverequest'>" +
            "  <serviceMemberName>Jane Doe</serviceMemberName>" +
            "  <rank>E5</rank>" +
            "  <originBase>Fort Bragg</originBase>" +
            "  <destinationBase>Fort Hood</destinationBase>" +
            "  <moveDate>2026-09-01</moveDate>" +
            "</submitMoveRequest>");

        StringSource expectedResponse = new StringSource(
            "<submitMoveRequestResponse xmlns='http://movetrack.com/soap/moverequest'>" +
            "  <id>42</id>" +
            "  <entitlementLbs>7000</entitlementLbs>" +
            "</submitMoveRequestResponse>");

        mockClient.sendRequest(withPayload(requestPayload))
            .andExpect(payload(expectedResponse));
    }
}
