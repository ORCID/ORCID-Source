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
package org.orcid.integration.blackbox.api.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.v2.rc4.BlackBoxBaseRC4;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.Work;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class AccessTokenSecurityChecksTest extends BlackBoxBaseRC4 {

    @BeforeClass
    public static void beforeClass() {
        BBBUtil.revokeApplicationsAccess(webDriver);
    }

    @AfterClass
    public static void afterClass() {
        BBBUtil.revokeApplicationsAccess(webDriver);
    }

    @Test
    public void testTokenIssuedForOneUserFailForOtherUsers() throws JSONException, InterruptedException, URISyntaxException {
        String accessToken = getNonCachedAccessTokens(getUser2OrcidId(), getUser2Password(), getScopes(), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        String orcid = getUser1OrcidId();
        Long putCode = 1L;

        Address address = (Address) unmarshallFromPath("/record_2.0_rc4/samples/address-2.0_rc4.xml", Address.class);
        evaluateResponse(memberV2ApiClient.createAddress(orcid, address, accessToken));

        Education education = (Education) unmarshallFromPath("/record_2.0_rc4/samples/education-2.0_rc4.xml", Education.class);
        evaluateResponse(memberV2ApiClient.createEducationJson(orcid, education, accessToken));
        evaluateResponse(memberV2ApiClient.createEducationXml(orcid, education, accessToken));

        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc4/samples/employment-2.0_rc4.xml", Employment.class);
        evaluateResponse(memberV2ApiClient.createEmploymentJson(orcid, employment, accessToken));
        evaluateResponse(memberV2ApiClient.createEmploymentXml(orcid, employment, accessToken));

        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0_rc4/samples/external-identifier-2.0_rc4.xml",
                PersonExternalIdentifier.class);
        evaluateResponse(memberV2ApiClient.createExternalIdentifier(orcid, externalIdentifier, accessToken));

        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc4/samples/funding-2.0_rc4.xml", Funding.class);
        evaluateResponse(memberV2ApiClient.createFundingJson(orcid, funding, accessToken));
        evaluateResponse(memberV2ApiClient.createFundingXml(orcid, funding, accessToken));

        Keyword keyword = (Keyword) unmarshallFromPath("/record_2.0_rc4/samples/keyword-2.0_rc4.xml", Keyword.class);
        evaluateResponse(memberV2ApiClient.createKeyword(orcid, keyword, accessToken));

        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc4/samples/other-name-2.0_rc4.xml", OtherName.class);
        evaluateResponse(memberV2ApiClient.createOtherName(orcid, otherName, accessToken));

        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc4/samples/peer-review-2.0_rc4.xml", PeerReview.class);
        evaluateResponse(memberV2ApiClient.createPeerReviewJson(orcid, peerReview, accessToken));
        evaluateResponse(memberV2ApiClient.createPeerReviewXml(orcid, peerReview, accessToken));

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_2.0_rc4/samples/researcher-url-2.0_rc4.xml", ResearcherUrl.class);
        evaluateResponse(memberV2ApiClient.createResearcherUrls(orcid, rUrl, accessToken));

        Work work = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
        evaluateResponse(memberV2ApiClient.createWorkJson(orcid, work, accessToken));
        evaluateResponse(memberV2ApiClient.createWorkXml(orcid, work, accessToken));

        evaluateResponse(memberV2ApiClient.deleteAddress(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteEducationXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteEmploymentXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteExternalIdentifier(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteFundingXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteKeyword(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteOtherName(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deletePeerReviewXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteResearcherUrl(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteWorkXml(orcid, putCode, accessToken));

        address.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateAddress(orcid, address, accessToken));

        education.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateEducation(orcid, education, accessToken));

        employment.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateEmployment(orcid, employment, accessToken));

        externalIdentifier.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateExternalIdentifier(orcid, externalIdentifier, accessToken));

        funding.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateFunding(orcid, funding, accessToken));

        keyword.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateKeyword(orcid, keyword, accessToken));

        otherName.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateOtherName(orcid, otherName, accessToken));

        peerReview.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updatePeerReview(orcid, peerReview, accessToken));

        rUrl.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateResearcherUrls(orcid, rUrl, accessToken));

        work.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateWork(orcid, work, accessToken));
        
        ClientResponse r = memberV2ApiClient.getResearcherUrls(orcid, accessToken);
        ResearcherUrls rUrls = r.getEntity(ResearcherUrls.class);
        if(rUrls != null && rUrls.getResearcherUrls() != null) {
            for(ResearcherUrl obj : rUrls.getResearcherUrls()) {
                assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
            }
        }
                                
        r = memberV2ApiClient.viewAddresses(orcid, accessToken);
        Addresses addresses = r.getEntity(Addresses.class);
        if(addresses != null && addresses.getAddress() != null) {
            for(Address obj : addresses.getAddress()) {
                assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
            }
        }
        
        r = memberV2ApiClient.viewExternalIdentifiers(orcid, accessToken);
        PersonExternalIdentifiers extIds = r.getEntity(PersonExternalIdentifiers.class);
        if(extIds != null && extIds.getExternalIdentifiers() != null) {
            for(PersonExternalIdentifier obj : extIds.getExternalIdentifiers()) {
                assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
            }
        }
        
        r = memberV2ApiClient.viewKeywords(orcid, accessToken);
        Keywords keywords = r.getEntity(Keywords.class);
        if(keywords != null && keywords.getKeywords() != null) {
            for(Keyword obj : keywords.getKeywords()) {
                assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
            }
        }
                
        r = memberV2ApiClient.viewOtherNames(orcid, accessToken);
        OtherNames otherNames = r.getEntity(OtherNames.class);
        if(otherNames != null && otherNames.getOtherNames() != null) {
            for(OtherName obj : otherNames.getOtherNames()) {
                assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
            }
        }
                
        r = memberV2ApiClient.viewBiography(orcid, accessToken);
        if(Status.OK.getStatusCode() == r.getStatus()) {
            Biography bio = r.getEntity(Biography.class);
            if(bio != null) {
                assertEquals(Visibility.PUBLIC.value(), bio.getVisibility().value());
            }
        } else if(Status.UNAUTHORIZED.getStatusCode() == r.getStatus()) {
            OrcidError error = r.getEntity(OrcidError.class);
            assertEquals(Integer.valueOf(9017), error.getErrorCode());
            assertTrue(error.getDeveloperMessage().contains("The biography is not public"));
        } else {
            fail("Expecting OK or UNAUTHORIZED, but got " + r.getStatus());
        }                
        
        r = memberV2ApiClient.viewPersonalDetailsXML(orcid, accessToken);
        PersonalDetails personalDetails = r.getEntity(PersonalDetails.class);
        if(personalDetails != null) {
            if(personalDetails.getBiography() != null) {
                assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
            }
            
            if(personalDetails.getName() != null) {
                assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
            } 
            
            if(personalDetails.getOtherNames() != null && personalDetails.getOtherNames().getOtherNames() != null) {
                for(OtherName obj : personalDetails.getOtherNames().getOtherNames()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
        }                
        
        r = memberV2ApiClient.viewActivities(orcid, accessToken);
        ActivitiesSummary summary = r.getEntity(ActivitiesSummary.class);
        if(summary != null) {
            if(summary.getEducations() != null && summary.getEducations().getSummaries() != null) {
                for(EducationSummary obj : summary.getEducations().getSummaries()){
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            
            if(summary.getEmployments() != null && summary.getEmployments().getSummaries() != null) {
                for(EmploymentSummary obj : summary.getEmployments().getSummaries()){
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            
            if(summary.getFundings() != null && summary.getFundings().getFundingGroup() != null) {
                for(FundingGroup g : summary.getFundings().getFundingGroup()) {
                    if(g.getFundingSummary() != null) {
                        for(FundingSummary obj : g.getFundingSummary()) {
                            assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                        }
                    }
                }
            }
            
            if(summary.getPeerReviews() != null && summary.getPeerReviews().getPeerReviewGroup() != null) {
                for(PeerReviewGroup g : summary.getPeerReviews().getPeerReviewGroup()) {
                    if(g.getPeerReviewSummary() != null) {
                        for(PeerReviewSummary obj : g.getPeerReviewSummary()) {
                            assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                        }
                    }
                }
            }
            
            if(summary.getWorks() != null && summary.getWorks().getWorkGroup() != null) {
                for(WorkGroup g : summary.getWorks().getWorkGroup()) {
                    if(g.getWorkSummary() != null) {
                        for(WorkSummary obj : g.getWorkSummary()) {
                            assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                        }
                    }
                }
            }
        }
        
        r = memberV2ApiClient.viewPerson(orcid, accessToken);           
        Person person = r.getEntity(Person.class);
        if(person != null) {
            if(person.getAddresses() != null && person.getAddresses().getAddress() != null) {
                for(Address obj : person.getAddresses().getAddress()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            if(person.getBiography() != null) {
                assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
            }
            if(person.getEmails() != null && person.getEmails().getEmails() != null) {
                for(Email obj : person.getEmails().getEmails()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            if(person.getExternalIdentifiers() != null && person.getExternalIdentifiers().getExternalIdentifiers() != null) {
                for(PersonExternalIdentifier obj : person.getExternalIdentifiers().getExternalIdentifiers()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            if(person.getKeywords() != null && person.getKeywords().getKeywords() != null) {
                for(Keyword obj : person.getKeywords().getKeywords()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            if(person.getName() != null) {
                assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());
            }
            if(person.getOtherNames() != null && person.getOtherNames().getOtherNames() != null) {
                for(OtherName obj : person.getOtherNames().getOtherNames()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
            if(person.getResearcherUrls() != null && person.getResearcherUrls().getResearcherUrls() != null) {
                for(ResearcherUrl obj : person.getResearcherUrls().getResearcherUrls()) {
                    assertEquals(Visibility.PUBLIC.value(), obj.getVisibility().value());
                }
            }
        }        
    }

    private List<String> getScopes() {
        return getScopes(ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.AFFILIATIONS_CREATE,
                ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AUTHENTICATE, ScopePathType.FUNDING_CREATE,
                ScopePathType.FUNDING_READ_LIMITED, ScopePathType.FUNDING_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE,
                ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    private void evaluateResponse(ClientResponse response) {
        assertNotNull(response);
        assertEquals(ClientResponse.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        OrcidError error = response.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: Access token is for a different record", error.getDeveloperMessage());
        assertEquals(Integer.valueOf(9017), error.getErrorCode());
    }
}
