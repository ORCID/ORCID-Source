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
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
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
public class ExternalIdentifiersTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
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
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getResponse = memberV2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());
        
        boolean foundPublic = false;
        boolean foundLimited = false;
        
        for(PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifier()) {
            if("A-0001".equals(e.getType())) {
                assertEquals("A-0001", e.getValue());
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                foundPublic = true;
            } else {
                assertEquals("A-0002", e.getValue());
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
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);        
        PersonExternalIdentifier externalIdentifier = getExternalIdentifier(); 
        
        //Create
        ClientResponse response = memberV2ApiClient.createExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get and verify
        response = memberV2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifier());
        assertEquals(3, ExternalIdentifiers.getExternalIdentifier().size());
        
        boolean haveOld1 = false;
        boolean haveOld2 = false;
        boolean haveNew = false;
        
        for(PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifier()) {
            if("A-0001".equals(e.getType())) {
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                haveOld1 = true;
            } else if("A-0002".equals(e.getType())) {
                assertEquals(Visibility.LIMITED, e.getVisibility());
                haveOld2 = true;
            } else {
                assertEquals("A-0003", e.getType());
                assertEquals("A-0003", e.getValue());
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
        response = memberV2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        externalIdentifier = response.getEntity(PersonExternalIdentifier.class);
        assertEquals("A-0003", externalIdentifier.getType());
        assertEquals("A-0003", externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/A-0003", externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        
        //Update it
        externalIdentifier.setType("A-0004");
        externalIdentifier.setValue("A-0004");
        externalIdentifier.setUrl(new Url("http://ext-id/A-0004"));
        response = memberV2ApiClient.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals("A-0004", externalIdentifier.getType());
        assertEquals("A-0004", externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/A-0004", externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        
        //Delete
        //Get access token to delete the external identifier
        String deleteAccessToken = super.getAccessToken(ScopePathType.ORCID_BIO_UPDATE.value(), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        response = memberV2ApiClient.deleteExternalIdentifier(getUser1OrcidId(), putCode, deleteAccessToken);
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
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getResponse = publicV2ApiClient.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        assertEquals("A-0001", externalIdentifiers.getExternalIdentifier().get(0).getType());
        assertEquals("A-0001", externalIdentifiers.getExternalIdentifier().get(0).getValue());
        assertEquals("http://ext-id/A-0001", externalIdentifiers.getExternalIdentifier().get(0).getUrl().getValue());
        
        Long putCode = externalIdentifiers.getExternalIdentifier().get(0).getPutCode();
        getResponse = publicV2ApiClient.viewExternalIdentifierXML(getUser1OrcidId(), putCode);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PersonExternalIdentifier extId = getResponse.getEntity(PersonExternalIdentifier.class);
        assertEquals("A-0001", extId.getType());
        assertEquals("A-0001", extId.getValue());
        assertEquals("http://ext-id/A-0001", extId.getUrl().getValue());
        assertEquals(putCode, extId.getPutCode());
        
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        
        PersonExternalIdentifier extId = getExternalIdentifier();       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
    
    private PersonExternalIdentifier getExternalIdentifier() {
        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml", PersonExternalIdentifier.class);
        assertNotNull(externalIdentifier);
        assertEquals("A-0003", externalIdentifier.getType());
        assertEquals("A-0003", externalIdentifier.getValue());
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
