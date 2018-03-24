package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV1ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class InvalidEndpointsTest extends BlackBoxBase {
    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2;
    @Resource
    private PublicV1ApiClientImpl publicV1ApiClient;
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient;

    private static String token = null;    

    @Test
    public void member1_2ApiTest() {
        ClientResponse response = t2OAuthClient_1_2.getInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = t2OAuthClient_1_2.postInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = t2OAuthClient_1_2.putInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = t2OAuthClient_1_2.deleteInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void public1_2ApiTest() {
        ClientResponse response = publicV1ApiClient.viewInvalidEndpoint(getUser1OrcidId());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void member2_0ApiTest() {
        ClientResponse response = memberV2ApiClient.viewInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = memberV2ApiClient.postInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = memberV2ApiClient.putInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = memberV2ApiClient.deleteInvalidEndpoint(getUser1OrcidId(), getAccessToken());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void public2_0ApiTest() {
        ClientResponse response = publicV2ApiClient.viewInvalidEndpoint(getUser1OrcidId());
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    private String getAccessToken() {
        if(token == null) {
            try {
                List<String> scopes = new ArrayList<String>();
                scopes.add(ScopePathType.READ_LIMITED.value());
                scopes.add(ScopePathType.ACTIVITIES_UPDATE.value());
                token = getAccessToken(scopes);
            } catch (Exception e) {
                fail();
            }
        }
        
        return token;
    }
}
