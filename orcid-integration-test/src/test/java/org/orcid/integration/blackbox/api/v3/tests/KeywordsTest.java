package org.orcid.integration.blackbox.api.v3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
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
public class KeywordsTest extends BlackBoxBaseV3_0_dev1 {

    @Resource(name = "memberV3_0_dev1ApiClient")
    private MemberV3Dev1ApiClientImpl memberV3Dev1ApiClient;
    
    @Resource(name = "publicV3_0_dev1ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClientImpl;

    private static String keyword1 = "keyword-1-" + System.currentTimeMillis();
    private static String keyword2 = "keyword-2-" + System.currentTimeMillis();

    private static org.orcid.jaxb.model.v3.dev1.common.Visibility currentDefaultVisibility = null;
    private static org.orcid.jaxb.model.v3.dev1.common.Visibility currentKeywordsVisibility = null;

    @BeforeClass
    public static void setup() {
        signin();
        openEditKeywordsModal();
        deleteKeywords();
        createKeyword(keyword1);
        createKeyword(keyword2);
        changeKeywordsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveKeywordsModal(); 
        currentKeywordsVisibility = org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC;
    }

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility v) {
        if (!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v.name(), false);
            currentDefaultVisibility = v;
        }
    }

    private static void changeCurrentKeywordsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility v) {
        if(!v.equals(currentKeywordsVisibility)) {
            showMyOrcidPage();
            openEditKeywordsModal();
            changeKeywordsVisibility(v.name());
            saveKeywordsModal();            
            currentKeywordsVisibility = v;
        }
    }
    
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED);
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        org.orcid.jaxb.model.v3.dev1.record.Keyword newKeyword = new org.orcid.jaxb.model.v3.dev1.record.Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);
        // Create
        ClientResponse response = memberV3Dev1ApiClient.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV3Dev1ApiClient.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Keywords keywords = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());

        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;

        for (org.orcid.jaxb.model.v3.dev1.record.Keyword existingKeyword : keywords.getKeywords()) {
            if (existingKeyword.getContent().equals(keyword1)) {
                assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if (existingKeyword.getContent().equals(keyword2)) {
                assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if (existingKeyword.getContent().equals(newKeyword.getContent())) {
                assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);

        // Get it
        response = memberV3Dev1ApiClient.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.v3.dev1.common.Visibility originalVisibility = newKeyword.getVisibility();
        org.orcid.jaxb.model.v3.dev1.common.Visibility updatedVisibility = org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE;

        // Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV3Dev1ApiClient.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.v3.dev1.error.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.v3.dev1.error.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);

        // Update
        newKeyword.setContent("keyword-3-updated");
        response = memberV3Dev1ApiClient.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV3Dev1ApiClient.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.v3.dev1.record.Keyword updatedKeyword = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());

        // Delete
        response = memberV3Dev1ApiClient.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.v3.dev1.record.Keyword keyword = new org.orcid.jaxb.model.v3.dev1.record.Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);
        keyword.setPutCode(1234567890L);

        ClientResponse response = memberV3Dev1ApiClient.updateKeyword(getUser1OrcidId(), keyword, accessToken);
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
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC);

        
        ClientResponse response = publicV3ApiClientImpl.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Keywords keywordsV2_1 = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Keywords.class);
        assertNotNull(keywordsV2_1);
        assertNotNull(keywordsV2_1.getKeywords());

        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.v3.dev1.record.Keyword keyword : keywordsV2_1.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, keyword.getVisibility());
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
        changeCurrentKeywordsVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED);        

        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        ClientResponse response = memberV3Dev1ApiClient.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Keywords keywordsV2_1 = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Keywords.class);
        assertNotNull(keywordsV2_1);
        assertNotNull(keywordsV2_1.getKeywords());
        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (org.orcid.jaxb.model.v3.dev1.record.Keyword keyword : keywordsV2_1.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, keyword.getVisibility());
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