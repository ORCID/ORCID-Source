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
import static org.orcid.api.common.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.t2.T2OrcidApiService;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t2-client-context.xml" })
public class T2OrcidApiClientIntegrationTest extends AbstractT2ClientIntegrationTest {

    @Resource
    private T2OrcidApiService<ClientResponse> t2Client;

    private String orcid;

    @Before
    public void init() throws Exception {
        super.init();
        createOrcidAndVerifyResponse201();
    }

    @After
    public void clearOrcid() {
        if (orcid != null) {            
            t2Client.deleteProfileXML(orcid);
            orcid = null;
        }
    }

    @Test
    public void testStatus() {
        ClientResponse clientResponse = t2Client.viewStatusText();
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_PLAIN + "; charset=UTF-8", clientResponse.getType().toString());
        assertEquals(STATUS_OK_MESSAGE, clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsHtml() throws Exception {

        // doesn't matter which format we use to create - it's only to get the
        // orcid back from the header location

        ClientResponse clientResponse = t2Client.viewBioDetailsHtml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML + "; charset=UTF-8", clientResponse.getType().toString());
        String orcidProfileBioOnly = clientResponse.getEntity(String.class);
        assertTrue(orcidProfileBioOnly.indexOf("<orcid-bio>") != -1);
        assertTrue(orcidProfileBioOnly.indexOf("<orcid-works>") == -1);
    }

    @Test
    public void testViewBioDetailsXml() throws Exception {

        // doesn't matter which format we use to create - it's only to get the
        // orcid back from the header location

        ClientResponse clientResponse = t2Client.viewBioDetailsXml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        List<Email> emails = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails);
        for(Email email : emails){
            assertTrue(email.getValue().equals(email.getValue().trim()));
        }
    }

    @Test
    public void testViewBioDetailsJson() throws Exception {

        ClientResponse clientResponse = t2Client.viewBioDetailsJson(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+json; charset=UTF-8; qs=4", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        List<Email> emails = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails);
        for(Email email : emails){
            assertTrue(email.getValue().equals(email.getValue().trim()));
        }
    }

    @Test
    public void testViewFullDetailsHtml() throws Exception {

        ClientResponse clientResponse = t2Client.viewFullDetailsHtml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML + "; charset=UTF-8", clientResponse.getType().toString());
        String orcidProfileFullDetails = clientResponse.getEntity(String.class);
        assertNotNull(orcidProfileFullDetails);
        assertTrue(orcidProfileFullDetails.indexOf("<orcid-bio>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<orcid-work put-code=") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<title>Work title 1</title>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<subtitle>Work subtitle 1</subtitle>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<title>Work title 2</title>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<subtitle>Work subtitle 2</subtitle>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<orcid-history>") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<email primary=\"false\" current=\"true\" verified=\"false\" visibility=\"private\">") != -1);
        assertTrue(orcidProfileFullDetails.indexOf("<email primary=\"true\" current=\"true\" verified=\"false\" visibility=\"private\">") != -1);        
    }

    @Test
    public void testViewFullDetailsXml() throws Exception {

        ClientResponse clientResponse = t2Client.viewFullDetailsXml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidHistory());
        List<Email> emails = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails);
        for(Email email : emails){
            assertTrue(email.getValue().equals(email.getValue().trim()));
        }
    }

    @Test
    public void testViewFullDetailsJson() throws Exception {

        ClientResponse clientResponse = t2Client.viewFullDetailsJson(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+json; charset=UTF-8; qs=4", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);

        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidHistory());
        List<Email> emails = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails);
        for(Email email : emails){
            assertTrue(email.getValue().equals(email.getValue().trim()));
        }
    }

    @Test
    public void testViewWorksDetailsHtml() throws Exception {

        ClientResponse clientResponse = t2Client.viewWorksDetailsHtml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML + "; charset=UTF-8", clientResponse.getType().toString());

        String orcidMessage = clientResponse.getEntity(String.class);
        assertNotNull(orcidMessage);

        assertTrue(orcidMessage.indexOf(this.orcid) != -1);
        assertTrue(orcidMessage.indexOf("<title>Work title 1</title>") != -1);
        assertTrue(orcidMessage.indexOf("<subtitle>Work subtitle 1</subtitle>") != -1);
        assertTrue(orcidMessage.indexOf("<title>Work title 2</title>") != -1);
        assertTrue(orcidMessage.indexOf("<subtitle>Work subtitle 2</subtitle>") != -1);
        assertTrue(orcidMessage.indexOf("<orcid-history>") != -1);
    }

    @Test
    public void testViewWorksDetailsXml() throws Exception {

        ClientResponse clientResponse = t2Client.viewWorksDetailsXml(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        assertNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidHistory());
    }

    @Test
    public void testViewWorksDetailsJson() throws Exception {

        ClientResponse clientResponse = t2Client.viewWorksDetailsJson(this.orcid);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals("application/vnd.orcid+json; charset=UTF-8; qs=4", clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(this.orcid, orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(orcidMessage.getOrcidProfile().retrieveOrcidWorks());
        assertNull(orcidMessage.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidHistory());
    }
    
    @Test
    public void testCreateProfileXML() throws Exception {
        // slightly clumsy -but every other test in this class creates an Orcid
        // VIA the JSON API
        // clear the 'JSON' created Orcid
        clearOrcid();

        // build via xml and verify
        ClientResponse clientResponse = createFullOrcidXml();
        verifyClientResponse201(clientResponse);
    }

    @Test
    public void testAddWorksXml() throws Exception {

        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        OrcidWork orcidWork = createWork("Single works");
        orcidWork.setWorkType(WorkType.UNDEFINED);

        orcidWorks = new OrcidWorks();
        // TODO JB electronic resource num
        // orcidWork.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));
        orcidWorks.getOrcidWork().add(orcidWork);
        message.getOrcidProfile().setOrcidWorks(orcidWorks);

        ClientResponse clientResponse = t2Client.addWorksXml(this.orcid, message);
        assertEquals(201, clientResponse.getStatus());

        clientResponse = t2Client.viewWorksDetailsXml(this.orcid);
        message = clientResponse.getEntity(OrcidMessage.class);
        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 4);

    }

    @Test
    public void testAddWorksJson() throws Exception {

        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);

        message.getOrcidProfile().setOrcid(this.orcid);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        OrcidWork orcidWork = createWork("Single works");

        orcidWorks = new OrcidWorks();
        // TODO JB electronic resource num
        // orcidWork.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));
        orcidWorks.getOrcidWork().add(orcidWork);
        message.getOrcidProfile().setOrcidWorks(orcidWorks);

        ClientResponse clientResponse = t2Client.addWorksJson(this.orcid, message);
        assertEquals(201, clientResponse.getStatus());

        clientResponse = t2Client.viewWorksDetailsXml(this.orcid);
        message = clientResponse.getEntity(OrcidMessage.class);
        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 4);
    }

    @Test
    public void testUpdateWorksXml() throws Exception {

        ClientResponse worksResponse = t2Client.viewWorksDetailsXml(this.orcid);
        OrcidMessage message = worksResponse.getEntity(OrcidMessage.class);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks.getOrcidWork().get(1);
        // String secondTitle = secondWork.getTitles().getTitle().getContent();
        // assertEquals("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555\n\t\t\t\t\t\tare\n\t\t\t\t\t\tassociated with bilocation ability\n\t\t\t\t\t",
        // secondTitle);
        // secondWork.getTitles().getTitle().setContent("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555");
        message.getOrcidProfile().getOrcidInternal().setSecurityDetails(null);
        ClientResponse updatedWorksResponse = t2Client.updateWorksXml(this.orcid, message);
        assertEquals(200, updatedWorksResponse.getStatus());

        worksResponse = t2Client.viewWorksDetailsXml(this.orcid);
        message = worksResponse.getEntity(OrcidMessage.class);
        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks.getOrcidWork().get(1);

    }

    @Test
    @Ignore("Ignoring search tests for now - needs solr to be populated with particular data")
    public void testSearchByQueryJSON() throws Exception {

        // from OrcidAPI spec 1
        String[] queries = getQueries();

        for (int i = 0; i < queries.length; i++) {
            ClientResponse clientResponse = t2Client.searchByQueryJSON(queries[i]);
            assertClientResponseFromSearch(clientResponse);
        }

    }

    @Test
    @Ignore("Ignoring search tests for now - needs solr to be populated with particular data")
    public void testSearchByQueryXML() throws Exception {
        // from OrcidAPI spec 1
        String[] queries = getQueries();

        for (int i = 0; i < queries.length; i++) {
            ClientResponse clientResponse = t2Client.searchByQueryXML(queries[i]);
            assertClientResponseFromSearch(clientResponse);
        }
    }

    private String[] getQueries() {
        return new String[] { "?q=family-name:Carberry&start=0&rows=10", "?q=text:Carberry&start=0&rows=10",
                "?q=family-name:Carberry%20AND%20keyword:Bilocation&start=0&rows=10", "?defType=edismax&q=Carberry&qf=given-names^1.0%20family-name^2.0&start=0&rows=10",
                "?defType=edismax&q=Carberry%20-orcid:%281877-5816-0747-5659%206181-9093-3346-6284%29&qf=given-names^1.0%20family-name^2.0&start=0&rows=10" };
    }

    private void assertClientResponseFromSearch(ClientResponse clientResponse) {
        assertNotNull(clientResponse);
        assertEquals(Status.OK, clientResponse.getClientResponseStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNull(orcidMessage.getOrcidProfile());
        assertTrue(orcidMessage.getOrcidSearchResults() != null && orcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult returnedResult = orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        assertNotNull(returnedResult.getRelevancyScore());
        assertEquals(this.orcid, returnedResult.getOrcidProfile().getOrcid().getValue());
        assertNotNull(returnedResult.getOrcidProfile().getOrcidBio());
        assertNull(returnedResult.getOrcidProfile().retrieveOrcidWorks());
        assertNull(returnedResult.getOrcidProfile().getOrcidHistory());
    }

    @Test
    public void testUpdateWorksJson() throws Exception {
        ClientResponse worksResponse = t2Client.viewWorksDetailsJson(this.orcid);
        OrcidMessage message = worksResponse.getEntity(OrcidMessage.class);
        OrcidWorks orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks.getOrcidWork().get(1);
        // String secondTitle = secondWork.getTitles().getTitle().getContent();
        // assertEquals("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555\n\t\t\t\t\t\tare\n\t\t\t\t\t\tassociated with bilocation ability\n\t\t\t\t\t",
        // secondTitle);
        // secondWork.getTitles().getTitle().setContent("Chromosome 5a55.5 microdeletions comprising AB555 and CD5555");
        message.getOrcidProfile().getOrcidInternal().setSecurityDetails(null);
        ClientResponse updatedWorksResponse = t2Client.updateWorksJson(this.orcid, message);
        assertEquals(200, updatedWorksResponse.getStatus());

        worksResponse = t2Client.viewWorksDetailsJson(this.orcid);
        message = worksResponse.getEntity(OrcidMessage.class);
        orcidWorks = message.getOrcidProfile().retrieveOrcidWorks();
        assertTrue(orcidWorks != null && orcidWorks.getOrcidWork() != null && orcidWorks.getOrcidWork().size() == 3);

        orcidWorks.getOrcidWork().get(1);
    }

    @Test
    public void testAddExternalIdentifiersXml() throws Exception {
        String sponsorOrcid = null;
        try {

            ClientResponse sponsorResponse = createSponsor();
            sponsorOrcid = extractOrcidFromResponseCreated(sponsorResponse);

            // get the bio details of the actual

            ClientResponse bioResponse = t2Client.viewBioDetailsXml(this.orcid);
            OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
            OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();
            assertTrue(orcidBio.getExternalIdentifiers() != null);
            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertTrue(externalIdentifiers.getExternalIdentifier().size() == 0);

            ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
            ExternalIdOrcid externalIdOrcid = new ExternalIdOrcid();
            externalIdOrcid.setValueAsString(sponsorOrcid);
            ExternalIdentifier additionalIdentifer = new ExternalIdentifier(externalIdOrcid, new ExternalIdReference("abc"));
            newExternalIdentifiers.getExternalIdentifier().add(additionalIdentifer);
            orcidBio.setExternalIdentifiers(newExternalIdentifiers);

            message.getOrcidProfile().getOrcidInternal().setSecurityDetails(null);
            ClientResponse updatedIdsResponse = t2Client.addExternalIdentifiersXml(this.orcid, message);
            assertEquals(200, updatedIdsResponse.getStatus());

            // retrieve the sponsor info
            bioResponse = t2Client.viewBioDetailsXml(this.orcid);
            message = bioResponse.getEntity(OrcidMessage.class);
            orcidBio = message.getOrcidProfile().getOrcidBio();
            assertTrue(orcidBio.getExternalIdentifiers() != null);
            externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertTrue(externalIdentifiers.getExternalIdentifier().size() == 1);

        } finally {
            // whatever happens get rid of the sponsor orcid we created
            t2Client.deleteProfileXML(sponsorOrcid);
        }
    }

    @Test
    public void testAddExternalIdentifiersJson() throws Exception {
        String sponsorOrcid = null;
        try {

            ClientResponse sponsorResponse = createSponsor();
            sponsorOrcid = extractOrcidFromResponseCreated(sponsorResponse);

            // get the bio details of the actual

            ClientResponse bioResponse = t2Client.viewBioDetailsJson(this.orcid);
            OrcidMessage message = bioResponse.getEntity(OrcidMessage.class);
            OrcidBio orcidBio = message.getOrcidProfile().getOrcidBio();
            assertTrue(orcidBio.getExternalIdentifiers() != null);
            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertTrue(externalIdentifiers.getExternalIdentifier().size() == 0);

            ExternalIdentifiers newExternalIdentifiers = new ExternalIdentifiers();
            ExternalIdOrcid externalIdOrcid = new ExternalIdOrcid();
            externalIdOrcid.setValueAsString(sponsorOrcid);
            ExternalIdentifier additionalIdentifer = new ExternalIdentifier(externalIdOrcid, new ExternalIdReference("abc"));
            newExternalIdentifiers.getExternalIdentifier().add(additionalIdentifer);
            orcidBio.setExternalIdentifiers(newExternalIdentifiers);

            message.getOrcidProfile().getOrcidInternal().setSecurityDetails(null);
            ClientResponse updatedIdsResponse = t2Client.addExternalIdentifiersJson(this.orcid, message);
            assertEquals(200, updatedIdsResponse.getStatus());

            // retrieve the sponsor info
            bioResponse = t2Client.viewBioDetailsJson(this.orcid);
            message = bioResponse.getEntity(OrcidMessage.class);
            orcidBio = message.getOrcidProfile().getOrcidBio();
            assertTrue(orcidBio.getExternalIdentifiers() != null);
            externalIdentifiers = orcidBio.getExternalIdentifiers();
            assertTrue(externalIdentifiers.getExternalIdentifier().size() == 1);

        } finally {
            // whatever happens get rid of the sponsor orcid we created
            if (sponsorOrcid != null) {
                t2Client.deleteProfileXML(sponsorOrcid);
            }
        }
    }

    @Test
    public void testUpdateBioDetailsJson() throws Exception {

        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        message.getOrcidProfile().setOrcidWorks(null);
        message.getOrcidProfile().getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName("Bowen"));
        List<Email> emails = message.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        Email email6 = new Email("test-new-email+" + System.currentTimeMillis() + "@email.com");
        email6.setPrimary(false);
        emails.add(email6);
        ClientResponse response = t2Client.updateBioDetailsJson(this.orcid, message);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        response = t2Client.viewFullDetailsJson(this.orcid);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage responseEntity = response.getEntity(OrcidMessage.class);
        assertNotNull(responseEntity);
        String familyName = responseEntity.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("Bowen", familyName);  
        List<Email> updatedEmails = responseEntity.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertTrue(updatedEmails.contains(email6));
    }
    
    @Test
    public void testUpdateBioDetailsXml() throws Exception {

        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        message.getOrcidProfile().setOrcid(this.orcid);
        message.getOrcidProfile().setOrcidWorks(null);
        message.getOrcidProfile().getOrcidBio().getPersonalDetails().setFamilyName(new FamilyName("Bowen"));
        List<Email> emails = message.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        Email email6 = new Email("test-new-email+" + System.currentTimeMillis() + "@email.com");
        email6.setPrimary(false);
        emails.add(email6);
        ClientResponse response = t2Client.updateBioDetailsXml(this.orcid, message);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        response = t2Client.viewFullDetailsXml(this.orcid);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage responseEntity = response.getEntity(OrcidMessage.class);
        assertNotNull(responseEntity);
        String familyName = responseEntity.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent();
        assertEquals("Bowen", familyName);
        List<Email> updatedEmails = responseEntity.getOrcidProfile().getOrcidBio().getContactDetails().getEmail();
        assertTrue(updatedEmails.contains(email6));
    }
    
    private void createOrcidAndVerifyResponse201() throws Exception {
        ClientResponse clientResponse = createFullOrcid();
        verifyClientResponse201(clientResponse);
    }

    private ClientResponse createFullOrcid() throws JAXBException {
        // really we don't care which media type is used
        // because in practice we only want the uri from the header
        return createFullOrcidJSON();
    }

    private ClientResponse createFullOrcidJSON() throws JAXBException {
        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        ClientResponse response = t2Client.createProfileJson(message);
        return response;
    }

    private ClientResponse createSponsor() throws JAXBException {
        OrcidMessage message = getInternalSponsor();
        ClientResponse response = t2Client.createProfileJson(message);
        return response;
    }

    private void verifyClientResponse201(ClientResponse clientResponse) {
        assertNotNull(clientResponse);
        assertEquals(201, clientResponse.getStatus());
        // assign new orcid as instance var so can be used in tests and wiped
        // down later
        this.orcid = extractOrcidFromResponseCreated(clientResponse);
        assertNotNull(this.orcid);
    }

}
