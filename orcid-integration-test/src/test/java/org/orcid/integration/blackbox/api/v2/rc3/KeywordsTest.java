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
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class KeywordsTest extends BlackBoxBaseRC3 {
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    private static String keyword1 = "keyword-1-" + System.currentTimeMillis();
    private static String keyword2 = "keyword-2-" + System.currentTimeMillis();
    
    @BeforeClass
    public static void setup(){
        signin();        
        openEditKeywordsModal();        
        createKeyword(keyword1);
        createKeyword(keyword2);
        changeKeywordsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveKeywordsModal();                
    }
    
    @AfterClass
    public static void after() { 
        showMyOrcidPage();
        openEditKeywordsModal(); 
        deleteKeywords();
        saveKeywordsModal();
        signout();
    }

    /**
     * PRECONDITIONS: The user should have two public keywords keyword1
     * and keyword2
     * 
     * @throws JSONException
     * @throws InterruptedException
     */    
    @Test
    public void testGetKeywordsWihtMembersAPI() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditKeywordsModal();       
        changeKeywordsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveKeywordsModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (Keyword keyword : keywords.getKeywords()) {
            assertEquals(Visibility.LIMITED, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;
                
            } else if (keyword.getContent().equals(keyword2)) {
                found2 = true;                
            }
        }

        assertTrue(found1);        
        assertTrue(found2);
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
        showMyOrcidPage();
        openEditKeywordsModal();       
        changeKeywordsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveKeywordsModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Keyword newKeyword = new Keyword();
        newKeyword.setContent("keyword-3");
        newKeyword.setVisibility(Visibility.PUBLIC);
        //Create
        ClientResponse response = memberV2ApiClient.createKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient.viewKeywords(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());             
        
        boolean found1 = false;
        boolean found2 = false;
        boolean foundNew = false;
        
        for(Keyword existingKeyword : keywords.getKeywords()) {            
            if(existingKeyword.getContent().equals(keyword1)) {
                assertEquals(Visibility.PUBLIC, existingKeyword.getVisibility());
                found1 = true;
            } else if(existingKeyword.getContent().equals(keyword2)) {
                assertEquals(Visibility.PUBLIC, existingKeyword.getVisibility());
                found2 = true;
            } else if(existingKeyword.getContent().equals(newKeyword.getContent())){
                assertEquals(Visibility.LIMITED, existingKeyword.getVisibility());
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(getClient1ClientId(), existingKeyword.getSource().retrieveSourcePath());
                foundNew = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(foundNew);
               
        //Get it
        response = memberV2ApiClient.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        newKeyword = response.getEntity(Keyword.class);
        assertNotNull(newKeyword);
        assertNotNull(newKeyword.getSource());
        assertEquals(getClient1ClientId(), newKeyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", newKeyword.getContent());
        assertEquals(Visibility.LIMITED, newKeyword.getVisibility());
        assertNotNull(newKeyword.getDisplayIndex());
        Long originalDisplayIndex = newKeyword.getDisplayIndex(); 
        
        //Save the original visibility
        Visibility originalVisibility = newKeyword.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        newKeyword.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        newKeyword.setVisibility(originalVisibility);
        
        //Update 
        newKeyword.setContent("keyword-3-updated");
        response = memberV2ApiClient.updateKeyword(getUser1OrcidId(), newKeyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        Keyword updatedKeyword = response.getEntity(Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(newKeyword.getPutCode(), updatedKeyword.getPutCode());
        assertEquals(originalDisplayIndex, updatedKeyword.getDisplayIndex());        
        
        //Delete
        response = memberV2ApiClient.deleteKeyword(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());                
    }

    /**
     * PRECONDITIONS: The user should have two public keywords keyword1
     * and keyword2
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetKeywordWithPublicAPI() throws InterruptedException, JSONException {
        openEditKeywordsModal();       
        changeKeywordsVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveKeywordsModal();
        
        ClientResponse response = publicV2ApiClient.viewKeywordsXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        
        // There should be at least two, one public and one limited
        boolean found1 = false;
        boolean found2 = false;

        for (Keyword keyword : keywords.getKeywords()) {
            assertEquals(Visibility.PUBLIC, keyword.getVisibility());
            if (keyword.getContent().equals(keyword1)) {
                found1 = true;
                
            } else if (keyword.getContent().equals(keyword2)) {
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
        
        Keyword keyword = new Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(Visibility.PUBLIC);       
        keyword.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateKeyword(getUser1OrcidId(), keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED));
    }

}
