package org.orcid.integration.blackbox.api.v3.rc1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.error.OrcidError;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class MemberV3Rc1Test extends BlackBoxBaseV3_0_rc1 {    
    private static boolean allSet = false;
    
    @BeforeClass
    public static void beforeClass() {
        signin();
    }
    
    @Before
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        if(allSet) {
            return;
        }
        showMyOrcidPage();
        createGroupIdsV3();
        allSet = true;
    }          

    @Test
    public void createViewUpdateAndDeleteEducation() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createEducationXml(this.getUser1OrcidId(), education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/education/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("department-name", gotEducation.getDepartmentName());
        assertEquals("role-title", gotEducation.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotEducation.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotEducation.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotEducation);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotEducation.setVisibility(originalVisibility);
        
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotEducation);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("updated dept. name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteEducationXml(this.getUser1OrcidId(), gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEducationWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createEducationXml(this.getUser1OrcidId(), education, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/education/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("department-name", gotEducation.getDepartmentName());
        assertEquals("role-title", gotEducation.getRoleTitle());
        gotEducation.setDepartmentName("updated dept. name");
        gotEducation.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEducation);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Education gotAfterUpdateEducation = getAfterUpdateResponse.getEntity(Education.class);
        assertEquals("department-name", gotAfterUpdateEducation.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateEducation.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteEducationXml(this.getUser1OrcidId(), gotEducation.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteEmployment() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employment-3.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createEmploymentXml(this.getUser1OrcidId(), employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/employment/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("department-name", gotEmployment.getDepartmentName());
        assertEquals("role-title", gotEmployment.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotEmployment.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotEmployment.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotEmployment);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotEmployment.setVisibility(originalVisibility);
        
        
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotEmployment);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("updated dept. name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteEmploymentXml(this.getUser1OrcidId(), gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateEmploymentWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employment-3.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createEmploymentXml(this.getUser1OrcidId(), employment, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/employment/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("department-name", gotEmployment.getDepartmentName());
        assertEquals("role-title", gotEmployment.getRoleTitle());
        gotEmployment.setDepartmentName("updated dept. name");
        gotEmployment.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotEmployment);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Employment gotAfterUpdateEmployment = getAfterUpdateResponse.getEntity(Employment.class);
        assertEquals("department-name", gotAfterUpdateEmployment.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateEmployment.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteEmploymentXml(this.getUser1OrcidId(), gotEmployment.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeleteFunding() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Funding funding = (Funding) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/funding-3.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID fExtId = new ExternalID();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setValue("Funding Id " + time);
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/funding/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        
        //Save the original visibility
        Visibility originalVisibility = gotFunding.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotFunding.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotFunding);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotFunding.setVisibility(originalVisibility);
        
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotFunding);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("Updated title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("Updated translated title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("es", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteFundingXml(this.getUser1OrcidId(), gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdateFundingWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Funding funding = (Funding) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/funding-3.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID fExtId = new ExternalID();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setValue("Funding Id " + time);
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/funding/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotFunding.getTitle().getTranslatedTitle().getLanguageCode());
        gotFunding.getTitle().getTitle().setContent("Updated title");
        gotFunding.getTitle().getTranslatedTitle().setContent("Updated translated title");
        gotFunding.getTitle().getTranslatedTitle().setLanguageCode("es");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotFunding);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Funding gotAfterUpdateFunding = getAfterUpdateResponse.getEntity(Funding.class);
        assertEquals("common:title", gotAfterUpdateFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", gotAfterUpdateFunding.getTitle().getTranslatedTitle().getLanguageCode());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteFundingXml(this.getUser1OrcidId(), gotFunding.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void createViewUpdateAndDeletePeerReview() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();

        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/peer-review/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PeerReview gotPeerReview = getResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:url", gotPeerReview.getUrl().getValue());
        assertEquals("peer-review:subject-name", gotPeerReview.getSubjectName().getTitle().getContent());
        assertEquals(groupRecords.get(0).getGroupId(), gotPeerReview.getGroupId());

        //Save the original visibility
        Visibility originalVisibility = gotPeerReview.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotPeerReview.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotPeerReview);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotPeerReview.setVisibility(originalVisibility);        
        gotPeerReview.getSubjectName().getTitle().setContent("updated title");

        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotPeerReview);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());

        PeerReview gotAfterUpdateWork = getAfterUpdateResponse.getEntity(PeerReview.class);
        assertEquals("updated title", gotAfterUpdateWork.getSubjectName().getTitle().getContent());
        assertEquals(groupRecords.get(0).getGroupId(), gotAfterUpdateWork.getGroupId());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deletePeerReviewXml(this.getUser1OrcidId(), gotAfterUpdateWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testUpdatePeerReviewWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        PeerReview peerReviewToCreate = (PeerReview) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml", PeerReview.class);
        peerReviewToCreate.setPutCode(null);
        peerReviewToCreate.setGroupId(groupRecords.get(0).getGroupId());
        peerReviewToCreate.setVisibility(Visibility.PUBLIC);
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        peerReviewToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();

        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReviewToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/peer-review/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PeerReview gotPeerReview = getResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:subject-name", gotPeerReview.getSubjectName().getTitle().getContent());
        gotPeerReview.getSubjectName().getTitle().setContent("updated title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotPeerReview);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        PeerReview gotAfterUpdatePeerReview = getAfterUpdateResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:subject-name", gotAfterUpdatePeerReview.getSubjectName().getTitle().getContent());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deletePeerReviewXml(this.getUser1OrcidId(), gotAfterUpdatePeerReview.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testViewActivitiesSummaries() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        
        String accessTokenForClient1 = getAccessToken();
        String accessTokenForClient2 = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(), getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        
        Distinction distinction = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml", Distinction.class);
        distinction.setPutCode(null);
        distinction.setVisibility(Visibility.PUBLIC);
        
        Education education = (Education) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);

        Employment employment = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employment-3.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);

        InvitedPosition invitedPosition = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        invitedPosition.setPutCode(null);
        invitedPosition.setVisibility(Visibility.PUBLIC);
        
        Membership membership = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml", Membership.class);
        membership.setPutCode(null);
        membership.setVisibility(Visibility.PUBLIC);
        
        Qualification qualification = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml", Qualification.class);
        qualification.setPutCode(null);
        qualification.setVisibility(Visibility.PUBLIC);
        
        Service service = (Service) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml", Service.class);
        service.setPutCode(null);
        service.setVisibility(Visibility.PUBLIC);
        
        Funding funding = (Funding) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/funding-3.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID fExtId = new ExternalID();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setValue("Funding Id " + time);
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
                
        Work work = (Work) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/work-3.0_rc1.xml", Work.class);
        work.setPutCode(null);
        work.setVisibility(Visibility.PUBLIC);
        work.getExternalIdentifiers().getExternalIdentifier().clear();
        work.setSource(null);
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        work.getExternalIdentifiers().getExternalIdentifier().add(wExtId);

        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setVisibility(Visibility.PUBLIC);
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        ExternalID pExtId = new ExternalID();
        pExtId.setValue("Work Id " + time);
        pExtId.setType(WorkExternalIdentifierType.AGR.value());
        pExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId);                

        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createDistinctionXml(this.getUser1OrcidId(), distinction, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createEducationXml(this.getUser1OrcidId(), education, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createEmploymentXml(this.getUser1OrcidId(), employment, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createInvitedPositionXml(this.getUser1OrcidId(), invitedPosition, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createMembershipXml(this.getUser1OrcidId(), membership, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createQualificationXml(this.getUser1OrcidId(), qualification, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        postResponse = memberV3Rc1ApiClientImpl.createServiceXml(this.getUser1OrcidId(), service, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        /**
         * Add 3 fundings 1 and 2 get grouped together 3 in another group
         * because it have different ext ids
         * **/

        // Add 1, the default funding
        postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        funding.getTitle().getTitle().setContent("Funding # 2");
        ExternalID fExtId3 = new ExternalID();
        fExtId3.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId3.setValue("extId3Value" + time);
        fExtId3.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId3);
        // Add 2, with the same ext ids +1
        postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        funding.getTitle().getTitle().setContent("Funding # 3");
        ExternalID fExtId4 = new ExternalID();
        fExtId4.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId4.setValue("extId4Value" + time);
        fExtId4.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId4);
        // Add 3, with different ext ids
        postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());        
        
        /**
         * Add 3 works 1 and 2 get grouped together 3 in another group because
         * it have different ext ids 
         **/
        // Add 1, the default work
        postResponse = memberV3Rc1ApiClientImpl.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        work.getWorkTitle().getTitle().setContent("Work # 2");
        ExternalID wExtId2 = new ExternalID();
        wExtId2.setType(WorkExternalIdentifierType.DOI.value());
        wExtId2.setValue("doi-ext-id" + time);
        wExtId2.setRelationship(Relationship.SELF);
        work.getExternalIdentifiers().getExternalIdentifier().add(wExtId2);
        // Add 2, with the same ext ids +1
        postResponse = memberV3Rc1ApiClientImpl.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        work.getWorkTitle().getTitle().setContent("Work # 3");
        ExternalID wExtId3 = new ExternalID();
        wExtId3.setType(WorkExternalIdentifierType.EID.value());
        wExtId3.setValue("eid-ext-id" + time);
        wExtId3.setRelationship(Relationship.SELF);
        work.getWorkExternalIdentifiers().getExternalIdentifier().clear();
        work.getWorkExternalIdentifiers().getExternalIdentifier().add(wExtId3);
        // Add 3, with different ext ids
        postResponse = memberV3Rc1ApiClientImpl.createWorkXml(this.getUser1OrcidId(), work, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        /**
         * Add 4 peer reviews 1 and 2 get grouped together 3 in another group because
         * it have different group id 4 in another group because it doesnt have
         * any group id
         **/
        // Add 1, the default peer review
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 2");
        peerReview.getCompletionDate().setDay(new Day(2));
        peerReview.getCompletionDate().setMonth(new Month(2));
        peerReview.getCompletionDate().setYear(new Year(2016));
        peerReview.setUrl(new Url("http://peer_review/2"));
        ExternalID pExtId2 = new ExternalID();
        pExtId2.setType(WorkExternalIdentifierType.DOI.value());
        pExtId2.setValue("doi-ext-id" + System.currentTimeMillis());
        pExtId2.setRelationship(Relationship.SELF);
        
        for(ExternalID wei : peerReview.getExternalIdentifiers().getExternalIdentifier()) {
            wei.setValue(wei.getValue()+ System.currentTimeMillis());
        }
        
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId2);
        // Add 2, with the same ext ids +1
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        peerReview.setGroupId(groupRecords.get(1).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 3");
        peerReview.getCompletionDate().setDay(new Day(3));
        peerReview.getCompletionDate().setMonth(new Month(3));
        peerReview.getCompletionDate().setYear(new Year(2017));
        peerReview.setUrl(new Url("http://peer_review/3"));
        ExternalID pExtId3 = new ExternalID();
        pExtId3.setType(WorkExternalIdentifierType.EID.value());
        pExtId3.setValue("eid-ext-id" + System.currentTimeMillis());
        pExtId3.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId3);
        // Add 3, with different ext ids
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getSubjectName().getTitle().setContent("PeerReview # 4");
        peerReview.getCompletionDate().setDay(new Day(4));
        peerReview.getCompletionDate().setMonth(new Month(4));
        peerReview.getCompletionDate().setYear(new Year(2018));
        peerReview.setUrl(new Url("http://peer_review/4"));
        
        ExternalID pExtId4 = new ExternalID();
        pExtId4.setType(WorkExternalIdentifierType.EID.value());
        pExtId4.setValue("eid-ext-id" + System.currentTimeMillis());
        pExtId4.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId4);
        
        // Add 4, without ext ids
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessTokenForClient1);
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

        ClientResponse activitiesResponse = memberV3Rc1ApiClientImpl.viewActivities(this.getUser1OrcidId(), accessTokenForClient1);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertFalse(activities.getEducations().retrieveGroups().isEmpty());
        
        boolean found = false;
        Long distinctionPutCode = null;
        for (AffiliationGroup<DistinctionSummary> group : activities.getDistinctions().retrieveGroups()) {
            for(DistinctionSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    distinctionPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Distinction not found", found);
        
        Long educationPutCode = null;
        for (AffiliationGroup<EducationSummary> group : activities.getEducations().retrieveGroups()) {
            for(EducationSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    educationPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Education not found", found);
                
        assertFalse(activities.getEmployments().retrieveGroups().isEmpty());        
        found = false;
        Long employmentPutCode = null;
        
        for (AffiliationGroup<EmploymentSummary> group : activities.getEmployments().retrieveGroups()) {
            for(EmploymentSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    employmentPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Employment not found", found);        
        
        Long invitedPositionPutCode = null;
        for (AffiliationGroup<InvitedPositionSummary> group : activities.getInvitedPositions().retrieveGroups()) {
            for(InvitedPositionSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    invitedPositionPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Invited position not found", found);        
        
        Long membershipPutCode = null;
        for (AffiliationGroup<MembershipSummary> group : activities.getMemberships().retrieveGroups()) {
            for(MembershipSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    membershipPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Membership not found", found);        
        
        Long qualificationPutCode = null;
        for (AffiliationGroup<QualificationSummary> group : activities.getQualifications().retrieveGroups()) {
            for(QualificationSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    qualificationPutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Qualification not found", found);        
        
        Long servicePutCode = null;
        for (AffiliationGroup<ServiceSummary> group : activities.getServices().retrieveGroups()) {
            for(ServiceSummary summary : group.getActivities()) {
                if(summary.getRoleTitle() != null && summary.getRoleTitle().equals("role-title")) {                
                    assertEquals("department-name", summary.getDepartmentName());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getStartDate());
                    assertEquals(FuzzyDate.valueOf(1848, 2, 2), summary.getEndDate());
                    servicePutCode = summary.getPutCode();
                    found = true;
                    break;
                }
            }
        }
        
        assertTrue("Service not found", found);        
        
        assertNotNull(activities.getFundings());        
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        List<Long> fundingPutCodes = new ArrayList<Long>();
        for (FundingGroup group : activities.getFundings().getFundingGroup()) {
            for(FundingSummary summary : group.getFundingSummary()) {
                if(summary.getTitle().getTitle().getContent().equals("common:title")) {
                    found1 = true;
                    fundingPutCodes.add(summary.getPutCode());
                } else if(summary.getTitle().getTitle().getContent().equals("Funding # 2")) {
                    found2 = true;
                    fundingPutCodes.add(summary.getPutCode());
                } else if(summary.getTitle().getTitle().getContent().equals("Funding # 3")) {
                    found3 = true;
                    fundingPutCodes.add(summary.getPutCode());
                } 
            }
        }

        assertTrue("One of the fundings was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ")", found1 == found2 == found3 == true);
        
        assertNotNull(activities.getWorks());        
        found1 = found2 = found3 = false;
        List<Long> worksPutCodes = new ArrayList<Long>(); 
        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            for(WorkSummary summary : group.getWorkSummary()) {
                if(summary.getTitle().getTitle().getContent().equals("common:title")) {
                    found1 = true;
                    worksPutCodes.add(summary.getPutCode());
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 2")) {
                    found2 = true;
                    worksPutCodes.add(summary.getPutCode());
                } else if(summary.getTitle().getTitle().getContent().equals("Work # 3")) {
                    found3 = true;
                    worksPutCodes.add(summary.getPutCode());
                } else {
                    fail("Couldnt find work with title: " + summary.getTitle().getTitle().getContent());
                }
            }
        }
        
        assertTrue("One of the works was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ")", found1 == found2 == found3 == true);
        
        assertNotNull(activities.getPeerReviews());        
        found1 = found2 = found3 = found4 = false;
        List<Long> peerReviewPutCodes = new ArrayList<Long>();
        for(PeerReviewGroup group : activities.getPeerReviews().getPeerReviewGroup()) {
            for(PeerReviewSummary summary : group.getPeerReviewSummary()) {
                if(summary.getCompletionDate() != null && summary.getCompletionDate().getYear() != null) {
                    if(summary.getCompletionDate().getYear().getValue().equals("1848")) {
                        found1 = true;
                        peerReviewPutCodes.add(summary.getPutCode());
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2016")) {
                        found2 = true;
                        peerReviewPutCodes.add(summary.getPutCode());
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2017")) {
                        found3 = true;
                        peerReviewPutCodes.add(summary.getPutCode());
                    } else if(summary.getCompletionDate().getYear().getValue().equals("2018")) {
                        found4 = true;
                        peerReviewPutCodes.add(summary.getPutCode());
                    }
                }                               
            }
        }
        
        assertTrue("One of the peer reviews was not found: 1(" + found1 + ") 2(" + found2 + ") 3(" + found3 + ") 4(" + found4 + ")", found1 == found2 == found3 == found4 == true);
      
        //Delete all created elements
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteDistinctionXml(this.getUser1OrcidId(), distinctionPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteEmploymentXml(this.getUser1OrcidId(), employmentPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
                        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteEducationXml(this.getUser1OrcidId(), educationPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteInvitedPositionXml(this.getUser1OrcidId(), invitedPositionPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteMembershipXml(this.getUser1OrcidId(), membershipPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteQualificationXml(this.getUser1OrcidId(), qualificationPutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV3Rc1ApiClientImpl.deleteServiceXml(this.getUser1OrcidId(), servicePutCode, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        for(Long putCode : fundingPutCodes) {
            deleteResponse = memberV3Rc1ApiClientImpl.deleteFundingXml(this.getUser1OrcidId(), putCode, accessTokenForClient1);
            assertNotNull(deleteResponse);
            if(Response.Status.NO_CONTENT.getStatusCode() != deleteResponse.getStatus()) {
                //It belongs to client2, so, delete it with client2 token
                deleteResponse = memberV3Rc1ApiClientImpl.deleteFundingXml(this.getUser1OrcidId(), putCode, accessTokenForClient2);
                assertNotNull(deleteResponse);
                assertEquals("Unable to delete funding " + putCode, Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
            }             
        }
        
        for(Long putCode : worksPutCodes) {
            deleteResponse = memberV3Rc1ApiClientImpl.deleteWorkXml(this.getUser1OrcidId(), putCode, accessTokenForClient1);
            assertNotNull(deleteResponse);
            if(Response.Status.NO_CONTENT.getStatusCode() != deleteResponse.getStatus()) {
                //It belongs to client2, so, delete it with client2 token
                deleteResponse = memberV3Rc1ApiClientImpl.deleteWorkXml(this.getUser1OrcidId(), putCode, accessTokenForClient2);
                assertNotNull(deleteResponse);
                assertEquals("Unable to delete work " + putCode, Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
            }             
        }
        
        for(Long putCode : peerReviewPutCodes) {
            deleteResponse = memberV3Rc1ApiClientImpl.deletePeerReviewXml(this.getUser1OrcidId(), putCode, accessTokenForClient1);
            assertNotNull(deleteResponse);
            if(Response.Status.NO_CONTENT.getStatusCode() != deleteResponse.getStatus()) {
                //It belongs to client2, so, delete it with client2 token
                deleteResponse = memberV3Rc1ApiClientImpl.deletePeerReviewXml(this.getUser1OrcidId(), putCode, accessTokenForClient2);
                assertNotNull(deleteResponse);
                assertEquals("Unable to delete peer review " + putCode, Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
            }             
        }        
    }
    
    @Test
    public void testPeerReviewMustHaveAtLeastOneExtId() throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId(groupRecords.get(0).getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        
        String accessToken = getAccessToken();

        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
    }    
    
    @SuppressWarnings({"deprecation", "rawtypes"}) 
    @Test
    public void testTokenWorksOnlyForTheScopeItWasIssued() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        List<String> scopes = getScopes(ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_UPDATE);
        String accessToken =  getAccessToken(scopes);
        Work work1 = (Work) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/work-3.0_rc1.xml", Work.class);
        work1.setPutCode(null);
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        org.orcid.jaxb.model.v3.rc1.record.WorkTitle title1 = new org.orcid.jaxb.model.v3.rc1.record.WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalID wExtId1 = new ExternalID();
        wExtId1.setValue("Work Id " + time);
        wExtId1.setType(WorkExternalIdentifierType.AGR.value());
        wExtId1.setRelationship(Relationship.SELF);
        wExtId1.setUrl(new Url("http://orcid.org/work#1"));
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        work1.getExternalIdentifiers().getExternalIdentifier().add(wExtId1);
                
        //Add the work
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createWorkXml(this.getUser1OrcidId(), work1, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), postResponse.getStatus());
        
        Funding funding = (Funding) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/funding-3.0_rc1.xml", Funding.class);
        funding.setPutCode(null);
        funding.setVisibility(Visibility.PUBLIC);
        funding.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID fExtId = new ExternalID();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setValue("Funding Id " + time);
        fExtId.setRelationship(Relationship.SELF);
        funding.getExternalIdentifiers().getExternalIdentifier().add(fExtId);
        
        //Add the funding
        postResponse = memberV3Rc1ApiClientImpl.createFundingXml(this.getUser1OrcidId(), funding, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        // Delete funding        
        Map map = postResponse.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));        
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteFundingXml(this.getUser1OrcidId(), putCode, accessToken);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testAddPeerReviewWithInvalidGroupingId() throws JSONException, InterruptedException, URISyntaxException {
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId("Invalid group id " + System.currentTimeMillis());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();        
        ExternalID pExtId = new ExternalID();
        pExtId.setValue("Work Id " + System.currentTimeMillis());
        pExtId.setType(WorkExternalIdentifierType.AGR.value());
        pExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(pExtId);        
        String accessToken = getAccessToken();

        //Pattern not valid
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Null group id 
        peerReview.setGroupId(null);
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Empty group id
        peerReview.setGroupId("");
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        //Invalid group id
        peerReview.setGroupId("orcid-generated:" + peerReview.getGroupId());
        postResponse = memberV3Rc1ApiClientImpl.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());        
    }        

    private String getAccessToken() throws InterruptedException, JSONException {                
        return getAccessToken(getScopes());
    }
    
    private List<String> getScopes() {
        return getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED);
    }
    
    @Test
    public void createViewUpdateAndDeleteDistinction() throws JSONException, InterruptedException, URISyntaxException {
        Distinction distinction = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml", Distinction.class);
        distinction.setPutCode(null);
        distinction.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createDistinctionXml(this.getUser1OrcidId(), distinction, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/distinction/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Distinction gotDistinction = getResponse.getEntity(Distinction.class);
        assertEquals("department-name", gotDistinction.getDepartmentName());
        assertEquals("role-title", gotDistinction.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotDistinction.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotDistinction.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotDistinction);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotDistinction.setVisibility(originalVisibility);
        
        gotDistinction.setDepartmentName("updated dept. name");
        gotDistinction.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotDistinction);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Distinction gotAfterUpdateDistinction = getAfterUpdateResponse.getEntity(Distinction.class);
        assertEquals("updated dept. name", gotAfterUpdateDistinction.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateDistinction.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteDistinctionXml(this.getUser1OrcidId(), gotDistinction.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateDistinctionWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Distinction distinction = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml", Distinction.class);
        distinction.setPutCode(null);
        distinction.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createDistinctionXml(this.getUser1OrcidId(), distinction, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/distinction/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Distinction gotDistinction = getResponse.getEntity(Distinction.class);
        assertEquals("department-name", gotDistinction.getDepartmentName());
        assertEquals("role-title", gotDistinction.getRoleTitle());
        gotDistinction.setDepartmentName("updated dept. name");
        gotDistinction.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotDistinction);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Distinction gotAfterUpdateDistinction = getAfterUpdateResponse.getEntity(Distinction.class);
        assertEquals("department-name", gotAfterUpdateDistinction.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateDistinction.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteDistinctionXml(this.getUser1OrcidId(), gotDistinction.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void createViewUpdateAndDeleteInvitedPosition() throws JSONException, InterruptedException, URISyntaxException {
        InvitedPosition invitedPosition = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        invitedPosition.setPutCode(null);
        invitedPosition.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createInvitedPositionXml(this.getUser1OrcidId(), invitedPosition, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/invited-position/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        InvitedPosition gotInvitedPosition = getResponse.getEntity(InvitedPosition.class);
        assertEquals("department-name", gotInvitedPosition.getDepartmentName());
        assertEquals("role-title", gotInvitedPosition.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotInvitedPosition.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotInvitedPosition.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotInvitedPosition);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotInvitedPosition.setVisibility(originalVisibility);
        
        gotInvitedPosition.setDepartmentName("updated dept. name");
        gotInvitedPosition.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotInvitedPosition);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        InvitedPosition gotAfterUpdateInvitedPosition = getAfterUpdateResponse.getEntity(InvitedPosition.class);
        assertEquals("updated dept. name", gotAfterUpdateInvitedPosition.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateInvitedPosition.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteInvitedPositionXml(this.getUser1OrcidId(), gotInvitedPosition.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateInvitedPositionWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        InvitedPosition invitedPosition = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        invitedPosition.setPutCode(null);
        invitedPosition.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createInvitedPositionXml(this.getUser1OrcidId(), invitedPosition, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/invited-position/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        InvitedPosition gotInvitedPosition = getResponse.getEntity(InvitedPosition.class);
        assertEquals("department-name", gotInvitedPosition.getDepartmentName());
        assertEquals("role-title", gotInvitedPosition.getRoleTitle());
        gotInvitedPosition.setDepartmentName("updated dept. name");
        gotInvitedPosition.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotInvitedPosition);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        InvitedPosition gotAfterUpdateInvitedPosition = getAfterUpdateResponse.getEntity(InvitedPosition.class);
        assertEquals("department-name", gotAfterUpdateInvitedPosition.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateInvitedPosition.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteInvitedPositionXml(this.getUser1OrcidId(), gotInvitedPosition.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void createViewUpdateAndDeleteMembership() throws JSONException, InterruptedException, URISyntaxException {
        Membership membership = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml", Membership.class);
        membership.setPutCode(null);
        membership.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createMembershipXml(this.getUser1OrcidId(), membership, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/membership/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Membership gotMembership = getResponse.getEntity(Membership.class);
        assertEquals("department-name", gotMembership.getDepartmentName());
        assertEquals("role-title", gotMembership.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotMembership.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotMembership.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotMembership);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotMembership.setVisibility(originalVisibility);
        
        gotMembership.setDepartmentName("updated dept. name");
        gotMembership.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotMembership);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Membership gotAfterUpdateMembership = getAfterUpdateResponse.getEntity(Membership.class);
        assertEquals("updated dept. name", gotAfterUpdateMembership.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateMembership.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteMembershipXml(this.getUser1OrcidId(), gotMembership.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateMembershipWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Membership membership = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml", Membership.class);
        membership.setPutCode(null);
        membership.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createMembershipXml(this.getUser1OrcidId(), membership, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/membership/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Membership gotMembership = getResponse.getEntity(Membership.class);
        assertEquals("department-name", gotMembership.getDepartmentName());
        assertEquals("role-title", gotMembership.getRoleTitle());
        gotMembership.setDepartmentName("updated dept. name");
        gotMembership.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotMembership);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Membership gotAfterUpdateMembership = getAfterUpdateResponse.getEntity(Membership.class);
        assertEquals("department-name", gotAfterUpdateMembership.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateMembership.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteMembershipXml(this.getUser1OrcidId(), gotMembership.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void createViewUpdateAndDeleteQualification() throws JSONException, InterruptedException, URISyntaxException {
        Qualification qualification = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml", Qualification.class);
        qualification.setPutCode(null);
        qualification.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createQualificationXml(this.getUser1OrcidId(), qualification, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/qualification/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Qualification gotQualification = getResponse.getEntity(Qualification.class);
        assertEquals("department-name", gotQualification.getDepartmentName());
        assertEquals("role-title", gotQualification.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotQualification.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotQualification.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotQualification);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotQualification.setVisibility(originalVisibility);
        
        gotQualification.setDepartmentName("updated dept. name");
        gotQualification.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotQualification);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Qualification gotAfterUpdateQualification = getAfterUpdateResponse.getEntity(Qualification.class);
        assertEquals("updated dept. name", gotAfterUpdateQualification.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateQualification.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteQualificationXml(this.getUser1OrcidId(), gotQualification.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateQualificationWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Qualification qualification = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml", Qualification.class);
        qualification.setPutCode(null);
        qualification.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createQualificationXml(this.getUser1OrcidId(), qualification, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/qualification/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Qualification gotQualification = getResponse.getEntity(Qualification.class);
        assertEquals("department-name", gotQualification.getDepartmentName());
        assertEquals("role-title", gotQualification.getRoleTitle());
        gotQualification.setDepartmentName("updated dept. name");
        gotQualification.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotQualification);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Qualification gotAfterUpdateQualification = getAfterUpdateResponse.getEntity(Qualification.class);
        assertEquals("department-name", gotAfterUpdateQualification.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateQualification.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteQualificationXml(this.getUser1OrcidId(), gotQualification.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void createViewUpdateAndDeleteService() throws JSONException, InterruptedException, URISyntaxException {
        Service service = (Service) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml", Service.class);
        service.setPutCode(null);
        service.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createServiceXml(this.getUser1OrcidId(), service, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/service/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Service gotService = getResponse.getEntity(Service.class);
        assertEquals("department-name", gotService.getDepartmentName());
        assertEquals("role-title", gotService.getRoleTitle());
        
        //Save the original visibility
        Visibility originalVisibility = gotService.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotService.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotService);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotService.setVisibility(originalVisibility);
        
        gotService.setDepartmentName("updated dept. name");
        gotService.setRoleTitle("updated role title");
        putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), accessToken, gotService);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Service gotAfterUpdateService = getAfterUpdateResponse.getEntity(Service.class);
        assertEquals("updated dept. name", gotAfterUpdateService.getDepartmentName());
        assertEquals("updated role title", gotAfterUpdateService.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteServiceXml(this.getUser1OrcidId(), gotService.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateServiceWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        Service service = (Service) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml", Service.class);
        service.setPutCode(null);
        service.setVisibility(Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV3Rc1ApiClientImpl.createServiceXml(this.getUser1OrcidId(), service, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v3.0_rc1/" + this.getUser1OrcidId() + "/service/\\d+"));
        ClientResponse getResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Service gotService = getResponse.getEntity(Service.class);
        assertEquals("department-name", gotService.getDepartmentName());
        assertEquals("role-title", gotService.getRoleTitle());
        gotService.setDepartmentName("updated dept. name");
        gotService.setRoleTitle("updated role title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV3Rc1ApiClientImpl.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotService);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV3Rc1ApiClientImpl.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Service gotAfterUpdateService = getAfterUpdateResponse.getEntity(Service.class);
        assertEquals("department-name", gotAfterUpdateService.getDepartmentName());
        assertEquals("role-title", gotAfterUpdateService.getRoleTitle());
        ClientResponse deleteResponse = memberV3Rc1ApiClientImpl.deleteServiceXml(this.getUser1OrcidId(), gotService.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
