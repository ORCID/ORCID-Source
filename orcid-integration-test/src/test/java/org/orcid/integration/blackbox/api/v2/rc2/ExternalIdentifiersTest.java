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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
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
public class ExternalIdentifiersTest extends BlackBoxBase {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    
    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
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
     *          The user should have two external identifiers: 
     *          1) A-0001 PUBLIC
     *          2) A-0002 LIMITED
     * @throws JSONException 
     * @throws InterruptedException 
     * */
    @Test
    public void testGetExternalIdentifiersWihtMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse getResponse = memberV2ApiClient.viewExternalIdentifiers(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ExternalIdentifiers externalIdentifiers = getResponse.getEntity(ExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());
        
        boolean foundPublic = false;
        boolean foundLimited = false;
        
        for(ExternalIdentifier e : externalIdentifiers.getExternalIdentifier()) {
            if("A-0001".equals(e.getCommonName())) {
                assertEquals("A-0001", e.getReference());
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else {
                assertEquals("A-0002", e.getReference());
                assertEquals(Visibility.LIMITED, e.getVisibility());
                foundLimited = true;
            }
        }
        
        assertTrue(foundPublic);
        assertTrue(foundLimited);
    }
    
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);        
        ExternalIdentifier externalIdentifier = getExternalIdentifier(); 
        
        //Create
        ClientResponse response = memberV2ApiClient.createExternalIdentifier(this.user1OrcidId, externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get and verify
        response = memberV2ApiClient.viewExternalIdentifiers(user1OrcidId, accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ExternalIdentifiers ExternalIdentifiers = response.getEntity(ExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifier());
        assertEquals(3, ExternalIdentifiers.getExternalIdentifier().size());
        
        boolean haveOld1 = false;
        boolean haveOld2 = false;
        boolean haveNew = false;
        
        for(ExternalIdentifier e : ExternalIdentifiers.getExternalIdentifier()) {
            if("A-0001".equals(e.getCommonName())) {
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                haveOld1 = true;
            } else if("A-0002".equals(e.getCommonName())) {
                assertEquals(Visibility.LIMITED, e.getVisibility());
                haveOld2 = true;
            } else {
                assertEquals("A-0003", e.getCommonName());
                assertEquals("A-0003", e.getReference());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/A-0003", e.getUrl().getValue());
                assertEquals(Visibility.LIMITED, e.getVisibility());
                haveNew = true;
            }
        }
        
        assertTrue(haveOld1);
        assertTrue(haveOld2);
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient.viewExternalIdentifier(this.user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        externalIdentifier = response.getEntity(ExternalIdentifier.class);
        assertEquals("A-0003", externalIdentifier.getCommonName());
        assertEquals("A-0003", externalIdentifier.getReference());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/A-0003", externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        
        //Update it
        externalIdentifier.setCommonName("A-0004");
        externalIdentifier.setReference("A-0004");
        externalIdentifier.setUrl(new Url("http://ext-id/A-0004"));
        response = memberV2ApiClient.updateExternalIdentifier(this.user1OrcidId, externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewExternalIdentifier(this.user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        assertEquals("A-0004", externalIdentifier.getCommonName());
        assertEquals("A-0004", externalIdentifier.getReference());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/A-0004", externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        
        //Delete
        response = memberV2ApiClient.deleteExternalIdentifier(this.user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was actually deleted
        testGetExternalIdentifiersWihtMembersAPI();
    }
    
    /**
     * PRECONDITIONS: 
     *          The user should have one public external identifiers: 
     *          1) A-0001 PUBLIC
     * @throws JSONException 
     * @throws InterruptedException 
     * */
    @Test
    public void testGetExternalIdentifiersWihtPublicAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse getResponse = publicV2ApiClient.viewExternalIdentifiersXML(user1OrcidId);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ExternalIdentifiers externalIdentifiers = getResponse.getEntity(ExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        assertEquals("A-0001", externalIdentifiers.getExternalIdentifier().get(0).getCommonName());
        assertEquals("A-0001", externalIdentifiers.getExternalIdentifier().get(0).getReference());
        assertEquals("http://ext-id/A-0001", externalIdentifiers.getExternalIdentifier().get(0).getUrl().getValue());
        
        Long putCode = externalIdentifiers.getExternalIdentifier().get(0).getPutCode();
        getResponse = publicV2ApiClient.viewExternalIdentifierXML(user1OrcidId, putCode);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ExternalIdentifier extId = getResponse.getEntity(ExternalIdentifier.class);
        assertEquals("A-0001", extId.getCommonName());
        assertEquals("A-0001", extId.getReference());
        assertEquals("http://ext-id/A-0001", extId.getUrl().getValue());
        assertEquals(putCode, extId.getPutCode());
        
    }
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
    
    private ExternalIdentifier getExternalIdentifier() {
        ExternalIdentifier externalIdentifier = (ExternalIdentifier) unmarshallFromPath("/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml", ExternalIdentifier.class);
        assertNotNull(externalIdentifier);
        assertEquals("A-0003", externalIdentifier.getCommonName());
        assertEquals("A-0003", externalIdentifier.getReference());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/A-0003", externalIdentifier.getUrl().getValue());        
        externalIdentifier.setVisibility(Visibility.LIMITED);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
}
