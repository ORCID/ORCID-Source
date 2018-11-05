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
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Employments;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_EmploymentsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
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
    public void testViewEmploymentsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployments(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 17L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmploymentSummary(ORCID, 17L);
    }

    @Test
    public void testViewEmploymentsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployments(ORCID);
        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employments", element.getPath());
        
        for (AffiliationGroup<EmploymentSummary> group : element.getEmploymentGroups()) {
            for (EmploymentSummary summary : group.getActivities()) {
                Utils.assertIsPublicOrSource(summary, "APP-5555555555555555");
            }
        }
    }

    @Test
    public void testViewEmploymentReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmploymentSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment(ORCID, 19L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(19L), employment.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/19", employment.getPath());
        assertEquals("PRIVATE Department", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewLimitedEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment(ORCID, 18L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(18L), employment.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/18", employment.getPath());
        assertEquals("LIMITED Department", employment.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewPrivateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment(ORCID, 19L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertEquals(Long.valueOf(19L), employment.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/19", employment.getPath());
        assertEquals("PRIVATE Department", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 24L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
        fail();
    }

    @Test
    public void testViewEmployments() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertEquals("/0000-0000-0000-0003/employments", employments.getPath());
        Utils.verifyLastModified(employments.getLastModifiedDate());
        assertNotNull(employments.retrieveGroups());
        assertEquals(3, employments.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;
        
        for (AffiliationGroup<EmploymentSummary> group : employments.retrieveGroups()) {
            EmploymentSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(18).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(19).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(23).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(17), group.getActivities().get(1).getPutCode());
                found3 = true;
            } else {
                fail("Invalid education found: " + element0.getPutCode());
            }
            
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testReadPublicScope_Employments() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(r);
        assertEquals(Employment.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        assertNotNull(r);
        assertEquals(EmploymentSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 18L);
        serviceDelegator.viewEmploymentSummary(ORCID, 18L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewEmployment(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 19L);
        serviceDelegator.viewEmploymentSummary(ORCID, 19L);
        // Private that am not the source of should fail
        try {
            serviceDelegator.viewEmployment(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().retrieveGroups());
        assertEquals(3, summary.getEmployments().retrieveGroups().size());
        assertNotNull(summary.getEmployments().retrieveGroups().iterator().next().getActivities().get(0));

        response = serviceDelegator.createEmployment(ORCID, (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT));
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
        assertNotNull(summaryWithNewElement.getEmployments());
        Utils.verifyLastModified(summaryWithNewElement.getEmployments().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getEmployments().retrieveGroups());
        assertEquals(4, summaryWithNewElement.getEmployments().retrieveGroups().size());
        
        boolean haveNew = false;

        for (AffiliationGroup<EmploymentSummary> group : summaryWithNewElement.getEmployments().retrieveGroups()) {
            for (EmploymentSummary eSummary : group.getActivities()) {
                assertNotNull(eSummary.getPutCode());
                Utils.verifyLastModified(eSummary.getLastModifiedDate());
                if (eSummary.getPutCode().equals(putCode)) {
                    assertEquals("My department name", eSummary.getDepartmentName());
                    haveNew = true;
                } else {
                    boolean found = false;
                    for (AffiliationGroup<EmploymentSummary> g : summaryWithNewElement.getEmployments().retrieveGroups()) { 
                        if (g.getActivities().contains(eSummary)) {
                            found = true;
                        }
                    }
                    assertTrue(found);
                }
            }
        }
        
        assertTrue(haveNew);
        
        //Delete new element
        serviceDelegator.deleteAffiliation(ORCID, putCode);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddEmploymentsDuplicateExternalIDs() {
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

        Employment employment = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        employment.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEmployment(ORCID, employment);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Employment duplicate = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createEmployment(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }

    @Test
    public void testUpdateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment(ORCID, 18L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("LIMITED Department", employment.getDepartmentName());
        assertEquals("LIMITED", employment.getRoleTitle());
        Utils.verifyLastModified(employment.getLastModifiedDate());
        LastModifiedDate before = employment.getLastModifiedDate();

        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        employment.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateEmployment(ORCID, 18L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEmployment(ORCID, 18L);
        assertNotNull(response);
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        Utils.verifyLastModified(employment.getLastModifiedDate());
        assertTrue(employment.getLastModifiedDate().after(before));
        assertEquals("Updated department name", employment.getDepartmentName());
        assertEquals("The updated role title", employment.getRoleTitle());

        // Rollback changes
        employment.setDepartmentName("LIMITED Department");
        employment.setRoleTitle("LIMITED");

        response = serviceDelegator.updateEmployment(ORCID, 18L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateEmploymentDuplicateExternalIDs() {
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

        Employment employment = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        employment.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEmployment(ORCID, employment);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Employment another = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        response = serviceDelegator.createEmployment(ORCID, another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewEmployment(ORCID, putCode2);
        another = (Employment) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateEmployment(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment(ORCID, 23L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");
        serviceDelegator.updateEmployment(ORCID, 23L, employment);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PUBLIC, employment.getVisibility());

        employment.setVisibility(Visibility.LIMITED);

        response = serviceDelegator.updateEmployment(ORCID, 17L, employment);
        fail();
    }

    @Test
    public void testUpdateEmploymentLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment(ORCID, 19L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(null);

        response = serviceDelegator.updateEmployment(ORCID, 19L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());
    }

    @Test
    public void testDeleteEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("0000-0000-0000-0002", 1002L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1002L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
        serviceDelegator.viewEmployment("0000-0000-0000-0002", 1002L);
        fail();
        }catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 23L);
        fail();
    }
}
