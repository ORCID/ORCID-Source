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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Visibility;
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
public class PerActivityAPINewScopesTest extends BlackBoxBaseV2Release {

    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2;

    @Test
    public void addWorkTest() throws Exception {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String accessToken = getAccessToken(userId, password, Arrays.asList("/orcid-works/read-limited", "/activities/update"), clientId, clientSecret, clientRedirectUri,
                true);
        
        String title = "Work " + System.currentTimeMillis();
        
        Long putCode = null;
        Api12Helper.addWork(userId, accessToken, title, t2OAuthClient_1_2);

        ClientResponse response = t2OAuthClient_1_2.viewWorksDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessageWithNewWork = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        boolean found = false;
        for(OrcidWork work : orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork()) {
            if(title.equals(work.getWorkTitle().getTitle().getContent())) {                
                assertNotNull(work.getPutCode());
                putCode = Long.valueOf(work.getPutCode());
                found = true;
            }
        }
        assertTrue(found);
        
        // Delete it
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), putCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void addFundingTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String accessToken = getAccessToken(userId, password, Arrays.asList("/funding/read-limited", "/activities/update"), clientId, clientSecret, clientRedirectUri,
                true);
        String fundingTitle = "Funding " + System.currentTimeMillis();
        Long putCode = null;
        Api12Helper.addFunding(userId, accessToken, fundingTitle, t2OAuthClient_1_2);

        ClientResponse response = t2OAuthClient_1_2.viewFundingDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        
        boolean found = false;
        for(Funding funding : orcidMessage.getOrcidProfile().getOrcidActivities().getFundings().getFundings()) {
            if(fundingTitle.equals(funding.getTitle().getTitle().getContent())) {
                assertNotNull(funding.getPutCode());
                putCode = Long.valueOf(funding.getPutCode());
                found = true;
            }
        }
        assertTrue(found);
        // Delete it
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), putCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void addAffiliationTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String accessToken = getAccessToken(userId, password, Arrays.asList("/affiliations/read-limited", "/activities/update"), clientId, clientSecret,
                clientRedirectUri, true);
        String orgName = "Org_" + System.currentTimeMillis();
        Long putCode = null;
        Api12Helper.addAffiliation(userId, accessToken, orgName, t2OAuthClient_1_2);

        ClientResponse response = t2OAuthClient_1_2.viewAffiliationDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", response.getType().toString());
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        boolean found = false;
        for(Affiliation affiliation : orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation()) {
            if(orgName.equals(affiliation.getOrganization().getName())) {
                assertNotNull(affiliation.getPutCode());
                putCode = Long.valueOf(affiliation.getPutCode());
                found = true;
            }
        }
        assertTrue(found);
        
        //Delete it
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), putCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void personUpdateTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String giveName = getUser1GivenName();
        String familyName = getUser1FamilyNames();
        String creditName = getUser1CreditName();        
        
        String accessToken = getAccessToken(userId, password, Arrays.asList("/person/update", "/orcid-bio/read-limited"), clientId, clientSecret, clientRedirectUri,
                true);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setGivenNames(new GivenNames("My given name"));
        personalDetails.setFamilyName(new FamilyName("My family name"));
        CreditName creditNameElement = new CreditName("My credit name");
        creditNameElement.setVisibility(Visibility.LIMITED);
        personalDetails.setCreditName(creditNameElement);
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);

        ClientResponse clientResponse = t2OAuthClient_1_2.updateBioDetailsXml(userId, orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
        ClientResponse response = t2OAuthClient_1_2.viewBioDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage orcidMessageWithBio = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithBio);
        assertNotNull(orcidMessageWithBio.getOrcidProfile());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames());
        assertEquals("My given name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName());
        assertEquals("My family name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName());
        assertEquals("My credit name", orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertEquals(Visibility.LIMITED, orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getVisibility());
        
        //Rollback changes
        orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidBio = new OrcidBio();
        personalDetails = new PersonalDetails();
        personalDetails.setGivenNames(new GivenNames(giveName));
        personalDetails.setFamilyName(new FamilyName(familyName));
        creditNameElement = new CreditName(creditName);
        creditNameElement.setVisibility(Visibility.PUBLIC);
        personalDetails.setCreditName(creditNameElement);
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);

        clientResponse = t2OAuthClient_1_2.updateBioDetailsXml(userId, orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
        
        response = t2OAuthClient_1_2.viewBioDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        orcidMessageWithBio = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithBio);
        assertNotNull(orcidMessageWithBio.getOrcidProfile());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames());
        assertEquals(giveName, orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName());
        assertEquals(familyName, orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertNotNull(orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName());
        assertEquals(creditName, orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, orcidMessageWithBio.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getVisibility());
        
    }

    @Test
    public void externalIdentifiersUpdateTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String accessToken = getAccessToken(userId, password, Arrays.asList("/person/update", "/orcid-bio/read-limited"), clientId, clientSecret, clientRedirectUri,
                true);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidBio orcidBio = new OrcidBio();
        ExternalIdentifier extId = new ExternalIdentifier();
        Long time = System.currentTimeMillis();
        String commonName = "ext-id-common-name-" + time;
        String idReference = "ext-id-reference-" + time;
        extId.setExternalIdCommonName(new ExternalIdCommonName(commonName));
        extId.setExternalIdReference(new ExternalIdReference(idReference));
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        extIds.getExternalIdentifier().add(extId);
        orcidBio.setExternalIdentifiers(extIds);
        orcidProfile.setOrcidBio(orcidBio);
        orcidMessage.setOrcidProfile(orcidProfile);
        ClientResponse clientResponse = t2OAuthClient_1_2.addExternalIdentifiersXml(userId, orcidMessage, accessToken);
        assertEquals(200, clientResponse.getStatus());
        ClientResponse response = t2OAuthClient_1_2.viewBioDetailsXml(userId, accessToken);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        OrcidMessage orcidMessageWithExtIds = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithExtIds);
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile());
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile().getOrcidBio());
        assertNotNull(orcidMessageWithExtIds.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        boolean found = false;
        for(ExternalIdentifier newExtId : orcidMessageWithExtIds.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
            if(commonName.equals(newExtId.getExternalIdCommonName().getContent())) {
                assertEquals(idReference, newExtId.getExternalIdReference().getContent());
                found = true;
            }
        }
        assertTrue(found);
        
        //TODO: how to delete this guy?
    }

    @Test
    public void activitiesReadLimitedTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();

        String workTitle = "Work " + System.currentTimeMillis();
        String fundingTitle = "Funding " + System.currentTimeMillis();
        String orgName = "Org_" + System.currentTimeMillis();
        
        String accessToken = getAccessToken(userId, password, Arrays.asList("/read-limited", "/activities/update"), clientId, clientSecret, clientRedirectUri, true);
        Long workPutCode = null;
        Api12Helper.addWork(userId, accessToken, workTitle, t2OAuthClient_1_2);        
        Long fundingPutCode = null; 
        Api12Helper.addFunding(userId, accessToken, fundingTitle, t2OAuthClient_1_2);
        Long affiliationPutCode = null;
        Api12Helper.addAffiliation(userId, accessToken, orgName, t2OAuthClient_1_2);

        ClientResponse worksResponse = t2OAuthClient_1_2.viewWorksDetailsXml(userId, accessToken);
        assertNotNull(worksResponse);
        assertEquals(200, worksResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", worksResponse.getType().toString());
        OrcidMessage orcidMessageWithNewWork = worksResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewWork);
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertTrue(orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork().size() > 0);

        boolean workFound = false;

        for (OrcidWork work : orcidMessageWithNewWork.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork()) {
            if (workTitle.equals(work.getWorkTitle().getTitle().getContent())) {
                // Default user visibility should be public
                assertEquals(Visibility.PUBLIC, work.getVisibility());
                assertNotNull(work.getPutCode());
                workPutCode = Long.valueOf(work.getPutCode());
                workFound = true;
            }
        }

        assertTrue(workFound);

        ClientResponse fundingResponse = t2OAuthClient_1_2.viewFundingDetailsXml(userId, accessToken);
        assertNotNull(fundingResponse);
        assertEquals(200, fundingResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", fundingResponse.getType().toString());
        OrcidMessage orcidMessageWithNewFunding = fundingResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewFunding);
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings());
        assertNotNull(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings());
        assertTrue(orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings().size() > 0);

        boolean fundingFound = false;

        for (Funding funding : orcidMessageWithNewFunding.getOrcidProfile().getOrcidActivities().getFundings().getFundings()) {            
            if (fundingTitle.equals(funding.getTitle().getTitle().getContent())) {
                // Default user visibility should be public
                assertEquals(Visibility.PUBLIC, funding.getVisibility());
                assertNotNull(funding.getPutCode());
                fundingPutCode = Long.valueOf(funding.getPutCode());
                fundingFound = true;
            }
        }

        assertTrue(fundingFound);

        ClientResponse affiliationResponse = t2OAuthClient_1_2.viewAffiliationDetailsXml(userId, accessToken);
        assertNotNull(affiliationResponse);
        assertEquals(200, affiliationResponse.getStatus());
        assertEquals("application/vnd.orcid+xml; charset=UTF-8; qs=5", affiliationResponse.getType().toString());
        OrcidMessage orcidMessageWithNewAffiliation = affiliationResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessageWithNewAffiliation);
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations());
        assertNotNull(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation());
        assertTrue(orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().size() > 0);

        boolean affiliationFound = false;

        for (Affiliation affiliation : orcidMessageWithNewAffiliation.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation()) {
            if (orgName.equals(affiliation.getOrganization().getName())) {
                // Default user visibility should be public
                assertEquals(Visibility.PUBLIC, affiliation.getVisibility());
                assertNotNull(affiliation.getPutCode());
                affiliationPutCode = Long.valueOf(affiliation.getPutCode());
                affiliationFound = true;
            }
        }

        assertTrue(affiliationFound);
        
        // Delete work
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), workPutCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        // Delete funding
        deleteResponse = memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), fundingPutCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
                
        // Delete affiliation
        deleteResponse = memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), affiliationPutCode, accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}