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
package org.orcid.api.memberV2.server.delegator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc3.Country;
import org.orcid.jaxb.model.common_rc3.Iso3166Country;
import org.orcid.jaxb.model.common_rc3.OrcidIdentifier;
import org.orcid.jaxb.model.common_rc3.Organization;
import org.orcid.jaxb.model.common_rc3.OrganizationAddress;
import org.orcid.jaxb.model.common_rc3.Subtitle;
import org.orcid.jaxb.model.common_rc3.Title;
import org.orcid.jaxb.model.common_rc3.TranslatedTitle;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.common_rc3.VisibilityType;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecords;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc3.Activity;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Addresses;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Citation;
import org.orcid.jaxb.model.record_rc3.CitationType;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.ExternalID;
import org.orcid.jaxb.model.record_rc3.ExternalIDs;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.FundingTitle;
import org.orcid.jaxb.model.record_rc3.FundingType;
import org.orcid.jaxb.model.record_rc3.History;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.PeerReviewType;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
import org.orcid.jaxb.model.record_rc3.Role;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.jaxb.model.record_rc3.WorkBulk;
import org.orcid.jaxb.model.record_rc3.WorkTitle;
import org.orcid.jaxb.model.record_rc3.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    //Now on, for any new test, PLAESE USER THIS ORCID ID
    private final String ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "memberV2ApiServiceDelegator")
    private MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @Resource
    private WorkManager workManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }
    
    @Test    
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        
        // Check works
        assertNotNull(summary.getWorks());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        boolean foundPrivateWork = false;
        for(WorkGroup group : summary.getWorks().getWorkGroup()) {
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            assertThat(work.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
            assertThat(work.getPath(),
                    anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
            assertThat(work.getTitle().getTitle().getContent(),
                    anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
            if(work.getPutCode().equals(Long.valueOf(7))) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
                foundPrivateWork = true;
            } 
        }
        
        assertTrue(foundPrivateWork);
        
        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(3, summary.getFundings().getFundingGroup().size());
        boolean foundPrivateFunding = false;
        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            assertThat(funding.getPutCode(), anyOf(is(Long.valueOf(4)), is(Long.valueOf(5)), is(Long.valueOf(8))));
            assertThat(funding.getPath(),
                    anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5"), is("/4444-4444-4444-4446/funding/8")));
            assertThat(funding.getTitle().getTitle().getContent(),
                    anyOf(is("Private Funding"), is("Public Funding"), is("Limited Funding")));
            if(funding.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, funding.getVisibility());                
                foundPrivateFunding = true;
            }
        }
        
        assertTrue(foundPrivateFunding);
        
        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(3, summary.getEducations().getSummaries().size());
        
        boolean foundPrivateEducation = false;
        for(EducationSummary education : summary.getEducations().getSummaries()) {
            assertThat(education.getPutCode(), anyOf(is(Long.valueOf(6)), is(Long.valueOf(7)), is(Long.valueOf(9))));
            assertThat(education.getPath(),
                    anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7"), is("/4444-4444-4444-4446/education/9")));
            assertThat(education.getDepartmentName(),
                    anyOf(is("Education Dept # 1"), is("Education Dept # 2"), is("Education Dept # 3")));

            if(education.getPutCode().equals(6L)) {
                assertEquals(Visibility.PRIVATE, education.getVisibility());
                foundPrivateEducation = true;
            }
        }
        
        assertTrue(foundPrivateEducation);
        
        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(3, summary.getEmployments().getSummaries().size());
        
        boolean foundPrivateEmployment = false;
        
        for(EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            assertThat(employment.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(8)), is(Long.valueOf(11))));
            assertThat(employment.getPath(),
                    anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8"), is("/4444-4444-4444-4446/employment/11")));
            assertThat(employment.getDepartmentName(),
                    anyOf(is("Employment Dept # 1"), is("Employment Dept # 2"), is("Employment Dept # 4")));
            if(employment.getPutCode().equals(5L)) {
                assertEquals(Visibility.PRIVATE, employment.getVisibility());
                foundPrivateEmployment = true;
            }
        }       
        
        assertTrue(foundPrivateEmployment);
        
        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        assertEquals(3, summary.getPeerReviews().getPeerReviewGroup().size());
        
        boolean foundPrivatePeerReview = false;
        for(PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            assertNotNull(group.getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            assertThat(peerReview.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(3)), is(Long.valueOf(4))));
            assertThat(peerReview.getGroupId(), anyOf(is("issn:0000001"), is("issn:0000002"), is("issn:0000003")));
            if(peerReview.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, peerReview.getVisibility());
                foundPrivatePeerReview = true;
            }
        }
        
        assertTrue(foundPrivatePeerReview);
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        for (int i = 0; i < 5; i++) {
            WorkSummary summary = new WorkSummary();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work " + i));
            title.setTranslatedTitle(new TranslatedTitle("", ""));
            summary.setTitle(title);
            group.getWorkSummary().add(summary);
        }
        works.getWorkGroup().add(group);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setWorks(works);

        ActivityUtils.cleanEmptyFields(as);

        assertNotNull(as);
        assertNotNull(as.getWorks());
        assertNotNull(as.getWorks().getWorkGroup());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertNotNull(as.getWorks().getWorkGroup().get(0).getWorkSummary());
        assertEquals(5, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        for (WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }

    }

    /**
     * TEST WORKS
     */
    @Test
    public void testViewPublicWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(5), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/5", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
    }

    @Test
    public void testViewLimitedWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article B", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(6), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/6", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
    }

    @Test
    public void testViewPrivateWork() {
        // Use the smallest scope in the pyramid to verify that you can read
        // your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 7L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article C", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(7), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/7", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork("4444-4444-4444-4446", 8L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewWorkThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test
    public void viewWorksTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works)r.getEntity();
        assertNotNull(works);
        assertNotNull(works.getWorkGroup());
        assertEquals(4, works.getWorkGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        
        for(WorkGroup workGroup : works.getWorkGroup()) {
            assertNotNull(workGroup.getIdentifiers());
            assertNotNull(workGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, workGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(workGroup.getWorkSummary());
            assertEquals(1, workGroup.getWorkSummary().size());
            WorkSummary summary = workGroup.getWorkSummary().get(0);
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            switch(workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(14), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
            
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);        
    }
    
    @Test
    public void testCleanEmptyFieldsOnWorks() {
        Work work = new Work();
        work.setWorkCitation(new Citation("", CitationType.FORMATTED_UNSPECIFIED));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("My Work"));
        title.setSubtitle(new Subtitle("My subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("", ""));
        work.setWorkTitle(title);

        ActivityUtils.cleanEmptyFields(work);

        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertEquals("My Work", work.getWorkTitle().getTitle().getContent());
        assertEquals("My subtitle", work.getWorkTitle().getSubtitle().getContent());

        assertNull(work.getWorkCitation());
        assertNull(work.getWorkTitle().getTranslatedTitle());
    }

    @Test
    public void testAddWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4445");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        assertNotNull(summary.getWorks().getWorkGroup().get(0));
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getWorkSummary());
        assertEquals(1, summary.getWorks().getWorkGroup().get(0).getWorkSummary().size());

        String title = "work # 1 " + System.currentTimeMillis();
        Work work = getWork(title);
       
        response = serviceDelegator.createWork("4444-4444-4444-4445", work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = getPutCode(response);
        
        response = serviceDelegator.viewActivities("4444-4444-4444-4445");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(2, summary.getWorks().getWorkGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (WorkGroup group : summary.getWorks().getWorkGroup()) {
            assertNotNull(group.getWorkSummary());
            assertNotNull(group.getWorkSummary().get(0));
            WorkSummary workSummary = group.getWorkSummary().get(0);
            assertNotNull(workSummary.getTitle());
            assertNotNull(workSummary.getTitle().getTitle());
            if ("A Book With Contributors JSON".equals(workSummary.getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if (title.equals(workSummary.getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
        
        //Delete them
        serviceDelegator.deleteWork("4444-4444-4444-4445", putCode);
    }
    
    @Test
    public void testCreateWorksWithBulkAllOK() {        
        Long time = System.currentTimeMillis();
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        WorkBulk bulk = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Bulk work " + i + " " + time));
            work.setWorkTitle(title);

            ExternalIDs extIds = new ExternalIDs();
            ExternalID extId = new ExternalID();
            extId.setRelationship(Relationship.SELF);
            extId.setType("doi");
            extId.setUrl(new Url("http://doi/" + i + "/" + time));
            extId.setValue("doi-" + i + "-" + time);
            extIds.getExternalIdentifier().add(extId);
            work.setWorkExternalIdentifiers(extIds);

            work.setWorkType(WorkType.BOOK);
            bulk.getBulk().add(work);
        }

        Response response = serviceDelegator.createWorks(ORCID, bulk);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        bulk = (WorkBulk) response.getEntity();

        assertNotNull(bulk);
        assertEquals(5, bulk.getBulk().size());

        for (int i = 0; i < 5; i++) {
            assertTrue(Work.class.isAssignableFrom(bulk.getBulk().get(i).getClass()));
            Work w = (Work) bulk.getBulk().get(i);
            assertNotNull(w.getPutCode());
            assertTrue(0L < w.getPutCode());
            assertEquals("Bulk work " + i + " " + time, w.getWorkTitle().getTitle().getContent());
            assertNotNull(w.getExternalIdentifiers().getExternalIdentifier());
            assertEquals("doi-" + i + "-" + time, w.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

            Response r = serviceDelegator.viewWork(ORCID, w.getPutCode());
            assertNotNull(r);
            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
            assertEquals("Bulk work " + i + " " + time, ((Work) r.getEntity()).getWorkTitle().getTitle().getContent());

            // Delete the work
            r = serviceDelegator.deleteWork(ORCID, w.getPutCode());
            assertNotNull(r);
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
        }
    }    

    @Test
    public void testUpdateWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("A day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        
        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");

        response = serviceDelegator.updateWork("4444-4444-4444-4443", 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewWork("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Updated work title", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.EDITED_BOOK, work.getWorkType());

        // Rollback changes so we dont break other tests
        work.setWorkType(WorkType.BOOK);
        work.getWorkTitle().getTitle().setContent("A day in the life");
        response = serviceDelegator.updateWork("4444-4444-4444-4443", 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(2), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Another day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());

        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");

        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(WorkExternalIdentifierType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);

        serviceDelegator.updateWork("4444-4444-4444-4443", 2L, work);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateWorkChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4447", 10L);
        assertNotNull(response);
        Work work = (Work) response.getEntity(); 
        assertNotNull(work);
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        
        work.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateWork("4444-4444-4444-4447", 10L, work);
        fail();
    }
    
    @Test
    public void testUpdateWorkLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4447", 10L);
        assertNotNull(response);
        Work work = (Work) response.getEntity(); 
        assertNotNull(work);
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        
        work.setVisibility(null);
        
        response = serviceDelegator.updateWork("4444-4444-4444-4447", 10L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Visibility.PUBLIC, work.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);

        response = serviceDelegator.deleteWork("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewWork("4444-4444-4444-4447", 9L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteWork("4444-4444-4444-4446", 8L);
        fail();
    }

    /**
     * TEST FUNDINGS
     */
    @Test
    public void testViewPublicFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(5), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/5", funding.getPath());
        assertEquals("Public Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewLimitedFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(1), funding.getPutCode());
        assertEquals("/4444-4444-4444-4443/funding/1", funding.getPath());
        assertEquals("Grant # 1", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewPrivateFunding() {
        // Use the smallest scope in the pyramid to verify that you can read
        // your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(4), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/4", funding.getPath());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Funding 1 belongs to 4444-4444-4444-4443
        serviceDelegator.viewFunding("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testViewFundings() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertNotNull(fundings.getFundingGroup());
        assertEquals(4, fundings.getFundingGroup().size());
        
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        
        for(FundingGroup fundingGroup : fundings.getFundingGroup()) {
            assertNotNull(fundingGroup.getIdentifiers());
            assertNotNull(fundingGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, fundingGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(fundingGroup.getFundingSummary());
            assertEquals(1, fundingGroup.getFundingSummary().size());
            FundingSummary summary = fundingGroup.getFundingSummary().get(0);
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            switch(fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
            
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }
    
    @Test
    public void testAddFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertNotNull(summary.getFundings().getFundingGroup().get(0));
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, summary.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0));
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle());
        assertEquals("Public Funding # 1", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());

        Funding newFunding = getFunding();

        response = serviceDelegator.createFunding("4444-4444-4444-4447", newFunding);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(2, summary.getFundings().getFundingGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary().get(0));
            assertNotNull(group.getFundingSummary().get(0).getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle().getContent());
            if ("Public Funding # 1".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if ("Public Funding # 2".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Public Funding # 1", funding.getTitle().getTitle().getContent());
        assertEquals("This is the description for funding with id 6", funding.getDescription());

        funding.getTitle().getTitle().setContent("Updated funding title");
        funding.setDescription("This is an updated description");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Updated funding title", funding.getTitle().getTitle().getContent());
        assertEquals("This is an updated description", funding.getDescription());

        // Rollback changes
        funding.getTitle().getTitle().setContent("Public Funding # 1");
        funding.setDescription("This is the description for funding with id 6");

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        funding.getTitle().getTitle().setContent("Updated funding title");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        serviceDelegator.updateFunding("4444-4444-4444-4446", 5L, funding);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateFundingChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
        
        funding.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        fail();       
    }

    @Test
    public void testUpdateFundingLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
        
        funding.setVisibility(null);
        
        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
            
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        funding = (Funding) response.getEntity();
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
    }            
    
    @Test(expected = NoResultException.class)
    public void testDeleteFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        response = serviceDelegator.deleteFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteFunding("4444-4444-4444-4446", 5L);
        fail();
    }

    /**
     * TEST EDUCATIONS
     */
    @Test
    public void testViewPublicEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 7L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
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
        assertNotNull(educations.getSummaries());
        assertEquals(4, educations.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for(EducationSummary summary : educations.getSummaries()) {
            if(Long.valueOf(20).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if(Long.valueOf(21).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if(Long.valueOf(22).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if(Long.valueOf(25).equals(summary.getPutCode())) {
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
    public void testAddEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4442");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertNotNull(summary.getEducations().getSummaries().get(0));
        assertEquals(Long.valueOf(1), summary.getEducations().getSummaries().get(0).getPutCode());
       
        response = serviceDelegator.createEducation("4444-4444-4444-4442", getEducation());
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
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EducationSummary educationSummary : summary.getEducations().getSummaries()) {
            assertNotNull(educationSummary.getPutCode());
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

        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        education = (Education) response.getEntity();
        assertNotNull(education);
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

    /**
     * TEST EMPLOYMENTS
     */
    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewLimitedEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(11L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/11", employment.getPath());
        assertEquals("Employment Dept # 4", employment.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewPrivateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 10L);
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
        assertNotNull(employments.getSummaries());
        assertEquals(4, employments.getSummaries().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for(EmploymentSummary summary : employments.getSummaries()) {
            if(Long.valueOf(17).equals(summary.getPutCode())) {
                assertEquals("PUBLIC Department", summary.getDepartmentName());
                found1 = true;
            } else if(Long.valueOf(18).equals(summary.getPutCode())) {
                assertEquals("LIMITED Department", summary.getDepartmentName());
                found2 = true;
            } else if(Long.valueOf(19).equals(summary.getPutCode())) {
                assertEquals("PRIVATE Department", summary.getDepartmentName());
                found3 = true;
            } else if(Long.valueOf(23).equals(summary.getPutCode())) {
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
    public void testAddEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertNotNull(summary.getEmployments().getSummaries().get(0));
        assertEquals(Long.valueOf(13), summary.getEmployments().getSummaries().get(0).getPutCode());        

        response = serviceDelegator.createEmployment("4444-4444-4444-4447", getEmployment());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EmploymentSummary EmploymentSummary : summary.getEmployments().getSummaries()) {
            assertNotNull(EmploymentSummary.getPutCode());
            if (EmploymentSummary.getPutCode() == 13L) {
                assertEquals("Employment Dept # 1", EmploymentSummary.getDepartmentName());
                haveOld = true;
            } else {
                assertEquals(putCode, EmploymentSummary.getPutCode());
                assertEquals("My department name", EmploymentSummary.getDepartmentName());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals("Researcher", employment.getRoleTitle());

        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Updated department name", employment.getDepartmentName());
        assertEquals("The updated role title", employment.getRoleTitle());

        // Rollback changes
        employment.setDepartmentName("Employment Dept # 1");
        employment.setRoleTitle("Researcher");

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");
        serviceDelegator.updateEmployment("4444-4444-4444-4446", 11L, employment);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(Visibility.LIMITED);
        
        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        fail();
    }
    
    @Test
    public void testUpdateEmploymentLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(null);
        
        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());        
    }    
    
    @Test(expected = NoResultException.class)
    public void testDeleteEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);

        response = serviceDelegator.deleteAffiliation("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 11L);
        fail();
    }

    /**
     * TEST PEER REVIEWS
     */
    @Test
    public void testViewPublicPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 1L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(1L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("reviewer", peerReview.getRole().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
        assertEquals("review", peerReview.getType().value());
        assertEquals("http://peer_review.com", peerReview.getUrl().getValue());
        assertEquals("Peer Review # 1", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("es", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals("artistic-performance", peerReview.getSubjectType().value());
        assertEquals("http://work.com", peerReview.getSubjectUrl().getValue());
        assertEquals("Peer Review # 1 container name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-external-identifier-id#1", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("agr", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("issn:0000001", peerReview.getGroupId());
    }

    @Test
    public void testViewLimitedPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 3L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(3L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("limited", peerReview.getVisibility().value());
        assertEquals("issn:0000002", peerReview.getGroupId());
    }

    @Test
    public void testViewPrivatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(4L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("issn:0000003", peerReview.getGroupId());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivatePeerReviewWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 2L);
        fail();
    }

    @Test
    public void testViewPeerReviewSummary() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
        assertNotNull(response);
        PeerReviewSummary peerReview = (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf("1"), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
    }

    @Test
    public void testViewPeerReviews() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews peerReviews = (PeerReviews) r.getEntity();
        assertNotNull(peerReviews);
        assertNotNull(peerReviews.getPeerReviewGroup());
        assertEquals(4, peerReviews.getPeerReviewGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for(PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary summary = group.getPeerReviewSummary().get(0);
            switch(group.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "issn:0000009":
                assertEquals("issn:0000009", summary.getGroupId());
                assertEquals(Long.valueOf(9), summary.getPutCode());
                found1 = true;
                break;
            case "issn:0000010":
                assertEquals("issn:0000010", summary.getGroupId());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                found2 = true;
                break;
            case "issn:0000011":
                assertEquals("issn:0000011", summary.getGroupId());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found3 = true;
                break;
            case "issn:0000012":
                assertEquals("issn:0000012", summary.getGroupId());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid group id found: " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }
    
    @Test
    public void testUpdatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        PeerReview updatedPeerReview = (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubjectName().getTitle().getContent());
    }

    @Test
    public void testUpdatePeerReviewWhenYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());

        // Update the info
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        peerReview.getExternalIdentifiers().getExternalIdentifier().iterator().next().setValue("different");
        
        try {
            response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 2L, peerReview);
            fail();
        } catch (WrongSourceException wse) {

        }
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", Long.valueOf(2));
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }
    
    @Test(expected = VisibilityMismatchException.class)    
    public void testUpdatePeerReviewChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
        
        peerReview.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        fail();
    }        
    
    @Test
    public void testUpdatePeerReviewLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
        
        peerReview.setVisibility(null);
        
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
    }
    
    @Test
    public void testAddPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0));
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0));
        assertEquals("issn:0000001", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());

        PeerReview peerReview = getPeerReview();

        response = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(2, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            if ("issn:0000001".equals(group.getPeerReviewSummary().get(0).getGroupId())) {
                haveOld = true;
            } else {
                assertEquals("issn:0000003", group.getPeerReviewSummary().get(0).getGroupId());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
        
        //Delete the new so it doesn't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode);
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddPeerReviewDuplicateFails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        
        peerReview.setPutCode(null);
        
        response = serviceDelegator.createPeerReview("4444-4444-4444-4447", peerReview);
    }
    
    @Test
    public void testAddPeerReviewWithSameExtIdValueButDifferentExtIdType() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        PeerReview peerReview1 = new PeerReview();
        ExternalIDs weis1 = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(null);
        wei1.setValue("same_but_different_type");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis1.getExternalIdentifier().add(wei1);
        peerReview1.setExternalIdentifiers(weis1);
        peerReview1.setGroupId("issn:0000003");
        peerReview1.setOrganization(getOrganization());
        peerReview1.setRole(Role.CHAIR);
        peerReview1.setSubjectContainerName(new Title("subject-container-name"));
        peerReview1.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("work-title"));
        peerReview1.setSubjectName(workTitle1);
        peerReview1.setSubjectType(WorkType.DATA_SET);
        peerReview1.setType(PeerReviewType.EVALUATION);

        Response response1 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview1);
        assertNotNull(response1);
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Map<?, ?> map = response1.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
                
        PeerReview peerReview2 = new PeerReview();
        ExternalIDs weis2 = new ExternalIDs();
        ExternalID wei2 = new ExternalID();
        wei2.setRelationship(null);
        wei2.setValue("same_but_different_type"); // Same value
        wei2.setType(WorkExternalIdentifierType.ARXIV.value()); // But different type
        weis2.getExternalIdentifier().add(wei2);
        peerReview2.setExternalIdentifiers(weis2);
        peerReview2.setGroupId("issn:0000003");
        peerReview2.setOrganization(getOrganization());
        peerReview2.setRole(Role.CHAIR);
        peerReview2.setSubjectContainerName(new Title("subject-container-name"));
        peerReview2.setSubjectExternalIdentifier(wei2);
        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("work-title"));
        peerReview2.setSubjectName(workTitle2);
        peerReview2.setSubjectType(WorkType.DATA_SET);
        peerReview2.setType(PeerReviewType.EVALUATION);

        Response response2 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview2);
        assertNotNull(response2);
        assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());
        map = response2.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
                
        //Delete new peer reviews so they don't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode1);
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode2);        
    }

    @Test
    public void testDeletePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        PeerReview review = (PeerReview) response.getEntity();
        assertNotNull(review);
        assertNotNull(review.getSubjectName());
        assertNotNull(review.getSubjectName().getTitle());
        assertEquals("Peer Review # 3", review.getSubjectName().getTitle().getContent());

        response = serviceDelegator.deletePeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

    }

    @Test(expected = WrongSourceException.class)
    public void testDeletePeerReviewYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deletePeerReview("4444-4444-4444-4447", 2L);
        fail();
    }

    /**
     * TEST GROUP ID
     */
    @Test
    public void testGetGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("2"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals(Long.valueOf(2), groupIdRecord.getPutCode());
        assertEquals("issn:0000002", groupIdRecord.getGroupId());
        assertEquals("TestGroup2", groupIdRecord.getName());
        assertEquals("TestDescription2", groupIdRecord.getDescription());
        assertEquals("publisher", groupIdRecord.getType());
    }

    @Test
    public void testCreateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();        
        Response response = serviceDelegator.createGroupIdRecord(getGroupIdRecord());
        // Response created with location as the group-id
        assertNotNull(response.getMetadata().get("Location").get(0));
        assertEquals(response.getMetadata().get("Location").get(0).toString(), "5");
    }

    @Test
    public void testUpdateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        // Verify the name
        assertEquals(groupIdRecord.getName(), "TestGroup3");
        // Set a new name for update
        groupIdRecord.setName("TestGroup33");
        serviceDelegator.updateGroupIdRecord(groupIdRecord, Long.valueOf("3"));

        // Get the entity again and verify the name
        response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        // Verify the name
        assertEquals(groupIdRecordNew.getName(), "TestGroup33");
    }

    @Test(expected = GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        // Verify if the record exists
        Response response = serviceDelegator.viewGroupIdRecord(4L);
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        // Delete the record
        serviceDelegator.deleteGroupIdRecord(Long.valueOf("4"));
        // Throws a record not found exception
        serviceDelegator.viewGroupIdRecord(Long.valueOf("4"));
    }

    @Test
    public void testGetGroupIdRecords() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        /*
         * At this point there should be at least 3 group ids and no more than
         * 5, since we are not sure if testDeleteGroupIdRecord and
         * testCreateGroupIdRecord have ran or not
         */

        // So, get a page with all
        Response response = serviceDelegator.viewGroupIdRecords("5", "1");
        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());

        int total = groupIdRecords1.getTotal();
        if (total < 3 || total > 5) {
            fail("There are more group ids than the expected, we are expecting between 3 and 5, total: " + total);
        }
    }

    /**
     * TEST RESEARCHER URLS
     */
    @Test
    public void testViewResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4443");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertEquals("/4444-4444-4444-4443/researcher-urls", researcherUrls.getPath());
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(5, researcherUrls.getResearcherUrls().size());
        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertThat(rUrl.getPutCode(),
                    anyOf(equalTo(Long.valueOf(2)), equalTo(Long.valueOf(3)), equalTo(Long.valueOf(5)), equalTo(Long.valueOf(7)), equalTo(Long.valueOf(8))));
            assertNotNull(rUrl.getSource());
            assertFalse(PojoUtil.isEmpty(rUrl.getSource().retrieveSourcePath()));
            assertNotNull(rUrl.getUrl());
            assertNotNull(rUrl.getUrlName());
            assertNotNull(rUrl.getVisibility());
            if (rUrl.getPutCode().equals(Long.valueOf(5)) || rUrl.getPutCode().equals(Long.valueOf(7))) {
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testViewLimitedResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }

    @Test
    public void testViewPrivateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 7L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);

        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 6L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 1L);
        fail();
    }

    @Test
    public void testAddResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);        
        Response response = serviceDelegator.createResearcherUrl("4444-4444-4444-4441", getResearcherUrl());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.myRUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My researcher Url", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testUpdateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=5", researcherUrl.getUrl().getValue());
        assertEquals("443_3", researcherUrl.getUrlName());

        researcherUrl.setUrl(new Url("http://theNewResearcherUrl.com"));
        researcherUrl.setUrlName("My Updated Researcher Url");

        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://theNewResearcherUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My Updated Researcher Url", researcherUrl.getUrlName());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());

        researcherUrl.setUrlName("Updated Name");
        serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 8L, researcherUrl);
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateResearcherUrlChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();        
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
        
        researcherUrl.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        fail();
    }  
    
    @Test
    public void testUpdateResearcherUrlLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();        
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
        
        researcherUrl.setVisibility(null);
        
        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        researcherUrl = (ResearcherUrl) response.getEntity();        
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }
    
    @Test
    public void testDeleteResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertFalse(researcherUrls.getResearcherUrls().isEmpty());
        ResearcherUrl toDelete = null;

        for (ResearcherUrl rurl : researcherUrls.getResearcherUrls()) {
            if (rurl.getSource().retrieveSourcePath().equals("APP-5555555555555555")) {
                toDelete = rurl;
                break;
            }
        }

        assertNotNull(toDelete);

        response = serviceDelegator.deleteResearcherUrl("4444-4444-4444-4445", toDelete.getPutCode());
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(0, researcherUrls.getResearcherUrls().size());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteResearcherUrl("4444-4444-4444-4443", 8L);
        fail();
    }

    /**
     * TEST EMAILS
     */    
    @Test
    public void testViewEmails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewEmails("4444-4444-4444-4443");
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(3, emails.getEmails().size());
        for (Email email : emails.getEmails()) {
            assertThat(email.getEmail(), anyOf(is("teddybass2@semantico.com"), is("teddybass3public@semantico.com"), is("teddybass3private@semantico.com")));
            switch (email.getEmail()) {
            case "teddybass2@semantico.com":
                assertEquals(Visibility.LIMITED, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                break;
            case "teddybass3public@semantico.com":
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                break;
            case "teddybass3private@semantico.com":
                assertEquals(Visibility.PRIVATE, email.getVisibility());
                assertEquals("APP-5555555555555555", email.retrieveSourcePath());
                break;
            }
        }
    }

    /**
     * TEST OTHER NAMES
     */
    @Test
    public void testViewOtherNames() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherNames("4444-4444-4444-4446");
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(3, otherNames.getOtherNames().size());
        for (OtherName otherName : otherNames.getOtherNames()) {
            assertThat(otherName.getPutCode(), anyOf(is(5L), is(6L), is(8L)));
            assertThat(otherName.getContent(), anyOf(is("Other Name # 1"), is("Other Name # 2"), is("Other Name # 4")));
            if (otherName.getPutCode() == 5L) {
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            } else if (otherName.getPutCode() == 6L) {
                assertEquals(Visibility.LIMITED, otherName.getVisibility());
                assertEquals("4444-4444-4444-4446", otherName.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 1", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewLimitedOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 2", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals("4444-4444-4444-4446", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPrivateOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 8L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 4", otherName.getContent());
        assertEquals(Visibility.PRIVATE, otherName.getVisibility());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateOtherNameWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4446", 7L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewOtherNameThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testAddOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.createOtherName("4444-4444-4444-4441", getOtherName());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewOtherName("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherName newOtherName = (OtherName) response.getEntity();
        assertNotNull(newOtherName);
        assertEquals("New Other Name", newOtherName.getContent());
        assertEquals(Visibility.PUBLIC, newOtherName.getVisibility());
        assertNotNull(newOtherName.getSource());
        assertEquals("APP-5555555555555555", newOtherName.getSource().retrieveSourcePath());
        assertNotNull(newOtherName.getCreatedDate());
        assertNotNull(newOtherName.getLastModifiedDate());
    }

    @Test
    public void testUpdateOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Slibberdy Slabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name");
        
        response = serviceDelegator.updateOtherName("4444-4444-4444-4443", 1L, otherName);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName updatedOtherName = (OtherName) response.getEntity();
        assertNotNull(updatedOtherName);
        assertEquals("Updated Other Name", updatedOtherName.getContent());
        assertEquals(Visibility.PUBLIC, updatedOtherName.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateOtherNameYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Flibberdy Flabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name " + System.currentTimeMillis());        

        serviceDelegator.updateOtherName("4444-4444-4444-4443", 2L, otherName);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateOtherNameChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateOtherName("4444-4444-4444-4443", 1L, otherName);
        fail();
    }

    @Test
    public void testUpdateOtherNameLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setVisibility(null);
        
        response = serviceDelegator.updateOtherName("4444-4444-4444-4443", 1L, otherName);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
    }
    
    @Test
    public void testDeleteOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherNames("4444-4444-4444-4447");
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(1, otherNames.getOtherNames().size());
        response = serviceDelegator.deleteOtherName("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewOtherNames("4444-4444-4444-4447");
        assertNotNull(response);
        otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertTrue(otherNames.getOtherNames().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteOtherNameYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteOtherName("4444-4444-4444-4446", 6L);
        fail();
    }

    /**
     * TEST EXTERNAL IDENTIFIERS
     */    
    @Test
    public void testViewExternalIdentifiers() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4442");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        List<PersonExternalIdentifier> extIdsList = extIds.getExternalIdentifiers();
        assertNotNull(extIdsList);
        assertEquals(3, extIdsList.size());

        for (PersonExternalIdentifier extId : extIdsList) {
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertThat(extId.getValue(), anyOf(is("abc123"), is("abc456"), is("abc012")));
            assertNotNull(extId.getUrl());
            assertThat(extId.getUrl().getValue(),
                    anyOf(is("http://www.facebook.com/abc123"), is("http://www.facebook.com/abc456"), is("http://www.facebook.com/abc012")));
            assertEquals("Facebook", extId.getType());
            assertNotNull(extId.getSource());
            if (extId.getPutCode().equals(2L)) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            } else if (extId.getPutCode().equals(3L)) {
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(2), extId.getPutCode());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test
    public void testViewLimitedExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(3), extId.getPutCode());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        assertEquals(Visibility.LIMITED, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test
    public void testViewPrivateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 5L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(5), extId.getPutCode());
        assertEquals("abc012", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
        assertEquals(Visibility.PRIVATE, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateExternalIdentifierWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 4L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 1L);
        fail();
    }

    @Test
    public void testAddExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(1), extIds.getExternalIdentifiers().get(0).getPutCode());
        assertNotNull(extIds.getExternalIdentifiers().get(0).getUrl());
        assertEquals("http://www.facebook.com/d3clan", extIds.getExternalIdentifiers().get(0).getUrl().getValue());
        assertEquals("d3clan", extIds.getExternalIdentifiers().get(0).getValue());
        assertEquals(Visibility.PUBLIC, extIds.getExternalIdentifiers().get(0).getVisibility());
       
        response = serviceDelegator.createExternalIdentifier("4444-4444-4444-4443", getPersonExternalIdentifier());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(2, extIds.getExternalIdentifiers().size());

        for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            assertNotNull(extId.getUrl());
            if (extId.getPutCode() != 1L) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("new-common-name", extId.getType());
                assertEquals("new-reference", extId.getValue());
                assertEquals("http://newUrl.com", extId.getUrl().getValue());
                assertEquals(putCode, extId.getPutCode());
            } else {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("Facebook", extId.getType());
                assertEquals("d3clan", extId.getValue());
                assertEquals("http://www.facebook.com/d3clan", extId.getUrl().getValue());
            }
        }
    }

    @Test
    public void testUpdateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        extId.setType("updated-common-name");
        extId.setValue("updated-reference");
        extId.setUrl(new Url("http://updatedUrl.com"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier updatedExtId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(updatedExtId);
        assertEquals("updated-common-name", updatedExtId.getType());
        assertEquals("updated-reference", updatedExtId.getValue());
        assertNotNull(updatedExtId.getUrl());
        assertEquals("http://updatedUrl.com", updatedExtId.getUrl().getValue());

        // Revert changes so other tests still works
        extId.setType("Facebook");
        extId.setValue("abc123");
        extId.setUrl(new Url("http://www.facebook.com/abc123"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateExaternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        extId.setType("other-common-name");
        extId.setValue("other-reference");
        extId.setUrl(new Url("http://otherUrl.com"));
        serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 3L, extId);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateExternalIdentifierChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        
        extId.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        fail();
    }
    
    @Test
    public void testUpdateExternalIdentifierLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        
        extId.setVisibility(null);
        
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
    }
    
    @Test
    public void testDeleteExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(6), extIds.getExternalIdentifiers().get(0).getPutCode());

        response = serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4444", 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertTrue(extIds.getExternalIdentifiers().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteExternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());

        serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4442", 3L);
        fail();
    }

    /**
     * TEST KEYWORDS
     */
    @Test
    public void testViewKeywords() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4443");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(3, keywords.getKeywords().size());

        for (Keyword keyword : keywords.getKeywords()) {
            assertThat(keyword.getPutCode(), anyOf(is(1L), is(2L), is(4L)));
            assertThat(keyword.getContent(), anyOf(is("tea making"), is("coffee making"), is("what else can we make?")));
            if (keyword.getPutCode() == 1L) {
                assertEquals(Visibility.PUBLIC, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            } else if (keyword.getPutCode() == 2L) {
                assertEquals(Visibility.LIMITED, keyword.getVisibility());
                assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("tea making", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewLimitedKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPrivateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 4L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("what else can we make?", keyword.getContent());
        assertEquals(Visibility.PRIVATE, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateKeywordWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewKeyword("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test
    public void testAddKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.createKeyword("4444-4444-4444-4441", getKeyword());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keyword newKeyword = (Keyword) response.getEntity();
        assertNotNull(newKeyword);
        assertEquals("New keyword", newKeyword.getContent());
        assertEquals(Visibility.PUBLIC, newKeyword.getVisibility());
        assertNotNull(newKeyword.getSource());
        assertEquals("APP-5555555555555555", newKeyword.getSource().retrieveSourcePath());
        assertNotNull(newKeyword.getCreatedDate());
        assertNotNull(newKeyword.getLastModifiedDate());
    }

    @Test
    public void testUpdateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("keyword-2", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setContent("Updated keyword");

        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("Updated keyword", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertNotNull(keyword.getSource());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());

        keyword.setContent("Updated Keyword " + System.currentTimeMillis());        

        serviceDelegator.updateKeyword("4444-4444-4444-4443", 2L, keyword);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateKeywordChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        fail();
    }
    
    @Test
    public void testUpdateKeywordLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(null);
        
        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
    }
    
    @Test
    public void testDeleteKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(1, keywords.getKeywords().size());
        response = serviceDelegator.deleteKeyword("4444-4444-4444-4499", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertTrue(keywords.getKeywords().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteKeyword("4444-4444-4444-4443", 3L);
        fail();
    }

    /**
     * TEST ADDRESSES
     */
    @Test
    public void testViewAddresses() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4447");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(3, addresses.getAddress().size());

        for (Address address : addresses.getAddress()) {
            assertThat(address.getPutCode(), anyOf(is(2L), is(3L), is(4L)));
            assertThat(address.getCountry().getValue(), anyOf(is(Iso3166Country.CR), is(Iso3166Country.US)));
            if (address.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, address.getVisibility());
                assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 4L) {
                assertEquals(Visibility.PRIVATE, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
    }

    @Test
    public void testViewLimitedAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 3L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.LIMITED, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test
    public void testViewPrivateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 4L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PRIVATE, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 1L);
        fail();
    }

    @Test
    public void testAddAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);       
        Response response = serviceDelegator.createAddress("4444-4444-4444-4442", getAddress());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        assertEquals(Iso3166Country.ES, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals("APP-5555555555555555", newAddress.getSource().retrieveSourcePath());
        assertNotNull(newAddress.getCreatedDate());
        assertNotNull(newAddress.getLastModifiedDate());

        // Remove it
        response = serviceDelegator.deleteAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.getCountry().setValue(Iso3166Country.PA);

        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.PA, address.getCountry().getValue());

        // Set it back to US again
        address.getCountry().setValue(Iso3166Country.US);
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertNotNull(address.getSource());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());

        address.getCountry().setValue(Iso3166Country.BR);

        serviceDelegator.updateAddress("4444-4444-4444-4447", 2L, address);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        fail();
    }    
    
    @Test
    public void testUpdateAddressLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(null);
        
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }
    
    @Test
    public void testDeleteAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        response = serviceDelegator.deleteAddress("4444-4444-4444-4499", 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertTrue(addresses.getAddress().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteAddress("4444-4444-4444-4447", 5L);
        fail();
    }
    
    @Test
    public void testViewPerson() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPerson("4444-4444-4444-4442");
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        assertNotNull(person);
        assertNotNull(person.getName());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());

        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCreatedDate());
        assertNotNull(person.getAddresses().getAddress().get(0).getLastModifiedDate());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());       
        assertEquals(Long.valueOf(1), person.getAddresses().getAddress().get(0).getPutCode());
        assertNotNull(person.getAddresses().getAddress().get(0).getSource());
        assertEquals("APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals("Biography for 4444-4444-4444-4442",
                person.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        assertEquals("michael@bentine.com", person.getEmails().getEmails().get(0).getEmail());
        assertEquals(Visibility.LIMITED, person.getEmails().getEmails().get(0).getVisibility());
        assertNotNull(person.getEmails().getEmails().get(0).getSource());
        assertEquals("4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retriveSourceUri());
        assertNull(person.getEmails().getEmails().get(0).getPutCode());
        assertNotNull(person.getEmails().getEmails().get(0).getLastModifiedDate());
        assertNotNull(person.getEmails().getEmails().get(0).getCreatedDate());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(3, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean found2 = false, found3 = false, found5 = false;

        List<PersonExternalIdentifier> extIds = person.getExternalIdentifiers().getExternalIdentifiers();
        for (PersonExternalIdentifier extId : extIds) {
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertNotNull(extId.getCreatedDate());
            assertNotNull(extId.getLastModifiedDate());
            assertNotNull(extId.getSource());
            if (extId.getPutCode() == 2L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc123", extId.getValue());
                assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found2 = true;
            } else if (extId.getPutCode() == 3L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc456", extId.getValue());
                assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", extId.getSource().retriveSourceUri());
                found3 = true;
            } else {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc012", extId.getValue());
                assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found5 = true;
            }
        }

        assertTrue(found2 && found3 && found5);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertEquals("My keyword", person.getKeywords().getKeywords().get(0).getContent());
        assertEquals(Long.valueOf(7), person.getKeywords().getKeywords().get(0).getPutCode());
        assertEquals("APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertNotNull(person.getKeywords().getKeywords().get(0).getCreatedDate());
        assertNotNull(person.getKeywords().getKeywords().get(0).getLastModifiedDate());

        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());

        boolean found9 = false, found10 = false;

        for (OtherName otherName : person.getOtherNames().getOtherNames()) {
            assertThat(otherName.getPutCode(), anyOf(is(10L), is(11L)));
            assertNotNull(otherName.getSource());
            assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", otherName.getSource().retriveSourceUri());
            if (otherName.getPutCode() == 10L) {
                assertEquals("Other Name # 1", otherName.getContent());
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                found9 = true;
            } else {
                assertEquals("Other Name # 2", otherName.getContent());
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                found10 = true;
            }
        }

        assertTrue(found9 && found10);

        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(3, person.getResearcherUrls().getResearcherUrls().size());

        found9 = false;
        found10 = false;
        boolean found12 = false;

        for (ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            assertNotNull(rUrl.getCreatedDate());
            assertNotNull(rUrl.getLastModifiedDate());
            assertNotNull(rUrl.getSource());
            assertThat(rUrl.getPutCode(), anyOf(is(9L), is(10L), is(12L)));
            assertNotNull(rUrl.getUrl());
            if (rUrl.getPutCode().equals(9L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=9", rUrl.getUrl().getValue());
                assertEquals("1", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
                found9 = true;
            } else if (rUrl.getPutCode().equals(10L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=10", rUrl.getUrl().getValue());
                assertEquals("2", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED, rUrl.getVisibility());
                found10 = true;
            } else {
                assertEquals(Long.valueOf(12), rUrl.getPutCode());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=12", rUrl.getUrl().getValue());
                assertEquals("4", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE, rUrl.getVisibility());
                found12 = true;
            }
        }

        assertTrue(found9 && found10 && found12);
    }

    @Test
    public void testReadPublicScope_Activities() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        
        //Check that lists returns only PUBLIC elements
        /**
         * ACTIVITIES
         * */        
        try {
            //Check you get only public activities
            Response r = serviceDelegator.viewActivities(ORCID);
            ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
            testActivities(as, ORCID);
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Record() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        testPerson(record.getPerson(), ORCID);
        testActivities(record.getActivitiesSummary(), ORCID);
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());        
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());        
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        assertNotNull(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());                
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());   
    }    
    
    private void testActivities(ActivitiesSummary as, String orcid) {
        //This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);
        assertNotNull(as);
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), as.getEducations().getSummaries().get(0).getPutCode());
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), as.getEmployments().getSummaries().get(0).getPutCode());
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
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
        
        //Limited fails
        try {
            serviceDelegator.viewEducation(ORCID, 21L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEducationSummary(ORCID, 21L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fails
        try {
            serviceDelegator.viewEducation(ORCID, 22L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEducationSummary(ORCID, 22L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
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
        
        //Limited fails
        try {
            serviceDelegator.viewEmployment(ORCID, 18L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 18L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fails
        try {
            serviceDelegator.viewEmployment(ORCID, 19L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 19L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Funding() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        assertEquals(Funding.class.getName(), r.getEntity().getClass().getName());
        
        r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        assertNotNull(r);
        assertEquals(FundingSummary.class.getName(), r.getEntity().getClass().getName());        
                
        //Limited fail
        try {
            serviceDelegator.viewFunding(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewFundingSummary(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewFunding(ORCID, 12L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
        
    @Test
    public void testViewPersonalDetails() {       
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertNotNull(personalDetails.getBiography().getLastModifiedDate());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertNotNull(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        assertNotNull(personalDetails.getName().getLastModifiedDate());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getLastModifiedDate());
        assertEquals(4, personalDetails.getOtherNames().getOtherNames().size());
        
        for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            if(otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if(otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if(otherName.getPutCode().equals(Long.valueOf(15))) { 
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if(otherName.getPutCode().equals(Long.valueOf(16))) { 
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid put code found: " + otherName.getPutCode());
            }
        }
        
        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());        
    }        
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewRecord(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrls(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmailsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmails(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNamesWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherNames(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifiersWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifiers(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeywords(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressesWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddresses(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBiographyWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonalDetailsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPersonalDetails(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPerson(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 20L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducations(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationSummaryWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducationSummary(ORCID, 20L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 17L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentsWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployments(ORCID);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentSummaryWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmploymentSummary(ORCID, 17L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifierWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 13L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 10L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingSummaryWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFundingSummary(ORCID, 10L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 9L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNameWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherName(ORCID, 13L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewSummaryWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 13L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 11L);
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkSummaryWrongToken() {        
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWorkSummary(ORCID, 11L);
    }
    
    @Test
    public void testViewRecordWrongScope() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        //Verify everything inside is public
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertIsPublic(record.getActivitiesSummary());
        assertIsPublic(record.getPerson());
    }
    
    @Test
    public void testViewRecordReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        assertIsPublic(record.getActivitiesSummary());
        assertIsPublic(record.getPerson());
    }
    
    @Test
    public void testViewActivitiesReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertNotNull(as);
        assertIsPublic(as);
    }
    
    @Test
    public void testViewResearcherUrlsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        ResearcherUrls element = (ResearcherUrls) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEmailsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmails(ORCID);
        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
        
    }
    
    @Test
    public void testViewOtherNamesReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewOtherNames(ORCID);
        OtherNames element = (OtherNames) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewExternalIdentifiersReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        PersonExternalIdentifiers element = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewKeywordsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewKeywords(ORCID);
        Keywords element = (Keywords) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewAddressesReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewAddresses(ORCID);
        Addresses element = (Addresses) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewBiographyReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewBiography(ORCID);
        Biography element = (Biography) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewPersonalDetailsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        PersonalDetails element = (PersonalDetails) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewPersonReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        Person element = (Person) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewAddressReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        Address element = (Address) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEducationReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        Education element = (Education) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEducationsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducations(ORCID);
        Educations element = (Educations) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEducationSummaryReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEducationSummary(ORCID, 20L);
        EducationSummary element = (EducationSummary) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEmploymentReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEmploymentsReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmployments(ORCID);
        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewEmploymentSummaryReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewExternalIdentifierReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        PersonExternalIdentifier element = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewFundingReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        Funding element = (Funding) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewFundingSummaryReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        FundingSummary element = (FundingSummary) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewKeywordReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        Keyword element = (Keyword) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewOtherNameReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewOtherName(ORCID, 13L);
        OtherName element = (OtherName) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewPeerReviewReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        PeerReview element = (PeerReview) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewPeerReviewSummaryReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
        PeerReviewSummary element = (PeerReviewSummary) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewResearcherUrlReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        ResearcherUrl element = (ResearcherUrl) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewWorkReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        Work element = (Work) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    @Test
    public void testViewWorkSummaryReadPublic() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("some-client", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        WorkSummary element = (WorkSummary) r.getEntity();
        assertNotNull(element);
        assertIsPublic(element);
    }
    
    private void assertIsPublic(VisibilityType v) {
    	assertEquals(Visibility.PUBLIC, v.getVisibility());
    }
    
    private void assertIsPublic(ActivitiesContainer c) {
    	Collection<? extends Activity> activities = c.retrieveActivities();
    	for(Activity a : activities) {
    		assertIsPublic(a);
    	}
    }
    
    private void assertIsPublic(Addresses elements) {
    	if(elements == null || elements.getAddress() == null) {
    		return;    		
    	}
    	
    	for(Address e : elements.getAddress()) {
    		assertIsPublic(e);
    	}
    }
    
	private void assertIsPublic(Keywords elements) {
		if(elements == null || elements.getKeywords() == null) {
    		return;    		
    	}
    	
    	for(Keyword e : elements.getKeywords()) {
    		assertIsPublic(e);
    	}
	}
	
	private void assertIsPublic(ResearcherUrls elements) {
		if(elements == null || elements.getResearcherUrls() == null) {
    		return;    		
    	}
    	
    	for(ResearcherUrl e : elements.getResearcherUrls()) {
    		assertIsPublic(e);
    	}
	}
	
	private void assertIsPublic(PersonExternalIdentifiers elements) {
		if(elements == null || elements.getExternalIdentifiers() == null) {
    		return;    		
    	}
    	
    	for(PersonExternalIdentifier e : elements.getExternalIdentifiers()) {
    		assertIsPublic(e);
    	}
	}
	
	private void assertIsPublic(Emails elements) {
		if (elements == null || elements.getEmails() == null) {
			return;
		}

		for (Email e : elements.getEmails()) {
			assertIsPublic(e);
		}
	}

	private void assertIsPublic(OtherNames elements) {
		if (elements == null || elements.getOtherNames() == null) {
			return;
		}

		for (OtherName e : elements.getOtherNames()) {
			assertIsPublic(e);
		}
	}

	private void assertIsPublic(PersonalDetails p) {
		if (p == null) {
			return;
		}

		assertIsPublic(p.getBiography());
		assertIsPublic(p.getOtherNames());
		assertIsPublic(p.getName());
	}

	private void assertIsPublic(Person p) {
		if (p == null) {
			return;
		}
		assertIsPublic(p.getAddresses());
		assertIsPublic(p.getBiography());
		assertIsPublic(p.getEmails());
		assertIsPublic(p.getExternalIdentifiers());
		assertIsPublic(p.getKeywords());
		assertIsPublic(p.getName());
		assertIsPublic(p.getOtherNames());
		assertIsPublic(p.getResearcherUrls());
	}
    @Test
    public void testViewRecord() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());        
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        assertNotNull(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());                
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
        //Validate person
        Person person = record.getPerson();
        assertNotNull(person);
        assertNotNull(person.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertEquals(4, person.getAddresses().getAddress().size());
        for(Address address : person.getAddresses().getAddress()) {
            assertNotNull(address.getLastModifiedDate());
            if(address.getPutCode().equals(Long.valueOf(9))) {                
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(Long.valueOf(0), address.getDisplayIndex());                
                assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
            } else if(address.getPutCode().equals(Long.valueOf(10))) {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(Long.valueOf(1), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/10", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else if(address.getPutCode().equals(Long.valueOf(11))) {
                assertEquals(Iso3166Country.GB, address.getCountry().getValue());
                assertEquals(Long.valueOf(2), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/11", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), address.getVisibility().value());
            } else if(address.getPutCode().equals(Long.valueOf(12))) {
                assertEquals(Iso3166Country.MX, address.getCountry().getValue());
                assertEquals(Long.valueOf(3), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/12", address.getPath());
                assertEquals("0000-0000-0000-0003", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else {
                fail("Invalid address found with put code: " + address.getPutCode());
            }
        }
        
        assertNotNull(person.getBiography());
        assertNotNull(person.getBiography().getLastModifiedDate());
        assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());
        
        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals(4, person.getEmails().getEmails().size());
        for(Email email : person.getEmails().getEmails()) {
            assertNotNull(email.getLastModifiedDate());
            if(email.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {                
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
            } else if(email.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else if(email.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), email.getVisibility().value());
            } else if(email.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("0000-0000-0000-0003", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else {
                fail("Invalid email found: " + email.getEmail());
            }
        }
        
        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals(4, person.getExternalIdentifiers().getExternalIdentifiers().size());
        for(PersonExternalIdentifier extId : person.getExternalIdentifiers().getExternalIdentifiers()) {
            assertNotNull(extId.getLastModifiedDate());
            if(extId.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("public_type", extId.getType());
                assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
                assertEquals("public_ref", extId.getValue());
                assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
            } else if(extId.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/14", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("limited_type", extId.getType());
                assertEquals("http://ext-id/limited_ref", extId.getUrl().getValue());
                assertEquals("limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else if(extId.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/15", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("private_type", extId.getType());
                assertEquals("http://ext-id/private_ref", extId.getUrl().getValue());
                assertEquals("private_ref", extId.getValue());
                assertEquals(Visibility.PRIVATE.value(), extId.getVisibility().value());
            } else if(extId.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/16", extId.getPath());
                assertEquals("0000-0000-0000-0003", extId.getSource().retrieveSourcePath());
                assertEquals("self_limited_type", extId.getType());
                assertEquals("http://ext-id/self/limited", extId.getUrl().getValue());
                assertEquals("self_limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else {
                fail("Invalid external identifier found: " + extId.getPutCode());
            }
        }
                
        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        assertEquals(4, person.getKeywords().getKeywords().size());
        for(Keyword keyword : person.getKeywords().getKeywords()) {
            assertNotNull(keyword.getLastModifiedDate());
            if(keyword.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("PUBLIC", keyword.getContent());
                assertEquals(Long.valueOf(0), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
            } else if(keyword.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(1), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/10", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else if(keyword.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("PRIVATE", keyword.getContent());
                assertEquals(Long.valueOf(2), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/11", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), keyword.getVisibility().value());
            } else if(keyword.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("SELF LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(3), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/12", keyword.getPath());
                assertEquals("0000-0000-0000-0003", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else {
                fail("Invalid keyword found: " + keyword.getPutCode());
            }
        }
        
        assertNotNull(person.getName());
        assertNotNull(person.getName().getLastModifiedDate());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());        
        assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());
        
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertEquals(4, person.getOtherNames().getOtherNames().size());
        for(OtherName otherName : person.getOtherNames().getOtherNames()) {
            assertNotNull(otherName.getLastModifiedDate());
            if(otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());                   
            } else if(otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());                
            } else if(otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());                
            } else if(otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());                
            } else {
                fail("Invalid other name found: " + otherName.getPutCode());
            }
        }
        
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        assertEquals(4, person.getResearcherUrls().getResearcherUrls().size());
        for(ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            assertNotNull(rUrl.getLastModifiedDate());
            if(rUrl.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
                assertEquals("public_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
            } else if(rUrl.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/14", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=14", rUrl.getUrl().getValue());
                assertEquals("limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else if(rUrl.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/15", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=15", rUrl.getUrl().getValue());
                assertEquals("private_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE.value(), rUrl.getVisibility().value());
            } else if(rUrl.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/16", rUrl.getPath());
                assertEquals("0000-0000-0000-0003", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=16", rUrl.getUrl().getValue());
                assertEquals("self_limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else {
                fail("Invalid researcher url found: " + rUrl.getPutCode());
            }
        }
        
        //Validate activities
        ActivitiesSummary activities = record.getActivitiesSummary();
        assertNotNull(activities);
        assertNotNull(activities.getLastModifiedDate());        
        
        assertNotNull(activities.getEducations());
        assertNotNull(activities.getEducations().getLastModifiedDate());
        assertEquals(4, activities.getEducations().getSummaries().size());
        for(EducationSummary education : activities.getEducations().getSummaries()) {
            assertNotNull(education.getLastModifiedDate());
            assertNotNull(education.getStartDate());
            assertEquals("2016", education.getStartDate().getYear().getValue());
            assertEquals("04", education.getStartDate().getMonth().getValue());
            assertEquals("01", education.getStartDate().getDay().getValue());                        
            assertNotNull(education.getEndDate());
            assertEquals("2030", education.getEndDate().getYear().getValue());
            assertEquals("01", education.getEndDate().getMonth().getValue());
            assertEquals("01", education.getEndDate().getDay().getValue());                        
            if(education.getPutCode().equals(Long.valueOf(20))) {
                assertEquals("PUBLIC Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
                assertEquals("PUBLIC", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());                
                assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
            } else if(education.getPutCode().equals(Long.valueOf(21))) {
                assertEquals("LIMITED Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/21", education.getPath());
                assertEquals("LIMITED", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());                
                assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
            } else if(education.getPutCode().equals(Long.valueOf(22))) {
                assertEquals("PRIVATE Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/22", education.getPath());
                assertEquals("PRIVATE", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());                
                assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
            } else if(education.getPutCode().equals(Long.valueOf(25))) {
                assertEquals("SELF LIMITED Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/25", education.getPath());
                assertEquals("SELF LIMITED", education.getRoleTitle());
                assertEquals("0000-0000-0000-0003", education.getSource().retrieveSourcePath());                
                assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
            } else {
                fail("Invalid education found: " + education.getPutCode());
            }
        }
        
        assertNotNull(activities.getEmployments());
        assertNotNull(activities.getEmployments().getLastModifiedDate());
        assertEquals(4, activities.getEmployments().getSummaries().size());
        for(EmploymentSummary employment : activities.getEmployments().getSummaries()) {
            assertNotNull(employment.getLastModifiedDate());
            if(employment.getPutCode().equals(Long.valueOf(17))) {
                assertEquals("PUBLIC Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
                assertEquals("PUBLIC", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());
            } else if(employment.getPutCode().equals(Long.valueOf(18))) {
                assertEquals("LIMITED Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/18", employment.getPath());
                assertEquals("LIMITED", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());                
            } else if(employment.getPutCode().equals(Long.valueOf(19))) {
                assertEquals("PRIVATE Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/19", employment.getPath());
                assertEquals("PRIVATE", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
            } else if(employment.getPutCode().equals(Long.valueOf(23))) {
                assertEquals("SELF LIMITED Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/23", employment.getPath());
                assertEquals("SELF LIMITED", employment.getRoleTitle());
                assertEquals("0000-0000-0000-0003", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
            } else {
                fail("Invalid employment found: " + employment.getPutCode());
            }
        }
                
        assertNotNull(activities.getFundings());
        assertNotNull(activities.getFundings().getLastModifiedDate());
        assertEquals(4, activities.getFundings().getFundingGroup().size());
        for(FundingGroup group : activities.getFundings().getFundingGroup()) {
            assertNotNull(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getFundingSummary());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            assertNotNull(funding.getLastModifiedDate());
            if(funding.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("0", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("1", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());                
                assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());   
            } else if(funding.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("1", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("2", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());                
                assertEquals("/0000-0000-0000-0003/funding/11", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else if(funding.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("2", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("3", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());                
                assertEquals("/0000-0000-0000-0003/funding/12", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
            } else if(funding.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("3", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("4", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());                
                assertEquals("/0000-0000-0000-0003/funding/13", funding.getPath());
                assertEquals("0000-0000-0000-0003", funding.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else {
                fail("Invalid funding found: " + funding.getPutCode());
            }           
        }
        
        assertNotNull(activities.getPeerReviews());
        assertNotNull(activities.getPeerReviews().getLastModifiedDate());
        assertEquals(4, activities.getPeerReviews().getPeerReviewGroup().size());
        for(PeerReviewGroup group : activities.getPeerReviews().getPeerReviewGroup()) {
            assertNotNull(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewSummary());            
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            assertNotNull(peerReview.getLastModifiedDate());
            assertNotNull(peerReview.getCompletionDate());
            assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
            assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
            assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
            assertNotNull(peerReview.getExternalIdentifiers());
            assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
            assertEquals("agr", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
            if(peerReview.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("0", peerReview.getDisplayIndex());              
                assertEquals("issn:0000009", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
                assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
            } else if(peerReview.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("1", peerReview.getDisplayIndex());
                assertEquals("issn:0000010", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/10", peerReview.getPath());
                assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else if(peerReview.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("2", peerReview.getDisplayIndex());
                assertEquals("issn:0000011", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/11", peerReview.getPath());
                assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), peerReview.getVisibility().value());
            } else if(peerReview.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("3", peerReview.getDisplayIndex());
                assertEquals("issn:0000012", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/12", peerReview.getPath());
                assertEquals("work:external-identifier-id#4", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("0000-0000-0000-0003", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else {
                fail("Invalid peer review found: " + peerReview.getPutCode());
            }            
        }
        
        assertNotNull(activities.getWorks());
        assertNotNull(activities.getWorks().getLastModifiedDate());
        assertEquals(4, activities.getWorks().getWorkGroup().size());
        for(WorkGroup group : activities.getWorks().getWorkGroup()) {
            assertNotNull(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            assertNotNull(work.getLastModifiedDate());
            if(work.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("0", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
            } else if(work.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("1", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("2", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/12", work.getPath());
            } else if(work.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("2", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("3", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/13", work.getPath());
            } else if(work.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("3", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("4", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("0000-0000-0000-0003", work.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/14", work.getPath());
            } else {
                fail("Invalid work found: " + work.getPutCode());
            }
        }
    }      
    
    @Test
    public void testReadPublicScope_PeerReview() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReview.class.getName(), r.getEntity().getClass().getName());
        
        r = serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReviewSummary.class.getName(), r.getEntity().getClass().getName());        
                
        //Limited fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 10L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 10L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }    
    }
    
    @Test
    public void testReadPublicScope_Works() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(r);
        assertEquals(Work.class.getName(), r.getEntity().getClass().getName());
        
        r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        assertNotNull(r);
        assertEquals(WorkSummary.class.getName(), r.getEntity().getClass().getName());        
                
        //Limited fail
        try {
            serviceDelegator.viewWork(ORCID, 12L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewWorkSummary(ORCID, 12L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewWork(ORCID, 13L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewWork(ORCID, 13L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_OtherNames() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewOtherNames(ORCID);
        assertNotNull(r);
        assertEquals(OtherNames.class.getName(), r.getEntity().getClass().getName());
        OtherNames o = (OtherNames) r.getEntity();
        assertNotNull(o);
        assertNotNull(o.getLastModifiedDate());
        assertEquals(1, o.getOtherNames().size());
        assertEquals(Long.valueOf(13), o.getOtherNames().get(0).getPutCode());
        
        r = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(r);
        assertEquals(OtherName.class.getName(), r.getEntity().getClass().getName());
        
        //Limited fail
        try {
            serviceDelegator.viewOtherName(ORCID, 14L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewOtherName(ORCID, 15L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Keywords() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewKeywords(ORCID);
        assertNotNull(r);
        assertEquals(Keywords.class.getName(), r.getEntity().getClass().getName());
        Keywords k = (Keywords) r.getEntity();
        assertNotNull(k);
        assertNotNull(k.getLastModifiedDate());
        assertEquals(1, k.getKeywords().size());
        assertEquals(Long.valueOf(9), k.getKeywords().get(0).getPutCode());
        
        r = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Keyword.class.getName(), r.getEntity().getClass().getName());
        
        //Limited fail
        try {
            serviceDelegator.viewKeyword(ORCID, 10L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewKeyword(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Address() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewAddresses(ORCID);
        assertNotNull(r);
        assertEquals(Addresses.class.getName(), r.getEntity().getClass().getName());
        Addresses a = (Addresses) r.getEntity();
        assertNotNull(a);
        assertNotNull(a.getLastModifiedDate());
        assertEquals(1, a.getAddress().size());
        assertEquals(Long.valueOf(9), a.getAddress().get(0).getPutCode());
        
        r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Address.class.getName(), r.getEntity().getClass().getName());
        
        //Limited fail
        try {
            serviceDelegator.viewAddress(ORCID, 10L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewAddress(ORCID, 11L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_ExternalIdentifiers() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifiers.class.getName(), r.getEntity().getClass().getName());
        PersonExternalIdentifiers p = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getLastModifiedDate());
        assertEquals(1, p.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(13), p.getExternalIdentifiers().get(0).getPutCode());
        
        r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifier.class.getName(), r.getEntity().getClass().getName());
        
        //Limited fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 14L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 15L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Emails() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmails(ORCID);
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertNotNull(email.getLastModifiedDate());
        assertEquals(1, email.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmails().get(0).getEmail());
    }
    
    @Test
    public void testReadPublicScope_ResearcherUrls() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        //Public works
        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        assertNotNull(r);
        assertEquals(ResearcherUrls.class.getName(), r.getEntity().getClass().getName());
        ResearcherUrls ru = (ResearcherUrls) r.getEntity();
        assertNotNull(ru);
        assertNotNull(ru.getLastModifiedDate());
        assertEquals(1, ru.getResearcherUrls().size());
        assertEquals(Long.valueOf(13), ru.getResearcherUrls().get(0).getPutCode());
        
        r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        assertEquals(ResearcherUrl.class.getName(), r.getEntity().getClass().getName());
        
        //Limited fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 14L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Private fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 15L);
            fail();
        } catch(OrcidUnauthorizedException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testReadPublicScope_Biography() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        assertEquals(Biography.class.getName(), r.getEntity().getClass().getName());
        
        try {
            //Bio for 0000-0000-0000-0002 should be limited
            String otherOrcid = "0000-0000-0000-0002";
            r = serviceDelegator.viewBiography(otherOrcid);
            fail();
        } catch (OrcidUnauthorizedException e) {
            
        } 
    }
        
    @Test
    public void testReadPublicScope_PersonalDetails() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        PersonalDetails p = (PersonalDetails) r.getEntity();
        assertEquals("Biography for 0000-0000-0000-0003", p.getBiography().getContent());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertEquals(Long.valueOf(13), p.getOtherNames().getOtherNames().get(0).getPutCode());
        assertEquals("Other Name PUBLIC", p.getOtherNames().getOtherNames().get(0).getContent());
        
        String otherOrcid = "0000-0000-0000-0002";
        SecurityContextTestUtils.setUpSecurityContext(otherOrcid, ScopePathType.READ_PUBLIC);
        r = serviceDelegator.viewPersonalDetails(otherOrcid);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        p = (PersonalDetails) r.getEntity();
        assertNull(p.getBiography());
        assertNull(p.getName());
        assertNull(p.getOtherNames());
    }
    
    @Test
    public void testReadPublicScope_Person() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        assertNotNull(r);
        assertEquals(Person.class.getName(), r.getEntity().getClass().getName());
        Person p = (Person) r.getEntity();
        testPerson(p, ORCID);
    }
    
    private void testPerson(Person p, String orcid) {
        //This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);
        assertNotNull(p);
        //Address
        assertNotNull(p.getAddresses());
        Addresses a = p.getAddresses();
        assertNotNull(a);
        assertNotNull(a.getLastModifiedDate());
        assertEquals(1, a.getAddress().size());
        assertEquals(Long.valueOf(9), a.getAddress().get(0).getPutCode());
                
        //Biography
        assertNotNull(p.getBiography());
        Biography b = p.getBiography();
        assertNotNull(b);
        assertEquals("Biography for 0000-0000-0000-0003", b.getContent());
        
        //Email
        assertNotNull(p.getEmails());
        Emails email = p.getEmails();
        assertNotNull(email);
        assertNotNull(email.getLastModifiedDate());
        assertEquals(1, email.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmails().get(0).getEmail());
        
        //External identifiers
        assertNotNull(p.getExternalIdentifiers());
        PersonExternalIdentifiers extIds = p.getExternalIdentifiers();
        assertNotNull(extIds);
        assertNotNull(extIds.getLastModifiedDate());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(13), extIds.getExternalIdentifiers().get(0).getPutCode());
                
        //Keywords
        assertNotNull(p.getKeywords());
        Keywords k = p.getKeywords();
        assertNotNull(k);
        assertNotNull(k.getLastModifiedDate());
        assertEquals(1, k.getKeywords().size());
        assertEquals(Long.valueOf(9), k.getKeywords().get(0).getPutCode());
        
        //Name
        assertNotNull(p.getName());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());
        
        //Other names
        assertNotNull(p.getOtherNames());
        OtherNames o = p.getOtherNames();
        assertNotNull(o);
        assertNotNull(o.getLastModifiedDate());
        assertEquals(1, o.getOtherNames().size());
        assertEquals(Long.valueOf(13), o.getOtherNames().get(0).getPutCode());
        
        //Researcher urls
        assertNotNull(p.getResearcherUrls());
        ResearcherUrls ru = p.getResearcherUrls();
        assertNotNull(ru);
        assertNotNull(ru.getLastModifiedDate());
        assertEquals(1, ru.getResearcherUrls().size());
        assertEquals(Long.valueOf(13), ru.getResearcherUrls().get(0).getPutCode());
        
        assertNotNull(p.getPath());
    }
    
    
    @Test    
    public void testAddWorkWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Work work = getWork("work # 1 " + System.currentTimeMillis());
        try {
            work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createWork(orcid, work);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
                
        //Assert that it could be created with a valid value
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        Response response = serviceDelegator.createWork(orcid, work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testAddPeerReviewWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        PeerReview peerReview = getPeerReview();
        
        //Set both to a correct value
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        
        //Check it fail on external identifier type
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        /* This case is now ok (external-id-api branch 05/16) - adapters ensure correct value is stored in DB.
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("DOI");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }*/
        
        //Set the ext id to a correct value to test the subject ext id
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        //Check it fail on subject external identifier type
        try {
            peerReview.getSubjectExternalIdentifier().setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        /*
        try {
            peerReview.getSubjectExternalIdentifier().setType("DOI");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }*/
        
        //Test it works with correct values
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        Response response = serviceDelegator.createPeerReview(orcid, peerReview);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
        
    @Test
    public void testAddFundingWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        
        Funding funding = getFunding();
        
        try {
            funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        /*  This case is now ok (external-id-api branch 05/16) - adapters ensure correct value is stored in DB.
        try {
            funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("GRANT_NUMBER");
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }*/
        
        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("grant_number");
        Response response = serviceDelegator.createFunding(orcid, funding);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testOrcidProfileCreate_CANT_AddOnClaimedAccounts() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();

        // Test can't create
        try {
            serviceDelegator.createAddress(ORCID, getAddress());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createEducation(ORCID, getEducation());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createEmployment(ORCID, getEmployment());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createExternalIdentifier(ORCID, getPersonExternalIdentifier());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createFunding(ORCID, getFunding());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createKeyword(ORCID, getKeyword());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createOtherName(ORCID, getOtherName());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createPeerReview(ORCID, getPeerReview());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createResearcherUrl(ORCID, getResearcherUrl());
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createWork(ORCID, getWork("work # 1 " + System.currentTimeMillis()));
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }        
        try {
            serviceDelegator.createGroupIdRecord(getGroupIdRecord());
        } catch (AccessControlException e) {
            assertEquals("Insufficient or wrong scope [/orcid-profile/create]", e.getMessage());
        } 
    }
    
    @Test 
    public void testOrcidProfileCreate_CANT_ViewOnClaimedAccounts() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        try {
            serviceDelegator.viewActivities(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewAddress(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewAddresses(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewBiography(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducation(ORCID, 20L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducationSummary(ORCID, 20L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducations(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmails(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmployment(ORCID, 17L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmployments(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewExternalIdentifiers(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFunding(ORCID, 10L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFundingSummary(ORCID, 10L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFundings(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewKeyword(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewKeywords(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewOtherName(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewOtherNames(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReview(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReviews(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPerson(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPersonalDetails(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewResearcherUrls(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWork(ORCID, 11L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWorkSummary(ORCID, 11L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWorks(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewRecord(ORCID);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewGroupIdRecord(1L);
        } catch (AccessControlException e) {
            assertEquals("Insufficient or wrong scope [/orcid-profile/create]", e.getMessage());
        }
        try {
            serviceDelegator.viewGroupIdRecords("10", "0");
        } catch (AccessControlException e) {
            assertEquals("Insufficient or wrong scope [/orcid-profile/create]", e.getMessage());
        }
    }
    
    @Test 
    public void testOrcidProfileCreate_CANT_DeleteOnClaimedAccounts() {        
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        try {
            serviceDelegator.deleteAddress(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteAffiliation(ORCID, 20L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteExternalIdentifier(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteFunding(ORCID, 10L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteKeyword(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteOtherName(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deletePeerReview(ORCID, 9L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteResearcherUrl(ORCID, 13L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteWork(ORCID, 11L);
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
    }
    
    @Test 
    public void testOrcidProfileCreate_CANT_UpdateOnClaimedAccounts() {        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(response);
        Address a = (Address) response.getEntity();
        assertNotNull(a);        
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateAddress(ORCID, a.getPutCode(), a);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education edu = (Education) response.getEntity();
        assertNotNull(edu);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateEducation(ORCID, edu.getPutCode(), edu);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
                
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment emp = (Employment) response.getEntity();
        assertNotNull(emp);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateEmployment(ORCID, emp.getPutCode(), emp);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }  
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewExternalIdentifier(ORCID, 13L); 
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateExternalIdentifier(ORCID, extId.getPutCode(), extId);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewFunding(ORCID, 10L); 
        assertNotNull(response);
        Funding f = (Funding) response.getEntity();
        assertNotNull(f);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateFunding(ORCID, f.getPutCode(), f);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }  
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(response);
        Keyword k = (Keyword) response.getEntity();
        assertNotNull(k);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateKeyword(ORCID, k.getPutCode(), k);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(response);
        OtherName o = (OtherName) response.getEntity();
        assertNotNull(o);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateOtherName(ORCID, o.getPutCode(), o);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewPeerReview(ORCID, 9L); 
        assertNotNull(response);
        PeerReview p = (PeerReview) response.getEntity();
        assertNotNull(p);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updatePeerReview(ORCID, p.getPutCode(), p);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(response);
        ResearcherUrl r = (ResearcherUrl) response.getEntity();
        assertNotNull(r);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateResearcherUrl(ORCID, r.getPutCode(), r);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
        
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewWork(ORCID, 11L); 
        assertNotNull(response);
        Work w = (Work) response.getEntity();
        assertNotNull(w);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateWork(ORCID, w.getPutCode(), w);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        } 
    }    
    
    @Test
    public void testOrcidProfileCreate_CAN_CRUDOnUnclaimedAccounts() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        //Test address
        Response response = serviceDelegator.createAddress(orcid, getAddress());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = getPutCode(response);
        response = serviceDelegator.viewAddress(orcid, putCode);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        address.getCountry().setValue(Iso3166Country.ZW);
        response = serviceDelegator.updateAddress(orcid, putCode, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test education
        response = serviceDelegator.createEducation(orcid, getEducation());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewEducation(orcid, putCode);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEducation(orcid, putCode, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test employment
        response = serviceDelegator.createEmployment(orcid, getEmployment());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewEmployment(orcid, putCode);
        assertNotNull(response);        
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEmployment(orcid, putCode, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test external identifiers
        response = serviceDelegator.createExternalIdentifier(orcid, getPersonExternalIdentifier());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewExternalIdentifier(orcid, putCode);
        assertNotNull(response);        
        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(externalIdentifier);
        response = serviceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteExternalIdentifier(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test funding
        response = serviceDelegator.createFunding(orcid, getFunding());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewFunding(orcid, putCode);
        assertNotNull(response);        
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        response = serviceDelegator.updateFunding(orcid, putCode, funding);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test keyword
        response = serviceDelegator.createKeyword(orcid, getKeyword());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewKeyword(orcid, putCode);
        assertNotNull(response);        
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        response = serviceDelegator.updateKeyword(orcid, putCode, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteKeyword(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test other names
        response = serviceDelegator.createOtherName(orcid, getOtherName());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewOtherName(orcid, putCode);
        assertNotNull(response);        
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        response = serviceDelegator.updateOtherName(orcid, putCode, otherName);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteOtherName(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test peer review
        response = serviceDelegator.createPeerReview(orcid, getPeerReview());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewPeerReview(orcid, putCode);
        assertNotNull(response);        
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        response = serviceDelegator.updatePeerReview(orcid, putCode, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test researcher url
        response = serviceDelegator.createResearcherUrl(orcid, getResearcherUrl());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewResearcherUrl(orcid, putCode);
        assertNotNull(response);        
        ResearcherUrl rUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(rUrl);
        response = serviceDelegator.updateResearcherUrl(orcid, putCode, rUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteResearcherUrl(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Test work
        response = serviceDelegator.createWork(orcid, getWork("work # 1 " + System.currentTimeMillis()));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = getPutCode(response);
        response = serviceDelegator.viewWork(orcid, putCode);
        assertNotNull(response);        
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        response = serviceDelegator.updateWork(orcid, putCode, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());        
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }    
        
    private Address getAddress() {
        Address address = new Address();
        address.setVisibility(Visibility.PUBLIC);
        address.setCountry(new Country(Iso3166Country.ES));
        return address;
    }    
    
    private Education getEducation() {
        Education education = new Education();
        education.setDepartmentName("My department name");
        education.setRoleTitle("My Role");
        education.setOrganization(getOrganization());
        return education;
    }
    
    private Employment getEmployment() {
        Employment employment = new Employment();
        employment.setDepartmentName("My department name");
        employment.setRoleTitle("My Role");
        employment.setOrganization(getOrganization());
        return employment;
    }
    
    private Work getWork(String title) {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(WorkExternalIdentifierType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        return work;
    }            
    
    private PeerReview getPeerReview() {
        PeerReview peerReview = new PeerReview();
        ExternalIDs weis = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(Relationship.PART_OF);
        wei1.setUrl(new Url("http://myUrl.com"));
        wei1.setValue("work-external-identifier-id");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis.getExternalIdentifier().add(wei1);
        peerReview.setExternalIdentifiers(weis);
        peerReview.setGroupId("issn:0000003");
        peerReview.setOrganization(getOrganization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work-title"));
        peerReview.setSubjectName(workTitle);
        peerReview.setSubjectType(WorkType.DATA_SET);
        peerReview.setType(PeerReviewType.EVALUATION);
        return peerReview;
    }
    
    private Funding getFunding(){
        Funding newFunding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Public Funding # 2"));
        newFunding.setTitle(title);
        newFunding.setType(FundingType.AWARD);
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        newFunding.setExternalIdentifiers(fExtIds);
        newFunding.setOrganization(getOrganization());
        return newFunding;
    }
    
    private Organization getOrganization() {
        Organization org = new Organization();
        org.setName("Org Name");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("city");
        add.setCountry(Iso3166Country.TT);
        org.setAddress(add);
        return org;
    }
    
    private PersonExternalIdentifier getPersonExternalIdentifier() {
        PersonExternalIdentifier newExtId = new PersonExternalIdentifier();
        newExtId.setType("new-common-name");
        newExtId.setValue("new-reference");
        newExtId.setUrl(new Url("http://newUrl.com"));
        newExtId.setVisibility(Visibility.LIMITED);
        return newExtId;
    }
    
    private Keyword getKeyword() {
        Keyword keyword = new Keyword();
        keyword.setContent("New keyword");
        keyword.setVisibility(Visibility.LIMITED);
        return keyword;
    }
    
    private OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("New Other Name");
        otherName.setVisibility(Visibility.LIMITED);
        return otherName;
    }
    
    private ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://www.myRUrl.com"));
        rUrl.setUrlName("My researcher Url");
        rUrl.setVisibility(Visibility.LIMITED);
        return rUrl;
    }
    
    private GroupIdRecord getGroupIdRecord() {
        GroupIdRecord newRecord = new GroupIdRecord();
        newRecord.setGroupId("issn:0000005");
        newRecord.setName("TestGroup5");
        newRecord.setDescription("TestDescription5");
        newRecord.setType("publisher");
        return newRecord;
    }    
    
    private Long getPutCode(Response response) {
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        return Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
    }
}
