package org.orcid.integration.blackbox.api.v3.tests;

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
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.rc1.BlackBoxBaseV3_0_rc1;
import org.orcid.integration.blackbox.api.v3.rc1.MemberV3Rc1ApiClientImpl;
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
public class OtherNamesTest extends BlackBoxBaseV3_0_rc1 {
    
    @Resource(name = "memberV3_0_rc1ApiClient")
    private MemberV3Rc1ApiClientImpl memberV3Rc1ApiClient;
    
    @Resource(name = "publicV3_0_rc1ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClient;
    
    private static String otherName1 = "other-name-1-" + System.currentTimeMillis();
    private static String otherName2 = "other-name-2-" + System.currentTimeMillis();    
    
    private static org.orcid.jaxb.model.v3.rc1.common.Visibility currentDefaultVisibility = null;
    private static org.orcid.jaxb.model.v3.rc1.common.Visibility currentOtherNamesVisibility = null;


    @BeforeClass
    public static void setup() {
        signin();
        openEditOtherNamesModal();
        deleteOtherNames();
        createOtherName(otherName1);
        createOtherName(otherName2);
        changeOtherNamesVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC.name());
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

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility v) {
        if (!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v.name(), false);
            currentDefaultVisibility = v;
        }
    }

    private static void changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility v) {
        if(!v.equals(currentOtherNamesVisibility)) {
            showMyOrcidPage();
            openEditOtherNamesModal();
            changeOtherNamesVisibility(v.name());
            saveOtherNamesModal();            
            currentOtherNamesVisibility = v;
        }
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC); 

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.v3.rc1.record.OtherName newOtherName = new org.orcid.jaxb.model.v3.rc1.record.OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);

        // Create
        ClientResponse response = memberV3Rc1ApiClient.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV3Rc1ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.rc1.record.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.v3.rc1.record.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.v3.rc1.record.OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if (newOtherName.getContent().equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV3Rc1ApiClient.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.v3.rc1.record.OtherName otherName = response.getEntity(org.orcid.jaxb.model.v3.rc1.record.OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.v3.rc1.common.Visibility originalVisibility = otherName.getVisibility();
        org.orcid.jaxb.model.v3.rc1.common.Visibility updatedVisibility = org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV3Rc1ApiClient.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.v3.rc1.error.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.v3.rc1.error.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV3Rc1ApiClient.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV3Rc1ApiClient.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(org.orcid.jaxb.model.v3.rc1.record.OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV3Rc1ApiClient.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }    

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.v3.rc1.record.OtherName otherName = new org.orcid.jaxb.model.v3.rc1.record.OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV3Rc1ApiClient.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    /**
     * --------- -- -- -- All -- -- -- ---------
     * 
     */
    /**
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetOtherNamesWithPublicAPI() throws InterruptedException, JSONException {
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC);
        
        ClientResponse response = publicV3ApiClient.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.rc1.record.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.v3.rc1.record.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertFalse(otherNames.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.v3.rc1.record.OtherName otherName : otherNames.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
    }
    
    /**
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetOtherNamesWithMembersAPI() throws InterruptedException, JSONException {
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);
                
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        ClientResponse getResponse = memberV3Rc1ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.v3.rc1.record.OtherNames otherNames = getResponse.getEntity(org.orcid.jaxb.model.v3.rc1.record.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.v3.rc1.record.OtherName otherName : otherNames.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));
    }
}
