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

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.jaxb.model.common_rc1.Day;
import org.orcid.jaxb.model.common_rc1.FuzzyDate;
import org.orcid.jaxb.model.common_rc1.Month;
import org.orcid.jaxb.model.common_rc1.Title;
import org.orcid.jaxb.model.common_rc1.Url;
import org.orcid.jaxb.model.common_rc1.Visibility;
import org.orcid.jaxb.model.common_rc1.Year;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc1.Identifier;
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
public class MemberV2Test extends BlackBoxBaseRC1 {    

    protected static Map<String,String> accessTokens = new HashMap<String, String>();
    
    static List<GroupIdRecord> groupRecords = null;

    @BeforeClass
    public static void beforeClass() {
        // we need to make sure the users activity visibility setting is public for this test
        WebDriver webDriver = new FirefoxDriver();
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        revokeApplicationsAccess();
        webDriver.quit();
    }
    
    @AfterClass
    public static void afterClass() {
        revokeApplicationsAccess();
    }
    
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
    public void testGetNotificationToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
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
        wExtId.setRelationship(Relationship.PART_OF);
        workToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("Current treatment of left main coronary artery disease", gotWork.getWorkTitle().getTitle().getContent());
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        
        //Save the original visibility
        Visibility originalVisibility = gotWork.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotWork.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotWork);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotWork.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotWork);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        assertEquals("updated title", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), gotWork.getPutCode(), accessToken);
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
        wExtId.setRelationship(Relationship.SELF);
        workToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("Current treatment of left main coronary artery disease", gotWork.getWorkTitle().getTitle().getContent());
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotWork);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        assertEquals("Current treatment of left main coronary artery disease", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEducation() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(this.getUser1OrcidId(), education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/education/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotEducation.getDepartmentName());
        assertEquals("education:role-title", gotEducation.getRoleTitle());
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        
        //Save the original visibility
        Visibility originalVisibility = gotEducation.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotEducation.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEducation);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotEducation.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEducation);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("updated dept. name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEducationWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(this.getUser1OrcidId(), education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/education/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotEducation.getDepartmentName());
        assertEquals("education:role-title", gotEducation.getRoleTitle());
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEducation);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("education:role-title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEmployment() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(this.getUser1OrcidId(), employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/employment/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotEmployment.getRoleTitle());
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        
        //Save the original visibility
        Visibility originalVisibility = gotEmployment.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotEmployment.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEmployment);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotEmployment.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotEmployment);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("updated dept. name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(this.getUser1OrcidId(), gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEmploymentWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(this.getUser1OrcidId(), employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/employment/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotEmployment.getRoleTitle());
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEmployment);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV2ApiClient.deleteEmploymentXml(this.getUser1OrcidId(), gotEmployment.getPutCode(), accessToken);
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
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/funding/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
                
        //Save the original visibility
        Visibility originalVisibility = gotFunding.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotFunding.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotFunding);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotFunding.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotFunding);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("Updated title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("Updated translated title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("es", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), gotFunding.getPutCode(), accessToken);
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
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/funding/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotFunding);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("common:title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeletePeerReview() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId.setRelationship(Relationship.SELF);
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());

        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/peer-review/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PeerReview gotPeerReview = getResponse.getEntity(PeerReview.class);
        
        //TODO: response does not contain full external identifier or disambig org.
        //Issue is that upgrading makes subject external identifier fail
        //https://www.diffchecker.com/g5laoiou
        //Note that this test does not hit disambiguated orgs (which are optional and also not being upgraded)
        
        assertEquals("peer-review:url", gotPeerReview.getUrl().getValue());
        assertEquals("peer-review:subject-name", gotPeerReview.getSubjectName().getTitle().getContent());
        assertEquals(groupRecords.get(0).getGroupId(), gotPeerReview.getGroupId());
        gotPeerReview.getSubjectName().getTitle().setContent("updated title");
        
        //Save the original visibility
        Visibility originalVisibility = gotPeerReview.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotPeerReview.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotPeerReview);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotPeerReview.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotPeerReview);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());

        PeerReview gotAfterUpdateWork = getAfterUpdateResponse.getEntity(PeerReview.class);
        assertEquals("updated title", gotAfterUpdateWork.getSubjectName().getTitle().getContent());
        assertEquals(groupRecords.get(0).getGroupId(), gotAfterUpdateWork.getGroupId());
        ClientResponse deleteResponse = memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), gotAfterUpdateWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdatePeerReviewWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(Visibility.PUBLIC);
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId.setRelationship(Relationship.SELF);
        peerReviewToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());

        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + this.getUser1OrcidId() + "/peer-review/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PeerReview gotPeerReview = getResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:subject-name", gotPeerReview.getSubjectName().getTitle().getContent());
        gotPeerReview.getSubjectName().getTitle().setContent("updated title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotPeerReview);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        PeerReview gotAfterUpdatePeerReview = getAfterUpdateResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:subject-name", gotAfterUpdatePeerReview.getSubjectName().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), gotAfterUpdatePeerReview.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testViewActivitiesSummaries() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        
        String accessTokenForClient1 = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        String accessTokenForClient2 = getAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), this.getClient2RedirectUri());
        
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
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
                
        Work work = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work.setPutCode(null);
        work.setVisibility(Visibility.PUBLIC);
        work.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId.setRelationship(Relationship.SELF);
        work.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);

        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setVisibility(Visibility.PUBLIC);
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        WorkExternalIdentifier pExtId = new WorkExternalIdentifier();
        pExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        pExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        pExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId);                

        ClientResponse postResponse = memberV2ApiClient.createEducationXml(this.getUser1OrcidId(), education, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV2ApiClient.createEmploymentXml(this.getUser1OrcidId(), employment, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        /**
         * Add 3 fundings 1 and 2 get grouped together 3 in another group
         * because it have different ext ids
         * **/

        // Add 1, the default funding
        postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        funding.getTitle().getTitle().setContent("Funding # 2");
        FundingExternalIdentifier fExtId3 = new FundingExternalIdentifier();
        fExtId3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId3.setValue("extId3Value" + time);
        fExtId3.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId3);
        funding.setVisibility(Visibility.PUBLIC);
        // Add 2, with the same ext ids +1
        postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());        
        
        funding.getTitle().getTitle().setContent("Funding # 3");
        FundingExternalIdentifier fExtId4 = new FundingExternalIdentifier();
        fExtId4.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId4.setValue("extId4Value" + time);
        fExtId4.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId4);
        funding.setVisibility(Visibility.PUBLIC);
        // Add 3, with different ext ids
        postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());        
        
        /**
         * Add 3 works 1 and 2 get grouped together 3 in another group because
         * it have different ext ids 
         **/
        // Add 1, the default work
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        work.getWorkTitle().getTitle().setContent("Work # 2");
        WorkExternalIdentifier wExtId2 = new WorkExternalIdentifier();
        wExtId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wExtId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-ext-id" + time));
        wExtId2.setRelationship(Relationship.SELF);
        work.getExternalIdentifiers().getExternalIdentifier().add(wExtId2);
        // Add 2, with the same ext ids +1
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        work.getWorkTitle().getTitle().setContent("Work # 3");
        WorkExternalIdentifier wExtId3 = new WorkExternalIdentifier();
        wExtId3.setWorkExternalIdentifierType(WorkExternalIdentifierType.EID);
        wExtId3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("eid-ext-id" + time));
        wExtId3.setRelationship(Relationship.SELF);
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        work.getWorkExternalIdentifiers().getExternalIdentifier().add(wExtId3);
        // Add 3, with different ext ids
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        /**
         * Add 4 peer reviews 1 and 2 get grouped together 3 in another group because
         * it have different group id 4 in another group because it doesnt have
         * any group id
         **/
        // Add 1, the default peer review
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 2");
        peerReview.getCompletionDate().setDay(new Day(2));
        peerReview.getCompletionDate().setMonth(new Month(2));
        peerReview.getCompletionDate().setYear(new Year(2016));
        peerReview.setUrl(new Url("http://peer_review/2"));
        WorkExternalIdentifier pExtId2 = new WorkExternalIdentifier();
        pExtId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        pExtId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-ext-id" + System.currentTimeMillis()));
        pExtId2.setRelationship(Relationship.SELF);
        
        for(WorkExternalIdentifier wei : peerReview.getExternalIdentifiers().getExternalIdentifier()) {
            WorkExternalIdentifierId id = wei.getWorkExternalIdentifierId();
            id.setContent(id.getContent() + System.currentTimeMillis());
            wei.setWorkExternalIdentifierId(id);
        }
        
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId2);
        // Add 2, with the same ext ids +1
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        peerReview.setGroupId(groupRecords.get(1).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 3");
        peerReview.getCompletionDate().setDay(new Day(3));
        peerReview.getCompletionDate().setMonth(new Month(3));
        peerReview.getCompletionDate().setYear(new Year(2017));
        peerReview.setUrl(new Url("http://peer_review/3"));
        WorkExternalIdentifier pExtId3 = new WorkExternalIdentifier();
        pExtId3.setWorkExternalIdentifierType(WorkExternalIdentifierType.EID);
        pExtId3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("eid-ext-id" + System.currentTimeMillis()));
        pExtId3.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId3);
        // Add 3, with different ext ids
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 4");
        peerReview.getCompletionDate().setDay(new Day(4));
        peerReview.getCompletionDate().setMonth(new Month(4));
        peerReview.getCompletionDate().setYear(new Year(2018));
        peerReview.setUrl(new Url("http://peer_review/4"));
        
        WorkExternalIdentifier pExtId4 = new WorkExternalIdentifier();
        pExtId4.setWorkExternalIdentifierType(WorkExternalIdentifierType.EID);
        pExtId4.setWorkExternalIdentifierId(new WorkExternalIdentifierId("eid-ext-id" + System.currentTimeMillis()));
        pExtId4.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId4);
        
        // Add 4, without ext ids
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        /**
         * Now, get the summaries and verify the following: - Education summary
         * is complete - Employment summary is complete - There are 3 groups of
         * fundings -- One group with 2 fundings -- One group with one funding
         * with ext ids -- One group with one funding without ext ids
         * 
         * - There are 3 groups of works -- One group with 2 works -- One group
         * with one work with ext ids -- One group with one work without ext ids
         * 
         * peer review -- There are 3 groups of peer reviews -- One group with 2 
         * peer reviews -- One groups with one peer review and ext ids -- One 
         * group with one peer review but without ext ids 
         **/

        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), accessTokenForClient1);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertFalse(activities.getEducations().getSummaries().isEmpty());
        
        boolean found = false;
        for(EducationSummary summary : activities.getEducations().getSummaries()) {
            if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("education:role-title")) {                
                assertEquals("education:department-name", summary.getDepartmentName());
                assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                found = true;
                break;
            }
        }
        
        assertTrue("Education not found", found);
                
        assertFalse(activities.getEmployments().getSummaries().isEmpty());        
        found = false;
        for(EmploymentSummary summary : activities.getEmployments().getSummaries()) {
            if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("affiliation:role-title")) {
                assertEquals("affiliation:department-name", summary.getDepartmentName());
                assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
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
                } 
            }
        }

        assertTrue("One of the fundings was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ")", found1 == found2 == found3 == true);
        
        assertNotNull(activities.getWorks());        
        found1 = found2 = found3 = false;
        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            for(WorkSummary summary : group.getWorkSummary()) {
                if(summary.getTitle().getTitle().getContent().equals("Current treatment of left main coronary artery disease")) {
                    found1 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 2")) {
                    found2 = true;
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 3")) {
                    found3 = true;
                } 
            }
        }
        
        assertTrue("One of the works was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ")", found1 == found2 == found3 == true);
        
        assertNotNull(activities.getPeerReviews());        
        found1 = found2 = found3 = found4 = false;
        for(PeerReviewGroup group : activities.getPeerReviews().getPeerReviewGroup()) {
            for(PeerReviewSummary summary : group.getPeerReviewSummary()) {
                if(summary.getCompletionDate() != null && summary.getCompletionDate().getYear() != null) {
                    if(summary.getCompletionDate().getYear().getValue().equals("1848")) {
                        found1 = true;
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2016")) {
                        found2 = true;
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2017")) {
                        found3 = true;
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2018")) {
                        found4 = true;
                    }
                }                               
            }
        }
        
        assertTrue("One of the peer reviews was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ") 4(" + found4 + ")", found1 == found2 == found3 == found4 == true);        
    }
    
    @Test
    public void testPeerReviewMustHaveAtLeastOneExtId() throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());

        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
    }    
    
    @Test
    public void testWorksWithPartOfRelationshipDontGetGrouped () throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        String accessTokenForClient1 = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        String accessTokenForClient2 = getAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), this.getClient2RedirectUri());
        
        Work work1 = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work1.setPutCode(null);
        work1.setVisibility(Visibility.PUBLIC);
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        org.orcid.jaxb.model.record_rc1.WorkTitle title1 = new org.orcid.jaxb.model.record_rc1.WorkTitle();
        title1.setTitle(new Title("Work # 1" + time));
        work1.setWorkTitle(title1);
        WorkExternalIdentifier wExtId1 = new WorkExternalIdentifier();
        wExtId1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId1.setRelationship(Relationship.SELF);
        wExtId1.setUrl(new Url("http://orcid.org/work#1"));
        work1.getExternalIdentifiers().getWorkExternalIdentifier().clear();
        work1.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId1);

        Work work2 = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work2.setPutCode(null);
        work2.setVisibility(Visibility.PUBLIC);
        org.orcid.jaxb.model.record_rc1.WorkTitle title2 = new org.orcid.jaxb.model.record_rc1.WorkTitle();
        title2.setTitle(new Title("Work # 2" + time));
        work2.setWorkTitle(title2);
        work2.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId2 = new WorkExternalIdentifier();
        wExtId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId2.setRelationship(Relationship.PART_OF);
        wExtId2.setUrl(new Url("http://orcid.org/work#2"));
        work2.getExternalIdentifiers().getWorkExternalIdentifier().clear();
        work2.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId2);
        
        Work work3 = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work3.setPutCode(null);
        work3.setVisibility(Visibility.PUBLIC);
        org.orcid.jaxb.model.record_rc1.WorkTitle title3 = new org.orcid.jaxb.model.record_rc1.WorkTitle();
        title3.setTitle(new Title("Work # 3" + time));
        work3.setWorkTitle(title3);        
        work3.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId3 = new WorkExternalIdentifier();
        wExtId3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId3.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId3.setRelationship(Relationship.SELF);
        wExtId3.setUrl(new Url("http://orcid.org/work#3"));
        work3.getExternalIdentifiers().getWorkExternalIdentifier().clear();
        work3.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId3);
        
        //Add the three works
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work1, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work2, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work3, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), accessTokenForClient1);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertFalse(activities.getWorks().getWorkGroup().isEmpty());
        
        WorkGroup work1Group = null; 
        WorkGroup work2Group = null;
        WorkGroup work3Group = null;
        
        boolean work1found = false;
        boolean work2found = false;
        boolean work3found = false;
                                
        for(WorkGroup group : activities.getWorks().getWorkGroup()) {
            if(group.getIdentifiers().getIdentifier() == null || group.getIdentifiers().getIdentifier().isEmpty()) {
                for(WorkSummary summary : group.getWorkSummary()) {
                    String title = summary.getTitle().getTitle().getContent(); 
                    if (("Work # 2" + time).equals(title)) {
                        work2found = true;
                        work2Group = group;
                    }
                }
            } else {
                for(Identifier id : group.getIdentifiers().getIdentifier()) {
                    //If it is the ID is the one we are looking for
                    if(id.getExternalIdentifierId().equals("Work Id " + time)) {                    
                        for(WorkSummary summary : group.getWorkSummary()) {
                            String title = summary.getTitle().getTitle().getContent(); 
                            if(("Work # 1" + time).equals(title)) {
                                work1found = true;
                                work1Group = group;
                            } else if(("Work # 3" + time).equals(title)) {
                                work3found = true;
                                work3Group = group;
                            }
                        }
                    }
                }
            }            
        }
        
        assertTrue("Work1: " + work1found + " work2 " + work2found + " work3 " + work3found, work1found && work2found && work3found);
        //Check that work # 1 and Work # 3 are in the same work
        assertEquals(work1Group, work3Group);
        //Check that work # 2 is not in the same group than group # 1
        assertThat(work2Group, not(work1Group));
    }
    
    @Test
    public void testTokenWorksOnlyForTheScopeItWasIssued() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        String accessToken =  getAccessToken(ScopePathType.FUNDING_CREATE, this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());
        Work work1 = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        work1.setPutCode(null);
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        org.orcid.jaxb.model.record_rc1.WorkTitle title1 = new org.orcid.jaxb.model.record_rc1.WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        WorkExternalIdentifier wExtId1 = new WorkExternalIdentifier();
        wExtId1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId1.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId1.setRelationship(Relationship.SELF);
        wExtId1.setUrl(new Url("http://orcid.org/work#1"));
        work1.getExternalIdentifiers().getWorkExternalIdentifier().clear();
        work1.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId1);        
        
        //Add the work
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work1, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), postResponse.getStatus());
        
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId.setValue("Funding Id " + time);
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        
        //Add the funding
        postResponse = memberV2ApiClient.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
    }
    
    @Test
    public void testAddPeerReviewWithInvalidGroupingId() throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc1/samples/peer-review-2.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId("Invalid group id " + System.currentTimeMillis());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        WorkExternalIdentifier pExtId = new WorkExternalIdentifier();
        pExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + System.currentTimeMillis()));
        pExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        pExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId);
        
        String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());

        //Pattern not valid
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Null group id 
        peerReview.setGroupId(null);
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Empty group id
        peerReview.setGroupId("");
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Invalid group id
        peerReview.setGroupId("orcid-generated:" + peerReview.getGroupId());
        postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());        
    }        
    
    private void cleanActivities(String token) throws JSONException, InterruptedException, URISyntaxException {
        // Remove all activities        
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), token);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        if (summary.getEducations() != null && !summary.getEducations().getSummaries().isEmpty()) {
            for (EducationSummary education : summary.getEducations().getSummaries()) {
                memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), education.getPutCode(), token);
            }
        }

        if (summary.getEmployments() != null && !summary.getEmployments().getSummaries().isEmpty()) {
            for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
                memberV2ApiClient.deleteEmploymentXml(this.getUser1OrcidId(), employment.getPutCode(), token);
            }
        }

        if (summary.getFundings() != null && !summary.getFundings().getFundingGroup().isEmpty()) {
            for (FundingGroup group : summary.getFundings().getFundingGroup()) {
                for (FundingSummary funding : group.getFundingSummary()) {
                    memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), funding.getPutCode(), token);
                }
            }
        }

        if (summary.getWorks() != null && !summary.getWorks().getWorkGroup().isEmpty()) {
            for (WorkGroup group : summary.getWorks().getWorkGroup()) {
                for (WorkSummary work : group.getWorkSummary()) {
                    memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), work.getPutCode(), token);
                }
            }
        }
        
        if(summary.getPeerReviews() != null && !summary.getPeerReviews().getPeerReviewGroup().isEmpty()) {
            for(PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
                for(PeerReviewSummary peerReview : group.getPeerReviewSummary()) {
                    memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), peerReview.getPutCode(), token);
                }
            }
        }
    }    
    
    public List<GroupIdRecord> createGroupIds() throws JSONException {
        //Use the existing ones
        if(groupRecords != null && !groupRecords.isEmpty()) 
            return groupRecords;
        
        List<GroupIdRecord> groups = new ArrayList<GroupIdRecord>();
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
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
    
    public String getAccessToken(ScopePathType scope, String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        String accessToken = super.getAccessToken(scope.value(), clientId, clientSecret, clientRedirectUri);
        return accessToken;
    }
    
    public String getAccessToken(String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {        
        if(accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }
        
        String accessToken = super.getAccessToken(ScopePathType.ACTIVITIES_UPDATE.value() + ' ' + ScopePathType.ACTIVITIES_READ_LIMITED.value(), clientId, clientSecret, clientRedirectUri);        
        accessTokens.put(clientId,  accessToken);        
        return accessToken;
    }    

    public void cleanActivities() throws JSONException, InterruptedException, URISyntaxException {
        for(String token : accessTokens.values()) {
            cleanActivities(token);
        }
    }
}
