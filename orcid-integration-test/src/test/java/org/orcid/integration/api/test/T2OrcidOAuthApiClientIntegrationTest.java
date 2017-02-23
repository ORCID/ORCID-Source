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
package org.orcid.integration.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OrcidClientHelper;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.DefaultPermissionChecker;
import org.orcid.core.security.PermissionChecker;
import org.orcid.integration.api.t2.BaseT2OrcidOAuthApiClientIntegrationTest;
import org.orcid.integration.api.t2.OrcidClientDataHelper;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdSource;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@RunWith(SpringJUnit4ClassRunner.class)
public class T2OrcidOAuthApiClientIntegrationTest extends BaseT2OrcidOAuthApiClientIntegrationTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(T2OrcidOAuthApiClientIntegrationTest.class);

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    public T2OrcidOAuthApiClientIntegrationTest() {
        super();
    }

    /**
     * The classes loaded from the app context are in fact proxies to the
     * OrcidProfileManagerImpl class, required for transactionality. However we
     * can only return the proxied interface from the app context
     * 
     * We need to mock the call to the OrcidIndexManager whenever a persist
     * method is called, but this dependency is only accessible on the impl (as
     * it should be).
     * 
     * To preserve the transactionality AND allow us to mock a dependency that
     * exists on the Impl we use the getTargetObject() method in the superclass
     * 
     * @throws Exception
     */

    // dependencies:
    // set up some client data - OrcidClientGroupManager
    // need a trust manager that allows us to establish an SSL connection with
    // tomcat, but WITHOUT
    // a certificate with CN of ORCID-T2-CLIENT-V1' -
    // (orcid-api-security-context.xml) which will then
    // make us hit the OAuth end point and go through the OAuth flow

    // NB:
    // Use a mime-type of application/orcid+xml to create a profile - this is
    // because the exception filter
    // currently doesn't render a type of Orcid+JSON (as opposed to JSON)

    // insert a client data set into the db

    // Flow Part 1:
    // No token at all, expect a 401 - a header telling us we need to
    // authenticate
    // Flow Part 2:

    // Flow Part 3:

    // tear down afterwards

    // login user to obtain cookie for


    @Test
    public void testAddDuplicatedExternalIdentifier_1() throws Exception {
        createNewOrcidUsingAccessToken();
        // get the bio details of the actual
        createAccessTokenFromCredentials();
        ClientResponse bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
        OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();

        OrcidHistory orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null);
        
        //Add one external identifier
        ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
        if(externalIdentifiers != null) {
            assertTrue(externalIdentifiers.getExternalIdentifier() == null || externalIdentifiers.getExternalIdentifier().isEmpty()); 
        }
        
        ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
        newExternalIdentifiers.setVisibility(Visibility.PUBLIC);
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(clientId));
        ExternalIdentifier externalIdentifier = new ExternalIdentifier();
        externalIdentifier.setExternalIdReference(new ExternalIdReference("abc123"));
        externalIdentifier.setExternalIdCommonName(new ExternalIdCommonName("456efg"));
        externalIdentifier.setSource(source);
        newExternalIdentifiers.getExternalIdentifier().add(externalIdentifier);
        orcidBio.setExternalIdentifiers(newExternalIdentifiers);

        createAccessTokenFromCredentials();
        ClientResponse updatedIdsResponse = oauthT2Client1_2.addExternalIdentifiersXml(this.orcid, message, accessToken);
        assertEquals(200, updatedIdsResponse.getStatus());
        
        //Get it again and check the ext id was created
        bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();

        orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null);
                
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        
        ExternalIdentifier otherExternalIdentifier = new ExternalIdentifier();
        otherExternalIdentifier.setExternalIdReference(new ExternalIdReference("abc123"));
        otherExternalIdentifier.setExternalIdCommonName(new ExternalIdCommonName("456efg"));        
        externalIdentifiers.getExternalIdentifier().add(otherExternalIdentifier);
        orcidBio.setExternalIdentifiers(externalIdentifiers);
        
        updatedIdsResponse = oauthT2Client1_2.addExternalIdentifiersXml(this.orcid, message, accessToken);
        assertEquals(200, updatedIdsResponse.getStatus());
        
        // Get it again and check that only one ext id was created
        bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();

        orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null);
                
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
    }
    
    @Test
    public void testAddDuplicatedExternalIdentifier_2() throws Exception {
        createNewOrcidUsingAccessToken();
        // get the bio details of the actual
        createAccessTokenFromCredentials();
        ClientResponse bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
        OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();

        OrcidHistory orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null);
        
        //Add one external identifier
        ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
        if(externalIdentifiers != null) {
            assertTrue(externalIdentifiers.getExternalIdentifier() == null || externalIdentifiers.getExternalIdentifier().isEmpty()); 
        }
        
        ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
        newExternalIdentifiers.setVisibility(Visibility.PUBLIC);
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(clientId));
        ExternalIdentifier externalIdentifier = new ExternalIdentifier();
        externalIdentifier.setExternalIdReference(new ExternalIdReference("abc123"));
        externalIdentifier.setExternalIdCommonName(new ExternalIdCommonName("456efg"));
        externalIdentifier.setSource(source);
        newExternalIdentifiers.getExternalIdentifier().add(externalIdentifier);
        orcidBio.setExternalIdentifiers(newExternalIdentifiers);

        createAccessTokenFromCredentials();
        ClientResponse updatedIdsResponse = oauthT2Client1_2.addExternalIdentifiersXml(this.orcid, message, accessToken);
        assertEquals(200, updatedIdsResponse.getStatus());
        
        //Get it again and check the ext id was created
        bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();

        orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null);
                
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        
        //Add two ext ids, one duplicated and other one new
        ExternalIdentifier otherExternalIdentifier1 = new ExternalIdentifier();
        otherExternalIdentifier1.setExternalIdReference(new ExternalIdReference("abc123"));
        otherExternalIdentifier1.setExternalIdCommonName(new ExternalIdCommonName("456efg"));        
        externalIdentifiers.getExternalIdentifier().add(otherExternalIdentifier1);
        
        ExternalIdentifier otherExternalIdentifier2 = new ExternalIdentifier();
        otherExternalIdentifier2.setExternalIdReference(new ExternalIdReference("other#2"));
        otherExternalIdentifier2.setExternalIdCommonName(new ExternalIdCommonName("other#2"));        
        externalIdentifiers.getExternalIdentifier().add(otherExternalIdentifier2);
        
        orcidBio.setExternalIdentifiers(externalIdentifiers);
        
        updatedIdsResponse = oauthT2Client1_2.addExternalIdentifiersXml(this.orcid, message, accessToken);
        assertEquals(200, updatedIdsResponse.getStatus());
        
        // Get it again and check that only one ext id was created
        bioResponse = oauthT2Client1_2.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();        
                
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());
        
        for(ExternalIdentifier extId : externalIdentifiers.getExternalIdentifier()) {
            String commonName = extId.getExternalIdCommonName().getContent();
            String idReference = extId.getExternalIdReference().getContent(); 
            if(commonName.equals("456efg") || commonName.equals("other#2")) {
                if(commonName.equals("456efg"))
                    assertEquals("For common name " + commonName + " id reference " + idReference + " is invalid", "abc123", idReference);
                else if(commonName.equals("other#2"))
                    assertEquals("For common name " + commonName + " id reference " + idReference + " is invalid", "other#2", idReference);                
            } else {
                fail("Invalid common name found: " + commonName);
            }                        
        }
    }
    
    @Test
    public void testRegisterAndUnRegisterWebhook() throws Exception {
        createNewOrcidUsingAccessToken();
        String webhookToken = createAccessTokenFromCredentials(ScopePathType.WEBHOOK.value());
        String webhookUri = URLEncoder.encode("http://nowhere.com", "UTF-8");
        ClientResponse putResponse = oauthT2Client1_2_rc6.registerWebhook(this.orcid, webhookUri, webhookToken);
        assertNotNull(putResponse);
        assertEquals(201, putResponse.getStatus());
        ClientResponse deleteResponse = oauthT2Client1_2_rc6.unregisterWebhook(this.orcid, webhookUri, webhookToken);
        assertNotNull(deleteResponse);
        assertEquals(204, deleteResponse.getStatus());
    }

}
