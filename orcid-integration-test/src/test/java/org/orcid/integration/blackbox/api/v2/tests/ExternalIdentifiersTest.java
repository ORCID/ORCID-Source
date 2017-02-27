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
package org.orcid.integration.blackbox.api.v2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class ExternalIdentifiersTest extends BlackBoxBaseV2Release {    
    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;

    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;

    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;
    
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient_release;
    
    org.orcid.jaxb.model.common_v2.Visibility currentUserVisibility = null;
    
    ArrayList<Long> createdPutCodes = new ArrayList<Long>();
    
    private static boolean allSet = false;
    
    private static String extId1Value = "A-0001-" + System.currentTimeMillis();
    private static String extId2Value = "A-0002-" + System.currentTimeMillis();
    
    private static Long putCode1 = null;
    private static Long putCode2 = null;
    
    @Before
    public void before() throws InterruptedException, JSONException {
        if(allSet) {
            return;
        }
        signin();
        showMyOrcidPage();
        if(hasExternalIdentifiers()) {
            openEditExternalIdentifiersModal();
            deleteExternalIdentifiers();
            saveExternalIdentifiersModal();            
        }
        putCode1 = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        putCode2 = createExternalIdentifier(extId2Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        allSet = true;
    }
    
    @AfterClass
    public static void after() {        
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        deleteExternalIdentifiers();
        saveExternalIdentifiersModal();
        signout();
    }
    
    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    @Test
    public void testGetExternalIdentifiersWihtMembersAPI_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Check you can view the external identifiers
        ClientResponse getResponse = memberV2ApiClient_rc2.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, e.getVisibility());
                assertEquals(putCode2, e.getPutCode());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);                       
    }
            
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier_rc2() throws InterruptedException, JSONException {        
        //Get access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        String extId1Value = "A-0003" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier_rc2(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV2ApiClient_rc2.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient_rc2.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier externalIdentifier = response.getEntity(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        org.orcid.jaxb.model.common_rc2.Visibility originalVisibility = externalIdentifier.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc2.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc2.Url("http://ext-id/" + updatedValue));
        response = memberV2ApiClient_rc2.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc2.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV2ApiClient_rc2.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse response = publicV2ApiClient_rc2.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers externalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, e.getVisibility());
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());                
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
                break;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV2ApiClient_rc2.viewExternalIdentifierXML(getUser1OrcidId(), putCode1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extId = response.getEntity(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode1, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extId = getExternalIdentifier_rc2("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc2.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
        
    @SuppressWarnings({ "rawtypes", "deprecation" })
    private Long createExternalIdentifier_rc2(String name, org.orcid.jaxb.model.common_v2.Visibility defaultUserVisibility) throws InterruptedException, JSONException {
        //Change user visibility if needed
        if(!defaultUserVisibility.equals(currentUserVisibility)) {
            changeDefaultUserVisibility(webDriver, defaultUserVisibility);
            currentUserVisibility = defaultUserVisibility;
        }
      //Get the access token
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));
        assertNotNull(accessToken);
        
        //Create the external identifier
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extId = getExternalIdentifier_rc2(name);                
        ClientResponse response = memberV2ApiClient_rc2.createExternalIdentifier(getUser1OrcidId(), extId, accessToken);
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
    
    private org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier getExternalIdentifier_rc2(String value) {
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier externalIdentifier = new org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc2.Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        externalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc2.Relationship.SELF);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @Test
    public void testGetExternalIdentifiersWihtMembersAPI_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
                
        //Check you can view the external identifiers
        ClientResponse getResponse = memberV2ApiClient_rc3.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, e.getVisibility());
                assertEquals(putCode2, e.getPutCode());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);                       
    }
            
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier_rc3() throws InterruptedException, JSONException {        
        //Get access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);                

        String extId1Value = "A-0003" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV2ApiClient_rc3.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient_rc3.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier externalIdentifier = response.getEntity(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        org.orcid.jaxb.model.common_rc3.Visibility originalVisibility = externalIdentifier.getVisibility();
        org.orcid.jaxb.model.common_rc3.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc3.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc3.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc3.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc3.Url("http://ext-id/" + updatedValue));
        response = memberV2ApiClient_rc3.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc3.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV2ApiClient_rc3.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse response = publicV2ApiClient_rc3.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers externalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, e.getVisibility());
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());                
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
                break;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV2ApiClient_rc3.viewExternalIdentifierXML(getUser1OrcidId(), putCode1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier extId = response.getEntity(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode1, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier extId = getExternalIdentifier_rc3("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc3.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }            
    
    private org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier getExternalIdentifier_rc3(String value) {
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier externalIdentifier = new org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc3.Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        externalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc3.Relationship.SELF);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    @Test
    public void testGetExternalIdentifiersWihtMembersAPI_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Check you can view the external identifiers
        ClientResponse getResponse = memberV2ApiClient_rc4.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, e.getVisibility());
                assertEquals(putCode2, e.getPutCode());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);                       
    }
            
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier_rc4() throws InterruptedException, JSONException {        
        //Get access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);                

        String extId1Value = "A-0003" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV2ApiClient_rc4.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient_rc4.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier externalIdentifier = response.getEntity(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        org.orcid.jaxb.model.common_rc4.Visibility originalVisibility = externalIdentifier.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc4.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc4.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc4.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc4.Url("http://ext-id/" + updatedValue));
        response = memberV2ApiClient_rc4.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc4.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV2ApiClient_rc4.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse response = publicV2ApiClient_rc4.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers externalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, e.getVisibility());
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());                
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
                break;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV2ApiClient_rc4.viewExternalIdentifierXML(getUser1OrcidId(), putCode1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier extId = response.getEntity(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode1, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier extId = getExternalIdentifier_rc4("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc4.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }        
    
    private org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier getExternalIdentifier_rc4(String value) {
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier externalIdentifier = new org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_rc4.Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        externalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc4.Relationship.SELF);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */    
    @Test
    public void testGetExternalIdentifiersWithMembersAPI_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Check you can view the external identifiers
        ClientResponse getResponse = memberV2ApiClient_release.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, e.getVisibility());
                assertEquals(putCode2, e.getPutCode());
                found2 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);                       
    }
            
    @Test
    public void testCreateGetUpdateAndDeleteExternalIdentifier_release() throws InterruptedException, JSONException {        
        //Get access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);                

        String extId1Value = "A-0003" + System.currentTimeMillis();
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV2ApiClient_release.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient_release.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifier externalIdentifier = response.getEntity(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = externalIdentifier.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility updatedVisibility = org.orcid.jaxb.model.common_v2.Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_release.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_v2.Url("http://ext-id/" + updatedValue));
        response = memberV2ApiClient_release.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_release.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV2ApiClient_release.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse response = publicV2ApiClient_release.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers externalIdentifiers = response.getEntity(org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, e.getVisibility());
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());                
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
                break;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV2ApiClient_release.viewExternalIdentifierXML(getUser1OrcidId(), putCode1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifier extId = response.getEntity(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode1, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifier extId = getExternalIdentifier_release("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_release.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
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
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifier extId = getExternalIdentifier_release(name);                
        ClientResponse response = memberV2ApiClient_release.createExternalIdentifier(getUser1OrcidId(), extId, accessToken);
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
    
    private org.orcid.jaxb.model.record_v2.PersonExternalIdentifier getExternalIdentifier_release(String value) {
        org.orcid.jaxb.model.record_v2.PersonExternalIdentifier externalIdentifier = new org.orcid.jaxb.model.record_v2.PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.common_v2.Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        externalIdentifier.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.SELF);
        externalIdentifier.setSource(null);        
        externalIdentifier.setPath(null);
        externalIdentifier.setLastModifiedDate(null);
        externalIdentifier.setCreatedDate(null);
        externalIdentifier.setPutCode(null);
        return externalIdentifier;
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));
    }
    
}
