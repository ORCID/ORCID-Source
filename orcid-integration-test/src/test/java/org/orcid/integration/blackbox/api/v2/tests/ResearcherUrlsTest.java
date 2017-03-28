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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

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
public class ResearcherUrlsTest extends BlackBoxBaseV2Release {
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

    private static String researcherUrl1 = "http://test.orcid.org/1/" + System.currentTimeMillis();

    @BeforeClass
    public static void setup() {
        signin();
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        createResearcherUrl(researcherUrl1);
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveResearcherUrlsModal();
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
        saveResearcherUrlsModal();
        signout();
    }

    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    @Test
    public void testCreateGetUpdateAndDeleteResearcherUrl_rc2() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url(url));
        rUrlToCreate.setUrlName(url);

        // Create
        ClientResponse postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0_rc2/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient_rc2.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc2.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrl.class);
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
        org.orcid.jaxb.model.common_rc2.Visibility originalVisibility = gotResearcherUrl.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE.equals(originalVisibility)
                ? org.orcid.jaxb.model.common_rc2.Visibility.LIMITED : org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE;

        // Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc2.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);

        // Update
        org.orcid.jaxb.model.common_rc2.LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient_rc2.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());

        updatedResearcherUrlResponse = memberV2ApiClient_rc2.getResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);

        org.orcid.jaxb.model.record_rc2.ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());

        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl_rc2() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        org.orcid.jaxb.model.record_rc2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String location = postResponse.getHeaders().getFirst("Location");
        Long putCode1 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Add it again
        postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(),
                getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        location = postResponse.getHeaders().getFirst("Location");
        Long putCode2 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Delete both
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode1, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode2, otherClientToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls_rc2() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        String _351Chars = new String();
        for (int i = 0; i < 531; i++) {
            _351Chars += "a";
        }

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url(_351Chars));
        postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient_rc2.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient_rc2.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc2.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrl.class);
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc2.Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        rUrlToCreate.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc2.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @Test
    public void testCreateGetUpdateAndDeleteResearcherUrl_rc3() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc3.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url(url));
        rUrlToCreate.setUrlName(url);

        // Create
        ClientResponse postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0_rc3/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient_rc3.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc3.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrl.class);
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
        org.orcid.jaxb.model.common_rc3.Visibility originalVisibility = gotResearcherUrl.getVisibility();
        org.orcid.jaxb.model.common_rc3.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE.equals(originalVisibility)
                ? org.orcid.jaxb.model.common_rc3.Visibility.LIMITED : org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE;

        // Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc3.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc3.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc3.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);

        // Update
        org.orcid.jaxb.model.common_rc3.LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient_rc3.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());
        org.orcid.jaxb.model.record_rc3.ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());

        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient_rc3.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl_rc3() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        org.orcid.jaxb.model.record_rc3.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String location = postResponse.getHeaders().getFirst("Location");
        Long putCode1 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Add it again
        postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(),
                getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        location = postResponse.getHeaders().getFirst("Location");
        Long putCode2 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Delete both
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode1, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode2, otherClientToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls_rc3() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc3.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        String _351Chars = new String();
        for (int i = 0; i < 531; i++) {
            _351Chars += "a";
        }

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url(_351Chars));
        postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient_rc3.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient_rc3.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc3.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrl.class);
        ClientResponse deleteResponse = memberV2ApiClient_rc3.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc3.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc3.Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        rUrlToCreate.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc3.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    @Test
    public void testCreateGetUpdateAndDeleteResearcherUrl_rc4() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc4.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc4.ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url(url));
        rUrlToCreate.setUrlName(url);

        // Create
        ClientResponse postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath,
                locationPath.matches(".*/v2.0_rc4/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient_rc4.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc4.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrl.class);
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
        org.orcid.jaxb.model.common_rc4.Visibility originalVisibility = gotResearcherUrl.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE.equals(originalVisibility)
                ? org.orcid.jaxb.model.common_rc4.Visibility.LIMITED : org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE;

        // Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc4.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc4.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc4.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);

        // Update
        org.orcid.jaxb.model.common_rc4.LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient_rc4.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());
        org.orcid.jaxb.model.record_rc4.ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());

        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient_rc4.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl_rc4() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        org.orcid.jaxb.model.record_rc4.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc4.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String location = postResponse.getHeaders().getFirst("Location");
        Long putCode1 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Add it again
        postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(),
                getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        location = postResponse.getHeaders().getFirst("Location");
        Long putCode2 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Delete both
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode1, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode2, otherClientToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls_rc4() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc4.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc4.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        String _351Chars = new String();
        for (int i = 0; i < 531; i++) {
            _351Chars += "a";
        }

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url(_351Chars));
        postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient_rc4.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient_rc4.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_rc4.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrl.class);
        ClientResponse deleteResponse = memberV2ApiClient_rc4.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc4.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_rc4.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_rc4.Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        rUrlToCreate.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc4.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */
    @Test
    public void testCreateGetUpdateAndDeleteResearcherUrl_release() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_v2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_v2.ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url(url));
        rUrlToCreate.setUrlName(url);

        // Create
        ClientResponse postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient_release.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_v2.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrl.class);
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
        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = gotResearcherUrl.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility updatedVisibility = org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.equals(originalVisibility)
                ? org.orcid.jaxb.model.common_v2.Visibility.LIMITED : org.orcid.jaxb.model.common_v2.Visibility.PRIVATE;

        // Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_release.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);

        // Update
        org.orcid.jaxb.model.common_v2.LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient_release.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());
        org.orcid.jaxb.model.record_v2.ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());

        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient_release.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl_release() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        org.orcid.jaxb.model.record_v2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_v2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String location = postResponse.getHeaders().getFirst("Location");
        Long putCode1 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Add it again
        postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(),
                getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        location = postResponse.getHeaders().getFirst("Location");
        Long putCode2 = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Delete both
        ClientResponse deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode1, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        deleteResponse = memberV2ApiClient_rc2.deleteResearcherUrl(getUser1OrcidId(), putCode2, otherClientToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls_release() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_v2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_v2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        String _351Chars = new String();
        for (int i = 0; i < 531; i++) {
            _351Chars += "a";
        }

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url(_351Chars));
        postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());

        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient_release.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient_release.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        org.orcid.jaxb.model.record_v2.ResearcherUrl gotResearcherUrl = getResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrl.class);
        ClientResponse deleteResponse = memberV2ApiClient_release.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_v2.ResearcherUrl rUrlToCreate = new org.orcid.jaxb.model.record_v2.ResearcherUrl();
        rUrlToCreate.setUrl(new org.orcid.jaxb.model.common_v2.Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        rUrlToCreate.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_release.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- ALL -- -- -- ---------
     * 
     */
    @Test
    public void testGetWithPublicAPI() throws InterruptedException, JSONException {
        // RC2
        boolean found = false;

        ClientResponse getAllResponse = publicV2ApiClient_rc2.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc2.ResearcherUrls researcherUrls_rc2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc2);
        assertNotNull(researcherUrls_rc2.getResearcherUrls());
        for (org.orcid.jaxb.model.record_rc2.ResearcherUrl rUrl : researcherUrls_rc2.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, rUrl.getVisibility());
            if (researcherUrl1.equals(rUrl.getUrl().getValue())) {
                found = true;
            }
        }

        assertTrue(found);

        // RC3
        found = false;

        getAllResponse = publicV2ApiClient_rc3.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc3.ResearcherUrls researcherUrls_rc3 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc3);
        assertNotNull(researcherUrls_rc3.getResearcherUrls());
        for (org.orcid.jaxb.model.record_rc3.ResearcherUrl rUrl : researcherUrls_rc3.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, rUrl.getVisibility());
            if (researcherUrl1.equals(rUrl.getUrl().getValue())) {
                found = true;
            }
        }

        assertTrue(found);

        // RC4
        found = false;

        getAllResponse = publicV2ApiClient_rc4.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc4.ResearcherUrls researcherUrls_rc4 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc4);
        assertNotNull(researcherUrls_rc4.getResearcherUrls());
        for (org.orcid.jaxb.model.record_rc4.ResearcherUrl rUrl : researcherUrls_rc4.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, rUrl.getVisibility());
            if (researcherUrl1.equals(rUrl.getUrl().getValue())) {
                found = true;
            }
        }

        assertTrue(found);

        // Release
        found = false;

        getAllResponse = publicV2ApiClient_release.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_v2.ResearcherUrls researcherUrls_v2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrls.class);
        assertNotNull(researcherUrls_v2);
        assertNotNull(researcherUrls_v2.getResearcherUrls());
        for (org.orcid.jaxb.model.record_v2.ResearcherUrl rUrl : researcherUrls_v2.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, rUrl.getVisibility());
            if (researcherUrl1.equals(rUrl.getUrl().getValue())) {
                found = true;
            }
        }

        assertTrue(found);

        // SET ALL TO LIMITED
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        saveResearcherUrlsModal();

        // RC2
        getAllResponse = publicV2ApiClient_rc2.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        researcherUrls_rc2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc2);
        assertNull(researcherUrls_rc2.getResearcherUrls());

        // RC3
        getAllResponse = publicV2ApiClient_rc3.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        researcherUrls_rc3 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc3);
        assertNull(researcherUrls_rc3.getResearcherUrls());

        // RC4
        getAllResponse = publicV2ApiClient_rc4.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        researcherUrls_rc4 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc4);
        assertNotNull(researcherUrls_rc4.getResearcherUrls());
        assertTrue(researcherUrls_rc4.getResearcherUrls().isEmpty());

        // Release
        getAllResponse = publicV2ApiClient_release.viewResearcherUrlsXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        researcherUrls_v2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrls.class);
        assertNotNull(researcherUrls_v2);
        assertNotNull(researcherUrls_v2.getResearcherUrls());
        assertTrue(researcherUrls_v2.getResearcherUrls().isEmpty());

        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveResearcherUrlsModal();
    }

    @Test
    public void testGetWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        // SET THEM ALL TO LIMITED
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        saveResearcherUrlsModal();

        // RC2
        ClientResponse getAllResponse = memberV2ApiClient_rc2.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc2.ResearcherUrls researcherUrls_rc2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc2);
        assertNotNull(researcherUrls_rc2.getResearcherUrls());
        assertEquals(1, researcherUrls_rc2.getResearcherUrls().size());
        assertNotNull(researcherUrls_rc2.getResearcherUrls().get(0).getUrl());
        assertEquals(researcherUrl1, researcherUrls_rc2.getResearcherUrls().get(0).getUrl().getValue());

        // RC3
        getAllResponse = memberV2ApiClient_rc3.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc3.ResearcherUrls researcherUrls_rc3 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc3);
        assertNotNull(researcherUrls_rc3.getResearcherUrls());
        assertEquals(1, researcherUrls_rc3.getResearcherUrls().size());
        assertNotNull(researcherUrls_rc3.getResearcherUrls().get(0).getUrl());
        assertEquals(researcherUrl1, researcherUrls_rc3.getResearcherUrls().get(0).getUrl().getValue());

        // RC4
        getAllResponse = memberV2ApiClient_rc4.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc4.ResearcherUrls researcherUrls_rc4 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc4);
        assertNotNull(researcherUrls_rc4.getResearcherUrls());
        assertEquals(1, researcherUrls_rc4.getResearcherUrls().size());
        assertNotNull(researcherUrls_rc4.getResearcherUrls().get(0).getUrl());
        assertEquals(researcherUrl1, researcherUrls_rc4.getResearcherUrls().get(0).getUrl().getValue());

        // Release
        getAllResponse = memberV2ApiClient_release.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_v2.ResearcherUrls researcherUrls_v2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrls.class);
        assertNotNull(researcherUrls_v2);
        assertNotNull(researcherUrls_v2.getResearcherUrls());
        assertEquals(1, researcherUrls_v2.getResearcherUrls().size());
        assertNotNull(researcherUrls_v2.getResearcherUrls().get(0).getUrl());
        assertEquals(researcherUrl1, researcherUrls_v2.getResearcherUrls().get(0).getUrl().getValue());

        // SET THEM ALL TO PRIVATE
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        saveResearcherUrlsModal();

        // RC2
        getAllResponse = memberV2ApiClient_rc2.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        researcherUrls_rc2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc2);
        assertNull(researcherUrls_rc2.getResearcherUrls());
        
        // RC3
        getAllResponse = memberV2ApiClient_rc3.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        researcherUrls_rc3 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc3);
        assertNull(researcherUrls_rc3.getResearcherUrls());
        
        // RC4
        getAllResponse = memberV2ApiClient_rc4.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        researcherUrls_rc4 = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.ResearcherUrls.class);
        assertNotNull(researcherUrls_rc4);
        assertNotNull(researcherUrls_rc4.getResearcherUrls());
        assertTrue(researcherUrls_rc4.getResearcherUrls().isEmpty());

        // Release
        getAllResponse = memberV2ApiClient_release.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        researcherUrls_v2 = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.ResearcherUrls.class);
        assertNotNull(researcherUrls_v2);
        assertNotNull(researcherUrls_v2.getResearcherUrls());
        assertTrue(researcherUrls_v2.getResearcherUrls().isEmpty());

        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveResearcherUrlsModal();
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED));
    }
}
