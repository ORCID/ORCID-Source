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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.common.FuzzyDate;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingExternalIdentifier;
import org.orcid.jaxb.model.record.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
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

    static String accessToken = null;

    @After    
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    @After
    public void after() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    @Test
    public void testGetNotificationToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
    }

    @Test
    public void createViewUpdateAndDeleteWork() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        workToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(user1OrcidId, gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateWorkWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(Visibility.PUBLIC);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        workToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(user1OrcidId, workToCreate, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(user1OrcidId, gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEducation() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, education, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(user1OrcidId, gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEducationWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(user1OrcidId, education, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(user1OrcidId, gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEmployment() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employment, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(user1OrcidId, gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEmploymentWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(user1OrcidId, employment, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(user1OrcidId, gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteFunding() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId.setValue("Funding Id " + time);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(user1OrcidId, gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateFundingWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId.setValue("Funding Id " + time);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(user1OrcidId, gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testViewActivitiesSummaries() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);

        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);

        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId.setValue("Funding Id " + time);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);

        Work work = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work.setPutCode(null);
        work.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        work.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);

        String accessToken = getAccessToken();

        memberV2ApiClient.createEducationXml(user1OrcidId, education, accessToken);
        memberV2ApiClient.createEmploymentXml(user1OrcidId, employment, accessToken);
        /**
         * Add 4 fundings 1 and 2 get grouped together 3 in another group
         * because it have different ext ids 4 in another group because it
         * doesnt have any ext ids
         * **/

        // Add 1, the default funding
        memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 2");
        FundingExternalIdentifier fExtId3 = new FundingExternalIdentifier();
        fExtId3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId3.setValue("extId3Value" + time);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId3);
        // Add 2, with the same ext ids +1
        memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 3");
        FundingExternalIdentifier fExtId4 = new FundingExternalIdentifier();
        fExtId4.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId4.setValue("extId4Value" + time);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId4);
        // Add 3, with different ext ids
        memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);

        funding.getTitle().getTitle().setContent("Funding # 4");
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        // Add 4 without ext ids
        memberV2ApiClient.createFundingXml(user1OrcidId, funding, accessToken);

        /**
         * Add 4 works 1 and 2 get grouped together 3 in another group because
         * it have different ext ids 4 in another group because it doesnt have
         * any ext ids
         **/
        // Add 1, the default work
        memberV2ApiClient.createWorkXml(user1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 2");
        WorkExternalIdentifier wExtId2 = new WorkExternalIdentifier();
        wExtId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wExtId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-ext-id" + time));
        work.getExternalIdentifiers().getExternalIdentifier().add(wExtId2);
        // Add 2, with the same ext ids +1
        memberV2ApiClient.createWorkXml(user1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 3");
        WorkExternalIdentifier wExtId3 = new WorkExternalIdentifier();
        wExtId3.setWorkExternalIdentifierType(WorkExternalIdentifierType.EID);
        wExtId3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("eid-ext-id" + time));
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        work.getWorkExternalIdentifiers().getExternalIdentifier().add(wExtId3);
        // Add 3, with different ext ids
        memberV2ApiClient.createWorkXml(user1OrcidId, work, accessToken);

        work.getWorkTitle().getTitle().setContent("Work # 4");
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        // Add 4, without ext ids
        memberV2ApiClient.createWorkXml(user1OrcidId, work, accessToken);

        /**
         * Now, get the summaries and verify the following: - Education summary
         * is complete - Employment summary is complete - There are 3 groups of
         * fundings -- One group with 2 fundings -- One group with one funding
         * with ext ids -- One group with one funding without ext ids
         * 
         * - There are 3 groups of works -- One group with 2 works -- One group
         * with one work with ext ids -- One group with one work without ext ids
         **/

        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertFalse(activities.getEducations().getSummaries().isEmpty());
        
        boolean found = false;
        for(EducationSummary summary : activities.getEducations().getSummaries()) {
            if(summary.getRoleTitle().equals("education:role-title")) {                
                assertEquals("education:department-name", summary.getDepartmentName());
                assertEquals(new FuzzyDate(1848, 2, 2), summary.getStartDate());
                assertEquals(new FuzzyDate(1848, 2, 2), summary.getEndDate());
                found = true;
                break;
            }
        }
        
        assertTrue("Education not found", found);
                
        assertFalse(activities.getEmployments().getSummaries().isEmpty());        
        found = false;
        for(EmploymentSummary summary : activities.getEmployments().getSummaries()) {
            if(summary.getRoleTitle().equals("affiliation:role-title")) {
                assertEquals("affiliation:department-name", summary.getDepartmentName());
                assertEquals(new FuzzyDate(1848, 2, 2), summary.getStartDate());
                assertEquals(new FuzzyDate(1848, 2, 2), summary.getEndDate());
                found = true;
                break;
            }
        }
        
        assertTrue("Employment not found", found);
        
        
        assertNotNull(activities.getFundings());        
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (FundingGroup group : activities.getFundings().getFundingGroup()) {
            for(FundingSummary summary : group.getFundingSummary()) {
                if(summary.getTitle().getTitle().getContent().equals("common:title")) {
                    found1 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Funding # 2")) {
                    found2 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Funding # 3")) {
                    found3 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Funding # 4")) {
                    found4 = true;
                }
            }
        }

        assertTrue("One of the fundings was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ") 4(" + found4 + ")", found1 == found2 == found3 == found4 == true);
        
        assertNotNull(activities.getWorks());
        
        found1 = found2 = found3 = found4 = false;
        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            for(WorkSummary summary : group.getWorkSummary()) {
                if(summary.getTitle().getTitle().getContent().equals("common:title")) {
                    found1 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 2")) {
                    found2 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 3")) {
                    found3 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 4")) {
                    found4 = true;
                }
            }
        }
        
        assertTrue("One of the works was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ") 4(" + found4 + ")", found1 == found2 == found3 == found4 == true);
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
    }

}
