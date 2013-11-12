/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.orcid.api.common.OrcidApiConstants.EXTERNAL_IDENTIFIER_PATH;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
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
    // (orcid-t2-security-context.xml) which will then
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
    public void testCreateProfileXml() throws Exception {

        OrcidMessage profileWithOutToken = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        ClientResponse clientResponseWithoutToken = oauthT2Client.createProfileXML(profileWithOutToken, null);
        assertClientResponse401Details(clientResponseWithoutToken);

        // now create with token
        ClientResponse clientResponse = createNewOrcidUsingAccessToken();
        assertEquals(201, clientResponse.getStatus());
        assertNotNull(this.orcid);
    }

    @Test
    public void testCreateProfileJSON() throws Exception {

        OrcidMessage profile = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        assertClientResponse401Details(oauthT2Client.createProfileJson(profile, null));
        // now create with token
        ClientResponse clientResponse = createNewOrcidUsingAccessToken();
        assertEquals(201, clientResponse.getStatus());
        assertNotNull(this.orcid);
    }

    @Test
    public void testUpdateBioXml() throws Exception {

        // put the message into the db
        createNewOrcidUsingAccessToken();
        // create and updated version of the message in the db
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        String originalFamilyName = message.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("familyName", originalFamilyName);
        message.getOrcidProfile().setOrcid(this.orcid);
        message.getOrcidProfile().setOrcidWorks(null);
        message.getOrcidProfile().getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName("Bowen"));
        assertClientResponse401Details(oauthT2Client.updateBioDetailsXml(orcid, message, null));
        // now get the access token and try again
        ClientResponse response = oauthT2Client.updateBioDetailsXml(orcid, message, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        // Should be still to use token to view
        response = oauthT2Client.viewFullDetailsXml(this.orcid, accessToken);
        assertEquals(200, response.getStatus());
        OrcidMessage responseEntity = response.getEntity(OrcidMessage.class);
        assertNotNull(responseEntity);
        String familyName = responseEntity.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("Bowen", familyName);
    }

    @Test
    public void testUpdateBioDetailsJson() throws Exception {

        // put the message into the db
        createNewOrcidUsingAccessToken();
        // create and updated version of the message in the db
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        String originalFamilyName = message.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("familyName", originalFamilyName);
        message.getOrcidProfile().setOrcid(this.orcid);
        message.getOrcidProfile().setOrcidWorks(null);
        message.getOrcidProfile().getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName("Bowen"));
        ClientResponse response = oauthT2Client.updateBioDetailsJson(orcid, message, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        // create a new token to do a view
        createAccessTokenFromCredentials();
        response = oauthT2Client.viewFullDetailsJson(this.orcid, accessToken);
        assertEquals(200, response.getStatus());
        OrcidMessage responseEntity = response.getEntity(OrcidMessage.class);
        assertNotNull(responseEntity);
        String familyName = responseEntity.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("Bowen", familyName);
    }

    @Test
    public void testUpdateWorksJson() throws Exception {
        // put the message into the db
        createNewOrcidUsingAccessToken();
        ClientResponse worksResponse = oauthT2Client.viewWorksDetailsJson(this.orcid, accessToken);
        OrcidMessage message = worksResponse.getEntity(OrcidMessage.class);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        OrcidWork secondWork = orcidWorks.getOrcidWork().get(1);
        WorkTitle secondMainTitle = new WorkTitle();
        Title mainTitle = new Title("Chromosome 5a55.5 microdeletions");
        Subtitle subTitle = new Subtitle("C Subtitle");
        secondMainTitle.setSubtitle(subTitle);
        secondMainTitle.setTitle(mainTitle);
        secondWork.setWorkTitle(secondMainTitle);
        ClientResponse updatedWorksResponse = oauthT2Client.updateWorksJson(this.orcid, message, accessToken);
        assertEquals(200, updatedWorksResponse.getStatus());

        createAccessTokenFromCredentials();
        worksResponse = oauthT2Client.viewWorksDetailsJson(this.orcid, accessToken);
        assertEquals(200, worksResponse.getStatus());
        message = worksResponse.getEntity(OrcidMessage.class);
        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        Collections.sort(orcidWorks.getOrcidWork(), new Comparator<OrcidWork>() {
            public int compare(OrcidWork searchRes1, OrcidWork searchRes2) {
                return ((String) searchRes1.getWorkTitle().getTitle().getContent()).compareToIgnoreCase((String) searchRes2.getWorkTitle().getTitle().getContent());
            }
        });
        secondWork = orcidWorks.getOrcidWork().get(0);
        assertEquals(secondMainTitle, secondWork.getWorkTitle());
    }

    @Test
    public void testAddWorksXml() throws Exception {

        createNewOrcidUsingAccessToken();
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks = new OrcidWorks();
        OrcidWork orcidWork = orcidClientDataHelper.createWork("Single works");
        orcidWork.setWorkType(WorkType.UNDEFINED);
        orcidWorks.getOrcidWork().add(orcidWork);        
        message.getOrcidProfile().setOrcidWorks(orcidWorks);

        assertClientResponse401Details(oauthT2Client.addWorksXml(orcid, message, null));
        ClientResponse clientResponse = oauthT2Client.addWorksXml(this.orcid, message, accessToken);
        assertEquals(201, clientResponse.getStatus());

        ClientResponse retrievedOrcidWorks = oauthT2Client.viewWorksDetailsXml(this.orcid, accessToken);
        assertTrue(retrievedOrcidWorks.getEntity(OrcidMessage.class).getOrcidProfile().retrieveOrcidWorks().getOrcidWork().size() == 4);
    }

    @Test
    public void testRemoveWriteScopesPastValitity() throws Exception {

        createNewOrcidUsingAccessToken();
        OrcidOauth2TokenDetail orcidOauth2TokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(accessToken);
        Date d = new Date();
        d.setTime(d.getTime() - 24 * 60 * 60 * 1000);
        orcidOauth2TokenDetail.setDateCreated(d);
        orcidOauthTokenDetailService.saveOrUpdate(orcidOauth2TokenDetail);
        
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks = new OrcidWorks();
        OrcidWork orcidWork = orcidClientDataHelper.createWork("Single works");
        orcidWork.setWorkType(WorkType.UNDEFINED);
        orcidWorks.getOrcidWork().add(orcidWork);        
        message.getOrcidProfile().setOrcidWorks(orcidWorks);
        assertClientResponse403SecurityProblem(oauthT2Client.addWorksXml(orcid, message, accessToken));

        // make sure write scope was removed
        orcidOauth2TokenDetail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(accessToken);
        assertTrue(!orcidOauth2TokenDetail.getScope().contains("create"));
        assertTrue(!orcidOauth2TokenDetail.getScope().contains("update"));
        
        // make sure read scope is still there
        assertTrue(orcidOauth2TokenDetail.getScope().contains("read"));        
    }

    
    @Test
    public void testUpdateBioDetailsXml() throws Exception {
        createNewOrcidUsingAccessToken();
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        message.getOrcidProfile().setOrcidWorks(null);
        message.getOrcidProfile().getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName("Bowen"));
        assertClientResponse401Details(oauthT2Client.updateBioDetailsXml(orcid, message, null));
        ClientResponse response = oauthT2Client.updateBioDetailsXml(orcid, message, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        createAccessTokenFromCredentials();
        response = oauthT2Client.viewFullDetailsXml(orcid, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage responseEntity = response.getEntity(OrcidMessage.class);
        assertNotNull(responseEntity);
        String familyName = responseEntity.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("Bowen", familyName);
    }

    @Test
    public void testAddWorksJson() throws Exception {
        createNewOrcidUsingAccessToken();
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        OrcidWork orcidWork = orcidClientDataHelper.createWork("Single works with title");
        orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(orcidWork);
        message.getOrcidProfile().setOrcidWorks(orcidWorks);

        ClientResponse clientResponse = oauthT2Client.addWorksJson(this.orcid, message, accessToken);
        assertEquals(201, clientResponse.getStatus());

        ClientResponse orcidWorksFromResponse = oauthT2Client.viewWorksDetailsJson(this.orcid, accessToken);
        List<OrcidWork> retrievedOrcidWorks = orcidWorksFromResponse.getEntity(OrcidMessage.class).getOrcidProfile().retrieveOrcidWorks().getOrcidWork();
        assertTrue(retrievedOrcidWorks.size() == 4);

        String clientOrcid = this.clientId;

        for (OrcidWork work : retrievedOrcidWorks) {
            WorkTitle workTitle = work.getWorkTitle();

            if (workTitle != null && workTitle.getTitle() != null) {
                if ("Single works with title".equals(workTitle.getTitle().getContent())) {
                    assertEquals(clientOrcid, work.getWorkSource().getContent());
                    break;
                }
            }
        }

    }

    @Test
    public void testUpdateWorksXml() throws Exception {

        createNewOrcidUsingAccessToken();
        OrcidMessage message = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        OrcidWork work1 = orcidWorks.getOrcidWork().get(0);
        OrcidWork workToUpdate = orcidWorks.getOrcidWork().get(1);
        OrcidWork work3 = orcidWorks.getOrcidWork().get(2);
        assertEquals("Work title 1", work1.getWorkTitle().getTitle().getContent());
        assertEquals("Work subtitle 1", work1.getWorkTitle().getSubtitle().getContent());
        assertEquals("Work title 2", workToUpdate.getWorkTitle().getTitle().getContent());
        assertEquals("Work subtitle 2", workToUpdate.getWorkTitle().getSubtitle().getContent());
        assertEquals("Work Title 3", work3.getWorkTitle().getTitle().getContent());
        assertEquals("Work subtitle 3", work3.getWorkTitle().getSubtitle().getContent());
        workToUpdate.getWorkTitle().getTitle().setContent("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555");
        workToUpdate.getWorkTitle().getSubtitle().setContent("Chromosome subtitle");
        message.getOrcidProfile().getOrcidInternal().setSecurityDetails(null);
        assertClientResponse401Details(oauthT2Client.updateWorksXml(this.orcid, message, null));

        if(work1.getWorkType() == null) {
            work1.setWorkType(WorkType.UNDEFINED);
        }
        
        if(workToUpdate.getWorkType() == null){
        	workToUpdate.setWorkType(WorkType.UNDEFINED);
        }
        
        if(work3.getWorkType() == null) {
        	work3.setWorkType(WorkType.UNDEFINED);
        }
        
        ClientResponse updatedWorksResponse = oauthT2Client.updateWorksXml(this.orcid, message, accessToken);

        assertEquals(200, updatedWorksResponse.getStatus());
        createAccessTokenFromCredentials();
        ClientResponse worksResponse = oauthT2Client.viewWorksDetailsXml(this.orcid, this.accessToken);
        assertEquals(200, worksResponse.getStatus());
        message = worksResponse.getEntity(OrcidMessage.class);

        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        Collections.sort(orcidWorks.getOrcidWork(), new Comparator<OrcidWork>() {
            public int compare(OrcidWork searchRes1, OrcidWork searchRes2) {
                return ((String) searchRes1.getWorkTitle().getTitle().getContent()).compareToIgnoreCase((String) searchRes2.getWorkTitle().getTitle().getContent());
            }
        });

        workToUpdate = orcidWorks.getOrcidWork().get(0);

        assertEquals("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555", workToUpdate.getWorkTitle().getTitle().getContent());
        assertEquals("Chromosome subtitle", workToUpdate.getWorkTitle().getSubtitle().getContent());
        assertEquals(this.clientId, workToUpdate.getWorkSource().getContent());

        // check other works unchanged
        assertEquals("Work title 1", orcidWorks.getOrcidWork().get(1).getWorkTitle().getTitle().getContent());
        assertEquals("Work subtitle 1", orcidWorks.getOrcidWork().get(1).getWorkTitle().getSubtitle().getContent());
        assertEquals("Work Title 3", orcidWorks.getOrcidWork().get(2).getWorkTitle().getTitle().getContent());
        assertEquals("Work subtitle 3", orcidWorks.getOrcidWork().get(2).getWorkTitle().getSubtitle().getContent());
    }

    @Test
    public void testAddExternalIdentifiersJson() throws Exception {
        String sponsorOrcid = null;
        try {

            createNewOrcidUsingAccessToken();
            OrcidMessage sponsorMessage = orcidClientDataHelper.createSponsor();
            sponsorOrcid = sponsorMessage.getOrcidProfile().getOrcid().getValue();

            // get the bio details of the actual
            createAccessTokenFromCredentials();
            ClientResponse bioResponse = oauthT2Client.viewBioDetailsJson(this.orcid, accessToken);
            OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
            OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();
            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertNull(externalIdentifiers);

            ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
            newExternalIdentifiers.setVisibility(Visibility.PUBLIC);
            ExternalIdentifier additionalIdentifer = new ExternalIdentifier(new ExternalIdOrcid(clientId), new ExternalIdReference("abc123"));
            newExternalIdentifiers.getExternalIdentifier().add(additionalIdentifer);
            orcidBio.setExternalIdentifiers(newExternalIdentifiers);

            createAccessTokenFromCredentials();
            ClientResponse updatedIdsResponse = oauthT2Client.addExternalIdentifiersJson(this.orcid, message, accessToken);
            assertEquals(200, updatedIdsResponse.getStatus());

            // retrieve the sponsor info
            createAccessTokenFromCredentials();
            bioResponse = oauthT2Client.viewBioDetailsJson(this.orcid, accessToken);
            message = bioResponse.getEntity(OrcidMessage.class);
            orcidBio = message.getOrcidProfile().getOrcidBio();
            assertTrue(orcidBio.getExternalIdentifiers() != null);
            externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertEquals(1, externalIdentifiers.getExternalIdentifier().size());

        } finally {
            // whatever happens get rid of the sponsor orcid we created
            orcidClientDataHelper.deleteOrcidProfile(sponsorOrcid);
        }
    }

    @Test
    public void testAddExternalIdentifiersXml() throws Exception {

        createNewOrcidUsingAccessToken();
        // get the bio details of the actual
        createAccessTokenFromCredentials();
        ClientResponse bioResponse = oauthT2Client.viewBioDetailsXml(this.orcid, accessToken);
        OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
        OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();
        OrcidHistory orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null && orcidHistory.getSource() != null);
        assertEquals("Ecological Complexity", orcidHistory.getSource().getSourceName().getContent());

        ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNull(externalIdentifiers);

        ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
        newExternalIdentifiers.setVisibility(Visibility.PUBLIC);
        ExternalIdentifier additionalIdentifer = new ExternalIdentifier(new ExternalIdOrcid(clientId), new ExternalIdReference("abc123"));
        newExternalIdentifiers.getExternalIdentifier().add(additionalIdentifer);
        orcidBio.setExternalIdentifiers(newExternalIdentifiers);

        createAccessTokenFromCredentials();
        ClientResponse updatedIdsResponse = oauthT2Client.addExternalIdentifiersXml(this.orcid, message, accessToken);
        assertEquals(200, updatedIdsResponse.getStatus());

        // retrieve the sponsor info
        createAccessTokenFromCredentials();
        bioResponse = oauthT2Client.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();
        assertTrue(orcidBio.getExternalIdentifiers() != null);
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());

    }

    @Test
    public void testAddExternalIdentifiersXmlLikeScopus() throws Exception {

        createNewOrcidUsingAccessToken();
        // get the bio details of the actual
        createAccessTokenFromCredentials();
        ClientResponse bioResponse = oauthT2Client.viewBioDetailsXml(this.orcid, accessToken);
        OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
        OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();
        OrcidHistory orcidHistory = message.getOrcidProfile().getOrcidHistory();
        assertTrue(orcidHistory != null && orcidHistory.getSource() != null);
        assertEquals("Ecological Complexity", orcidHistory.getSource().getSourceName().getContent());

        ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertNull(externalIdentifiers);

        ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
        newExternalIdentifiers.setVisibility(Visibility.PUBLIC);
        ExternalIdentifier additionalIdentifer = new ExternalIdentifier(new ExternalIdOrcid(clientId), new ExternalIdReference("abc123"));
        newExternalIdentifiers.getExternalIdentifier().add(additionalIdentifer);
        orcidBio.setExternalIdentifiers(newExternalIdentifiers);

        createAccessTokenFromCredentials();

        OrcidClientHelper orcidClientHelper = new OrcidClientHelper(t2BaseUrl, jerseyClient);
        URI externalIdentifiersUriWithOrcid = orcidClientHelper.deriveUriFromRestPath(EXTERNAL_IDENTIFIER_PATH, orcid);
        WebResource rootResource = orcidClientHelper.createRootResource(externalIdentifiersUriWithOrcid);
        Builder builder = rootResource.header("Authorization", "Bearer " + accessToken).accept(MediaType.WILDCARD).type(OrcidApiConstants.ORCID_XML);
        ClientResponse updatedIdsResponse = builder.post(ClientResponse.class, message);
        assertEquals(200, updatedIdsResponse.getStatus());

        // retrieve the sponsor info
        createAccessTokenFromCredentials();
        bioResponse = oauthT2Client.viewBioDetailsXml(this.orcid, accessToken);
        message = bioResponse.getEntity(OrcidMessage.class);
        orcidBio = message.getOrcidProfile().getOrcidBio();
        assertTrue(orcidBio.getExternalIdentifiers() != null);
        externalIdentifiers = orcidBio.getExternalIdentifiers();
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());

    }

    @Test
    public void testRegisterAndUnRegisterWebhook() throws Exception {
        createNewOrcidUsingAccessToken();
        createAccessTokenFromCredentials(ScopePathType.WEBHOOK.value());
        String webhookUri = URLEncoder.encode("http://nowhere.com", "UTF-8");
        ClientResponse putResponse = oauthT2Client.registerWebhook(this.orcid, webhookUri, this.accessToken);
        assertNotNull(putResponse);
        assertEquals(201, putResponse.getStatus());
        ClientResponse deleteResponse = oauthT2Client.unregisterWebhook(this.orcid, webhookUri, this.accessToken);
        assertNotNull(deleteResponse);
        assertEquals(204, deleteResponse.getStatus());
    }

}
