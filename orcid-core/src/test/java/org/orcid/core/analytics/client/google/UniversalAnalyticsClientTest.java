package org.orcid.core.analytics.client.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.core.analytics.AnalyticsData;
import org.springframework.test.util.ReflectionTestUtils;
import org.yaml.snakeyaml.util.UriEncoder;

public class UniversalAnalyticsClientTest {
    
    @Test
    public void testSendAnalyticsData() {
        UniversalAnalyticsClient client = new UniversalAnalyticsClientStub();
        ReflectionTestUtils.setField(client, "analyticsTrackingCode", "some-tracking-code");
        AnalyticsData data = getData();
        client.sendAnalyticsData(data);
        String payload = ((UniversalAnalyticsClientStub) client).getPayload();
        assertNotNull(payload);
        
        String[] params = UriEncoder.decode(payload).split("&");
        assertEquals(14, params.length);
        assertEquals(UniversalAnalyticsClient.PROTOCOL_VERSION_PARAM + "=1", params[0]);
        assertEquals(UniversalAnalyticsClient.TRACKING_ID_PARAM + "=some-tracking-code", params[1]);
        assertEquals(UniversalAnalyticsClient.CLIENT_ID_PARAM + "=" + data.getClientId(), params[2]);
        assertEquals(UniversalAnalyticsClient.IP_ADDRESS_PARAM + "=" + data.getIpAddress(), params[3]);
        assertEquals(UniversalAnalyticsClient.USER_AGENT_PARAM + "=" + data.getUserAgent(), params[4]);
        assertEquals(UniversalAnalyticsClient.HIT_TYPE_PARAM + "=event", params[5]);
        assertEquals(UniversalAnalyticsClient.EVENT_ACTION_PARAM + "=" + data.getMethod(), params[6]);
        assertEquals(UniversalAnalyticsClient.EVENT_CATEGORY_PARAM + "=" + data.getCategory(), params[7]);
        assertEquals(UniversalAnalyticsClient.API_VERSION_PARAM + "=" + data.getApiVersion(), params[8]);
        assertEquals(UniversalAnalyticsClient.CONTENT_TYPE_PARAM + "=" + data.getContentType(), params[9]);
        assertEquals(UniversalAnalyticsClient.RESPONSE_CODE_PARAM + "=" + data.getResponseCode(), params[10]);
        assertEquals(UniversalAnalyticsClient.CLIENT_PARAM + "=" + data.getClientDetailsString(), params[11]);
        assertEquals(UniversalAnalyticsClient.SESSION_CONTROL_PARAM + "=" + UniversalAnalyticsClient.SESSION_CONTROL_VALUE, params[12]);
        assertEquals(UniversalAnalyticsClient.EVENT_LABEL_PARAM + "=" + data.getCategory() + ":" + data.getMethod(), params[13]);
    }
    
    @Test
    public void testSendAnalyticsDataNoTrackingCode() {
        UniversalAnalyticsClient client = new UniversalAnalyticsClientStub();
        AnalyticsData data = getData();
        client.sendAnalyticsData(data);
        String payload = ((UniversalAnalyticsClientStub) client).getPayload();
        assertNull(payload); // didn't get sent
    }
    
    private AnalyticsData getData() {
        AnalyticsData data = new AnalyticsData();
        data.setApiVersion("v2.0");
        data.setCategory("works");
        data.setClientId("client id");
        data.setClientDetailsString("Client name - client id");
        data.setContentType("application/xml");
        data.setUserAgent("blah");
        data.setResponseCode(200);
        data.setIpAddress("37.14.150.0");
        data.setMethod("GET");
        return data;
    }
    
    /**
     * Use this class to get access to the string passed to the postData method, which is what we are testing
     *
     */
    private class UniversalAnalyticsClientStub extends UniversalAnalyticsClient {
        
        private String payload;
        
        @Override
        protected void postData(String payload) {
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }
    }

}
