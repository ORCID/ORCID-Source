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
package org.orcid.integration.blackbox.api.v3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.dev1.BlackBoxBaseV3_0_dev1;
import org.orcid.integration.blackbox.api.v3.dev1.MemberV3Dev1ApiClientImpl;
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
public class ResearcherUrlsTest extends BlackBoxBaseV3_0_dev1 {
    
    @Resource(name = "memberV3_0_dev1ApiClient")
    private MemberV3Dev1ApiClientImpl memberV3Dev1ApiClient;
    
    @Resource(name = "publicV3_0_dev1ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClient;

    private static String researcherUrl1 = "http://test.orcid.org/1/" + System.currentTimeMillis();

    @BeforeClass
    public static void setup() {
        signin();
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        createResearcherUrl(researcherUrl1);
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveResearcherUrlsModal();
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name(), false);
    }

    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        saveResearcherUrlsModal();
        signout();
    }

    
    @Test
    public void testCreateGetUpdateAndDeleteResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url(url));
        rUrlToCreate.setUrlName(url);

        // Create
        ClientResponse postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_dev1/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV3Dev1ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl.class);
        assertNotNull(gotResearcherUrl);
        assertNotNull(gotResearcherUrl.getPutCode());
        assertNotNull(gotResearcherUrl.getSource());
        assertNotNull(gotResearcherUrl.getCreatedDate());
        assertNotNull(gotResearcherUrl.getLastModifiedDate());
        assertEquals(getClient1ClientId(), gotResearcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://test.orcid.org/test/" + time, gotResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time, gotResearcherUrl.getUrlName());
        assertEquals("public", gotResearcherUrl.getVisibility().value());
        assertNotNull(gotResearcherUrl.getDisplayIndex());
        Long originalDisplayIndex = gotResearcherUrl.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.v3.dev1.common.Visibility originalVisibility = gotResearcherUrl.getVisibility();
        org.orcid.jaxb.model.v3.dev1.common.Visibility updatedVisibility = org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE.equals(originalVisibility)
                ? org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED : org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE;

        // Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV3Dev1ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);

        // Update
        org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV3Dev1ApiClient.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());

        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV3Dev1ApiClient.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String location = postResponse.getHeaders().getFirst("Location");
        Long putCode1 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Add it again
        postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(),
                getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        location = postResponse.getHeaders().getFirst("Location");
        Long putCode2 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Delete both
        ClientResponse deleteResponse = memberV3Dev1ApiClient.deleteResearcherUrl(getUser1OrcidId(), putCode1, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        deleteResponse = memberV3Dev1ApiClient.deleteResearcherUrl(getUser1OrcidId(), putCode2, otherClientToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        String _2001Chars = new String();
        for (int i = 0; i < 2001; i++) {
            _2001Chars += "a";
        }

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url(_2001Chars));
        postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_2001Chars);
        postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrlName("The name");
        postResponse = memberV3Dev1ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Read it to delete it
        ClientResponse getResponse = memberV3Dev1ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl.class);
        ClientResponse deleteResponse = memberV3Dev1ApiClient.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.v3.dev1.common.Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);
        rUrlToCreate.setPutCode(1234567890L);

        ClientResponse response = memberV3Dev1ApiClient.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    /**
     * --------- -- -- -- ALL -- -- -- ---------
     * 
     */
    @Test
    public void testGetWithPublicAPI() throws InterruptedException, JSONException {
        boolean found = false;

        ClientResponse getAllResponse = publicV3ApiClient.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls researcherUrls = getAllResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        for (org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, rUrl.getVisibility());
            if (researcherUrl1.equals(rUrl.getUrl().getValue())) {
                found = true;
            }
        }
        
        assertTrue(found);

        // SET ALL TO LIMITED
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED.name());
        saveResearcherUrlsModal();

        getAllResponse = publicV3ApiClient.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        researcherUrls = getAllResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertTrue(researcherUrls.getResearcherUrls().isEmpty());
        
        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveResearcherUrlsModal();
    }

    @Test
    public void testGetWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        // SET THEM ALL TO LIMITED
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED.name());
        saveResearcherUrlsModal();

        ClientResponse getAllResponse = memberV3Dev1ApiClient.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls researcherUrls = getAllResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(1, researcherUrls.getResearcherUrls().size());
        assertNotNull(researcherUrls.getResearcherUrls().get(0).getUrl());
        assertEquals(researcherUrl1, researcherUrls.getResearcherUrls().get(0).getUrl().getValue());

        // SET THEM ALL TO PRIVATE
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE.name());
        saveResearcherUrlsModal();

        getAllResponse = memberV3Dev1ApiClient.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        researcherUrls = getAllResponse.getEntity(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertTrue(researcherUrls.getResearcherUrls().isEmpty());

        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveResearcherUrlsModal();
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED));
    }
}
