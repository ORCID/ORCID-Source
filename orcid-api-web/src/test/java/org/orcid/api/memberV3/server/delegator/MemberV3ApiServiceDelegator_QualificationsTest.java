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
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_QualificationsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
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
    public void testViewQualificationsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualifications(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewQualificationWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualification(ORCID, 42L);
    }

    @Test
    public void testViewQualificationReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewQualification(ORCID, 42L);
        Qualification element = (Qualification) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewQualificationSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualificationSummary(ORCID, 42L);
    }

    @Test
    public void testViewQualificationsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewQualifications(ORCID);
        Qualifications element = (Qualifications) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualifications", element.getPath());
        
        for (AffiliationGroup<QualificationSummary> group : element.getQualificationGroups()) {
            for (QualificationSummary summary : group.getActivities()) {
                Utils.assertIsPublicOrSource(summary, "APP-5555555555555555");
            }
        }
    }

    @Test
    public void testViewQualificationSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewQualificationSummary(ORCID, 42L);
        QualificationSummary element = (QualificationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        Utils.verifyLastModified(qualification.getLastModifiedDate());
        assertEquals(Long.valueOf(42L), qualification.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/42", qualification.getPath());
        assertEquals("PUBLIC Department", qualification.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), qualification.getVisibility().value());
    }

    @Test
    public void testViewLimitedQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewQualification(ORCID, 45L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        Utils.verifyLastModified(qualification.getLastModifiedDate());
        assertEquals(Long.valueOf(45L), qualification.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/45", qualification.getPath());
        assertEquals("SELF LIMITED Department", qualification.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), qualification.getVisibility().value());
    }

    @Test
    public void testViewPrivateQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewQualification(ORCID, 44L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        Utils.verifyLastModified(qualification.getLastModifiedDate());
        assertEquals(Long.valueOf(44L), qualification.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/44", qualification.getPath());
        assertEquals("PRIVATE Department", qualification.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), qualification.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateQualificationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualification(ORCID, 46L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewQualificationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Qualification 42 belongs to 0000-0000-0000-0003
        serviceDelegator.viewQualification("4444-4444-4444-4446", 42L);
        fail();
    }

    @Test
    public void testViewQualifications() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewQualifications(ORCID);
        assertNotNull(r);
        Qualifications qualifications = (Qualifications) r.getEntity();
        assertNotNull(qualifications);
        assertEquals("/0000-0000-0000-0003/qualifications", qualifications.getPath());
        Utils.verifyLastModified(qualifications.getLastModifiedDate());
        assertNotNull(qualifications.retrieveGroups());
        assertEquals(3, qualifications.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;
        
        for (AffiliationGroup<QualificationSummary> group : qualifications.retrieveGroups()) {
            QualificationSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(43).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(44).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(45).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid qualification found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testReadPublicScope_Qualifications() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(r);
        assertEquals(Qualification.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewQualificationSummary(ORCID, 42L);
        assertNotNull(r);
        assertEquals(QualificationSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewQualification(ORCID, 43L);
        serviceDelegator.viewQualificationSummary(ORCID, 43L);
        
        // Private that am the source of should work
        serviceDelegator.viewQualification(ORCID, 44L);
        serviceDelegator.viewQualificationSummary(ORCID, 44L);
        
        
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewQualification(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewQualificationSummary(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am not the source of should fails
        try {
            serviceDelegator.viewQualification(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewQualificationSummary(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary originalSummary = (ActivitiesSummary) response.getEntity();
        assertNotNull(originalSummary);
        Utils.verifyLastModified(originalSummary.getLastModifiedDate());
        assertNotNull(originalSummary.getQualifications());
        Utils.verifyLastModified(originalSummary.getQualifications().getLastModifiedDate());
        assertNotNull(originalSummary.getQualifications().retrieveGroups());
        assertEquals(3, originalSummary.getQualifications().retrieveGroups().size());
        
        QualificationSummary qualificationSummary = originalSummary.getQualifications().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(qualificationSummary);
        Utils.verifyLastModified(qualificationSummary.getLastModifiedDate());

        response = serviceDelegator.createQualification(ORCID, (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION));
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
        assertNotNull(summaryWithNewElement.getQualifications());
        Utils.verifyLastModified(summaryWithNewElement.getQualifications().getLastModifiedDate());
        assertNotNull(summaryWithNewElement.getQualifications().retrieveGroups());
        assertEquals(4, summaryWithNewElement.getQualifications().retrieveGroups().size());
        
        boolean haveNew = false;

        for (AffiliationGroup<QualificationSummary> group : summaryWithNewElement.getQualifications().retrieveGroups()) {
            for (QualificationSummary qs : group.getActivities()) {
                assertNotNull(qs.getPutCode());
                Utils.verifyLastModified(qs.getLastModifiedDate());
                if (qs.getPutCode().equals(putCode)) {
                    assertEquals("My department name", qs.getDepartmentName());
                    haveNew = true;
                } else {
                    boolean found = false;
                    for (AffiliationGroup<QualificationSummary> g : originalSummary.getQualifications().retrieveGroups()) {
                        if (g.getActivities().contains(qs)) {
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
    public void testAddQualificationsDuplicateExternalIDs() {
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

        Qualification qualification = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
        qualification.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createQualification(ORCID, qualification);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        try {
            Qualification duplicate = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
            duplicate.setExternalIDs(externalIDs);
            serviceDelegator.createQualification(ORCID, duplicate);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode);
        }
    }

    @Test
    public void testUpdateQualification() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewQualification(ORCID, 43L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        assertEquals("LIMITED Department", qualification.getDepartmentName());
        assertEquals("LIMITED", qualification.getRoleTitle());
        Utils.verifyLastModified(qualification.getLastModifiedDate());

        LastModifiedDate before = qualification.getLastModifiedDate();

        qualification.setDepartmentName("Updated department name");
        qualification.setRoleTitle("The updated role title");
        
        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        qualification.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        response = serviceDelegator.updateQualification(ORCID, 43L, qualification);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewQualification(ORCID, 43L);
        assertNotNull(response);
        qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        Utils.verifyLastModified(qualification.getLastModifiedDate());
        assertTrue(qualification.getLastModifiedDate().after(before));
        assertEquals("Updated department name", qualification.getDepartmentName());
        assertEquals("The updated role title", qualification.getRoleTitle());

        // Rollback changes
        qualification.setDepartmentName("LIMITED Department");
        qualification.setRoleTitle("QUALIFICATION");

        response = serviceDelegator.updateQualification(ORCID, 43L, qualification);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateQualificationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewQualification(ORCID, 45L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        qualification.setDepartmentName("Updated department name");
        qualification.setRoleTitle("The updated role title");
        serviceDelegator.updateQualification(ORCID, 45L, qualification);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateQualificationChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewQualification(ORCID, 43L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        assertEquals(Visibility.LIMITED, qualification.getVisibility());

        qualification.setVisibility(Visibility.PUBLIC);

        response = serviceDelegator.updateQualification(ORCID, 43L, qualification);
        fail();
    }

    @Test
    public void testUpdateQualificationLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        assertEquals(Visibility.PUBLIC, qualification.getVisibility());
        
        qualification.setVisibility(null);

        response = serviceDelegator.updateQualification(ORCID, 42L, qualification);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        assertEquals(Visibility.PUBLIC, qualification.getVisibility());
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateQualificationDuplicateExternalIDs() {
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

        Qualification qualification = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
        qualification.setExternalIDs(externalIDs);

        Response response = serviceDelegator.createQualification(ORCID, qualification);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        Qualification another = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
        response = serviceDelegator.createQualification(ORCID, another);
        
        map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        response = serviceDelegator.viewQualification(ORCID, putCode2);
        another = (Qualification) response.getEntity();
        another.setExternalIDs(externalIDs);
        
        try {
            serviceDelegator.updateQualification(ORCID, putCode2, another);
        } finally {
            serviceDelegator.deleteAffiliation(ORCID, putCode1);
            serviceDelegator.deleteAffiliation(ORCID, putCode2);
        }
    }

    @Test
    public void testDeleteQualification() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewQualification("0000-0000-0000-0002", 1005L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1005L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
        serviceDelegator.viewQualification("0000-0000-0000-0002", 1005L);
        fail();
        } catch(NoResultException nre) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteQualificationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation(ORCID, 45L);
        fail();
    }

}
