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
import org.orcid.core.exception.InvalidOrgAddressException;
import org.orcid.core.exception.InvalidOrgException;
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
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-api-web-context.xml" })
public class MemberV3ApiServiceDelegator_EducationsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
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
    public void testViewEducationsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducations(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 20L);
    }

    @Test
    public void testViewEducationReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        Education element = (Education) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducationSummary(ORCID, 20L);
    }

    @Test
    public void testViewEducationsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducations(ORCID);
        Educations element = (Educations) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/educations", element.getPath());
        
        for (AffiliationGroup<EducationSummary> group : element.getEducationGroups()) {
            for (EducationSummary summary : group.getActivities()) {
                Utils.assertIsPublicOrSource(summary, "APP-5555555555555555");
            }
        }
    }

    @Test
    public void testViewEducationSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducationSummary(ORCID, 20L);
        EducationSummary element = (EducationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(20L), education.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        assertEquals("PUBLIC Department", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
    }

    @Test
    public void testViewLimitedEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation(ORCID, 21L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(21L), education.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/21", education.getPath());
        assertEquals("LIMITED Department", education.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
    }

    @Test
    public void testViewPrivateEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation(ORCID, 22L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(22L), education.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/22", education.getPath());
        assertEquals("PRIVATE Department", education.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 26L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Education 20 belongs to 0000-0000-0000-0003
        serviceDelegator.viewEducation("4444-4444-4444-4446", 20L);
        fail();
    }

    @Test
    public void testViewEducations() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations educations = (Educations) r.getEntity();
        assertNotNull(educations);
        assertEquals("/0000-0000-0000-0003/educations", educations.getPath());
        Utils.verifyLastModified(educations.getLastModifiedDate());
        assertNotNull(educations.retrieveGroups());
        assertEquals(3, educations.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;
        
        for (AffiliationGroup<EducationSummary> group : educations.retrieveGroups()) {
            EducationSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(21).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(22).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(25).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
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
    public void testReadPublicScope_Educations() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(r);
        assertEquals(Education.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewEducationSummary(ORCID, 20L);
        assertNotNull(r);
        assertEquals(EducationSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewEducation(ORCID, 21L);
        serviceDelegator.viewEducationSummary(ORCID, 21L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewEducation(ORCID, 25L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 25L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewEducation(ORCID, 22L);
        serviceDelegator.viewEducationSummary(ORCID, 22L);
        // Private that am not the source of should fails
        try {
            serviceDelegator.viewEducation(ORCID, 26L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 26L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().retrieveGroups());
        assertEquals(3, summary.getEducations().retrieveGroups().size());
        
        EducationSummary educationSummary = summary.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(educationSummary);
        Utils.verifyLastModified(educationSummary.getLastModifiedDate());

        response = serviceDelegator.createEducation(ORCID, (Education) Utils.getAffiliation(AffiliationType.EDUCATION));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summaryWithNewElement = (ActivitiesSummary) response.getEntity();
        assertNotNull(summaryWithNewElement);
        Utils.verifyLastModified(summaryWithNewElement.getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getEducations());
        Utils.verifyLastModified(summaryWithNewElement.getEducations().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getEducations().retrieveGroups());
        assertEquals(4, summaryWithNewElement.getEducations().retrieveGroups().size());

        boolean haveNew = true;
        
        for (AffiliationGroup<EducationSummary> group : summary.getEducations().retrieveGroups()) {
            for (EducationSummary edSummary : group.getActivities()) {
                assertNotNull(edSummary.getPutCode());
                Utils.verifyLastModified(edSummary.getLastModifiedDate());
                if (edSummary.getPutCode().equals(putCode)) {
                    assertEquals("My department name", edSummary.getDepartmentName());
                    haveNew = true;
                } else {
                    boolean found = false;
                    for (AffiliationGroup<EducationSummary> g : summary.getEducations().retrieveGroups()) {
                        if (g.getActivities().contains(educationSummary)) {
                            found = true;
                            break;
                        }
                    }
                    assertTrue(found);
                }
            }
        }

        assertTrue(haveNew);
        
        //Delete the just created element
        serviceDelegator.deleteAffiliation(ORCID, putCode);
    }
    
    
    @Test(expected = InvalidOrgAddressException.class)
    public void testAddEducationNoCityNoCountry() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().retrieveGroups());
        assertEquals(3, summary.getEducations().retrieveGroups().size());
        
        EducationSummary educationSummary = summary.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(educationSummary);
        Utils.verifyLastModified(educationSummary.getLastModifiedDate());

        response = serviceDelegator.createEducation(ORCID, (Education) Utils.getAffiliationNoCityNoCountry(AffiliationType.EDUCATION));

    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddEducationsDuplicateExternalIDs() {
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

        Education education = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        education.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEducation(ORCID, education);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Long putCode = Utils.getPutCode(response);

        try {
            Education duplicate = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createEducation(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }


    @Test
    public void testUpdateEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("PUBLIC Department", education.getDepartmentName());
        assertEquals("PUBLIC", education.getRoleTitle());
        Utils.verifyLastModified(education.getLastModifiedDate());

        LastModifiedDate before = education.getLastModifiedDate();

        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");
        
        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        education.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateEducation(ORCID, 20L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertTrue(education.getLastModifiedDate().after(before));
        assertEquals("Updated department name", education.getDepartmentName());
        assertEquals("The updated role title", education.getRoleTitle());

        // Rollback changes
        education.setDepartmentName("PUBLIC Department");
        education.setRoleTitle("PUBLIC");

        response = serviceDelegator.updateEducation(ORCID, 20L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation(ORCID, 25L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");
        serviceDelegator.updateEducation(ORCID, 25L, education);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEducationChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());

        education.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateEducation(ORCID, 20L, education);
        fail();
    }

    @Test
    public void testUpdateEducationLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());
        
        education.setVisibility(null);

        response = serviceDelegator.updateEducation(ORCID, 20L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateEducationDuplicateExternalIDs() {
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

        Education education = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        education.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createEducation(ORCID, education);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Long putCode1 = Utils.getPutCode(response);

        Education another = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        response = serviceDelegator.createEducation(ORCID, another);
        
        Long putCode2 = Utils.getPutCode(response);
        
        response = serviceDelegator.viewEducation(ORCID, putCode2);
        another = (Education) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateEducation(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteEducation() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("0000-0000-0000-0002", 1001L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1001L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        try {
        serviceDelegator.viewEducation("0000-0000-0000-0002", 1001L);
        fail();
        }catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 23L);
        fail();
    }

}
