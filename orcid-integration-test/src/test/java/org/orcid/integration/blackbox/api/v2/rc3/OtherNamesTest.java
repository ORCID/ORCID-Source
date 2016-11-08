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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
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
public class OtherNamesTest extends BlackBoxBaseRC3 {
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient;

    private static String otherName1 = "other-name-1-" + System.currentTimeMillis();
    private static String otherName2 = "other-name-2-" + System.currentTimeMillis();
    
    @BeforeClass
    public static void setup(){
        signin();        
        openEditOtherNamesModal();        
        createOtherName(otherName1);
        createOtherName(otherName2);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();                
    }
    
    @AfterClass
    public static void after() {  
        showMyOrcidPage();
        openEditOtherNamesModal(); 
        deleteOtherNames();
        saveOtherNamesModal();
        signout();
    }

    /**
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetOtherNamesWithMembersAPI() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditOtherNamesModal();                
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveOtherNamesModal();                
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse getResponse = memberV2ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        OtherNames otherNames = getResponse.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (OtherName otherName : otherNames.getOtherNames()) {
            assertEquals(Visibility.LIMITED, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
                
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;                
            }
        }

        assertTrue(found1);        
        assertTrue(found2);
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
        showMyOrcidPage();
        openEditOtherNamesModal();       
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        OtherName newOtherName = new OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(Visibility.LIMITED);        

        // Create
        ClientResponse response = memberV2ApiClient.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV2ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherNames otherNames = response.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if(newOtherName.getContent().equals(existingOtherName.getContent())) {                
                assertEquals(Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        OtherName otherName = response.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        Visibility originalVisibility = otherName.getVisibility();
        Visibility updatedVisibility = Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV2ApiClient.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV2ApiClient.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());       
    }

    /**
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetOtherNamesWithPublicAPI() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditOtherNamesModal();       
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveOtherNamesModal();
        
        ClientResponse response = publicV2ApiClient.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherNames otherNames = response.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertFalse(otherNames.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (OtherName otherName : otherNames.getOtherNames()) {
            assertEquals(Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;
                
            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;                
            }
        }

        assertTrue(found1);        
        assertTrue(found2);
    }

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        OtherName otherName = new OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));        
    }
}
