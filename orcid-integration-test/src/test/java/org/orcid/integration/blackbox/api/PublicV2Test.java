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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.common.Day;
import org.orcid.jaxb.model.common.Month;
import org.orcid.jaxb.model.common.Year;
import org.orcid.jaxb.model.error.OrcidError;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
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
import org.orcid.jaxb.model.record_rc1.Activity;
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
import org.springframework.beans.factory.annotation.Value;
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
public class PublicV2Test {
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.publicClient1.clientId}")
    public String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    public String publicClientSecret;
        
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource(name = "memberV2ApiClient_rc1")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc1")
    private PublicV2ApiClientImpl publicV2ApiClient;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private OauthHelper oauthHelper;

    static String accessToken = null;
    
    static String publicAccessToken = null;

    static List<GroupIdRecord> groupRecords = null;
    
    @Before
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
        groupRecords = createGroupIds();
    }

    @After
    public void after() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    @Test
    public void testCantGetTokenForInternalScopes() {
        
    }
    
    
    @Test
    public void testPublicClientCanGetAccessToken() throws InterruptedException, JSONException {
        String publicAccessToken = oauthHelper.getClientCredentialsAccessToken(publicClientId, publicClientSecret, ScopePathType.READ_PUBLIC, APIRequestType.PUBLIC);
        assertFalse(PojoUtil.isEmpty(publicAccessToken));
    }
    
    @Test
    public void testGetInfoWithEmptyToken() throws InterruptedException, JSONException {
        ClientResponse activitiesResponse = publicV2ApiClient.viewActivities(user1OrcidId, "");
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
        checkWorks(null);
    }
        
    @Test
    public void testViewWorkAndWorkSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkWorks(getReadPublicAccessToken());
    }
    
    private void checkWorks(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        ClientResponse getWorkResponse = null;
        if(readPublicToken != null) {
            getWorkResponse = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getWorkResponse = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode);
        }
        checkResponse(getWorkResponse);        
        assertNotNull(getWorkResponse);
        Work work = getWorkResponse.getEntity(Work.class);
        assertNotNull(work);
        assertEquals("Current treatment of left main coronary artery disease", work.getWorkTitle().getTitle().getContent());

        ClientResponse getWorkSummaryResponse = null;
        
        if(readPublicToken != null) {
            getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode);
        }
        
        assertNotNull(getWorkSummaryResponse);
        checkResponse(getWorkSummaryResponse);
        WorkSummary summary = getWorkSummaryResponse.getEntity(WorkSummary.class);
        assertNotNull(summary);
        assertEquals("Current treatment of left main coronary artery disease", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkFunding(null);
    }
    
    @Test
    public void testViewFundingAndFundingSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkFunding(getReadPublicAccessToken());
    }
    
    private void checkFunding(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
        assertNotNull(postResponse);        
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getFundingResponse = null;
        
        if(readPublicToken != null) {
            getFundingResponse = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getFundingResponse = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode);
        }
        
        assertNotNull(getFundingResponse);
        checkResponse(getFundingResponse);
        Funding funding = getFundingResponse.getEntity(Funding.class);
        assertNotNull(funding);
        assertEquals("common:title", funding.getTitle().getTitle().getContent());

        ClientResponse getFundingSummaryResponse = null;
        
        if(readPublicToken != null) {
            getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode);
        }
        
        assertNotNull(getFundingSummaryResponse);
        checkResponse(getFundingSummaryResponse);
        FundingSummary summary = getFundingSummaryResponse.getEntity(FundingSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }

    @Test
    public void testViewEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkEmployment(null);
    }
    
    @Test
    public void testViewEmploymentAndEmploymentSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkEmployment(getReadPublicAccessToken());
    }
        
    public void checkEmployment(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEmploymentResponse = null;
        
        if(readPublicToken != null) {
            getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(user1OrcidId, putCode);
        }
        
        assertNotNull(getEmploymentResponse);
        checkResponse(getEmploymentResponse);
        Employment employment = getEmploymentResponse.getEntity(Employment.class);
        assertNotNull(employment);
        assertEquals("affiliation:department-name", employment.getDepartmentName());

        ClientResponse getEmploymentSummaryResponse = null;
        
        if(readPublicToken != null) {
            getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(user1OrcidId, putCode);
        }
        
        assertNotNull(getEmploymentSummaryResponse);
        checkResponse(getEmploymentSummaryResponse);
        EmploymentSummary summary = getEmploymentSummaryResponse.getEntity(EmploymentSummary.class);
        assertNotNull(summary);
        assertEquals("affiliation:department-name", summary.getDepartmentName());
    }
    
    @Test
    public void testViewEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkEducation(null);
    }
    
    @Test
    public void testViewEducationAndEducationSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkEducation(getReadPublicAccessToken());
    }
        
    public void checkEducation(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getEducationResponse = null;
        if(readPublicToken != null) {
            getEducationResponse = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getEducationResponse = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode);
        }
        assertNotNull(getEducationResponse);
        checkResponse(getEducationResponse);
        Education education = getEducationResponse.getEntity(Education.class);
        assertNotNull(education);
        assertEquals("education:department-name", education.getDepartmentName());

        ClientResponse getEducationSummaryResponse = null;
        if(readPublicToken != null) {
            getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode);
        }
        assertNotNull(getEducationSummaryResponse);
        checkResponse(getEducationSummaryResponse);
        EducationSummary summary = getEducationSummaryResponse.getEntity(EducationSummary.class);
        assertNotNull(summary);        
        assertEquals("education:department-name", summary.getDepartmentName());
    }
    
    @Test
    public void testViewPeerReviewAndPeerReviewSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkPeerReview(null);
    }
    
    @Test
    public void testViewPeerReviewAndPeerReviewSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkPeerReview(getReadPublicAccessToken());
    }    
    
    public void checkPeerReview(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(user1OrcidId, peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse getPeerReviewResponse = null;
        if(readPublicToken != null) {
            getPeerReviewResponse = publicV2ApiClient.viewPeerReviewXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getPeerReviewResponse = publicV2ApiClient.viewPeerReviewXml(user1OrcidId, putCode);
        }
        assertNotNull(getPeerReviewResponse);
        checkResponse(getPeerReviewResponse);
        PeerReview peerReview = getPeerReviewResponse.getEntity(PeerReview.class);
        assertNotNull(peerReview);
        assertEquals("peer-review:url", peerReview.getUrl().getValue());

        ClientResponse getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(user1OrcidId, putCode);
        if(readPublicToken != null) {
            getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            getPeerReviewSummaryResponse = publicV2ApiClient.viewPeerReviewSummaryXml(user1OrcidId, putCode);
        }
        assertNotNull(getPeerReviewSummaryResponse);
        checkResponse(getPeerReviewSummaryResponse);
        PeerReviewSummary summary = getPeerReviewSummaryResponse.getEntity(PeerReviewSummary.class);
        assertNotNull(summary);        
        assertEquals("1848", summary.getCompletionDate().getYear().getValue());
    }
                
    @Test
    public void testViewPublicActivities() throws JSONException, InterruptedException, URISyntaxException {
        checkPublicActivities(null);
    }
    
    @Test
    public void testViewPublicActivitiesUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkPublicActivities(getReadPublicAccessToken());
    }
    
    public void checkPublicActivities(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        createActivities();                
        ClientResponse activitiesResponse = null;
        
        if(readPublicToken != null) {
            activitiesResponse = publicV2ApiClient.viewActivities(user1OrcidId, readPublicToken);
        } else {
            activitiesResponse = publicV2ApiClient.viewActivities(user1OrcidId);
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
        String wrongToken = getReadPublicAccessToken() + "!";
        ClientResponse activitiesResponse = publicV2ApiClient.viewActivities(user1OrcidId, wrongToken);
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
        checkLimitedWork(getReadPublicAccessToken());
    }
         
    public void checkLimitedWork(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewWorkXml(user1OrcidId, putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewWorkSummaryXml(user1OrcidId, putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedFunding(null);
    }
    
    @Test
    public void testViewLimitedFundingAndFundingSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedFunding(getReadPublicAccessToken());
    }
    
    public void checkLimitedFunding(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewFundingXml(user1OrcidId, putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewFundingSummaryXml(user1OrcidId, putCode);
        }
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEmployment(null);
    }
    
    @Test
    public void testViewLimitedEmploymentAndEmploymentSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEmployment(getReadPublicAccessToken());
    }
    
    public void checkLimitedEmployment(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = publicV2ApiClient.viewEmploymentXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = publicV2ApiClient.viewEmploymentSummaryXml(user1OrcidId, putCode);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testViewLimitedEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEducation(null);
    }
    
    @Test
    public void testViewLimitedEducationAndEducationSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedEducation(getReadPublicAccessToken());
    }
    
    public void checkLimitedEducation(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
        
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewEducationXml(user1OrcidId, putCode);
        }
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewEducationSummaryXml(user1OrcidId, putCode);
        }
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }
    
    @Test
    public void testViewLimitedPeerReviewAndPeerReviewSummary() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedPeerReview(null);
    }
    
    @Test
    public void testViewLimitedPeerReviewAndPeerReviewSummaryUsingToken() throws JSONException, InterruptedException, URISyntaxException {
        checkLimitedPeerReview(getReadPublicAccessToken());
    }
    
    public void checkLimitedPeerReview(String readPublicToken) throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);

        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(user1OrcidId, peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());

        ClientResponse response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewPeerReviewXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewPeerReviewXml(user1OrcidId, putCode);
        }
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        OrcidError result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());

        response = null;
        if(readPublicToken != null) {
            response = publicV2ApiClient.viewPeerReviewSummaryXml(user1OrcidId, putCode, readPublicToken);
        } else {
            response = publicV2ApiClient.viewPeerReviewSummaryXml(user1OrcidId, putCode);
        }
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        result = response.getEntity(OrcidError.class);
        assertNotNull(result);
        assertEquals(new Integer(9017), result.getErrorCode());
        assertEquals("org.orcid.core.exception.OrcidUnauthorizedException: The activity is not public", result.getDeveloperMessage());
    }

    @Test
    public void testNotFoundReturn404() throws InterruptedException, JSONException {
        ClientResponse response = publicV2ApiClient.viewActivities("0000-0000-0000-0000");     
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        response = publicV2ApiClient.viewActivities("0000-0000-0000-0000", getReadPublicAccessToken());     
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());        
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {
        if (accessToken == null) {
            webDriver = new FirefoxDriver();
            webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
            oauthHelper.setWebDriverHelper(webDriverHelper);
            accessToken = oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, ScopePathType.ACTIVITIES_UPDATE.value(), user1UserName, user1Password,
                    redirectUri);
            webDriver.quit();
        }
        return accessToken;
    }

    public Activity unmarshallFromPath(String path, Class<? extends Activity> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Activity result = null;
            if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            } else if (PeerReview.class.equals(type)) {
                result = (PeerReview) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Object unmarshall(Reader reader, Class<? extends Activity> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
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
                workToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
            else if (i == 1)
                workToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
            else
                workToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
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
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
            else if (i == 1)
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
            else
                fundingToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, fundingToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        for (int i = 0; i < 4; i++) {
            employmentToCreate.setPutCode(null);
            employmentToCreate.setRoleTitle("Employment # " + i);
            if (i == 0 || i == 3)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
            else if (i == 1)
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
            else
                employmentToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employmentToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        for (int i = 0; i < 4; i++) {
            educationToCreate.setPutCode(null);
            educationToCreate.setRoleTitle("Education # " + i);
            if (i == 0 || i == 3)
                educationToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
            else if (i == 1)
                educationToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
            else
                educationToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);

            ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, educationToCreate, accessToken);
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
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PUBLIC);
            } else if (i == 1) {
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.LIMITED);
            } else {
                peerReviewToCreate.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
            }              

            ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(user1OrcidId, peerReviewToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }
    }

    public void cleanActivities() throws JSONException, InterruptedException, URISyntaxException {
        // Remove all activities
        String token = getAccessToken();
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(user1OrcidId, token);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        if (summary.getEducations() != null && !summary.getEducations().getSummaries().isEmpty()) {
            for (EducationSummary education : summary.getEducations().getSummaries()) {
                memberV2ApiClient.deleteEducationXml(user1OrcidId, education.getPutCode(), token);
            }
        }

        if (summary.getEmployments() != null && !summary.getEmployments().getSummaries().isEmpty()) {
            for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
                memberV2ApiClient.deleteEmploymentXml(user1OrcidId, employment.getPutCode(), token);
            }
        }

        if (summary.getFundings() != null && !summary.getFundings().getFundingGroup().isEmpty()) {
            for (FundingGroup group : summary.getFundings().getFundingGroup()) {
                for (FundingSummary funding : group.getFundingSummary()) {
                    memberV2ApiClient.deleteFundingXml(user1OrcidId, funding.getPutCode(), token);
                }
            }
        }

        if (summary.getWorks() != null && !summary.getWorks().getWorkGroup().isEmpty()) {
            for (WorkGroup group : summary.getWorks().getWorkGroup()) {
                for (WorkSummary work : group.getWorkSummary()) {
                    memberV2ApiClient.deleteWorkXml(user1OrcidId, work.getPutCode(), token);
                }
            }
        }
        
        if (summary.getPeerReviews() != null && !summary.getPeerReviews().getPeerReviewGroup().isEmpty()) {
            for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
                for (PeerReviewSummary peerReview : group.getPeerReviewSummary()) {
                    memberV2ApiClient.deletePeerReviewXml(user1OrcidId, peerReview.getPutCode(), token);
                }
            }
        }
    }
    
    private void checkResponse(ClientResponse response) {
        if(Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
            fail("Activity is not public, please verify users default visibility is public");
        }
    }
    
    private String getReadPublicAccessToken() throws InterruptedException, JSONException {
        if(publicAccessToken == null) {
            publicAccessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.READ_PUBLIC);
        }
        return publicAccessToken;                    
    }
    
    public List<GroupIdRecord> createGroupIds() throws JSONException {
        //Use the existing ones
        if(groupRecords != null && !groupRecords.isEmpty()) 
            return groupRecords;
        
        List<GroupIdRecord> groups = new ArrayList<GroupIdRecord>();
        String token = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.GROUP_ID_RECORD_UPDATE);
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:01" + System.currentTimeMillis());
        g1.setName("Group # 1");
        g1.setType("publisher");
        
        GroupIdRecord g2 = new GroupIdRecord();
        g2.setDescription("Description");
        g2.setGroupId("orcid-generated:02" + System.currentTimeMillis());
        g2.setName("Group # 2");
        g2.setType("publisher");                
        
        ClientResponse r1 = memberV2ApiClient.createGroupIdRecord(g1, token);
        
        String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc1/group-id-record/", "");
        g1.setPutCode(Long.valueOf(r1LocationPutCode));
        groups.add(g1);
        
        ClientResponse r2 = memberV2ApiClient.createGroupIdRecord(g2, token);
        String r2LocationPutCode = r2.getLocation().getPath().replace("/orcid-api-web/v2.0_rc1/group-id-record/", "");
        g2.setPutCode(Long.valueOf(r2LocationPutCode));
        groups.add(g2);
        
        return groups;
    }
}
