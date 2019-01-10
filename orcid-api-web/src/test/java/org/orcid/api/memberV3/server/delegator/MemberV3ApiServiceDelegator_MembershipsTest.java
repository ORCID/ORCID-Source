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
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Memberships;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_MembershipsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USE THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

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
    public void testViewMembershipsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewMemberships(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewMembershipWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewMembership(ORCID, 37L);
    }

    @Test
    public void testViewMembershipReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewMembership(ORCID, 37L);
        Membership element = (Membership) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewMembershipSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewMembershipSummary(ORCID, 37L);
    }

    @Test
    public void testViewMembershipsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewMemberships(ORCID);
        Memberships element = (Memberships) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/memberships", element.getPath());
        
        for (AffiliationGroup<MembershipSummary> group : element.getMembershipGroups()) {
            for (MembershipSummary summary : group.getActivities()) {
                Utils.assertIsPublicOrSource(summary, "APP-5555555555555555");
            }
        }
    }

    @Test
    public void testViewMembershipSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewMembershipSummary(ORCID, 37L);
        MembershipSummary element = (MembershipSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        Utils.verifyLastModified(membership.getLastModifiedDate());
        assertEquals(Long.valueOf(37L), membership.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/37", membership.getPath());
        assertEquals("PUBLIC Department", membership.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), membership.getVisibility().value());
    }

    @Test
    public void testViewLimitedMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewMembership(ORCID, 40L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        Utils.verifyLastModified(membership.getLastModifiedDate());
        assertEquals(Long.valueOf(40L), membership.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/40", membership.getPath());
        assertEquals("SELF LIMITED Department", membership.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), membership.getVisibility().value());
    }

    @Test
    public void testViewPrivateMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewMembership(ORCID, 39L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        Utils.verifyLastModified(membership.getLastModifiedDate());
        assertEquals(Long.valueOf(39L), membership.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/39", membership.getPath());
        assertEquals("PRIVATE Department", membership.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), membership.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateMembershipWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewMembership(ORCID, 41L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewMembershipThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Membership 37 belongs to 0000-0000-0000-0003
        serviceDelegator.viewMembership("4444-4444-4444-4446", 37L);
        fail();
    }

    @Test
    public void testViewMemberships() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewMemberships(ORCID);
        assertNotNull(r);
        Memberships memberships = (Memberships) r.getEntity();
        assertNotNull(memberships);
        assertEquals("/0000-0000-0000-0003/memberships", memberships.getPath());
        Utils.verifyLastModified(memberships.getLastModifiedDate());
        assertNotNull(memberships.retrieveGroups());
        assertEquals(3, memberships.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;
        
        for (AffiliationGroup<MembershipSummary> group : memberships.retrieveGroups()) {
            MembershipSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(38).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(39).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(40).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(37), group.getActivities().get(1).getPutCode());
                found3 = true;
            } else {
                fail("Invalid membership found: " + element0.getPutCode());
            }            
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testReadPublicScope_Memberships() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(r);
        assertEquals(Membership.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewMembershipSummary(ORCID, 37L);
        assertNotNull(r);
        assertEquals(MembershipSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewMembership(ORCID, 38L);
        serviceDelegator.viewMembershipSummary(ORCID, 38L);
        
        // Private that am the source of should work
        serviceDelegator.viewMembership(ORCID, 39L);
        serviceDelegator.viewMembershipSummary(ORCID, 39L);
        
        
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewMembership(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewMembershipSummary(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am not the source of should fails
        try {
            serviceDelegator.viewMembership(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewMembershipSummary(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary originalSummary = (ActivitiesSummary) response.getEntity();
        assertNotNull(originalSummary);
        Utils.verifyLastModified(originalSummary.getLastModifiedDate());
        assertNotNull(originalSummary.getMemberships());
        Utils.verifyLastModified(originalSummary.getMemberships().getLastModifiedDate());
        assertNotNull(originalSummary.getMemberships().retrieveGroups());
        assertEquals(3, originalSummary.getMemberships().retrieveGroups().size());
        
        MembershipSummary membershipSummary = originalSummary.getMemberships().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(membershipSummary);
        Utils.verifyLastModified(membershipSummary.getLastModifiedDate());

        response = serviceDelegator.createMembership(ORCID, (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP));
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
        assertNotNull(summaryWithNewElement.getMemberships());
        Utils.verifyLastModified(summaryWithNewElement.getMemberships().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getMemberships().retrieveGroups());
        assertEquals(4, summaryWithNewElement.getMemberships().retrieveGroups().size());
        
        boolean haveNew = false;

        for (AffiliationGroup<MembershipSummary> group : summaryWithNewElement.getMemberships().retrieveGroups()) {
            for (MembershipSummary ms : group.getActivities()) {
                assertNotNull(ms.getPutCode());
                Utils.verifyLastModified(ms.getLastModifiedDate());
                if (ms.getPutCode().equals(putCode)) {
                    assertEquals("My department name", ms.getDepartmentName());
                    haveNew = true;
                } else {
                    boolean found = false;
                    for (AffiliationGroup<MembershipSummary> g : originalSummary.getMemberships().retrieveGroups()) {
                        if (g.getActivities().contains(membershipSummary)) {
                            found = true;
                        }
                    }
                    assertTrue(found);
                }
            }
        }
        
        assertTrue(haveNew);
        
        //Remove new element
        serviceDelegator.deleteAffiliation(ORCID, putCode);          
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddMembershipsDuplicateExternalIDs() {
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

        Membership membership = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
        membership.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createMembership(ORCID, membership);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Membership duplicate = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createMembership(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }


    @Test
    public void testUpdateMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewMembership(ORCID, 39L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        assertEquals("PRIVATE Department", membership.getDepartmentName());
        assertEquals("PRIVATE", membership.getRoleTitle());
        Utils.verifyLastModified(membership.getLastModifiedDate());

        LastModifiedDate before = membership.getLastModifiedDate();

        membership.setDepartmentName("Updated department name");
        membership.setRoleTitle("The updated role title");
        
        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        membership.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateMembership(ORCID, 39L, membership);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewMembership(ORCID, 39L);
        assertNotNull(response);
        membership = (Membership) response.getEntity();
        assertNotNull(membership);
        Utils.verifyLastModified(membership.getLastModifiedDate());
        assertTrue(membership.getLastModifiedDate().after(before));
        assertEquals("Updated department name", membership.getDepartmentName());
        assertEquals("The updated role title", membership.getRoleTitle());

        // Rollback changes
        membership.setDepartmentName("PRIVATE Department");
        membership.setRoleTitle("PRIVATE");

        response = serviceDelegator.updateMembership(ORCID, 39L, membership);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateMembershipYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewMembership(ORCID, 40L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        membership.setDepartmentName("Updated department name");
        membership.setRoleTitle("The updated role title");
        serviceDelegator.updateMembership(ORCID, 40L, membership);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateMembershipChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        assertEquals(Visibility.PUBLIC, membership.getVisibility());

        membership.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateMembership(ORCID, 37L, membership);
        fail();
    }

    @Test
    public void testUpdateMembershipLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        assertEquals(Visibility.PUBLIC, membership.getVisibility());
        
        membership.setVisibility(null);

        response = serviceDelegator.updateMembership(ORCID, 37L, membership);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        membership = (Membership) response.getEntity();
        assertNotNull(membership);
        assertEquals(Visibility.PUBLIC, membership.getVisibility());
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateMembershipDuplicateExternalIDs() {
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

        Membership membership = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
        membership.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createMembership(ORCID, membership);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Membership another = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
        response = serviceDelegator.createMembership(ORCID, another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewMembership(ORCID, putCode2);
        another = (Membership) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateMembership(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteMembership() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewMembership("0000-0000-0000-0002", 1004L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1004L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
        serviceDelegator.viewMembership("0000-0000-0000-0002", 1004L);
        fail();
        }catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteMembershipYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 40L);
        fail();
    }

}
