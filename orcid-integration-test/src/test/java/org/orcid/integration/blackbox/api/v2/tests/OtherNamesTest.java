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
public class OtherNamesTest extends BlackBoxBaseV2Release {
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
    
    private static String otherName1 = "other-name-1-" + System.currentTimeMillis();
    private static String otherName2 = "other-name-2-" + System.currentTimeMillis();    
    
    private static org.orcid.jaxb.model.common_v2.Visibility currentDefaultVisibility = null;
    private static org.orcid.jaxb.model.common_v2.Visibility currentOtherNamesVisibility = null;


    @BeforeClass
    public static void setup() {
        signin();
        openEditOtherNamesModal();
        deleteOtherNames();
        createOtherName(otherName1);
        createOtherName(otherName2);
        changeOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
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

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility v) {
        if (!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v);
            currentDefaultVisibility = v;
        }
    }

    private static void changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility v) {
        if(!v.equals(currentOtherNamesVisibility)) {
            showMyOrcidPage();
            openEditOtherNamesModal();
            changeOtherNamesVisibility(v);
            saveOtherNamesModal();            
            currentOtherNamesVisibility = v;
        }
    }
    
    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName_rc2() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc2.OtherName newOtherName = new org.orcid.jaxb.model.record_rc2.OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED);

        // Create
        ClientResponse response = memberV2ApiClient_rc2.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV2ApiClient_rc2.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.record_rc2.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc2.OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if (newOtherName.getContent().equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc2.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.OtherName otherName = response.getEntity(org.orcid.jaxb.model.record_rc2.OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc2.Visibility originalVisibility = otherName.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc2.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV2ApiClient_rc2.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc2.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(org.orcid.jaxb.model.record_rc2.OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc2.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc2.OtherName otherName = new org.orcid.jaxb.model.record_rc2.OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc2.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName_rc3() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc3.OtherName newOtherName = new org.orcid.jaxb.model.record_rc3.OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);

        // Create
        ClientResponse response = memberV2ApiClient_rc3.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV2ApiClient_rc3.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.record_rc3.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc3.OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if (newOtherName.getContent().equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc3.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.OtherName otherName = response.getEntity(org.orcid.jaxb.model.record_rc3.OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc3.Visibility originalVisibility = otherName.getVisibility();
        org.orcid.jaxb.model.common_rc3.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc3.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc3.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc3.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV2ApiClient_rc3.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc3.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(org.orcid.jaxb.model.record_rc3.OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc3.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc3.OtherName otherName = new org.orcid.jaxb.model.record_rc3.OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc3.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName_rc4() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc4.OtherName newOtherName = new org.orcid.jaxb.model.record_rc4.OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);

        // Create
        ClientResponse response = memberV2ApiClient_rc4.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV2ApiClient_rc4.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.record_rc4.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc4.OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if (newOtherName.getContent().equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc4.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.OtherName otherName = response.getEntity(org.orcid.jaxb.model.record_rc4.OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc4.Visibility originalVisibility = otherName.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc4.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc4.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc4.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV2ApiClient_rc4.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc4.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(org.orcid.jaxb.model.record_rc4.OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc4.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }    

    @Test
    public void testInvalidPutCodeReturns404_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc4.OtherName otherName = new org.orcid.jaxb.model.record_rc4.OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc4.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName_release() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC); 

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_v2.OtherName newOtherName = new org.orcid.jaxb.model.record_v2.OtherName();
        newOtherName.setContent("other-name-3" + System.currentTimeMillis());
        newOtherName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Create
        ClientResponse response = memberV2ApiClient_release.createOtherName(getUser1OrcidId(), newOtherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get and verify
        response = memberV2ApiClient_release.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.OtherNames otherNames = response.getEntity(org.orcid.jaxb.model.record_v2.OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_v2.OtherName existingOtherName : otherNames.getOtherNames()) {
            if (otherName1.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, existingOtherName.getVisibility());
                found1 = true;
            } else if (otherName2.equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, existingOtherName.getVisibility());
                found2 = true;
            } else if (newOtherName.getContent().equals(existingOtherName.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, existingOtherName.getVisibility());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_release.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.OtherName otherName = response.getEntity(org.orcid.jaxb.model.record_v2.OtherName.class);
        assertNotNull(otherName);
        assertEquals(newOtherName.getContent(), otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = otherName.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility updatedVisibility = org.orcid.jaxb.model.common_v2.Visibility.PUBLIC;

        // Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_release.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);

        // Update it
        otherName.setContent("Other Name #1 - Updated");
        response = memberV2ApiClient_release.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_release.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(org.orcid.jaxb.model.record_v2.OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1 - Updated", otherName.getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertEquals(originalDisplayIndex, otherName.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_release.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }    

    @Test
    public void testInvalidPutCodeReturns404_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_v2.OtherName otherName = new org.orcid.jaxb.model.record_v2.OtherName();
        otherName.setContent("Other Name #1 " + System.currentTimeMillis());
        otherName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        otherName.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_release.updateOtherName(getUser1OrcidId(), otherName, accessToken);
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
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        
        //RC2
        ClientResponse response = publicV2ApiClient_rc2.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.OtherNames otherNamesRc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.OtherNames.class);
        assertNotNull(otherNamesRc2);
        assertNotNull(otherNamesRc2.getOtherNames());
        assertFalse(otherNamesRc2.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : otherNamesRc2.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //RC3
        response = publicV2ApiClient_rc3.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.OtherNames otherNamesRc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.OtherNames.class);
        assertNotNull(otherNamesRc3);
        assertNotNull(otherNamesRc3.getOtherNames());
        assertFalse(otherNamesRc3.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : otherNamesRc3.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //RC4
        response = publicV2ApiClient_rc4.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.OtherNames otherNamesRc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.OtherNames.class);
        assertNotNull(otherNamesRc4);
        assertNotNull(otherNamesRc4.getOtherNames());
        assertFalse(otherNamesRc4.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : otherNamesRc4.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //Release      
        response = publicV2ApiClient_release.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.OtherNames otherNamesV2 = response.getEntity(org.orcid.jaxb.model.record_v2.OtherNames.class);
        assertNotNull(otherNamesV2);
        assertNotNull(otherNamesV2.getOtherNames());
        assertFalse(otherNamesV2.getOtherNames().isEmpty());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_v2.OtherName otherName : otherNamesV2.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, otherName.getVisibility());
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
        changeCurrentOtherNamesVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
                
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        //Rc2
        ClientResponse getResponse = memberV2ApiClient_rc2.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc2.OtherNames otherNamesRc2 = getResponse.getEntity(org.orcid.jaxb.model.record_rc2.OtherNames.class);
        assertNotNull(otherNamesRc2);
        assertNotNull(otherNamesRc2.getOtherNames());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.record_rc2.OtherName otherName : otherNamesRc2.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //Rc3
        getResponse = memberV2ApiClient_rc3.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc3.OtherNames otherNamesRc3 = getResponse.getEntity(org.orcid.jaxb.model.record_rc3.OtherNames.class);
        assertNotNull(otherNamesRc3);
        assertNotNull(otherNamesRc3.getOtherNames());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc3.OtherName otherName : otherNamesRc3.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //Rc4
        getResponse = memberV2ApiClient_rc4.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc4.OtherNames otherNamesRc4 = getResponse.getEntity(org.orcid.jaxb.model.record_rc4.OtherNames.class);
        assertNotNull(otherNamesRc4);
        assertNotNull(otherNamesRc4.getOtherNames());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc4.OtherName otherName : otherNamesRc4.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, otherName.getVisibility());
            if (otherName.getContent().equals(otherName1)) {
                found1 = true;

            } else if (otherName.getContent().equals(otherName2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        
        //Release
        getResponse = memberV2ApiClient_rc2.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_v2.OtherNames otherNamesV2 = getResponse.getEntity(org.orcid.jaxb.model.record_v2.OtherNames.class);
        assertNotNull(otherNamesV2);
        assertNotNull(otherNamesV2.getOtherNames());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_v2.OtherName otherName : otherNamesV2.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, otherName.getVisibility());
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
