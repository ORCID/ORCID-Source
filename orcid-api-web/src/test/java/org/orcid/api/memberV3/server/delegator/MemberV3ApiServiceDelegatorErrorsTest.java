package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;


@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-api-web-context.xml" })
public class MemberV3ApiServiceDelegatorErrorsTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml",
            "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml",
            "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    @Resource(name = "memberV3ApiServiceDelegator")
    private MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

    @Resource
    private ProfileDao profileDao;

    @Value("${org.orcid.core.works.bulk.read.max:100}")
    private Long bulkReadSize;

    private String nonExistingUser = "0000-0000-0000-000X";
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";
    private String deactivatedUserOrcid = "0000-0000-0000-0007";

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-6666666666666666");
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    /**
     * Security checks
     */

    /**
     * 404 for invalid orcids
     */
    @Test(expected = OrcidNoResultException.class)
    public void test00ViewRecord() {
        serviceDelegator.viewRecord(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewActivities() {
        serviceDelegator.viewActivities(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewWork() {
        serviceDelegator.viewWork(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewWorkSummary() {
        serviceDelegator.viewWorkSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateWork() {
        serviceDelegator.createWork(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateWork() {
        serviceDelegator.updateWork(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteWork() {
        serviceDelegator.deleteWork(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewFunding() {
        serviceDelegator.viewFunding(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewFundingSummary() {
        serviceDelegator.viewFundingSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateFunding() {
        serviceDelegator.createFunding(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateFunding() {
        serviceDelegator.updateFunding(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteFunding() {
        serviceDelegator.deleteFunding(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewEducation() {
        serviceDelegator.viewEducation(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewEducationSummary() {
        serviceDelegator.viewEducationSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateEducation() {
        serviceDelegator.createEducation(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateEducation() {
        serviceDelegator.updateEducation(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewEmployment() {
        serviceDelegator.viewEmployment(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewEmploymentSummary() {
        serviceDelegator.viewEmploymentSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateEmployment() {
        serviceDelegator.createEmployment(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateEmployment() {
        serviceDelegator.updateEmployment(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteAffiliation() {
        serviceDelegator.deleteAffiliation(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewPeerReview() {
        serviceDelegator.viewPeerReview(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewPeerReviewSummary() {
        serviceDelegator.viewPeerReviewSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreatePeerReview() {
        serviceDelegator.createPeerReview(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdatePeerReview() {
        serviceDelegator.updatePeerReview(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeletePeerReview() {
        serviceDelegator.deletePeerReview(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewResearcherUrl() {
        serviceDelegator.viewResearcherUrl(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateResearcherUrl() {
        serviceDelegator.updateResearcherUrl(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateResearcherUrl() {
        serviceDelegator.createResearcherUrl(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteResearcherUrl() {
        serviceDelegator.deleteResearcherUrl(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewResearchResources() {
        serviceDelegator.viewResearchResources(nonExistingUser);
        fail();
    }
    
    @Test(expected = OrcidNoResultException.class)
    public void test00ViewResearchResource() {
        serviceDelegator.viewResearchResource(nonExistingUser, 0L);
        fail();
    }
    
    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateResearchResource() {
        serviceDelegator.updateResearchResource(nonExistingUser, 0L, null);
        fail();
    }
        
    @Test(expected = OrcidNoResultException.class)
    public void test00CreateResearchResource() {
        serviceDelegator.createResearchResource(nonExistingUser, null);
        fail();
    }
    
    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteResearchResource() {
        serviceDelegator.deleteResearchResource(nonExistingUser, 0L);
        fail();
    }
    
    @Test(expected = OrcidNoResultException.class)
    public void test00ViewEmails() {
        serviceDelegator.viewEmails(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewOtherNames() {
        serviceDelegator.viewOtherNames(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewOtherName() {
        serviceDelegator.viewOtherName(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateOtherName() {
        serviceDelegator.createOtherName(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateOtherName() {
        serviceDelegator.updateOtherName(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteOtherName() {
        serviceDelegator.deleteOtherName(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewExternalIdentifier() {
        serviceDelegator.viewExternalIdentifier(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateExternalIdentifier() {
        serviceDelegator.updateExternalIdentifier(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateExternalIdentifier() {
        serviceDelegator.createExternalIdentifier(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteExternalIdentifier() {
        serviceDelegator.deleteExternalIdentifier(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewBiography() {
        serviceDelegator.viewBiography(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewKeywords() {
        serviceDelegator.viewKeywords(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewKeyword() {
        serviceDelegator.viewKeyword(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateKeyword() {
        serviceDelegator.createKeyword(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateKeyword() {
        serviceDelegator.updateKeyword(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteKeyword() {
        serviceDelegator.deleteKeyword(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewAddresses() {
        serviceDelegator.viewAddresses(nonExistingUser);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewAddress() {
        serviceDelegator.viewAddress(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00CreateAddress() {
        serviceDelegator.createAddress(nonExistingUser, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00UpdateAddress() {
        serviceDelegator.updateAddress(nonExistingUser, 0L, null);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00DeleteAddress() {
        serviceDelegator.deleteAddress(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = OrcidNoResultException.class)
    public void test00ViewPerson() {
        serviceDelegator.viewPerson(nonExistingUser);
        fail();
    }

    /**
     * Locked account throws an exception
     */
    @Test(expected = LockedException.class)
    public void test01ViewRecord() {
        serviceDelegator.viewRecord(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewActivities() {
        serviceDelegator.viewActivities(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewWork() {
        serviceDelegator.viewWork(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewWorkSummary() {
        serviceDelegator.viewWorkSummary(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateWork() {
        serviceDelegator.createWork(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateWork() {
        serviceDelegator.updateWork(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteWork() {
        serviceDelegator.deleteWork(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewFunding() {
        serviceDelegator.viewFunding(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewFundingSummary() {
        serviceDelegator.viewFundingSummary(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateFunding() {
        serviceDelegator.createFunding(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateFunding() {
        serviceDelegator.updateFunding(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteFunding() {
        serviceDelegator.deleteFunding(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewEducation() {
        serviceDelegator.viewEducation(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewEducationSummary() {
        serviceDelegator.viewEducationSummary(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateEducation() {
        serviceDelegator.createEducation(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateEducation() {
        serviceDelegator.updateEducation(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewEmployment() {
        serviceDelegator.viewEmployment(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewEmploymentSummary() {
        serviceDelegator.viewEmploymentSummary(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateEmployment() {
        serviceDelegator.createEmployment(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateEmployment() {
        serviceDelegator.updateEmployment(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteAffiliation() {
        serviceDelegator.deleteAffiliation(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewPeerReview() {
        serviceDelegator.viewPeerReview(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewPeerReviewSummary() {
        serviceDelegator.viewPeerReviewSummary(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreatePeerReview() {
        serviceDelegator.createPeerReview(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdatePeerReview() {
        serviceDelegator.updatePeerReview(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeletePeerReview() {
        serviceDelegator.deletePeerReview(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearcherUrl() {
        serviceDelegator.viewResearcherUrl(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateResearcherUrl() {
        serviceDelegator.updateResearcherUrl(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateResearcherUrl() {
        serviceDelegator.createResearcherUrl(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteResearcherUrl() {
        serviceDelegator.deleteResearcherUrl(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearchResources() {
        serviceDelegator.viewResearchResources(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearchResource() {
        serviceDelegator.viewResearchResource(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateResearchResource() {
        serviceDelegator.updateResearchResource(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateResearchResource() {
        serviceDelegator.createResearchResource(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteResearchResource() {
        serviceDelegator.deleteResearchResource(lockedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = LockedException.class)
    public void test01ViewEmails() {
        serviceDelegator.viewEmails(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewOtherNames() {
        serviceDelegator.viewOtherNames(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewOtherName() {
        serviceDelegator.viewOtherName(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateOtherName() {
        serviceDelegator.createOtherName(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateOtherName() {
        serviceDelegator.updateOtherName(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteOtherName() {
        serviceDelegator.deleteOtherName(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewExternalIdentifier() {
        serviceDelegator.viewExternalIdentifier(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateExternalIdentifier() {
        serviceDelegator.updateExternalIdentifier(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateExternalIdentifier() {
        serviceDelegator.createExternalIdentifier(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteExternalIdentifier() {
        serviceDelegator.deleteExternalIdentifier(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewBiography() {
        serviceDelegator.viewBiography(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewKeywords() {
        serviceDelegator.viewKeywords(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewKeyword() {
        serviceDelegator.viewKeyword(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateKeyword() {
        serviceDelegator.createKeyword(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateKeyword() {
        serviceDelegator.updateKeyword(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteKeyword() {
        serviceDelegator.deleteKeyword(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewAddresses() {
        serviceDelegator.viewAddresses(lockedUserOrcid);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewAddress() {
        serviceDelegator.viewAddress(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01CreateAddress() {
        serviceDelegator.createAddress(lockedUserOrcid, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01UpdateAddress() {
        serviceDelegator.updateAddress(lockedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01DeleteAddress() {
        serviceDelegator.deleteAddress(lockedUserOrcid, 0L);
        fail();
    }

    @Test(expected = LockedException.class)
    public void test01ViewPerson() {
        serviceDelegator.viewPerson(lockedUserOrcid);
        fail();
    }

    /**
     * Deprecated account throws an exception
     */
    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewRecord() {
        serviceDelegator.viewRecord(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test0102ViewActivities() {
        serviceDelegator.viewActivities(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewWork() {
        serviceDelegator.viewWork(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewWorkSummary() {
        serviceDelegator.viewWorkSummary(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateWork() {
        serviceDelegator.createWork(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateWork() {
        serviceDelegator.updateWork(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteWork() {
        serviceDelegator.deleteWork(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewFunding() {
        serviceDelegator.viewFunding(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewFundingSummary() {
        serviceDelegator.viewFundingSummary(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateFunding() {
        serviceDelegator.createFunding(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateFunding() {
        serviceDelegator.updateFunding(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteFunding() {
        serviceDelegator.deleteFunding(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEducation() {
        serviceDelegator.viewEducation(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEducationSummary() {
        serviceDelegator.viewEducationSummary(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateEducation() {
        serviceDelegator.createEducation(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateEducation() {
        serviceDelegator.updateEducation(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmployment() {
        serviceDelegator.viewEmployment(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmploymentSummary() {
        serviceDelegator.viewEmploymentSummary(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateEmployment() {
        serviceDelegator.createEmployment(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateEmployment() {
        serviceDelegator.updateEmployment(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteAffiliation() {
        serviceDelegator.deleteAffiliation(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPeerReview() {
        serviceDelegator.viewPeerReview(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPeerReviewSummary() {
        serviceDelegator.viewPeerReviewSummary(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreatePeerReview() {
        serviceDelegator.createPeerReview(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdatePeerReview() {
        serviceDelegator.updatePeerReview(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeletePeerReview() {
        serviceDelegator.deletePeerReview(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearcherUrl() {
        serviceDelegator.viewResearcherUrl(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateResearcherUrl() {
        serviceDelegator.updateResearcherUrl(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateResearcherUrl() {
        serviceDelegator.createResearcherUrl(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteResearcherUrl() {
        serviceDelegator.deleteResearcherUrl(deprecatedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearchResources() {
        serviceDelegator.viewResearchResources(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearchResource() {
        serviceDelegator.viewResearchResource(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateResearchResource() {
        serviceDelegator.updateResearchResource(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateResearchResource() {
        serviceDelegator.createResearchResource(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteResearchResource() {
        serviceDelegator.deleteResearchResource(deprecatedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmails() {
        serviceDelegator.viewEmails(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewOtherNames() {
        serviceDelegator.viewOtherNames(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewOtherName() {
        serviceDelegator.viewOtherName(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateOtherName() {
        serviceDelegator.createOtherName(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateOtherName() {
        serviceDelegator.updateOtherName(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteOtherName() {
        serviceDelegator.deleteOtherName(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewExternalIdentifier() {
        serviceDelegator.viewExternalIdentifier(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateExternalIdentifier() {
        serviceDelegator.updateExternalIdentifier(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateExternalIdentifier() {
        serviceDelegator.createExternalIdentifier(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteExternalIdentifier() {
        serviceDelegator.deleteExternalIdentifier(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewBiography() {
        serviceDelegator.viewBiography(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewKeywords() {
        serviceDelegator.viewKeywords(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewKeyword() {
        serviceDelegator.viewKeyword(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateKeyword() {
        serviceDelegator.createKeyword(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateKeyword() {
        serviceDelegator.updateKeyword(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteKeyword() {
        serviceDelegator.deleteKeyword(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewAddresses() {
        serviceDelegator.viewAddresses(deprecatedUserOrcid);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewAddress() {
        serviceDelegator.viewAddress(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateAddress() {
        serviceDelegator.createAddress(deprecatedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateAddress() {
        serviceDelegator.updateAddress(deprecatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteAddress() {
        serviceDelegator.deleteAddress(deprecatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPerson() {
        serviceDelegator.viewPerson(deprecatedUserOrcid);
        fail();
    }

    /**
     * Unclaimed account throws an exception before the claim period ends
     */
    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewRecord() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewRecord(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test0103ViewActivities() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewActivities(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWork() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewWork(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWorkSummary() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewWorkSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateWork() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createWork(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateWork() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateWork(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteWork() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteWork(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFunding() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewFunding(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFundingSummary() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewFundingSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateFunding() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createFunding(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateFunding() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateFunding(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteFunding() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteFunding(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducation() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewEducation(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducationSummary() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewEducationSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEducation() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createEducation(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEducation() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateEducation(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmployment() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewEmployment(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmploymentSummary() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewEmploymentSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEmployment() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createEmployment(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEmployment() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateEmployment(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAffiliation() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteAffiliation(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReview() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewPeerReview(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReviewSummary() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewPeerReviewSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreatePeerReview() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createPeerReview(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdatePeerReview() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updatePeerReview(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeletePeerReview() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deletePeerReview(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrls() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewResearcherUrls(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrl() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewResearcherUrl(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateResearcherUrl() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateResearcherUrl(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateResearcherUrl() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createResearcherUrl(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteResearcherUrl() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteResearcherUrl(unclaimedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearchResources() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewResearchResources(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearchResource() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewResearchResource(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateResearchResource() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateResearchResource(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateResearchResource() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createResearchResource(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteResearchResource() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteResearchResource(unclaimedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmails() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewEmails(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherNames() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewOtherNames(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherName() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewOtherName(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateOtherName() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createOtherName(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateOtherName() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateOtherName(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteOtherName() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteOtherName(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPersonalDetails() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewPersonalDetails(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifiers() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewExternalIdentifiers(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifier() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewExternalIdentifier(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateExternalIdentifier() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateExternalIdentifier(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateExternalIdentifier() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createExternalIdentifier(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteExternalIdentifier() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteExternalIdentifier(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewBiography() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewBiography(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeywords() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewKeywords(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeyword() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewKeyword(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateKeyword() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createKeyword(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateKeyword() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateKeyword(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteKeyword() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteKeyword(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddresses() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewAddresses(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddress() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewAddress(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateAddress() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.createAddress(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateAddress() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.updateAddress(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAddress() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.deleteAddress(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPerson() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewPerson(unclaimedUserOrcid);
        fail();
    }   

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() {
        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
    }

    @Test
    public void testViewBulkWorks() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0003", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,16");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work); // private work
                                                               // but matching
                                                               // source
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError); // private
                                                                     // work not
                                                                     // matching
                                                                     // source
    }

    @Test
    public void testViewBulkWorksWithBadPutCode() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0003", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,9999");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work); // private work
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError); // bad put
                                                                     // code
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testViewBulkWorksWithTooManyPutCodes() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0003", ScopePathType.READ_LIMITED);
        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= bulkReadSize; i++) {
            tooManyPutCodes.append(",").append(i);
        }
        serviceDelegator.viewBulkWorks("0000-0000-0000-0003", tooManyPutCodes.toString());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBulkWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("something-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13");
    }

    @Test
    public void test3_0() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0003", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord("0000-0000-0000-0003");
        Record record = (Record) response.getEntity();
        assertNotNull(record.getActivitiesSummary());
        ActivitiesSummary activitiesSummary = record.getActivitiesSummary();
        if (activitiesSummary.getEducations() != null) {
            activitiesSummary.getEducations().getEducationGroups().forEach(e -> {
                for (EducationSummary s : e.getActivities()) {
                    assertSourceElement(s);
                }
            });
        }

        if (activitiesSummary.getEmployments() != null) {
            activitiesSummary.getEmployments().getEmploymentGroups().forEach(e -> {
                for (EmploymentSummary s : e.getActivities()) {
                    assertSourceElement(s);
                }
            });
        }

        if (activitiesSummary.getFundings() != null) {
            activitiesSummary.getFundings().getFundingGroup().forEach(g -> {
                g.getFundingSummary().forEach(e -> assertSourceElement(e));
            });
        }

        if (activitiesSummary.getWorks() != null) {
            activitiesSummary.getWorks().getWorkGroup().forEach(g -> {
                g.getWorkSummary().forEach(e -> assertSourceElement(e));
            });
        }

        if (activitiesSummary.getPeerReviews() != null) {
            activitiesSummary.getPeerReviews().getPeerReviewGroup().forEach(g -> {
                for (PeerReviewDuplicateGroup pg : g.getPeerReviewGroup()) {
                    pg.getPeerReviewSummary().forEach(e -> assertSourceElement(e));
                }
            });
        }
        
        if(activitiesSummary.getResearchResources() != null) {
            activitiesSummary.getResearchResources().getResearchResourceGroup().forEach(g -> {
                for(ResearchResourceSummary rs : g.getResearchResourceSummary()) {
                    assertSourceElement(rs);
                }
            }); 
        }

        assertNotNull(record.getPerson());

        Person person = record.getPerson();
        if (person.getAddresses() != null) {
            person.getAddresses().getAddress().forEach(e -> assertSourceElement(e));
        }

        if (person.getExternalIdentifiers() != null) {
            person.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertSourceElement(e));
        }

        if (person.getKeywords() != null) {
            person.getKeywords().getKeywords().forEach(e -> assertSourceElement(e));
        }

        if (person.getOtherNames() != null) {
            person.getOtherNames().getOtherNames().forEach(e -> assertSourceElement(e));
        }

        if (person.getResearcherUrls() != null) {
            person.getResearcherUrls().getResearcherUrls().forEach(e -> assertSourceElement(e));
        }
    }

    /**
     * Deactivated elements tests
     */    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewActivities() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewRecord() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewRecord(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPerson() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPerson(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewAddresses() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddresses(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEducations() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducations(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmails() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmails(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmployments() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployments(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewExternalIdentifiers() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifiers(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewFundings() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFundings(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewKeywords() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeywords(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewOtherNames() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherNames(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPeerReviews() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReviews(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPersonalDetails(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrls(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewResearchResources() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearchResources(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewWorks() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0007", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWorks(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateWork() {
        serviceDelegator.createWork(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateWork() {
        serviceDelegator.updateWork(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteWork() {
        serviceDelegator.deleteWork(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateFunding() {
        serviceDelegator.createFunding(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateFunding() {
        serviceDelegator.updateFunding(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteFunding() {
        serviceDelegator.deleteFunding(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateEducation() {
        serviceDelegator.createEducation(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateEducation() {
        serviceDelegator.updateEducation(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateEmployment() {
        serviceDelegator.createEmployment(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateEmployment() {
        serviceDelegator.updateEmployment(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteAffiliation() {
        serviceDelegator.deleteAffiliation(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreatePeerReview() {
        serviceDelegator.createPeerReview(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdatePeerReview() {
        serviceDelegator.updatePeerReview(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeletePeerReview() {
        serviceDelegator.deletePeerReview(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateResearcherUrl() {
        serviceDelegator.updateResearcherUrl(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateResearcherUrl() {
        serviceDelegator.createResearcherUrl(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteResearcherUrl() {
        serviceDelegator.deleteResearcherUrl(deactivatedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateResearchResource() {
        serviceDelegator.updateResearchResource(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateResearchResource() {
        serviceDelegator.createResearchResource(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteResearchResource() {
        serviceDelegator.deleteResearchResource(deactivatedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateOtherName() {
        serviceDelegator.createOtherName(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateOtherName() {
        serviceDelegator.updateOtherName(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteOtherName() {
        serviceDelegator.deleteOtherName(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateExternalIdentifier() {
        serviceDelegator.updateExternalIdentifier(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateExternalIdentifier() {
        serviceDelegator.createExternalIdentifier(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteExternalIdentifier() {
        serviceDelegator.deleteExternalIdentifier(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateKeyword() {
        serviceDelegator.createKeyword(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateKeyword() {
        serviceDelegator.updateKeyword(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteKeyword() {
        serviceDelegator.deleteKeyword(deactivatedUserOrcid, 0L);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateAddress() {
        serviceDelegator.createAddress(deactivatedUserOrcid, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateAddress() {
        serviceDelegator.updateAddress(deactivatedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteAddress() {
        serviceDelegator.deleteAddress(deactivatedUserOrcid, 0L);
        fail();
    }

    private void assertSourceElement(SourceAware element) {
        if (element.getSource() != null && element.getSource().getSourceOrcid() != null) {
            assertProtocol(element.getSource().getSourceOrcid().getUri());
        }
    }

    private void assertProtocol(String url) {
        assertTrue(url.startsWith("https://"));        
    }

    private void updateProfileSubmissionDate(String orcid, int increment) {
        // Update the submission date so it is long enough
        ProfileEntity profileEntity = profileDao.find(orcid);
        profileEntity.setSubmissionDate(DateUtils.addDays(new Date(), increment));
        profileDao.merge(profileEntity);
        profileDao.flush();
    }
}
