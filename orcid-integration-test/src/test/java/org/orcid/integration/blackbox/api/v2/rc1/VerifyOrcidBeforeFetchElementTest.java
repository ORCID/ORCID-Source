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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc1.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
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
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class VerifyOrcidBeforeFetchElementTest extends BlackBoxBaseRC1 {    
    @Resource(name = "memberV2ApiClient_rc1")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc1")
    private PublicV2ApiClientImpl publicV2ApiClient;

    @Before
    public void before() throws JSONException, InterruptedException, URISyntaxException {
        createGroupIds();
    }        

    @Test
    public void testWork() throws InterruptedException, JSONException, URISyntaxException {                                
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        WorkExternalIdentifier wExtId = new WorkExternalIdentifier();
        wExtId.setWorkExternalIdentifierId(new WorkExternalIdentifierId("Work Id " + time));
        wExtId.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        wExtId.setRelationship(Relationship.PART_OF);
        workToCreate.getExternalIdentifiers().getWorkExternalIdentifier().add(wExtId);
        String user1AccessToken = getAccessToken();
        // Create a work
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(getUser1OrcidId(), workToCreate, user1AccessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + getUser1OrcidId() + "/work/\\d+"));
        // Fetch it with the owner
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), user1AccessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("Current treatment of left main coronary artery disease", gotWork.getWorkTitle().getTitle().getContent());
        String user2AccessToken = getAccessToken(getUser2OrcidId(), getUser2Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        // Try to fetch it with other user orcid
        // Using the members API        
        ClientResponse user2GetResponse = memberV2ApiClient.viewWorkXml(getUser2OrcidId(), gotWork.getPutCode(), user2AccessToken);
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        OrcidError error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Using the public API        
        user2GetResponse = publicV2ApiClient.viewWorkXml(getUser2OrcidId(), String.valueOf(gotWork.getPutCode()));
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Delete it
        ClientResponse deletedResponse = memberV2ApiClient.deleteWorkXml(getUser1OrcidId(), gotWork.getPutCode(), user1AccessToken);
        assertNotNull(deletedResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
    }

    @Test
    public void testEducation() throws InterruptedException, JSONException, URISyntaxException {
        Education education = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        education.setPutCode(null);
        education.setVisibility(Visibility.PUBLIC);
        String user1AccessToken = getAccessToken();
        // Create an education
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(getUser1OrcidId(), education, user1AccessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + getUser1OrcidId() + "/education/\\d+"));
        // Fetch it with the owner
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), user1AccessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Education gotEducation = getResponse.getEntity(Education.class);
        assertEquals("education:department-name", gotEducation.getDepartmentName());
        assertEquals("education:role-title", gotEducation.getRoleTitle());                
        String user2AccessToken = getAccessToken(getUser2OrcidId(), getUser2Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        // Try to fetch it with other user orcid
        // Using the members API        
        ClientResponse user2GetResponse = memberV2ApiClient.viewEducationXml(getUser2OrcidId(), gotEducation.getPutCode(), user2AccessToken);
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        OrcidError error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Using the public API
        user2GetResponse = publicV2ApiClient.viewEducationXml(getUser2OrcidId(), String.valueOf(gotEducation.getPutCode()));
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Delete it
        ClientResponse deletedResponse = memberV2ApiClient.deleteEducationXml(getUser1OrcidId(), gotEducation.getPutCode(), user1AccessToken);
        assertNotNull(deletedResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
    }

    @Test
    public void testEmployment() throws InterruptedException, JSONException, URISyntaxException {
        Employment employment = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employment.setPutCode(null);
        employment.setVisibility(Visibility.PUBLIC);
        String user1AccessToken = getAccessToken();
        // Create an employment
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(getUser1OrcidId(), employment, user1AccessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + getUser1OrcidId() + "/employment/\\d+"));
        // Fetch it with the owner
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), user1AccessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Employment gotEmployment = getResponse.getEntity(Employment.class);
        assertEquals("affiliation:department-name", gotEmployment.getDepartmentName());
        assertEquals("affiliation:role-title", gotEmployment.getRoleTitle());        
        // Try to fetch it with other user orcid
        // Using the members API
        String user2AccessToken = getAccessToken(getUser2OrcidId(), getUser2Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        ClientResponse user2GetResponse = memberV2ApiClient.viewEmploymentXml(getUser2OrcidId(), gotEmployment.getPutCode(), user2AccessToken);
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        OrcidError error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Using the public API
        user2GetResponse = publicV2ApiClient.viewEmploymentXml(getUser2OrcidId(), String.valueOf(gotEmployment.getPutCode()));
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Delete it
        ClientResponse deletedResponse = memberV2ApiClient.deleteEducationXml(getUser1OrcidId(), gotEmployment.getPutCode(), user1AccessToken);
        assertNotNull(deletedResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
    }

    @Test
    public void testFunding() throws InterruptedException, JSONException, URISyntaxException {
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
        String user1AccessToken = getAccessToken();
        // Create an funding
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(getUser1OrcidId(), funding, user1AccessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + getUser1OrcidId() + "/funding/\\d+"));
        // Fetch it with the owner
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), user1AccessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Funding gotFunding = getResponse.getEntity(Funding.class);
        assertEquals("common:title", gotFunding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", gotFunding.getTitle().getTranslatedTitle().getContent());
        // Try to fetch it with other user orcid
        // Using the members API
        String user2AccessToken = getAccessToken(getUser2OrcidId(), getUser2Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        ClientResponse user2GetResponse = memberV2ApiClient.viewFundingXml(getUser2OrcidId(), gotFunding.getPutCode(), user2AccessToken);
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        OrcidError error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Using the public API
        user2GetResponse = publicV2ApiClient.viewFundingXml(getUser2OrcidId(), String.valueOf(gotFunding.getPutCode()));
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());
        // Delete it
        ClientResponse deletedResponse = memberV2ApiClient.deleteFundingXml(getUser1OrcidId(), gotFunding.getPutCode(), user1AccessToken);
        assertNotNull(deletedResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
    }

    @Test
    public void testPeerReview() throws InterruptedException, JSONException, URISyntaxException {
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
        String user1AccessToken = getAccessToken();
        // Create an peer review
        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(getUser1OrcidId(), peerReviewToCreate, user1AccessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc1/" + getUser1OrcidId() + "/peer-review/\\d+"));
        // Fetch it with the owner
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), user1AccessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        PeerReview gotPeerReview = getResponse.getEntity(PeerReview.class);
        assertEquals("peer-review:url", gotPeerReview.getUrl().getValue());
        assertEquals("peer-review:subject-name", gotPeerReview.getSubjectName().getTitle().getContent());
         // Try to fetch it with other user orcid
        // Using the members API
        String user2AccessToken = getAccessToken(getUser2OrcidId(), getUser2Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        ClientResponse user2GetResponse = memberV2ApiClient.viewPeerReviewXml(getUser2OrcidId(), gotPeerReview.getPutCode(), user2AccessToken);
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        OrcidError error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());          
        //Using the public API
        user2GetResponse = publicV2ApiClient.viewPeerReviewXml(getUser2OrcidId(), String.valueOf(gotPeerReview.getPutCode()));
        assertNotNull(user2GetResponse);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), user2GetResponse.getStatus());
        error = user2GetResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9016), error.getErrorCode());                
        // Delete it
        ClientResponse deletedResponse = memberV2ApiClient.deletePeerReviewXml(getUser1OrcidId(), gotPeerReview.getPutCode(), user1AccessToken);
        assertNotNull(deletedResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
    }
    
    public String getAccessToken() throws InterruptedException,
            JSONException {
        return getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE));
    }
}
