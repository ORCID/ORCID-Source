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
package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class MemberV1Test extends BlackBoxBaseV2Release {
    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2; 
    
    @Test
    public void testCreateNewProfile() throws Exception {
        String accessToken = getClientCredentialsAccessToken(ScopePathType.ORCID_PROFILE_CREATE, this.getClient1ClientId(), this.getClient1ClientSecret(), APIRequestType.MEMBER);
        
        OrcidMessage orcidMessage = (OrcidMessage) unmarshallFromPath("/samples/small_orcid_profile.xml");
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        //Update email
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().get(0).setValue(System.currentTimeMillis() + "@api.com");                        
        ClientResponse response = t2OAuthClient_1_2.createProfileXML(orcidMessage, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String locationPath = response.getLocation().getPath();
        String orcid = locationPath.substring(0, locationPath.lastIndexOf('/'));
        orcid = orcid.substring(orcid.lastIndexOf('/') + 1);
        assertNotNull(orcid);
        response = t2OAuthClient_1_2.viewFullDetailsXml(orcid, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OrcidMessage newMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(newMessage);
        assertNotNull(newMessage.getOrcidProfile());
        OrcidProfile newProfile = newMessage.getOrcidProfile();
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getBiography().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getExternalIdentifiers().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getKeywords().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getResearcherUrls().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility().value());
        assertEquals(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().value(), newProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility().value());
    }

    public OrcidMessage unmarshallFromPath(String path) throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            JAXBContext context = JAXBContext.newInstance(OrcidProfile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (OrcidMessage)unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file from classpath", e);
        } 
    }
}
