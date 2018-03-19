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
import org.orcid.jaxb.model.v3.dev1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.AffiliationType;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.jaxb.model.v3.dev1.record.WorkBulk;
import org.orcid.jaxb.model.v3.dev1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.Distinctions;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_DistinctionsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USE THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_dev1")
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
    public void testViewDistinctionsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinctions(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinction(ORCID, 27L);
    }

    @Test
    public void testViewDistinctionReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        Distinction element = (Distinction) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinctionSummary(ORCID, 27L);
    }

    @Test
    public void testViewDistinctionsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewDistinctions(ORCID);
        Distinctions element = (Distinctions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinctions", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewDistinctionSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewDistinctionSummary(ORCID, 27L);
        DistinctionSummary element = (DistinctionSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        Utils.verifyLastModified(distinction.getLastModifiedDate());
        assertEquals(Long.valueOf(27L), distinction.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/27", distinction.getPath());
        assertEquals("PUBLIC Department", distinction.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), distinction.getVisibility().value());
    }

    @Test
    public void testViewLimitedDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewDistinction(ORCID, 30L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        Utils.verifyLastModified(distinction.getLastModifiedDate());
        assertEquals(Long.valueOf(30L), distinction.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/30", distinction.getPath());
        assertEquals("SELF LIMITED Department", distinction.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), distinction.getVisibility().value());
    }

    @Test
    public void testViewPrivateDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewDistinction(ORCID, 29L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        Utils.verifyLastModified(distinction.getLastModifiedDate());
        assertEquals(Long.valueOf(29L), distinction.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/29", distinction.getPath());
        assertEquals("PRIVATE Department", distinction.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), distinction.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateDistinctionWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinction(ORCID, 31L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewDistinctionThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Distinction 27 belongs to 0000-0000-0000-0003
        serviceDelegator.viewDistinction("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test
    public void testViewDistinctions() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewDistinctions(ORCID);
        assertNotNull(r);
        Distinctions distinctions = (Distinctions) r.getEntity();
        assertNotNull(distinctions);
        assertEquals("/0000-0000-0000-0003/distinctions", distinctions.getPath());
        Utils.verifyLastModified(distinctions.getLastModifiedDate());
        assertNotNull(distinctions.getSummaries());
        assertEquals(4, distinctions.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (DistinctionSummary summary : distinctions.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            if (Long.valueOf(27).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(28).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(29).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if (Long.valueOf(30).equals(summary.getPutCode())) {
                assertEquals("SELF LIMITED Department", summary.getDepartmentName());
                found4 = true;
            } else {
                fail("Invalid distinction found: " + summary.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadPublicScope_Distinctions() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(r);
        assertEquals(Distinction.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewDistinctionSummary(ORCID, 27L);
        assertNotNull(r);
        assertEquals(DistinctionSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewDistinction(ORCID, 28L);
        serviceDelegator.viewDistinctionSummary(ORCID, 28L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewDistinction(ORCID, 30L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewDistinctionSummary(ORCID, 30L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewDistinction(ORCID, 29L);
        serviceDelegator.viewDistinctionSummary(ORCID, 29L);
        // Private that am not the source of should fails
        try {
            serviceDelegator.viewDistinction(ORCID, 31L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewDistinctionSummary(ORCID, 31L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary originalSummary = (ActivitiesSummary) response.getEntity();
        assertNotNull(originalSummary);
        Utils.verifyLastModified(originalSummary.getLastModifiedDate());
        assertNotNull(originalSummary.getDistinctions());
        Utils.verifyLastModified(originalSummary.getDistinctions().getLastModifiedDate());
        assertNotNull(originalSummary.getDistinctions().getSummaries());
        assertNotNull(originalSummary.getDistinctions().getSummaries().get(0));
        Utils.verifyLastModified(originalSummary.getDistinctions().getSummaries().get(0).getLastModifiedDate());
        assertEquals(4, originalSummary.getDistinctions().getSummaries().size());

        response = serviceDelegator.createDistinction(ORCID, (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION));
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
        assertNotNull(summaryWithNewElement.getDistinctions());
        Utils.verifyLastModified(summaryWithNewElement.getDistinctions().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getDistinctions().getSummaries());
        assertEquals(5, summaryWithNewElement.getDistinctions().getSummaries().size());
        
        boolean haveNew = false;

        for (DistinctionSummary distinctionSummary : summaryWithNewElement.getDistinctions().getSummaries()) {
            assertNotNull(distinctionSummary.getPutCode());
            Utils.verifyLastModified(distinctionSummary.getLastModifiedDate());
            if (distinctionSummary.getPutCode().equals(putCode)) {
                assertEquals("My department name", distinctionSummary.getDepartmentName());
                haveNew = true;
            } else {
                assertTrue(originalSummary.getDistinctions().getSummaries().contains(distinctionSummary));
            }
        }
        
        assertTrue(haveNew);
        
        //Remove new element
        serviceDelegator.deleteAffiliation(ORCID, putCode);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void testAddDistinctionNoStartDate() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Distinction distinction = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        distinction.setStartDate(null);
        serviceDelegator.createDistinction(ORCID, distinction);
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddDistinctionsDuplicateExternalIDs() {
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

        Distinction distinction = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        distinction.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createDistinction(ORCID, distinction);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Distinction duplicate = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createDistinction(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }


    @Test
    public void testUpdateDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        assertEquals("PUBLIC Department", distinction.getDepartmentName());
        assertEquals("PUBLIC", distinction.getRoleTitle());
        Utils.verifyLastModified(distinction.getLastModifiedDate());

        LastModifiedDate before = distinction.getLastModifiedDate();

        distinction.setDepartmentName("Updated department name");
        distinction.setRoleTitle("The updated role title");
        
        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        distinction.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateDistinction(ORCID, 27L, distinction);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        Utils.verifyLastModified(distinction.getLastModifiedDate());
        assertTrue(distinction.getLastModifiedDate().after(before));
        assertEquals("Updated department name", distinction.getDepartmentName());
        assertEquals("The updated role title", distinction.getRoleTitle());

        // Rollback changes
        distinction.setDepartmentName("PUBLIC Department");
        distinction.setRoleTitle("PUBLIC");

        response = serviceDelegator.updateDistinction(ORCID, 27L, distinction);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateDistinctionYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewDistinction(ORCID, 30L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        distinction.setDepartmentName("Updated department name");
        distinction.setRoleTitle("The updated role title");
        serviceDelegator.updateDistinction(ORCID, 30L, distinction);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateDistinctionChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        assertEquals(Visibility.PUBLIC, distinction.getVisibility());

        distinction.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateDistinction(ORCID, 27L, distinction);
        fail();
    }

    @Test
    public void testUpdateDistinctionLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        assertEquals(Visibility.PUBLIC, distinction.getVisibility());
        
        distinction.setVisibility(null);

        response = serviceDelegator.updateDistinction(ORCID, 27L, distinction);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        assertEquals(Visibility.PUBLIC, distinction.getVisibility());
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateDistinctionDuplicateExternalIDs() {
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

        Distinction distinction = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        distinction.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createDistinction(ORCID, distinction);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Distinction another = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        response = serviceDelegator.createDistinction(ORCID, another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewDistinction(ORCID, putCode2);
        another = (Distinction) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateDistinction(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteDistinction() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewDistinction("0000-0000-0000-0002", 1000L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1000L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
        serviceDelegator.viewDistinction("0000-0000-0000-0002", 1000L);
        fail();
        }catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteDistinctionYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 30L);
        fail();
    }

}
