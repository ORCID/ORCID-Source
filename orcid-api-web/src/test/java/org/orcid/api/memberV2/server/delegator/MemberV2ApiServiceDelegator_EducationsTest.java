package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.test.helper.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_EducationsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @Resource
    protected EmailFrequencyManager emailFrequencyManager;
    
    @Mock
    protected EmailFrequencyManager mockEmailFrequencyManager;
        
    @Resource(name = "notificationManager")
    private NotificationManager notificationManager;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.QUARTERLY_TIPS, String.valueOf(true));
        
        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(map);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailFrequencyManager", mockEmailFrequencyManager); 
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailFrequencyManager", emailFrequencyManager);         
    }
    
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
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
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
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 7L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(7L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/7", education.getPath());
        assertEquals("Education Dept # 2", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
    }

    @Test
    public void testViewLimitedEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 9L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(9L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/9", education.getPath());
        assertEquals("Education Dept # 3", education.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
    }

    @Test
    public void testViewPrivateEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertEquals(Long.valueOf(6L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/6", education.getPath());
        assertEquals("Education Dept # 1", education.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation("4444-4444-4444-4446", 10L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Education 1 belongs to 4444-4444-4444-4442
        serviceDelegator.viewEducation("4444-4444-4444-4446", 1L);
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
        assertNotNull(educations.getSummaries());
        assertEquals(4, educations.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (EducationSummary summary : educations.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            if (Long.valueOf(20).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(21).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(22).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if (Long.valueOf(25).equals(summary.getPutCode())) {
                assertEquals("SELF LIMITED Department", summary.getDepartmentName());
                found4 = true;
            } else {
                fail("Invalid education found: " + summary.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
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
            serviceDelegator.viewEducation(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 23L);
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
            serviceDelegator.viewEducation(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4442");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().getSummaries());
        assertNotNull(summary.getEducations().getSummaries().get(0));
        Utils.verifyLastModified(summary.getEducations().getSummaries().get(0).getLastModifiedDate());
        assertEquals(Long.valueOf(1), summary.getEducations().getSummaries().get(0).getPutCode());

        response = serviceDelegator.createEducation("4444-4444-4444-4442", Utils.getEducation());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4442");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EducationSummary educationSummary : summary.getEducations().getSummaries()) {
            assertNotNull(educationSummary.getPutCode());
            Utils.verifyLastModified(educationSummary.getLastModifiedDate());
            if (educationSummary.getPutCode() == 1L) {
                assertEquals("A Department", educationSummary.getDepartmentName());
                haveOld = true;
            } else {
                assertEquals(putCode, educationSummary.getPutCode());
                assertEquals("My department name", educationSummary.getDepartmentName());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("Another Department", education.getDepartmentName());
        assertEquals("Student", education.getRoleTitle());
        Utils.verifyLastModified(education.getLastModifiedDate());

        LastModifiedDate before = education.getLastModifiedDate();

        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        education = (Education) response.getEntity();
        assertNotNull(education);
        Utils.verifyLastModified(education.getLastModifiedDate());
        assertTrue(education.getLastModifiedDate().after(before));
        assertEquals("Updated department name", education.getDepartmentName());
        assertEquals("The updated role title", education.getRoleTitle());

        // Rollback changes
        education.setDepartmentName("Another Department");
        education.setRoleTitle("Student");

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");
        serviceDelegator.updateEducation("4444-4444-4444-4442", 1L, education);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEducationChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());

        education.setVisibility(education.getVisibility().equals(Visibility.PRIVATE) ? Visibility.LIMITED : Visibility.PRIVATE);

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        fail();
    }

    @Test
    public void testUpdateEducationLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());

        education.setVisibility(null);

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4447", 12L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);

        response = serviceDelegator.deleteAffiliation("4444-4444-4444-4447", 12L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewEducation("4444-4444-4444-4447", 12L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 9L);
        fail();
    }

}
