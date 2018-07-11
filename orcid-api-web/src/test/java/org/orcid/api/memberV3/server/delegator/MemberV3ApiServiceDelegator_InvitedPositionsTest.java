package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_InvitedPositionsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USE THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_rc1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPositions(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPosition(ORCID, 32L);
    }

    @Test
    public void testViewInvitedPositionReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        InvitedPosition element = (InvitedPosition) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
    }

    @Test
    public void testViewInvitedPositionsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        InvitedPositions element = (InvitedPositions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-positions", element.getPath());

        for (AffiliationGroup<InvitedPositionSummary> group : element.getInvitedPositionGroups()) {
            for (InvitedPositionSummary summary : group.getActivities()) {
                Utils.assertIsPublicOrSource(summary, "APP-5555555555555555");
            }
        }
    }

    @Test
    public void testViewInvitedPositionSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
        InvitedPositionSummary element = (InvitedPositionSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        Utils.verifyLastModified(invitedPosition.getLastModifiedDate());
        assertEquals(Long.valueOf(32L), invitedPosition.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/32", invitedPosition.getPath());
        assertEquals("PUBLIC Department", invitedPosition.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), invitedPosition.getVisibility().value());
    }

    @Test
    public void testViewLimitedInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 35L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        Utils.verifyLastModified(invitedPosition.getLastModifiedDate());
        assertEquals(Long.valueOf(35L), invitedPosition.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/35", invitedPosition.getPath());
        assertEquals("SELF LIMITED Department", invitedPosition.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), invitedPosition.getVisibility().value());
    }

    @Test
    public void testViewPrivateInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 34L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        Utils.verifyLastModified(invitedPosition.getLastModifiedDate());
        assertEquals(Long.valueOf(34L), invitedPosition.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/34", invitedPosition.getPath());
        assertEquals("PRIVATE Department", invitedPosition.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), invitedPosition.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateInvitedPositionWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPosition(ORCID, 36L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewInvitedPositionThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // InvitedPosition 32 belongs to 0000-0000-0000-0003
        serviceDelegator.viewInvitedPosition("4444-4444-4444-4446", 32L);
        fail();
    }

    @Test
    public void testViewInvitedPositions() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        assertNotNull(r);
        InvitedPositions invitedPositions = (InvitedPositions) r.getEntity();
        assertNotNull(invitedPositions);
        assertEquals("/0000-0000-0000-0003/invited-positions", invitedPositions.getPath());
        Utils.verifyLastModified(invitedPositions.getLastModifiedDate());
        assertNotNull(invitedPositions.retrieveGroups());
        assertEquals(3, invitedPositions.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;
        
        for (AffiliationGroup<InvitedPositionSummary> group : invitedPositions.retrieveGroups()) {
            InvitedPositionSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(33).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(34).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(35).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(32), group.getActivities().get(1).getPutCode());
                found3 = true;
            } else {
                fail("Invalid invitedPosition found: " + element0.getPutCode());
            }            
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);        
    }

    @Test
    public void testReadPublicScope_InvitedPositions() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(r);
        assertEquals(InvitedPosition.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
        assertNotNull(r);
        assertEquals(InvitedPositionSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewInvitedPosition(ORCID, 33L);
        serviceDelegator.viewInvitedPositionSummary(ORCID, 33L);

        // Private that am the source of should work
        serviceDelegator.viewInvitedPosition(ORCID, 34L);
        serviceDelegator.viewInvitedPositionSummary(ORCID, 34L);

        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewInvitedPosition(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewInvitedPositionSummary(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am not the source of should fails
        try {
            serviceDelegator.viewInvitedPosition(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewInvitedPositionSummary(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary originalSummary = (ActivitiesSummary) response.getEntity();
        assertNotNull(originalSummary);
        Utils.verifyLastModified(originalSummary.getLastModifiedDate());
        assertNotNull(originalSummary.getInvitedPositions());
        Utils.verifyLastModified(originalSummary.getInvitedPositions().getLastModifiedDate());
        assertNotNull(originalSummary.getInvitedPositions().retrieveGroups());
        assertEquals(3, originalSummary.getInvitedPositions().retrieveGroups().size());
        
        InvitedPositionSummary invitedPositionSummary = originalSummary.getInvitedPositions().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(invitedPositionSummary);
        Utils.verifyLastModified(invitedPositionSummary.getLastModifiedDate());

        response = serviceDelegator.createInvitedPosition(ORCID, (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summaryWithNewElement = (ActivitiesSummary) response.getEntity();
        assertNotNull(summaryWithNewElement);
        Utils.verifyLastModified(summaryWithNewElement.getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getInvitedPositions());
        Utils.verifyLastModified(summaryWithNewElement.getInvitedPositions().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getInvitedPositions().retrieveGroups());
        assertEquals(4, summaryWithNewElement.getInvitedPositions().retrieveGroups().size());

        boolean haveNew = false;

        for (AffiliationGroup<InvitedPositionSummary> group : summaryWithNewElement.getInvitedPositions().retrieveGroups()) {
            for (InvitedPositionSummary ips : group.getActivities()) {
                assertNotNull(ips.getPutCode());
                Utils.verifyLastModified(ips.getLastModifiedDate());
                if (ips.getPutCode().equals(putCode)) {
                    assertEquals("My department name", ips.getDepartmentName());
                    haveNew = true;
                } else {
                    boolean found = false;
                    for (AffiliationGroup<InvitedPositionSummary> g : originalSummary.getInvitedPositions().retrieveGroups()) {
                        if (g.getActivities().contains(ips)) {
                            found = true;
                        }
                    }
                    assertTrue(found);
                }
            }
        }

        assertTrue(haveNew);

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, putCode);
    }

    @Test(expected = OrcidValidationException.class)
    public void testAddInvitedPositionNoStartDate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        InvitedPosition invitedPosition = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        invitedPosition.setStartDate(null);
        serviceDelegator.createInvitedPosition(ORCID, invitedPosition);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddInvitedPositionsDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        InvitedPosition invitedPosition = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        invitedPosition.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createInvitedPosition(ORCID, invitedPosition);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            InvitedPosition duplicate = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createInvitedPosition(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }

    @Test
    public void testUpdateInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        assertEquals("PUBLIC Department", invitedPosition.getDepartmentName());
        assertEquals("PUBLIC", invitedPosition.getRoleTitle());
        Utils.verifyLastModified(invitedPosition.getLastModifiedDate());

        LastModifiedDate before = invitedPosition.getLastModifiedDate();

        invitedPosition.setDepartmentName("Updated department name");
        invitedPosition.setRoleTitle("The updated role title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        invitedPosition.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, invitedPosition);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        Utils.verifyLastModified(invitedPosition.getLastModifiedDate());
        assertTrue(invitedPosition.getLastModifiedDate().after(before));
        assertEquals("Updated department name", invitedPosition.getDepartmentName());
        assertEquals("The updated role title", invitedPosition.getRoleTitle());

        // Rollback changes
        invitedPosition.setDepartmentName("PUBLIC Department");
        invitedPosition.setRoleTitle("PUBLIC");

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, invitedPosition);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateInvitedPositionYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 35L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        invitedPosition.setDepartmentName("Updated department name");
        invitedPosition.setRoleTitle("The updated role title");
        serviceDelegator.updateInvitedPosition(ORCID, 35L, invitedPosition);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateInvitedPositionChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        assertEquals(Visibility.PUBLIC, invitedPosition.getVisibility());

        invitedPosition.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, invitedPosition);
        fail();
    }

    @Test
    public void testUpdateInvitedPositionLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        assertEquals(Visibility.PUBLIC, invitedPosition.getVisibility());

        invitedPosition.setVisibility(null);

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, invitedPosition);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        assertEquals(Visibility.PUBLIC, invitedPosition.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateInvitedPositionDuplicateExternalIDs() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);

        InvitedPosition invitedPosition = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        invitedPosition.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createInvitedPosition(ORCID, invitedPosition);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        InvitedPosition another = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        response = serviceDelegator.createInvitedPosition(ORCID, another);

        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewInvitedPosition(ORCID, putCode2);
        another = (InvitedPosition) response.getEntity();
        another.setExternalIDs(externalIDs);

        try {
            serviceDelegator.updateInvitedPosition(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewInvitedPosition("0000-0000-0000-0002", 1003L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1003L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        try {
            serviceDelegator.viewInvitedPosition("0000-0000-0000-0002", 1003L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }

    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteInvitedPositionYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 35L);
        fail();
    }

}
