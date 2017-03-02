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
package org.orcid.integration.blackbox.api.v2.rc1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc1.Day;
import org.orcid.jaxb.model.common_rc1.Month;
import org.orcid.jaxb.model.common_rc1.Year;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Relationship;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
public class PublicV2Test extends BlackBoxBaseRC1 {
    @Resource(name = "publicV2ApiClient_rc1")
    private PublicV2ApiClientImpl publicV2ApiClient;    
    
    @Before
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
        createGroupIds();
    }

    @After
    public void after() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }    
        
    @Test
    public void testPublicClientCanGetAccessToken() throws InterruptedException, JSONException {
        String publicAccessToken = oauthHelper.getClientCredentialsAccessToken(getPublicClientId(), getPublicClientSecret(), ScopePathType.READ_PUBLIC, APIRequestType.PUBLIC);
        assertFalse(PojoUtil.isEmpty(publicAccessToken));
    }
    
    @Test
    public void testGetInfoWithEmptyToken() throws InterruptedException, JSONException {
        ClientResponse activitiesResponse = publicV2ApiClient.viewActivities(getUser1OrcidId(), "");
        assertNotNull(activitiesResponse);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
    }    
    
    /**
     * VIEW PUBLIC INFO
     * */
    @Test
    public void testViewWorkAndWorkSummary() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkWorks(null);
    }
        
    @Test
    public void testViewWorkAndWorkSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkWorks(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }
    
    private void checkWorks(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        ClientResponse getWorkResponse = null;
        if(readPublicToken != null) {
            getWorkResponse = publicV2ApiClient.viewWorkXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getWorkResponse = publicV2ApiClient.viewWorkXml(getUser1OrcidId(), putCode);
        }
        checkResponse(getWorkResponse);        
        assertNotNull(getWorkResponse);
        Work work = getWorkResponse.getEntity(Work.class);
        assertNotNull(work);
        assertEquals("Current treatment of left main coronary artery disease", work.getWorkTitle().getTitle().getContent());

        ClientResponse getWorkSummaryResponse = null;
        
        if(readPublicToken != null) {
            getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(getUser1OrcidId(), putCode);
        }
        
        assertNotNull(getWorkSummaryResponse);
        checkResponse(getWorkSummaryResponse);
        WorkSummary summary = getWorkSummaryResponse.getEntity(WorkSummary.class);
        assertNotNull(summary);
        assertEquals("Current treatment of left main coronary artery disease", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkFunding(null);
    }
    
    @Test
    public void testViewFundingAndFundingSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkFunding(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }
    
    private void checkFunding(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(getUser1OrcidId(), fundingToCreate, accessToken);
        assertNotNull(postResponse);        
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getFundingResponse = null;
        
        if(readPublicToken != null) {
            getFundingResponse = publicV2ApiClient.viewFundingXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getFundingResponse = publicV2ApiClient.viewFundingXml(getUser1OrcidId(), putCode);
        }
        
        assertNotNull(getFundingResponse);
        checkResponse(getFundingResponse);
        Funding funding = getFundingResponse.getEntity(Funding.class);
        assertNotNull(funding);
        assertEquals("common:title", funding.getTitle().getTitle().getContent());

        ClientResponse getFundingSummaryResponse = null;
        
        if(readPublicToken != null) {
            getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(getUser1OrcidId(), putCode);
        }
        
        assertNotNull(getFundingSummaryResponse);
        checkResponse(getFundingSummaryResponse);
        FundingSummary summary = getFundingSummaryResponse.getEntity(FundingSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkEmployment(null);
    }
    
    @Test
    public void testViewEmploymentAndEmploymentSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkEmployment(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }
        
    public void checkEmployment(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(getUser1OrcidId(), employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEmploymentResponse = null;
        
        if(readPublicToken != null) {
            getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(getUser1OrcidId(), putCode);
        }
        
        assertNotNull(getEmploymentResponse);
        checkResponse(getEmploymentResponse);
        Employment employment = getEmploymentResponse.getEntity(Employment.class);
        assertNotNull(employment);
        assertEquals("affiliation:department-name", employment.getDepartmentName());

        ClientResponse getEmploymentSummaryResponse = null;
        
        if(readPublicToken != null) {
            getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(getUser1OrcidId(), putCode);
        }
        
        assertNotNull(getEmploymentSummaryResponse);
        checkResponse(getEmploymentSummaryResponse);
        EmploymentSummary summary = getEmploymentSummaryResponse.getEntity(EmploymentSummary.class);
        assertNotNull(summary);
        assertEquals("affiliation:department-name", summary.getDepartmentName());
    }
    
    @Test
    public void testViewEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkEducation(null);
    }
    
    @Test
    public void testViewEducationAndEducationSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkEducation(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }
        
    public void checkEducation(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(getUser1OrcidId(), educationToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEducationResponse = null;
        if(readPublicToken != null) {
            getEducationResponse = publicV2ApiClient.viewEducationXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getEducationResponse = publicV2ApiClient.viewEducationXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(getEducationResponse);
        checkResponse(getEducationResponse);
        Education education = getEducationResponse.getEntity(Education.class);
        assertNotNull(education);
        assertEquals("education:department-name", education.getDepartmentName());

        ClientResponse getEducationSummaryResponse = null;
        if(readPublicToken != null) {
            getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(getEducationSummaryResponse);
        checkResponse(getEducationSummaryResponse);
        EducationSummary summary = getEducationSummaryResponse.getEntity(EducationSummary.class);
        assertNotNull(summary);        
        assertEquals("education:department-name", summary.getDepartmentName());
    }
    
    @Test
    public void testViewPeerReviewAndPeerReviewSummary() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkPeerReview(null);
    }
    
    @Test
    public void testViewPeerReviewAndPeerReviewSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkPeerReview(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }    
    
    public void checkPeerReview(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getPeerReviewResponse = null;
        if(readPublicToken != null) {
            getPeerReviewResponse = publicV2ApiClient.viewPeerReviewXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getPeerReviewResponse = publicV2ApiClient.viewPeerReviewXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(getPeerReviewResponse);
        checkResponse(getPeerReviewResponse);
        PeerReview peerReview = getPeerReviewResponse.getEntity(PeerReview.class);
        assertNotNull(peerReview);
        assertEquals("peer-review:url", peerReview.getUrl().getValue());

        ClientResponse getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(getUser1OrcidId(), putCode);
        if(readPublicToken != null) {
            getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(getPeerReviewSummaryResponse);
        checkResponse(getPeerReviewSummaryResponse);
        PeerReviewSummary summary = getPeerReviewSummaryResponse.getEntity(PeerReviewSummary.class);
        assertNotNull(summary);        
        assertEquals("1848", summary.getCompletionDate().getYear().getValue());
    }
                
    @Test
    public void testViewPublicActivities() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkPublicActivities(null);
    }
    
    @Test
    public void testViewPublicActivitiesUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        checkPublicActivities(getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()));
    }
    
    public void checkPublicActivities(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        createActivities();                
        ClientResponse activitiesResponse = null;
        
        if(readPublicToken != null) {
            activitiesResponse = publicV2ApiClient.viewActivities(getUser1OrcidId(), readPublicToken);
        } else {
            activitiesResponse = publicV2ApiClient.viewActivities(getUser1OrcidId());
        }
        
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        assertNotNull("There are no educations, please verify users default visibility is public", summary.getEducations());
        assertFalse(summary.getEducations().getSummaries().isEmpty());
        boolean found0 = false, found3 = false;
        for(EducationSummary education : summary.getEducations().getSummaries()) {
            if(education.getDepartmentName() != null) {
                if(education.getDepartmentName().equals("Education # 0")) {
                    found0 = true;
                } else if (education.getDepartmentName().equals("Education # 3")) {
                    found3 = true;
                }
            }            
        }
        
        assertTrue("One of the educations was not found: 0(" + found0 + ") 3(" + found3 + "), please verify users default visibility is public", found0 == found3 == true);
        
        assertNotNull("There are no employment, please verify users default visibility is public", summary.getEmployments());
        assertFalse(summary.getEmployments().getSummaries().isEmpty());
        found0 = found3 = false;
        for(EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            if(employment.getDepartmentName() != null) {
                if(employment.getDepartmentName().equals("Employment # 0")) {
                    found0 = true;
                } else if (employment.getDepartmentName().equals("Employment # 3")) {
                    found3 = true;
                }
            }            
        }
        
        assertTrue("One of the employments was not found: 0(" + found0 + ") 3(" + found3 + "), please verify users default visibility is public", found0 == found3 == true);
        
        assertNotNull(summary.getFundings());
        found0 = found3 = false;
        for(FundingGroup group : summary.getFundings().getFundingGroup()) {
            for(FundingSummary funding : group.getFundingSummary()) {
                if(funding.getTitle() != null && funding.getTitle().getTitle() != null) {
                    if(funding.getTitle().getTitle().getContent().equals("Funding # 0")) {
                        found0 = true;
                    } else if(funding.getTitle().getTitle().getContent().equals("Funding # 3")) {
                        found3 = true;
                    }
                }                
            }
        }
        
        assertTrue("One of the fundings was not found: 0(" + found0 + ") 3(" + found3 + "), please verify users default visibility is public", found0 == found3 == true);
        
        assertNotNull(summary.getWorks());
        found0 = found3 = false;
        for(WorkGroup group : summary.getWorks().getWorkGroup()) {
            for(WorkSummary work : group.getWorkSummary()) {
                if(work.getTitle().getTitle().getContent().equals("Work # 0")) {
                    found0 = true;
                } else if(work.getTitle().getTitle().getContent().equals("Work # 3")) {
                    found3 = true;
                }
            }
        }
        
        assertTrue("One of the works was not found: 0(" + found0 + ") 3(" + found3 + "), please verify users default visibility is public", found0 == found3 == true);
        
        assertNotNull(summary.getPeerReviews());
        found0 = found3 = false;
        for(PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            for(PeerReviewSummary peerReview : group.getPeerReviewSummary()) {
                if(peerReview.getCompletionDate() != null && peerReview.getCompletionDate().getYear() != null) {
                    if(peerReview.getCompletionDate().getYear().getValue().equals("1000")) {
                        found0 = true;
                    } else if(peerReview.getCompletionDate().getYear().getValue().equals("4000")) {
                        found3 = true;
                    }
                }
            }
        }
        
        assertTrue("One of the peer reviews was not found: 0(" + found0 + ") 3(" + found3 + "), please verify users default visibility is public", found0 == found3 == true);
    }
    
    @Test
    public void testViewPublicActivitiesUsingInvalidToken() throws JSONException, InterruptedException, URISyntaxException {
        createActivities();                
        String wrongToken = getReadPublicAccessToken(getClient1ClientId(), getClient1ClientSecret()) + "!";
        ClientResponse activitiesResponse = publicV2ApiClient.viewActivities(getUser1OrcidId(), wrongToken);
        assertNotNull(activitiesResponse);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), activitiesResponse.getStatus());
    }

    /**
     * TRY TO VIEW LIMITED INFO
     * */
    @Test
    public void testViewLimitedWorkAndWorkSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedWork(null);
    }
    
    @Test
    public void testViewLimitedWorkAndWorkSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedWork(getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));
    }
         
    public void checkLimitedWork(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewWorkXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewWorkXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewWorkSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewWorkSummaryXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

    }

    @Test
    public void testViewLimitedFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedFunding(null);
    }
    
    @Test
    public void testViewLimitedFundingAndFundingSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedFunding(getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));
    }
    
    public void checkLimitedFunding(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(getUser1OrcidId(), fundingToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewFundingXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewFundingXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewFundingSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewFundingSummaryXml(getUser1OrcidId(), putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

    }

    @Test
    public void testViewLimitedEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEmployment(null);
    }
    
    @Test
    public void testViewLimitedEmploymentAndEmploymentSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEmployment(getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));
    }
    
    public void checkLimitedEmployment(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(getUser1OrcidId(), employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewEmploymentXml(getUser1OrcidId(), putCode);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

        response = publicV2ApiClient.viewEmploymentSummaryXml(getUser1OrcidId(), putCode);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());
        
    }

    @Test
    public void testViewLimitedEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEducation(null);
    }
    
    @Test
    public void testViewLimitedEducationAndEducationSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEducation(getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));
    }
    
    public void checkLimitedEducation(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        //Change the default user visibility to public so we can create a limited work
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(getUser1OrcidId(), educationToCreate, accessToken);
        
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewEducationXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewEducationXml(getUser1OrcidId(), putCode);
        }
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewEducationSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewEducationSummaryXml(getUser1OrcidId(), putCode);
        }
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());
        
    }
    
    @Test
    public void testViewLimitedPeerReviewAndPeerReviewSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedPeerReview(null);
    }
    
    @Test
    public void testViewLimitedPeerReviewAndPeerReviewSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedPeerReview(getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));
    }
    
    public void checkLimitedPeerReview(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        //Change the default user visibility to public so we can create a limited work
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewPeerReviewXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewPeerReviewXml(getUser1OrcidId(), putCode);
        }
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewPeerReviewSummaryXml(getUser1OrcidId(), putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewPeerReviewSummaryXml(getUser1OrcidId(), putCode);
        }
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9039), result.getErrorCode());
        assertEquals("403 Forbidden: The item is not public and cannot be accessed with the Public API.", result.getDeveloperMessage());
        
    }

    @Test
    public void testNotFoundReturn404() throws InterruptedException, JSONException {
        ClientResponse response = publicV2ApiClient.viewActivities("0000-0000-0000-0000");     
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = publicV2ApiClient.viewActivities("0000-0000-0000-0000", getReadPublicAccessToken(getClient2ClientId(), getClient2ClientSecret()));     
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());        
    }        
   
    private void createActivities() throws JSONException, InterruptedException, URISyntaxException {
        String accessToken = getAccessToken();
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);

        for (int i = 0; i < 4; i++) {            
            workToCreate.setPutCode(null);
            workToCreate.getWorkTitle().getTitle().setContent("Work # " + i);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
            WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
            wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId(time + " Work Id " + i));
            wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
            wExtId.setRelationship(Relationship.SELF);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
            if (i == 0 || i == 3)
                workToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
            else if (i == 1)
                workToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
            else
                workToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createWorkXml(getUser1OrcidId(), workToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        for (int i = 0; i < 4; i++) {
            fundingToCreate.setPutCode(null);
            fundingToCreate.getTitle().getTitle().setContent("Funding # " + i);
            fundingToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
            FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
            fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
            fExtId.setValue(time + " funding Id " + i);
            fExtId.setRelationship(Relationship.SELF);
            fundingToCreate.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
            if (i == 0 || i == 3)
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
            else if (i == 1)
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
            else
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createFundingXml(getUser1OrcidId(), fundingToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        for (int i = 0; i < 4; i++) {
            employmentToCreate.setPutCode(null);
            employmentToCreate.setRoleTitle("Employment # " + i);
            if (i == 0 || i == 3)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
            else if (i == 1)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
            else
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(getUser1OrcidId(), employmentToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        for (int i = 0; i < 4; i++) {
            educationToCreate.setPutCode(null);
            educationToCreate.setRoleTitle("Education # " + i);
            if (i == 0 || i == 3)
                educationToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
            else if (i == 1)
                educationToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
            else
                educationToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEducationXml(getUser1OrcidId(), educationToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }
        
        
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        for (int i = 0; i < 4; i++) {
            peerReviewToCreate.setPutCode(null);
            peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
            peerReviewToCreate.getSubjectName().getTitle().setContent("PeerReview # " + i);
            peerReviewToCreate.getCompletionDate().setDay(new Day(i + 1));
            peerReviewToCreate.getCompletionDate().setMonth(new Month(i + 1));
            peerReviewToCreate.getCompletionDate().setYear(new Year((i + 1) * 1000));
            peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().setContent("extId-" + (i + 1));
            if (i == 0 || i == 3) {
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PUBLIC);
            } else if (i == 1) {
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.LIMITED);
            } else {
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common_rc1.Visibility.PRIVATE);
            }              

            ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(getUser1OrcidId(), peerReviewToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }
    }

    public void cleanActivities() throws JSONException, InterruptedException, URISyntaxException {
        // Remove all activities
        String token = getAccessToken();
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(getUser1OrcidId(), token);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        if (summary.getEducations() != null && !summary.getEducations().getSummaries().isEmpty()) {
            for (EducationSummary education : summary.getEducations().getSummaries()) {
                memberV2ApiClient.deleteEducationXml(getUser1OrcidId(), education.getPutCode(), token);
            }
        }

        if (summary.getEmployments() != null && !summary.getEmployments().getSummaries().isEmpty()) {
            for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
                memberV2ApiClient.deleteEmploymentXml(getUser1OrcidId(), employment.getPutCode(), token);
            }
        }

        if (summary.getFundings() != null && !summary.getFundings().getFundingGroup().isEmpty()) {
            for (FundingGroup group : summary.getFundings().getFundingGroup()) {
                for (FundingSummary funding : group.getFundingSummary()) {
                    memberV2ApiClient.deleteFundingXml(getUser1OrcidId(), funding.getPutCode(), token);
                }
            }
        }

        if (summary.getWorks() != null && !summary.getWorks().getWorkGroup().isEmpty()) {
            for (WorkGroup group : summary.getWorks().getWorkGroup()) {
                for (WorkSummary work : group.getWorkSummary()) {
                    memberV2ApiClient.deleteWorkXml(getUser1OrcidId(), work.getPutCode(), token);
                }
            }
        }
        
        if (summary.getPeerReviews() != null && !summary.getPeerReviews().getPeerReviewGroup().isEmpty()) {
            for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
                for (PeerReviewSummary peerReview : group.getPeerReviewSummary()) {
                    memberV2ApiClient.deletePeerReviewXml(getUser1OrcidId(), peerReview.getPutCode(), token);
                }
            }
        }
    }
    
    private void checkResponse(ClientResponse response) {
        if(Response.Status.FORBIDDEN.getStatusCode() == response.getStatus()) {
            fail("Activity is not public, please verify users default visibility is public");
        }
    }
    
    private String getReadPublicAccessToken(String clientId, String clientSecret) throws InterruptedException, JSONException {
        return getClientCredentialsAccessToken(ScopePathType.READ_PUBLIC, clientId, clientSecret, APIRequestType.PUBLIC);
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED));        
    }    
}
