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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
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
public class OtherNamesTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    /**
     * PRECONDITIONS: 
     *          The user should have two public other names "other-name-1" and "other-name-2"
     * @throws JSONException 
     * @throws InterruptedException 
     * */    
    @Test
    public void testGetOtherNamesWihtMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ClientResponse getResponse = memberV2ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        OtherNames otherNames = getResponse.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(2, otherNames.getOtherNames().size());
        assertThat(otherNames.getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(otherNames.getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC, otherNames.getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, otherNames.getOtherNames().get(1).getVisibility());
    }    
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteOtherName() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, Visibility.LIMITED);
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);        
        OtherName otherName = getOtherName(); 
        
        //Create
        ClientResponse response = memberV2ApiClient.createOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get and verify
        response = memberV2ApiClient.viewOtherNames(getUser1OrcidId(), accessToken);        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherNames otherNames = response.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(3, otherNames.getOtherNames().size());
        
        boolean haveOld1 = false;
        boolean haveOld2 = false;        
        boolean haveNew = false;
        
        for(OtherName existingOtherName : otherNames.getOtherNames()) {
            if("other-name-1".equals(existingOtherName.getContent())) {
                assertEquals(Visibility.PUBLIC, existingOtherName.getVisibility());
                haveOld1 = true;
            } else if("other-name-2".equals(existingOtherName.getContent())) {
                assertEquals(Visibility.PUBLIC, existingOtherName.getVisibility());
                haveOld2 = true;
            } else {
                assertEquals("Other Name #1", existingOtherName.getContent());
                assertEquals(Visibility.LIMITED, existingOtherName.getVisibility());
                haveNew = true;
            }
        }
        
        assertTrue(haveOld1);
        assertTrue(haveOld2);
        assertTrue(haveNew);
        
        //Get it
        response = memberV2ApiClient.viewOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        otherName = response.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(putCode, otherName.getPutCode());
        assertNotNull(otherName.getDisplayIndex());
        Long originalDisplayIndex = otherName.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = otherName.getVisibility();
        Visibility updatedVisibility = Visibility.PUBLIC;
        
        //Verify you cant update the visibility
        otherName.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateOtherName(this.getUser1OrcidId(), otherName, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        otherName.setVisibility(originalVisibility);
        
        //Update it
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
        
        //Delete
        response = memberV2ApiClient.deleteOtherName(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was actually deleted
        testGetOtherNamesWihtMembersAPI();
        changeDefaultUserVisibility(webDriver, Visibility.PUBLIC);
    }
    
    /**
     * PRECONDITIONS: 
     *          The user should have two public other names "other-name-1" and "other-name-2"
     * @throws JSONException 
     * @throws InterruptedException 
     * */
    @Test
    public void testGetOtherNamesWithPublicAPI() throws InterruptedException, JSONException {
        ClientResponse getResponse = publicV2ApiClient.viewOtherNamesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        OtherNames otherNames = getResponse.getEntity(OtherNames.class);
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(2, otherNames.getOtherNames().size());
        assertThat(otherNames.getOtherNames().get(0).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertThat(otherNames.getOtherNames().get(1).getContent(), anyOf(is("other-name-1"), is("other-name-2")));
        assertEquals(Visibility.PUBLIC, otherNames.getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, otherNames.getOtherNames().get(1).getVisibility());
        
        OtherName otherName1 = otherNames.getOtherNames().get(0);
        
        getResponse = publicV2ApiClient.viewOtherNameXML(getUser1OrcidId(), otherName1.getPutCode());
        assertNotNull(getResponse);
        
        OtherName otherName = getResponse.getEntity(OtherName.class);
        assertNotNull(otherName);
        assertEquals(otherName1.getContent(), otherName.getContent());
        assertEquals(otherName1.getVisibility(), otherName.getVisibility());
        assertEquals(otherName1.getPutCode(), otherName.getPutCode());
    }
        
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        
        OtherName otherName = getOtherName();      
        otherName.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateOtherName(getUser1OrcidId(), otherName, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
    
    private OtherName getOtherName() {
        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc2/samples/other-name-2.0_rc2.xml", OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1", otherName.getContent());
        otherName.setVisibility(Visibility.LIMITED);
        otherName.setSource(null);        
        otherName.setPath(null);
        otherName.setLastModifiedDate(null);
        otherName.setCreatedDate(null);
        otherName.setPutCode(null);
        return otherName;
    }    
}
