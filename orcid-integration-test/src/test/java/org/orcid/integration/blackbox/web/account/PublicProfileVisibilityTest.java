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
package org.orcid.integration.blackbox.web.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Organization;
import org.orcid.jaxb.model.common_v2.OrganizationAddress;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Role;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class PublicProfileVisibilityTest extends BlackBoxBaseV2Release {
    @BeforeClass
    public static void before() {
        signin();
    }

    @AfterClass
    public static void after() {
        signout();
    }

    @Test
    public void emailPrivacyTest() throws InterruptedException {
        //Add a public email
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        addEmail(emailValue, Visibility.PRIVATE);
        
        showPublicProfilePage(getUser1OrcidId());
        try {
            //Verify it doesn't appear in the public page
            emailAppearsInPublicPage(emailValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updateEmailVisibility(emailValue, Visibility.LIMITED);
        showPublicProfilePage(getUser1OrcidId());
        try {
            //Verify it doesn't appear in the public page
            emailAppearsInPublicPage(emailValue);
            fail();
        } catch(Exception e) {
            
        }        
        
        //Change visibility to public
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updateEmailVisibility(emailValue, Visibility.PUBLIC);
        //Verify it appears in the public page
        showPublicProfilePage(getUser1OrcidId());
        emailAppearsInPublicPage(emailValue);
        
        //Delete the new email
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        removeEmail(emailValue);
    }

    @Test
    public void otherNamesPrivacyTest() throws InterruptedException, JSONException {
        String otherNameValue = "added-other-name-" + System.currentTimeMillis();
                        
        //Create a new other name and set it to public  
        showMyOrcidPage();
        openEditOtherNamesModal();
        createOtherName(otherNameValue);
        changeOtherNamesVisibility(Visibility.PRIVATE);
        saveOtherNamesModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            otherNamesAppearsInPublicPage(otherNameValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditOtherNamesModal();        
        changeOtherNamesVisibility(Visibility.LIMITED);
        saveOtherNamesModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            otherNamesAppearsInPublicPage(otherNameValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility again to public
        showMyOrcidPage();
        openEditOtherNamesModal();        
        changeOtherNamesVisibility(Visibility.PUBLIC);
        saveOtherNamesModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        otherNamesAppearsInPublicPage(otherNameValue);
        
        //Delete it
        showMyOrcidPage();
        openEditOtherNamesModal();
        deleteOtherNames();
        saveOtherNamesModal();
    }    
        
    @Test
    public void addressPrivacyTest() throws InterruptedException, JSONException {        
        openEditAddressModal();
        deleteAddresses();
        createAddress(Iso3166Country.AD.name());
        changeAddressVisibility(Visibility.PUBLIC);
        saveEditAddressModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        addressAppearsInPublicPage("Andorra");
        
        //Change visibility to private
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(Visibility.PRIVATE);
        saveEditAddressModal();
        
        //Verify it doesn't appears in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            addressAppearsInPublicPage("Andorra");
            fail();
        } catch(Exception e) {
            
        }
                
        //Change visibility to limited
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(Visibility.LIMITED);
        saveEditAddressModal();
        
        //Verify it doesn't appears again in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            addressAppearsInPublicPage("Andorra");
            fail();
        } catch(Exception e) {
            
        }                               
        
        //Change it to public again and verify it appears in the public paget
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(Visibility.PUBLIC);
        saveEditAddressModal();
        showPublicProfilePage(getUser1OrcidId());
        addressAppearsInPublicPage("Andorra");
        
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();
    }
    
    @Test
    public void keywordPrivacyTest() throws InterruptedException, JSONException {
        String keywordValue = "added-keyword-" + System.currentTimeMillis();
        //Create a new other name and set it to public
        showMyOrcidPage();
        openEditKeywordsModal();
        createKeyword(keywordValue);
        changeKeywordsVisibility(Visibility.PRIVATE);
        saveKeywordsModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            keywordsAppearsInPublicPage(keywordValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditKeywordsModal();        
        changeKeywordsVisibility(Visibility.LIMITED);
        saveKeywordsModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            keywordsAppearsInPublicPage(keywordValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility again to public
        showMyOrcidPage();
        openEditKeywordsModal();
        changeKeywordsVisibility(Visibility.PUBLIC);
        saveKeywordsModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        keywordsAppearsInPublicPage(keywordValue);
        
        //Delete it
        showMyOrcidPage();
        openEditKeywordsModal();        
        deleteKeywords();
        saveKeywordsModal();
    }

    @Test
    public void websitesPrivacyTest() throws InterruptedException, JSONException {
        String rUrl = "http://test.orcid.org/" + System.currentTimeMillis();        
        //Create a new other name and set it to public
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        createResearcherUrl(rUrl);
        changeResearcherUrlsVisibility(Visibility.PRIVATE);
        saveResearcherUrlsModal();
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            researcherUrlAppearsInPublicPage(rUrl);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(Visibility.LIMITED);
        saveResearcherUrlsModal();
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            researcherUrlAppearsInPublicPage(rUrl);
            fail();
        } catch(Exception e) {
            
        }
          
        //Change visibility to public
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(Visibility.PUBLIC);
        saveResearcherUrlsModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        researcherUrlAppearsInPublicPage(rUrl);
        
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        deleteResearcherUrls();
    }
    
    @Test
    public void externalIdentifiersPrivacyTest() throws InterruptedException, JSONException {
        String extId = "added-ext-id-" + System.currentTimeMillis();
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE));
        //Create a new external identifier and set it to public
        createExternalIdentifier(extId, getUser1OrcidId(), accessToken);
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.PRIVATE);
        saveExternalIdentifiersModal();
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            externalIdentifiersAppearsInPublicPage(extId);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.LIMITED);
        saveExternalIdentifiersModal();
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            externalIdentifiersAppearsInPublicPage(extId);
            fail();
        } catch(Exception e) {
            
        }              
        
        //Change visibility back to public
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.PUBLIC);
        saveExternalIdentifiersModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        externalIdentifiersAppearsInPublicPage(extId);
        
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        deleteExternalIdentifiers();
    }

    @Test
    public void workPrivacyTest() throws InterruptedException, JSONException {
        String workTitle = "added-work-" + System.currentTimeMillis();
        showMyOrcidPage();
        openAddWorkModal();
        createWork(workTitle);
        changeWorksVisibility(workTitle, Visibility.PRIVATE);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            workAppearsInPublicPage(workTitle);
            fail();
        } catch(Exception e) {
            
        }    
    
        //Change visibility to limited
        showMyOrcidPage();
        changeWorksVisibility(workTitle, Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            workAppearsInPublicPage(workTitle);
            fail();
        } catch(Exception e) {
            
        }
        
        showMyOrcidPage();
        changeWorksVisibility(workTitle, Visibility.PUBLIC);
        
        //Verify it appear in the public page
        showPublicProfilePage(getUser1OrcidId());
        workAppearsInPublicPage(workTitle);
        
        showMyOrcidPage();
        deleteWork(workTitle);
    }
    
    @Test
    public void educationPrivacyTest() {
        String institutionName = "added-education-" + System.currentTimeMillis();
        showMyOrcidPage();
        openAddEducationModal();
        createEducation(institutionName);
        changeEducationVisibility(institutionName, Visibility.PRIVATE);        
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            educationAppearsInPublicPage(institutionName);
            fail();
        } catch(Exception e) {
            
        }   
        
        showMyOrcidPage();
        changeEducationVisibility(institutionName, Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            educationAppearsInPublicPage(institutionName);
            fail();
        } catch(Exception e) {
            
        } 
        
        showMyOrcidPage();
        changeEducationVisibility(institutionName, Visibility.PUBLIC);
        
        //Verify it appears in the public page
        showPublicProfilePage(getUser1OrcidId());
        educationAppearsInPublicPage(institutionName);
        
        showMyOrcidPage();
        deleteEducation(institutionName);
    }
    
    @Test
    public void employmentPrivacyTest() {
        String institutionName = "added-employment-" + System.currentTimeMillis();
        showMyOrcidPage();
        openAddEmploymentModal();
        createEmployment(institutionName);
        changeEmploymentVisibility(institutionName, Visibility.PRIVATE);        
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            employmentAppearsInPublicPage(institutionName);
            fail();
        } catch(Exception e) {
            
        }
        
        showMyOrcidPage();
        changeEmploymentVisibility(institutionName, Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            employmentAppearsInPublicPage(institutionName);
            fail();
        } catch(Exception e) {
            
        } 
        
        showMyOrcidPage();
        changeEmploymentVisibility(institutionName, Visibility.PUBLIC);
        
        //Verify it appears in the public page
        showPublicProfilePage(getUser1OrcidId());
        employmentAppearsInPublicPage(institutionName);
        
        showMyOrcidPage();
        deleteEmployment(institutionName);
    }

    @Test
    public void fundingPrivacyTest() throws InterruptedException {
        String fundingTitle = "added-funding-" + System.currentTimeMillis();
        showMyOrcidPage();
        openAddFundingModal();
        createFunding(fundingTitle);
        changeFundingVisibility(fundingTitle, Visibility.PRIVATE);       
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            fundingAppearsInPublicPage(fundingTitle);
            fail();
        } catch(Exception e) {
            
        }
        
        showMyOrcidPage();
        changeFundingVisibility(fundingTitle, Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            fundingAppearsInPublicPage(fundingTitle);
            fail();
        } catch(Exception e) {
            
        } 
        
        showMyOrcidPage();
        changeFundingVisibility(fundingTitle, Visibility.PUBLIC);
        
        //Verify it appears in the public page
        showPublicProfilePage(getUser1OrcidId());
        fundingAppearsInPublicPage(fundingTitle);
        
        showMyOrcidPage();
        deleteFunding(fundingTitle);
    }
    
    @Test
    public void peerReviewPrivacyTest() throws InterruptedException, JSONException, URISyntaxException {
        // Create peer review group               
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE));
        List<GroupIdRecord> groups = createGroupIds();
        assertNotNull(groups);
        assertTrue(groups.size() > 0);
        GroupIdRecord g1 = groups.get(0);

        // Create peer review
        long time = System.currentTimeMillis();
        PeerReview peerReview = new PeerReview();        
        peerReview.setGroupId(g1.getGroupId());
        ExternalIDs extIds = new ExternalIDs();
        peerReview.setExternalIdentifiers(extIds);
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        Organization organization = new Organization();
        organization.setName("My org name " + System.currentTimeMillis());
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("Imagination city");
        address.setCountry(Iso3166Country.US);
        organization.setAddress(address);
        peerReview.setOrganization(organization);
        peerReview.setRole(Role.CHAIR);
        peerReview.setType(PeerReviewType.EVALUATION);
        peerReview.setCompletionDate(new FuzzyDate(new Year(2016), new Month(1), new Day(1)));
        
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        peerReview = getResponse.getEntity(PeerReview.class);
        
        showMyOrcidPage();
        changePeerReviewVisibility(g1.getName(), Visibility.PRIVATE);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            peerReviewAppearsInPublicPage(g1.getName());
            fail();
        } catch(Exception e) {
            
        } 
        
        showMyOrcidPage();
        changePeerReviewVisibility(g1.getName(), Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            peerReviewAppearsInPublicPage(g1.getName());
            fail();
        } catch(Exception e) {
            
        }
        
        showMyOrcidPage();
        changePeerReviewVisibility(g1.getName(), Visibility.PUBLIC);
        
        showPublicProfilePage(getUser1OrcidId());
        peerReviewAppearsInPublicPage(g1.getName());
        
        // Rollback
        ClientResponse deleteResponse = memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), peerReview.getPutCode(), accessToken);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
