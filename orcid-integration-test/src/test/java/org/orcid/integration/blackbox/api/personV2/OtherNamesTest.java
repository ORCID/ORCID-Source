/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.api.personV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class OtherNamesTest extends BlackBoxBase {
    
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;            
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;    
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    
    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    /**
     * PRECONDITIONS: 
     *          The user should have just one other name "Other name" which should be public
     * @throws JSONException 
     * @throws InterruptedException 
     * */
    @Test
    public void testGetOtherNamesWihtMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse getResponse = memberV2ApiClient.viewOtherNames(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        OtherNames otherNames = getResponse.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(1, otherNames.getOtherNames().size());
        assertEquals("Other name", otherNames.getOtherNames().get(0).getContent());
        assertEquals(Visibility.PUBLIC, otherNames.getOtherNames().get(0).getVisibility());
    }
    
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);        
        OtherName otherName = getOtherName(); 
        
        //Create
        ClientResponse response = memberV2ApiClient.createOtherName(this.user1OrcidId, otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get and verify
        response = memberV2ApiClient.viewOtherNames(user1OrcidId, accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherNames otherNames = response.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(2, otherNames.getOtherNames().size());
        
        boolean haveOld = false;
        boolean haveNew = false;
        
        for(OtherName existingOtherName : otherNames.getOtherNames()) {
            if("Other name".equals(existingOtherName.getContent())) {
                assertEquals(Visibility.PUBLIC, existingOtherName.getVisibility());
                haveOld = true;
            } else {
                assertEquals("Other Name #1", existingOtherName.getContent());
                assertEquals(Visibility.LIMITED, existingOtherName.getVisibility());
                haveNew = true;
            }
        }
        
        assertTrue(haveOld);
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient.viewOtherName(this.user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        
        //Update it
        otherName.setContent("Other Name #1 - Updated");        
        response = memberV2ApiClient.updateOtherName(this.user1OrcidId, otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewOtherName(this.user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());        
        
        //Delete
        response = memberV2ApiClient.deleteOtherName(this.client1ClientId, putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was actually deleted
        testGetOtherNamesWihtMembersAPI();
    }
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
    
    private OtherName getOtherName() {
        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc2/samples/other-name-2.0_rc2.xml", OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1", otherName.getContent());
        otherName.setVisibility(Visibility.LIMITED);
        otherName.setSource(null);        
        otherName.setPath(null);
        otherName.setLastModifiedDate(null);
        otherName.setCreatedDate(null);
        otherName.setPutCode(null);
        return otherName;
    }
    
}
