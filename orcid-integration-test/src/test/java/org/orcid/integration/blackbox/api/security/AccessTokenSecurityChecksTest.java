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

import java.net.URISyntaxException;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.jaxb.model.error_rc2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.Work;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class AccessTokenSecurityChecksTest extends BlackBoxBaseRC2 {

    @BeforeClass
    public static void beforeClass() {
        revokeApplicationsAccess();
    }

    @AfterClass
    public static void afterClass() {
        revokeApplicationsAccess();
    }

    @Test
    public void testTokenIssuedForOneUserFailForOtherUsers() throws JSONException, InterruptedException, URISyntaxException {
        String accessToken = super.getAccessToken(getScopesString(), this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        String orcid = getUser2OrcidId();
        Long putCode = 1L;

        Address address = (Address) unmarshallFromPath("/record_2.0_rc2/samples/address-2.0_rc2.xml", Address.class);
        evaluateResponse(memberV2ApiClient.createAddress(orcid, address, accessToken));

        Education education = (Education) unmarshallFromPath("/record_2.0_rc2/samples/education-2.0_rc2.xml", Education.class);
        evaluateResponse(memberV2ApiClient.createEducationJson(orcid, education, accessToken));
        evaluateResponse(memberV2ApiClient.createEducationXml(orcid, education, accessToken));

        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc2/samples/employment-2.0_rc2.xml", Employment.class);
        evaluateResponse(memberV2ApiClient.createEmploymentJson(orcid, employment, accessToken));
        evaluateResponse(memberV2ApiClient.createEmploymentXml(orcid, employment, accessToken));

        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml",
                PersonExternalIdentifier.class);
        evaluateResponse(memberV2ApiClient.createExternalIdentifier(orcid, externalIdentifier, accessToken));

        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc2/samples/funding-2.0_rc2.xml", Funding.class);
        evaluateResponse(memberV2ApiClient.createFundingJson(orcid, funding, accessToken));
        evaluateResponse(memberV2ApiClient.createFundingXml(orcid, funding, accessToken));

        Keyword keyword = (Keyword) unmarshallFromPath("/record_2.0_rc2/samples/keyword-2.0_rc2.xml", Keyword.class);
        evaluateResponse(memberV2ApiClient.createKeyword(orcid, keyword, accessToken));

        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc2/samples/other-name-2.0_rc2.xml", OtherName.class);
        evaluateResponse(memberV2ApiClient.createOtherName(orcid, otherName, accessToken));

        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc2/samples/peer-review-2.0_rc2.xml", PeerReview.class);
        evaluateResponse(memberV2ApiClient.createPeerReviewJson(orcid, peerReview, accessToken));
        evaluateResponse(memberV2ApiClient.createPeerReviewXml(orcid, peerReview, accessToken));

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_2.0_rc2/samples/researcher-url-2.0_rc2.xml", ResearcherUrl.class);
        evaluateResponse(memberV2ApiClient.createResearcherUrls(orcid, rUrl, accessToken));

        Work work = (Work) unmarshallFromPath("/record_2.0_rc2/samples/work-2.0_rc2.xml", Work.class);
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
        evaluateResponse(memberV2ApiClient.getEmails(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.getResearcherUrl(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.getResearcherUrls(orcid, accessToken));

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

        evaluateResponse(memberV2ApiClient.viewActivities(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewAddress(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewAddresses(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewBiography(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewEducationXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewEmploymentXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewExternalIdentifier(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewExternalIdentifiers(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewFundingXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewKeyword(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewKeywords(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewOtherName(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewOtherNames(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewPeerReviewXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.viewPerson(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewPersonalDetailsXML(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewWorkXml(orcid, putCode, accessToken));
    }

    private String getScopesString() {
        return ScopePathType.ACTIVITIES_READ_LIMITED.value() + " " + ScopePathType.ACTIVITIES_UPDATE.value() + " " + ScopePathType.AFFILIATIONS_CREATE.value() + " "
                + ScopePathType.AFFILIATIONS_READ_LIMITED.value() + " " + ScopePathType.AFFILIATIONS_UPDATE.value() + " " + ScopePathType.AUTHENTICATE.value() + " "
                + ScopePathType.FUNDING_CREATE.value() + " " + ScopePathType.FUNDING_READ_LIMITED.value() + " " + ScopePathType.FUNDING_UPDATE.value() + " "
                + ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.value() + " " + ScopePathType.ORCID_BIO_READ_LIMITED.value() + " "
                + ScopePathType.ORCID_BIO_UPDATE.value() + " " + ScopePathType.ORCID_PROFILE_READ_LIMITED.value() + " " + ScopePathType.ORCID_WORKS_CREATE.value() + " "
                + ScopePathType.ORCID_WORKS_READ_LIMITED.value() + " " + ScopePathType.ORCID_WORKS_UPDATE.value() + " " + ScopePathType.PEER_REVIEW_CREATE.value() + " "
                + ScopePathType.PEER_REVIEW_READ_LIMITED.value();
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
