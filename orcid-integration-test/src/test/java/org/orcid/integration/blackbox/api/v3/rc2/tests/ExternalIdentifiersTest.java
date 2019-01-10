package org.orcid.integration.blackbox.api.v3.rc2.tests;

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
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.rc2.BlackBoxBaseV3_0_rc2;
import org.orcid.integration.blackbox.api.v3.rc2.MemberV3Rc2ApiClientImpl;
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
public class ExternalIdentifiersTest extends BlackBoxBaseV3_0_rc2 {    
    
    @Resource(name = "memberV3_0_rc2ApiClient")
    private MemberV3Rc2ApiClientImpl memberV3Rc2ApiClient;
    
    @Resource(name = "publicV3_0_rc2ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClientImpl;
    
    org.orcid.jaxb.model.v3.rc2.common.Visibility currentUserVisibility = null;
    
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
        putCode1 = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC);
        putCode2 = createExternalIdentifier(extId2Value, org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);
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
    
    @Test
    public void testGetExternalIdentifiersWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Check you can view the external identifiers
        ClientResponse getResponse = memberV3Rc2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers externalIdentifiers = getResponse.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, e.getVisibility());
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
            } else if(extId2Value.equals(e.getType())) {
                assertEquals(extId2Value, e.getValue());
                assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, e.getVisibility());
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
        Long putCode = createExternalIdentifier(extId1Value, org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);               

        //Get and verify
        ClientResponse response = memberV3Rc2ApiClient.viewExternalIdentifiers(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers ExternalIdentifiers = response.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers.class);
        assertNotNull(ExternalIdentifiers);
        assertNotNull(ExternalIdentifiers.getExternalIdentifiers());        
        
        boolean haveNew = false;
        
        for(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier e : ExternalIdentifiers.getExternalIdentifiers()) {
            if(extId1Value.equals(e.getType())) {                
                assertEquals(extId1Value, e.getType());
                assertEquals(extId1Value, e.getValue());
                assertNotNull(e.getUrl());
                assertEquals("http://ext-id/" + extId1Value, e.getUrl().getValue());
                assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, e.getVisibility());
                assertEquals("APP-9999999999999901", e.getSource().retrieveSourcePath());
                assertEquals("Client APP-9999999999999901 - Fastest's Elephant", e.getSource().getSourceName().getContent());
                assertEquals(putCode, e.getPutCode());
                haveNew = true;
            }
        }
        
        assertTrue(haveNew);
        
        //Get it
        response = memberV3Rc2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier externalIdentifier = response.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier.class);
        assertEquals(extId1Value, externalIdentifier.getType());
        assertEquals(extId1Value, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + extId1Value, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());
        assertNotNull(externalIdentifier.getDisplayIndex());
        Long originalDisplayIndex = externalIdentifier.getDisplayIndex();
        
        //Save the original visibility
        org.orcid.jaxb.model.v3.rc2.common.Visibility originalVisibility = externalIdentifier.getVisibility();
        org.orcid.jaxb.model.v3.rc2.common.Visibility updatedVisibility = org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC;
        
        //Verify you can't update the visibility
        externalIdentifier.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc2ApiClient.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.v3.rc2.error.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.v3.rc2.error.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        externalIdentifier.setVisibility(originalVisibility);
        
        //Update it
        String updatedValue = "A-0004" + System.currentTimeMillis();
        externalIdentifier.setType(updatedValue);
        externalIdentifier.setValue(updatedValue);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://ext-id/" + updatedValue));
        response = memberV3Rc2ApiClient.updateExternalIdentifier(getUser1OrcidId(), externalIdentifier, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV3Rc2ApiClient.viewExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(updatedValue, externalIdentifier.getType());
        assertEquals(updatedValue, externalIdentifier.getValue());
        assertNotNull(externalIdentifier.getUrl());
        assertEquals("http://ext-id/" + updatedValue, externalIdentifier.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, externalIdentifier.getVisibility());
        assertEquals(putCode, externalIdentifier.getPutCode());       
        assertEquals(originalDisplayIndex, externalIdentifier.getDisplayIndex());
        
        //Delete it
        response = memberV3Rc2ApiClient.deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());   
        
        //Remove the put code from the list of elements to delete
        createdPutCodes.remove(putCode);
    }
    
    @Test
    public void testGetExternalIdentifiersWithPublicAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse response = publicV3ApiClientImpl.viewExternalIdentifiersXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers externalIdentifiers = response.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers.class);
        
        boolean found1 = false;
        
        for(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier e : externalIdentifiers.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC, e.getVisibility());
            if(extId1Value.equals(e.getType())) {
                assertEquals(extId1Value, e.getValue());                
                assertEquals(putCode1, e.getPutCode());
                found1 = true;
                break;
            } 
        }
        
        assertTrue(found1);
        
        response = publicV3ApiClientImpl.viewExternalIdentifierXML(getUser1OrcidId(), putCode1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier extId = response.getEntity(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier.class);
        assertEquals(extId1Value, extId.getType());
        assertEquals(extId1Value, extId.getValue());
        assertEquals("http://ext-id/" + extId1Value, extId.getUrl().getValue());
        assertEquals(putCode1, extId.getPutCode());        
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier extId = getExternalIdentifier_release("A-0004" + System.currentTimeMillis());       
        extId.setPutCode(1234567890L);
        
        ClientResponse response = memberV3Rc2ApiClient.updateExternalIdentifier(getUser1OrcidId(), extId, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    private Long createExternalIdentifier(String name, org.orcid.jaxb.model.v3.rc2.common.Visibility defaultUserVisibility) throws InterruptedException, JSONException {
        //Change user visibility if needed
        if(!defaultUserVisibility.equals(currentUserVisibility)) {
            changeDefaultUserVisibility(webDriver, defaultUserVisibility.name(), false);
            currentUserVisibility = defaultUserVisibility;
        }
        
        //Get the access token
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Create the external identifier
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier extId = getExternalIdentifier_release(name);                
        ClientResponse response = memberV3Rc2ApiClient.createExternalIdentifier(getUser1OrcidId(), extId, accessToken);
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
    
    private org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier getExternalIdentifier_release(String value) {
        org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier externalIdentifier = new org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier();
        externalIdentifier.setType(value);
        externalIdentifier.setValue(value);
        externalIdentifier.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://ext-id/" + value));        
        externalIdentifier.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC);
        externalIdentifier.setRelationship(org.orcid.jaxb.model.v3.rc2.record.Relationship.SELF);
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
