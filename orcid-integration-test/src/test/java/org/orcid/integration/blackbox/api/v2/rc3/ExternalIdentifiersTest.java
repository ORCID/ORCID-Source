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
package org.orcid.integration.blackbox.api.v2.rc3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.Relationship;
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
public class ExternalIdentifiersTest extends BlackBoxBaseRC3 {    
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    org.orcid.jaxb.model.common_v2.Visibility currentUserVisibility = null;
    
    ArrayList<Long> createdPutCodes = new ArrayList<Long>();
    
    @After
    public void after() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        for(Long putCodeToDelete : createdPutCodes) {
            ClientResponse response = memberV2ApiClient.deleteExternalIdentifier(getUser1OrcidId(), putCodeToDelete, accessToken);
            assertNotNull(response);
            assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }
    
    @Test
    public void testGetExternalIdentifiersWihtMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        String extId1Value = "A-0001" + System.currentTimeMillis();
        String extId2Value = "A-0002" + System.currentTimeMillis();
        
        Long putCode1 = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        Long putCode2 = createExternalIdentifier(extId2Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        
        //Check you can view them
        ClientResponse getResponse = memberV2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(Visibility.LIMITED, e.getVisibility());
                assertEquals(putCode2, e.getPutCode());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);                       
    }
            
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier() throws InterruptedException, JSONException {        
        //Get access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);                

        String extId1Value = "A-0003" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        PersonExternalIdentifier externalIdentifier = response.getEntity(PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = externalIdentifier.getVisibility();
        Visibility updatedVisibility = Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new Url("http://ext-id/" + updatedValue));
        response = memberV2ApiClient.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV2ApiClient.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        String extId1Value = "A-0001" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
                
        ClientResponse response = publicV2ApiClient.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifiers externalIdentifiers = response.getEntity(PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode, e.getPutCode());
                found1 = true;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV2ApiClient.viewExternalIdentifierXML(getUser1OrcidId(), putCode);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifier extId = response.getEntity(PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        PersonExternalIdentifier extId = getExternalIdentifier("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    private Long createExternalIdentifier(String name, org.orcid.jaxb.model.common_v2.Visibility defaultUserVisibility) throws InterruptedException, JSONException {
        //Change user visibility if needed
        if(!defaultUserVisibility.equals(currentUserVisibility)) {
            changeDefaultUserVisibility(webDriver, defaultUserVisibility);
            currentUserVisibility = defaultUserVisibility;
        }
        
        //Get the access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Create the external identifier
        PersonExternalIdentifier extId = getExternalIdentifier(name);                
        ClientResponse response = memberV2ApiClient.createExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        createdPutCodes.add(putCode);
        return putCode;
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));
    }
    
    private PersonExternalIdentifier getExternalIdentifier(String value) {
        PersonExternalIdentifier externalIdentifier = new PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(Visibility.PUBLIC);
        externalIdentifier.setRelationship(Relationship.SELF);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
}
