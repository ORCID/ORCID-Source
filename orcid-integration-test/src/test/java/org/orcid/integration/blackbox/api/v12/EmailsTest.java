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
package org.orcid.integration.blackbox.api.v12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
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
public class EmailsTest extends BlackBoxBaseV2Release {        

    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2;
    
    /**
     * Test update email for a specific user
     * */
    @Test
    public void cantUpdateEmailsTest() throws JSONException, InterruptedException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String originalEmail = getUser1UserName();
        String password = getUser1Password();
        
        String accessToken = getAccessToken(userId, password, Arrays.asList("/orcid-bio/update"), clientId, clientSecret, clientRedirectUri,
                true);
        
        long time = System.currentTimeMillis();
        String updatedEmail = time + "@update.com";
        ContactDetails contactDetails = new ContactDetails();        
        contactDetails.getEmail().add(new Email(updatedEmail));
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setContactDetails(contactDetails);
        
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.USER);
        orcidProfile.setOrcidBio(orcidBio);
        
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2");
        orcidMessage.setOrcidProfile(orcidProfile);
        
        ClientResponse clientResponse = t2OAuthClient_1_2.updateBioDetailsXml(userId, orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
                
        clientResponse = t2OAuthClient_1_2.viewBioDetailsXml(userId, accessToken);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage result = clientResponse.getEntity(OrcidMessage.class);
        
        // Check returning message
        assertNotNull(result);
        assertNotNull(result.getOrcidProfile());
        assertNotNull(result.getOrcidProfile().getOrcidBio());
        assertNotNull(result.getOrcidProfile().getOrcidBio().getContactDetails());
        assertNotNull(result.getOrcidProfile().getOrcidBio().getContactDetails().getEmail());
        boolean haveOriginalEmail = false;
        for(Email email : result.getOrcidProfile().getOrcidBio().getContactDetails().getEmail()) {
            assertFalse(email.getValue().equals(updatedEmail));
            if(email.getValue().equals(originalEmail)) {
                haveOriginalEmail = true;
            }
        }
        assertTrue(haveOriginalEmail);
    }
    
    /**
     * Test update email using already existing email
     * */
    @Test
    public void updateEmailsUsingAlreadyExistingEmailTest() throws JSONException, InterruptedException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();        
        String password = getUser1Password();
        
        String user2Email = getUser2UserName();
        
        String accessToken = getAccessToken(userId, password, Arrays.asList("/orcid-bio/update"), clientId, clientSecret, clientRedirectUri,
                true);
               
        //Email already used by 9999-9999-9999-9990
        ContactDetails contactDetails = new ContactDetails();        
        contactDetails.getEmail().add(new Email(user2Email));
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setContactDetails(contactDetails);
        
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.USER);
        orcidProfile.setOrcidBio(orcidBio);
        
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2");
        orcidMessage.setOrcidProfile(orcidProfile);
        
        ClientResponse clientResponse = t2OAuthClient_1_2.updateBioDetailsXml(userId, orcidMessage, accessToken);
        assertEquals(400, clientResponse.getStatus());        
        OrcidMessage errorMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(errorMessage);
        assertNotNull(errorMessage.getErrorDesc());
        assertEquals("Bad Request: Invalid incoming message: Email " + user2Email + " belongs to other user", errorMessage.getErrorDesc().getContent());
    }    
}
