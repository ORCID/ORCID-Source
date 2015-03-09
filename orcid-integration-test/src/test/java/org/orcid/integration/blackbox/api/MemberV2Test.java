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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.either;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

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
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingExternalIdentifier;
import org.orcid.jaxb.model.record.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record.FuzzyDate;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.FundingGroup;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.WorkGroup;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class MemberV2Test {

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
    public String testUser1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource
    private MemberV2ApiClientImpl memberV2ApiClient;

    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private OauthHelper oauthHelper;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
    }

    @After
    public void after() {
        webDriver.quit();
    }

    @Test
    public void testGetNotificationToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
    }

    @Test
    public void createViewUpdateAndDeleteWork() throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(testUser1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotWork);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        assertEquals("updated title", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(testUser1OrcidId, gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateWorkWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(testUser1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(client2ClientId, client2ClientSecret, ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotWork);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        assertEquals("common:title", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(testUser1OrcidId, gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEducation() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(testUser1OrcidId, education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/education/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotEducation.getDepartmentName());
        assertEquals("education:role-title", gotEducation.getRoleTitle());
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEducation);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("updated dept. name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(testUser1OrcidId, gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEducationWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(testUser1OrcidId, education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/education/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotEducation.getDepartmentName());
        assertEquals("education:role-title", gotEducation.getRoleTitle());
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(client2ClientId, client2ClientSecret, ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEducation);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("education:role-title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(testUser1OrcidId, gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEmployment() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(testUser1OrcidId, employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/employment/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotEmployment.getRoleTitle());
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEmployment);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("updated dept. name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(testUser1OrcidId, gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEmploymentWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(testUser1OrcidId, employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/employment/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotEmployment.getRoleTitle());
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(client2ClientId, client2ClientSecret, ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEmployment);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(testUser1OrcidId, gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteFunding() throws JSONException, InterruptedException, URISyntaxException {
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/funding/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotFunding);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("Updated title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("Updated translated title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("es", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(testUser1OrcidId, gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateFundingWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + user1OrcidId + "/funding/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(client2ClientId, client2ClientSecret, ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotFunding);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("common:title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(testUser1OrcidId, gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testViewActivitiesSummaries() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);

        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);

        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);

        Work work = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work.setPutCode(null);
        work.setVisibility(Visibility.PUBLIC);

        String accessToken = getAccessToken();

        memberV2ApiClient.createEducationXml(testUser1OrcidId, education, accessToken);
        memberV2ApiClient.createEmploymentXml(testUser1OrcidId, employment, accessToken);
        /**
         * Add 4 fundings 1 and 2 get grouped together 3 in another group
         * because it have different ext ids 4 in another group because it
         * doesnt have any ext ids
         * **/

        // Add 1, the default funding
        memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 2");
        FundingExternalIdentifier fExtId3 = new FundingExternalIdentifier();
        fExtId3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId3.setValue("extId3Value");
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId3);
        // Add 2, with the same ext ids +1
        memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 3");
        FundingExternalIdentifier fExtId4 = new FundingExternalIdentifier();
        fExtId4.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId4.setValue("extId4Value");
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId4);
        // Add 3, with different ext ids
        memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 4");
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        // Add 4 without ext ids
        memberV2ApiClient.createFundingXml(testUser1OrcidId, funding, accessToken);

        /**
         * Add 4 works 1 and 2 get grouped together 3 in another group because
         * it have different ext ids 4 in another group because it doesnt have
         * any ext ids
         **/
        // Add 1, the default work
        memberV2ApiClient.createWorkXml(testUser1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 2");
        WorkExternalIdentifier wExtId2 = new WorkExternalIdentifier();
        wExtId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wExtId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-ext-id"));
        work.getExternalIdentifiers().getExternalIdentifier().add(wExtId2);
        // Add 2, with the same ext ids +1
        memberV2ApiClient.createWorkXml(testUser1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 3");
        WorkExternalIdentifier wExtId3 = new WorkExternalIdentifier();
        wExtId3.setWorkExternalIdentifierType(WorkExternalIdentifierType.EID);
        wExtId3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("eid-ext-id"));
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        work.getWorkExternalIdentifiers().getExternalIdentifier().add(wExtId3);
        // Add 3, with different ext ids
        memberV2ApiClient.createWorkXml(testUser1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 4");
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        // Add 4, without ext ids
        memberV2ApiClient.createWorkXml(testUser1OrcidId, work, accessToken);

        /**
         * Now, get the summaries and verify the following: - Education summary
         * is complete - Employment summary is complete - There are 3 groups of
         * fundings -- One group with 2 fundings -- One group with one funding
         * with ext ids -- One group with one funding without ext ids
         * 
         * - There are 3 groups of works -- One group with 2 works -- One group
         * with one work with ext ids -- One group with one work without ext ids
         **/

        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(testUser1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertEquals(1, activities.getEducations().getSummaries().size());
        assertEquals("education:role-title", activities.getEducations().getSummaries().get(0).getRoleTitle());
        assertEquals("education:department-name", activities.getEducations().getSummaries().get(0).getDepartmentName());
        assertEquals(new FuzzyDate(1848, 2, 2), activities.getEducations().getSummaries().get(0).getStartDate());
        assertEquals(new FuzzyDate(1848, 2, 2), activities.getEducations().getSummaries().get(0).getEndDate());
        memberV2ApiClient.deleteEducationXml(testUser1OrcidId, activities.getEducations().getSummaries().get(0).getPutCode(), accessToken);

        assertEquals(1, activities.getEmployments().getSummaries().size());
        assertEquals("affiliation:role-title", activities.getEmployments().getSummaries().get(0).getRoleTitle());
        assertEquals("affiliation:department-name", activities.getEmployments().getSummaries().get(0).getDepartmentName());
        assertEquals(new FuzzyDate(1848, 2, 2), activities.getEmployments().getSummaries().get(0).getStartDate());
        assertEquals(new FuzzyDate(1848, 2, 2), activities.getEmployments().getSummaries().get(0).getEndDate());
        memberV2ApiClient.deleteEmploymentXml(testUser1OrcidId, activities.getEmployments().getSummaries().get(0).getPutCode(), accessToken);

        assertNotNull(activities.getFundings());
        assertEquals(3, activities.getFundings().getFundingGroup().size());

        for (FundingGroup group : activities.getFundings().getFundingGroup()) {
            // Check the group that contains the funding without ext ids
            if (group.getIdentifiers().getIdentifier().isEmpty()) {
                assertEquals(1, group.getFundingSummary().size());
                assertNotNull(group.getFundingSummary().get(0).getPutCode());
                assertEquals("Funding # 4", group.getFundingSummary().get(0).getTitle().getTitle().getContent());
                assertEquals(new FuzzyDate(1848, 2, 2), group.getFundingSummary().get(0).getStartDate());
                assertEquals(new FuzzyDate(1848, 2, 2), group.getFundingSummary().get(0).getEndDate());
            } else if (group.getFundingSummary().size() == 1) {
                // Check the group that contains one funding
                assertEquals(1, group.getFundingSummary().size());
                assertNotNull(group.getFundingSummary().get(0).getPutCode());
                assertEquals("Funding # 3", group.getFundingSummary().get(0).getTitle().getTitle().getContent());
                assertEquals(new FuzzyDate(1848, 2, 2), group.getFundingSummary().get(0).getStartDate());
                assertEquals(new FuzzyDate(1848, 2, 2), group.getFundingSummary().get(0).getEndDate());
                assertEquals(1, group.getIdentifiers().getIdentifier().size());
                assertEquals("extId4Value", group.getIdentifiers().getIdentifier().get(0).getExternalIdentifierId());
                assertEquals(FundingExternalIdentifierType.GRANT_NUMBER.name(), group.getIdentifiers().getIdentifier().get(0).getExternalIdentifierType());
            } else {
                // Check the group that contains two fundings
                assertEquals(2, group.getFundingSummary().size());
                assertEquals(3, group.getIdentifiers().getIdentifier().size());
                assertNotNull(group.getFundingSummary().get(0).getDisplayIndex());
                assertNotNull(group.getFundingSummary().get(1).getDisplayIndex());
                assertThat(group.getFundingSummary().get(0).getTitle().getTitle().getContent(), either(containsString("common:title")).or(containsString("Funding # 2")));
                assertThat(group.getFundingSummary().get(1).getTitle().getTitle().getContent(), either(containsString("common:title")).or(containsString("Funding # 2")));
            }

            // delete activities in that group
            for (FundingSummary summary : group.getFundingSummary()) {
                memberV2ApiClient.deleteFundingXml(testUser1OrcidId, summary.getPutCode(), accessToken);
            }
        }

        assertNotNull(activities.getWorks());
        assertEquals(3, activities.getWorks().getWorkGroup().size());

        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            // Check the group that contains the work without ext ids
            if (group.getIdentifiers().getIdentifier().isEmpty()) {
                assertEquals(1, group.getWorkSummary().size());
                assertNotNull(group.getWorkSummary().get(0).getPutCode());
                assertEquals("Work # 4", group.getWorkSummary().get(0).getTitle().getTitle().getContent());
            } else if (group.getWorkSummary().size() == 1) {
                // Check the group that contains one work
                assertEquals(1, group.getWorkSummary().size());
                assertNotNull(group.getWorkSummary().get(0).getPutCode());
                assertEquals("Work # 3", group.getWorkSummary().get(0).getTitle().getTitle().getContent());
                assertEquals(1, group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("eid-ext-id", group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId()
                        .getContent());
                assertEquals(WorkExternalIdentifierType.EID.name(), group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0)
                        .getWorkExternalIdentifierType().name());
            } else {
                // Check the group that contains two works
                assertEquals(2, group.getWorkSummary().size());
                assertEquals(2, group.getIdentifiers().getIdentifier().size());
                assertNotNull(group.getWorkSummary().get(0).getDisplayIndex());
                assertNotNull(group.getWorkSummary().get(1).getDisplayIndex());
                assertThat(group.getWorkSummary().get(0).getTitle().getTitle().getContent(), either(containsString("common:title")).or(containsString("Work # 2")));
                assertThat(group.getWorkSummary().get(1).getTitle().getTitle().getContent(), either(containsString("common:title")).or(containsString("Work # 2")));
            }

            // delete activities in that group
            for (WorkSummary summary : group.getWorkSummary()) {
                memberV2ApiClient.deleteWorkXml(testUser1OrcidId, summary.getPutCode(), accessToken);
            }
        }
    }

    private String getAccessToken() throws InterruptedException, JSONException {
        return oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, ScopePathType.ACTIVITIES_UPDATE.value(), user1UserName, user1Password, redirectUri);
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

}
