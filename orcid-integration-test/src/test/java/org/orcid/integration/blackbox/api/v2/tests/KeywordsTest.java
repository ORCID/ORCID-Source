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
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
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
public class KeywordsTest extends BlackBoxBaseV2Release {

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

    private static String keyword1 = "keyword-1-" + System.currentTimeMillis();
    private static String keyword2 = "keyword-2-" + System.currentTimeMillis();

    private static org.orcid.jaxb.model.common_v2.Visibility currentDefaultVisibility = null;
    private static org.orcid.jaxb.model.common_v2.Visibility currentKeywordsVisibility = null;

    @BeforeClass
    public static void setup() {
        signin();
        openEditKeywordsModal();
        deleteKeywords();
        createKeyword(keyword1);
        createKeyword(keyword2);
        changeKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveKeywordsModal(); 
        currentKeywordsVisibility = org.orcid.jaxb.model.common_v2.Visibility.PUBLIC;
    }

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility v) {
        if (!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v);
            currentDefaultVisibility = v;
        }
    }

    private static void changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility v) {
        if(!v.equals(currentKeywordsVisibility)) {
            showMyOrcidPage();
            openEditKeywordsModal();
            changeKeywordsVisibility(v);
            saveKeywordsModal();            
            currentKeywordsVisibility = v;
        }
    }
    
    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */   
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword_rc2() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);        
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc2.Keyword newKeyword = new org.orcid.jaxb.model.record_rc2.Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        // Create
        ClientResponse response = memberV2ApiClient_rc2.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc2.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Keywords keywords = response.getEntity(org.orcid.jaxb.model.record_rc2.Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc2.Keyword existingKeyword : keywords.getKeywords()) {
            if (existingKeyword.getContent().equals(keyword1)) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if (existingKeyword.getContent().equals(keyword2)) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if (existingKeyword.getContent().equals(newKeyword.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc2.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(org.orcid.jaxb.model.record_rc2.Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc2.Visibility originalVisibility = newKeyword.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE;

        // Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc2.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);

        // Update
        newKeyword.setContent("keyword-3-updated");
        response = memberV2ApiClient_rc2.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc2.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.Keyword updatedKeyword = response.getEntity(org.orcid.jaxb.model.record_rc2.Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc2.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc2.Keyword keyword = new org.orcid.jaxb.model.record_rc2.Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        keyword.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc2.updateKeyword(getUser1OrcidId(), keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword_rc3() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc3.Keyword newKeyword = new org.orcid.jaxb.model.record_rc3.Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        // Create
        ClientResponse response = memberV2ApiClient_rc3.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc3.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc3.Keyword existingKeyword : keywords.getKeywords()) {
            if (existingKeyword.getContent().equals(keyword1)) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if (existingKeyword.getContent().equals(keyword2)) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if (existingKeyword.getContent().equals(newKeyword.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc3.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc3.Visibility originalVisibility = newKeyword.getVisibility();
        org.orcid.jaxb.model.common_rc3.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE;

        // Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc3.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc3.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc3.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);

        // Update
        newKeyword.setContent("keyword-3-updated");
        response = memberV2ApiClient_rc3.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc3.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.Keyword updatedKeyword = response.getEntity(Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc3.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc3.Keyword keyword = new org.orcid.jaxb.model.record_rc3.Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
        keyword.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc3.updateKeyword(getUser1OrcidId(), keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword_rc4() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);        
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_rc4.Keyword newKeyword = new org.orcid.jaxb.model.record_rc4.Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        // Create
        ClientResponse response = memberV2ApiClient_rc4.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc4.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Keywords keywords = response.getEntity(org.orcid.jaxb.model.record_rc4.Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_rc4.Keyword existingKeyword : keywords.getKeywords()) {
            if (existingKeyword.getContent().equals(keyword1)) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if (existingKeyword.getContent().equals(keyword2)) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if (existingKeyword.getContent().equals(newKeyword.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_rc4.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(org.orcid.jaxb.model.record_rc4.Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc4.Visibility originalVisibility = newKeyword.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE;

        // Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc4.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc4.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc4.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);

        // Update
        newKeyword.setContent("keyword-3-updated");
        response = memberV2ApiClient_rc4.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc4.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Keyword updatedKeyword = response.getEntity(org.orcid.jaxb.model.record_rc4.Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc4.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc4.Keyword keyword = new org.orcid.jaxb.model.record_rc4.Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        keyword.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc4.updateKeyword(getUser1OrcidId(), keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword_release() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.record_v2.Keyword newKeyword = new org.orcid.jaxb.model.record_v2.Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        // Create
        ClientResponse response = memberV2ApiClient_release.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_release.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Keywords keywords = response.getEntity(org.orcid.jaxb.model.record_v2.Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.record_v2.Keyword existingKeyword : keywords.getKeywords()) {
            if (existingKeyword.getContent().equals(keyword1)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if (existingKeyword.getContent().equals(keyword2)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if (existingKeyword.getContent().equals(newKeyword.getContent())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV2ApiClient_release.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(org.orcid.jaxb.model.record_v2.Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = newKeyword.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility updatedVisibility = org.orcid.jaxb.model.common_v2.Visibility.PRIVATE;

        // Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_release.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);

        // Update
        newKeyword.setContent("keyword-3-updated");
        response = memberV2ApiClient_release.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_release.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.Keyword updatedKeyword = response.getEntity(org.orcid.jaxb.model.record_v2.Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_release.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_v2.Keyword keyword = new org.orcid.jaxb.model.record_v2.Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        keyword.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_release.updateKeyword(getUser1OrcidId(), keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }        
    
    /**
     * --------- -- -- -- All -- -- -- ---------
     * 
     */
    /**
     * PRECONDITIONS: The user should have two public keywords keyword1 and
     * keyword2
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetKeywordWithPublicAPI() throws InterruptedException, JSONException {
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);

        // RC2
        ClientResponse response = publicV2ApiClient_rc2.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Keywords keywordsRc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Keywords.class);
        assertNotNull(keywordsRc2);
        assertNotNull(keywordsRc2.getKeywords());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.record_rc2.Keyword keyword : keywordsRc2.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // RC3
        response = publicV2ApiClient_rc3.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.Keywords keywordsRc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Keywords.class);
        assertNotNull(keywordsRc3);
        assertNotNull(keywordsRc3.getKeywords());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc3.Keyword keyword : keywordsRc3.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // RC4
        response = publicV2ApiClient_rc4.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Keywords keywordsRc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Keywords.class);
        assertNotNull(keywordsRc4);
        assertNotNull(keywordsRc4.getKeywords());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc4.Keyword keyword : keywordsRc4.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Release
        response = publicV2ApiClient_release.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Keywords keywordsV2 = response.getEntity(org.orcid.jaxb.model.record_v2.Keywords.class);
        assertNotNull(keywordsV2);
        assertNotNull(keywordsV2.getKeywords());

        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_v2.Keyword keyword : keywordsV2.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
    }

    /**
     * PRECONDITIONS: The user should have two public keywords keyword1 and
     * keyword2
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetKeywordsWihtMembersAPI() throws InterruptedException, JSONException {        
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        // RC2
        ClientResponse response = memberV2ApiClient_rc2.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Keywords keywordsRc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Keywords.class);
        assertNotNull(keywordsRc2);
        assertNotNull(keywordsRc2.getKeywords());
        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.record_rc2.Keyword keyword : keywordsRc2.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // RC3
        response = memberV2ApiClient_rc3.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.Keywords keywordsRc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Keywords.class);
        assertNotNull(keywordsRc3);
        assertNotNull(keywordsRc3.getKeywords());
        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc3.Keyword keyword : keywordsRc3.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // RC4
        response = memberV2ApiClient_rc4.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Keywords keywordsRc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Keywords.class);
        assertNotNull(keywordsRc4);
        assertNotNull(keywordsRc4.getKeywords());
        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_rc4.Keyword keyword : keywordsRc4.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Release
        response = memberV2ApiClient_release.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Keywords keywordsV2 = response.getEntity(org.orcid.jaxb.model.record_v2.Keywords.class);
        assertNotNull(keywordsV2);
        assertNotNull(keywordsV2.getKeywords());
        // There should be at least two, one public and one limited
        found1 = false;
        found2 = false;

        for (org.orcid.jaxb.model.record_v2.Keyword keyword : keywordsV2.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;

            } else if (keyword.getContent().equals(keyword2)) {
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
