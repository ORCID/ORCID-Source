package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;

/**
 * Every entry point of the member V3 API delegator, driven through the five
 * profile states that must stop it dead.
 *
 * <p>
 * Which states those are -- unknown, locked, deprecated, unclaimed and still
 * inside the claim period, deactivated -- and how each is detected is
 * {@code OrcidSecurityManagerImpl.checkProfile}'s business, and orcid-core's
 * {@code OrcidSecurityManagerTest.checkProfile_*} proves it. What this class
 * proves is the other half, and it is worth two hundred and ninety tests: that
 * the endpoints listed below ask, and that none of them swallows the refusal.
 * The {@code @Before} makes the security manager refuse each of the five
 * records, and every test then asserts the refusal came out of its endpoint
 * unchanged.
 *
 * <p>
 * The list is not the whole delegator, and never was. Twenty-eight of its
 * ninety-four record-scoped endpoints are absent from this matrix -- every
 * distinction, invited-position, membership, qualification and service
 * endpoint, plus {@code createWorks}, {@code viewResearchResourceSummary} and
 * {@code getRecordSummary}. That gap predates the mocked conversion; it is
 * recorded here so nobody reads this class as proof of coverage it does not
 * have.
 *
 * <p>
 * The unclaimed group used to push a profile's submission date back through
 * {@code ProfileDao} inside a transaction; that was the only write to the
 * database in this family, and with the guard mocked it is gone along with the
 * Spring context.
 */
public class MemberV3ApiServiceDelegatorErrorsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private String nonExistingUser = "0000-0000-0000-000X";
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";
    private String deactivatedUserOrcid = "0000-0000-0000-0007";

    @Before
    public void before() {
        doThrow(new OrcidNoResultException("No such record: " + nonExistingUser)).when(orcidSecurityManager).checkProfile(nonExistingUser);
        doThrow(new LockedException("The record is locked", lockedUserOrcid)).when(orcidSecurityManager).checkProfile(lockedUserOrcid);
        doThrow(new OrcidDeprecatedException(new HashMap<String, String>())).when(orcidSecurityManager).checkProfile(deprecatedUserOrcid);
        doThrow(new OrcidNotClaimedException()).when(orcidSecurityManager).checkProfile(unclaimedUserOrcid);
        doThrow(new DeactivatedException("The record is deactivated", deactivatedUserOrcid)).when(orcidSecurityManager).checkProfile(deactivatedUserOrcid);
    }

    /**
     * Every test in this class is a refusal: 291 of the 295 declare an
     * {@code expected} exception, and the four that do not
     * ({@code testViewClient}, {@code testViewBulkWorks},
     * {@code testViewBulkWorksWithBadPutCode}, {@code test3_0}) are reads that
     * go no further than a read-only manager. So no test here may reach a
     * manager that writes, and proving the refusal happened is only half the
     * proof - the other half is that nothing was written on the way out.
     *
     * Catches a guard moved below the manager call: e.g. moving
     * {@code checkProfileStatus} under {@code workManager.checkSourceAndRemoveWork}
     * in {@code MemberV3ApiServiceDelegatorImpl.deleteWork}, which still raises
     * the expected exception - after the row is gone - and leaves every test in
     * this class green without this check.
     */
    @After
    public void nothingWasWritten() {
        verifyNoInteractions(workManager, profileFundingManager, affiliationsManager, peerReviewManager, researcherUrlManager, researchResourceManager,
                otherNameManager, externalIdentifierManager, profileKeywordManager, addressManager, groupIdRecordManager);
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
        serviceDelegator.viewRecord(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test0103ViewActivities() {
        serviceDelegator.viewActivities(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWork() {
        serviceDelegator.viewWork(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWorkSummary() {
        serviceDelegator.viewWorkSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateWork() {
        serviceDelegator.createWork(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateWork() {
        serviceDelegator.updateWork(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteWork() {
        serviceDelegator.deleteWork(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFunding() {
        serviceDelegator.viewFunding(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFundingSummary() {
        serviceDelegator.viewFundingSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateFunding() {
        serviceDelegator.createFunding(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateFunding() {
        serviceDelegator.updateFunding(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteFunding() {
        serviceDelegator.deleteFunding(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducation() {
        serviceDelegator.viewEducation(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducationSummary() {
        serviceDelegator.viewEducationSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEducation() {
        serviceDelegator.createEducation(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEducation() {
        serviceDelegator.updateEducation(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmployment() {
        serviceDelegator.viewEmployment(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmploymentSummary() {
        serviceDelegator.viewEmploymentSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEmployment() {
        serviceDelegator.createEmployment(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEmployment() {
        serviceDelegator.updateEmployment(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAffiliation() {
        serviceDelegator.deleteAffiliation(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReview() {
        serviceDelegator.viewPeerReview(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReviewSummary() {
        serviceDelegator.viewPeerReviewSummary(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreatePeerReview() {
        serviceDelegator.createPeerReview(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdatePeerReview() {
        serviceDelegator.updatePeerReview(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeletePeerReview() {
        serviceDelegator.deletePeerReview(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrl() {
        serviceDelegator.viewResearcherUrl(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateResearcherUrl() {
        serviceDelegator.updateResearcherUrl(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateResearcherUrl() {
        serviceDelegator.createResearcherUrl(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteResearcherUrl() {
        serviceDelegator.deleteResearcherUrl(unclaimedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearchResources() {
        serviceDelegator.viewResearchResources(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearchResource() {
        serviceDelegator.viewResearchResource(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateResearchResource() {
        serviceDelegator.updateResearchResource(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateResearchResource() {
        serviceDelegator.createResearchResource(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteResearchResource() {
        serviceDelegator.deleteResearchResource(unclaimedUserOrcid, 0L);
        fail();
    }
    
    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmails() {
        serviceDelegator.viewEmails(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherNames() {
        serviceDelegator.viewOtherNames(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherName() {
        serviceDelegator.viewOtherName(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateOtherName() {
        serviceDelegator.createOtherName(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateOtherName() {
        serviceDelegator.updateOtherName(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteOtherName() {
        serviceDelegator.deleteOtherName(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifier() {
        serviceDelegator.viewExternalIdentifier(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateExternalIdentifier() {
        serviceDelegator.updateExternalIdentifier(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateExternalIdentifier() {
        serviceDelegator.createExternalIdentifier(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteExternalIdentifier() {
        serviceDelegator.deleteExternalIdentifier(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewBiography() {
        serviceDelegator.viewBiography(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeywords() {
        serviceDelegator.viewKeywords(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeyword() {
        serviceDelegator.viewKeyword(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateKeyword() {
        serviceDelegator.createKeyword(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateKeyword() {
        serviceDelegator.updateKeyword(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteKeyword() {
        serviceDelegator.deleteKeyword(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddresses() {
        serviceDelegator.viewAddresses(unclaimedUserOrcid);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddress() {
        serviceDelegator.viewAddress(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateAddress() {
        serviceDelegator.createAddress(unclaimedUserOrcid, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateAddress() {
        serviceDelegator.updateAddress(unclaimedUserOrcid, 0L, null);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAddress() {
        serviceDelegator.deleteAddress(unclaimedUserOrcid, 0L);
        fail();
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPerson() {
        serviceDelegator.viewPerson(unclaimedUserOrcid);
        fail();
    }   

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        when(clientManagerReadOnly.getSummary("some-client-that-doesn't-exist")).thenThrow(new NoResultException());

        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() {
        ClientSummary summary = new ClientSummary();
        summary.setName("Source Client 2");
        summary.setDescription("A test source client");
        when(clientManagerReadOnly.getSummary("APP-6666666666666666")).thenReturn(summary);

        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
        verify(orcidSecurityManager).checkScopes(ScopePathType.READ_PUBLIC);
    }

    @Test
    public void testViewBulkWorks() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        // The fourth element is already an OrcidError because the security
        // manager replaced the work the caller may not read. That substitution
        // is checkAndFilter's, is in place, and is proved by orcid-core's
        // OrcidSecurityManager_WorkBulkTest; here what is asserted is that the
        // delegator returns the bulk the filter left and sets the path on the
        // elements that are still works.
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC));
        stored.getBulk().add(work(12L, "LIMITED", Visibility.LIMITED));
        stored.getBulk().add(work(13L, "PRIVATE", Visibility.PRIVATE));
        stored.getBulk().add(new OrcidError());
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,16")).thenReturn(stored);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,16");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError);
        assertEquals("/0000-0000-0000-0003/work/11", ((Work) workBulk.getBulk().get(0)).getPath());
        assertEquals("/0000-0000-0000-0003/work/12", ((Work) workBulk.getBulk().get(1)).getPath());
        assertEquals("/0000-0000-0000-0003/work/13", ((Work) workBulk.getBulk().get(2)).getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testViewBulkWorksWithBadPutCode() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC));
        stored.getBulk().add(work(12L, "LIMITED", Visibility.LIMITED));
        stored.getBulk().add(work(13L, "PRIVATE", Visibility.PRIVATE));
        stored.getBulk().add(new OrcidError());
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,9999")).thenReturn(stored);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,9999");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError); // bad put code
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testViewBulkWorksWithTooManyPutCodes() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= BULK_READ_SIZE; i++) {
            tooManyPutCodes.append(",").append(i);
        }
        // The limit lives in WorkManagerReadOnly.findWorkBulk together with the
        // org.orcid.core.works.bulk.read.max property; the delegator never reads
        // it and only has to let the exception through.
        when(workManagerReadOnly.findWorkBulk(eq(ORCID), anyString())).thenThrow(new ExceedMaxNumberOfPutCodesException(BULK_READ_SIZE));

        serviceDelegator.viewBulkWorks(ORCID, tooManyPutCodes.toString());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBulkWrongToken() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC));
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13")).thenReturn(stored);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(WorkBulk.class), eq(ScopePathType.ORCID_WORKS_READ_LIMITED));

        serviceDelegator.viewBulkWorks(ORCID, "11,12,13");
    }


    /**
     * Walks a whole record and checks every element that carries a source.
     *
     * <p>
     * It used to assert that each source URI starts with {@code https://}. That
     * protocol is applied by the JPA to JAXB adapters and the configured base
     * URL, neither of which the delegator touches, so with a mocked record
     * manager the assertion would only be checking the URI this test wrote. It
     * has moved, verbatim, to MemberV3ApiServiceDelegatorDatabaseRulesTest, which
     * keeps the DBUnit fixture and runs in the db-tests stage. What is checked
     * here instead is the delegator's own contribution to the same walk: the
     * real SourceUtils resolved every source's display name.
     */
    @Test
    public void test3_0() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(recordWithSources());

        Response response = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) response.getEntity();
        assertNotNull(record.getActivitiesSummary());
        ActivitiesSummary activitiesSummary = record.getActivitiesSummary();
        if (activitiesSummary.getWorks() != null) {
            activitiesSummary.getWorks().getWorkGroup().forEach(g -> {
                g.getWorkSummary().forEach(e -> assertSourceElement(e));
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
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    /**
     * Deactivated elements tests
     */    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewActivities() {
        serviceDelegator.viewActivities(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewRecord() {
        serviceDelegator.viewRecord(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPerson() {
        serviceDelegator.viewPerson(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewAddresses() {
        serviceDelegator.viewAddresses(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEducations() {
        serviceDelegator.viewEducations(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmails() {
        serviceDelegator.viewEmails(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmployments() {
        serviceDelegator.viewEmployments(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewFundings() {
        serviceDelegator.viewFundings(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewKeywords() {
        serviceDelegator.viewKeywords(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewOtherNames() {
        serviceDelegator.viewOtherNames(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPeerReviews() {
        serviceDelegator.viewPeerReviews(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewResearchResources() {
        serviceDelegator.viewResearchResources(deactivatedUserOrcid);
        fail();
    }
    
    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewWorks() {
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
        if (element.getSource() != null) {
            assertNotNull(element.getSource().getSourceName());
            assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        }
    }

    /**
     * The limit is enforced inside {@code WorkManagerReadOnly.findWorkBulk}; the
     * delegator does not read the property, so this is only the size the test
     * builds a request of.
     */
    private static final int BULK_READ_SIZE = 100;

    private Work work(long putCode, String title, Visibility visibility) {
        Work work = new Work();
        work.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        work.setVisibility(visibility);
        work.setSource(clientSource(CLIENT_1));
        work.setLastModifiedDate(lastModified());
        return work;
    }

    private Record recordWithSources() {
        Source source = clientSource(CLIENT_1);
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(ORCID));

        Person person = new Person();
        org.orcid.jaxb.model.v3.release.record.Addresses addresses = new org.orcid.jaxb.model.v3.release.record.Addresses();
        org.orcid.jaxb.model.v3.release.record.Address address = new org.orcid.jaxb.model.v3.release.record.Address();
        address.setPutCode(9L);
        address.setCountry(new org.orcid.jaxb.model.v3.release.common.Country(org.orcid.jaxb.model.common.Iso3166Country.US));
        address.setVisibility(Visibility.PUBLIC);
        address.setSource(source);
        address.setLastModifiedDate(lastModified());
        addresses.setAddress(new java.util.ArrayList<>(java.util.Arrays.asList(address)));
        person.setAddresses(addresses);

        org.orcid.jaxb.model.v3.release.record.Keywords keywords = new org.orcid.jaxb.model.v3.release.record.Keywords();
        org.orcid.jaxb.model.v3.release.record.Keyword keyword = new org.orcid.jaxb.model.v3.release.record.Keyword();
        keyword.setPutCode(9L);
        keyword.setContent("keyword");
        keyword.setVisibility(Visibility.PUBLIC);
        keyword.setSource(source);
        keyword.setLastModifiedDate(lastModified());
        keywords.setKeywords(new java.util.ArrayList<>(java.util.Arrays.asList(keyword)));
        person.setKeywords(keywords);

        org.orcid.jaxb.model.v3.release.record.OtherNames otherNames = new org.orcid.jaxb.model.v3.release.record.OtherNames();
        org.orcid.jaxb.model.v3.release.record.OtherName otherName = new org.orcid.jaxb.model.v3.release.record.OtherName();
        otherName.setPutCode(13L);
        otherName.setContent("other name");
        otherName.setVisibility(Visibility.PUBLIC);
        otherName.setSource(source);
        otherName.setLastModifiedDate(lastModified());
        otherNames.setOtherNames(new java.util.ArrayList<>(java.util.Arrays.asList(otherName)));
        person.setOtherNames(otherNames);

        org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers extIds = new org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers();
        org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier extId = new org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier();
        extId.setPutCode(13L);
        extId.setType("Facebook");
        extId.setValue("abc123");
        extId.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.facebook.com/abc123"));
        extId.setVisibility(Visibility.PUBLIC);
        extId.setSource(source);
        extId.setLastModifiedDate(lastModified());
        extIds.setExternalIdentifiers(new java.util.ArrayList<>(java.util.Arrays.asList(extId)));
        person.setExternalIdentifiers(extIds);

        org.orcid.jaxb.model.v3.release.record.ResearcherUrls researcherUrls = new org.orcid.jaxb.model.v3.release.record.ResearcherUrls();
        org.orcid.jaxb.model.v3.release.record.ResearcherUrl researcherUrl = new org.orcid.jaxb.model.v3.release.record.ResearcherUrl();
        researcherUrl.setPutCode(13L);
        researcherUrl.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.researcherurl.com"));
        researcherUrl.setUrlName("url");
        researcherUrl.setVisibility(Visibility.PUBLIC);
        researcherUrl.setSource(source);
        researcherUrl.setLastModifiedDate(lastModified());
        researcherUrls.setResearcherUrls(new java.util.ArrayList<>(java.util.Arrays.asList(researcherUrl)));
        person.setResearcherUrls(researcherUrls);

        record.setPerson(person);

        ActivitiesSummary activities = emptyActivitiesSummary();
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(11L);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("PUBLIC"));
        summary.setTitle(title);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        summary.setVisibility(Visibility.PUBLIC);
        summary.setSource(source);
        summary.setLastModifiedDate(lastModified());
        group.getWorkSummary().add(summary);
        works.getWorkGroup().add(group);
        activities.setWorks(works);
        record.setActivitiesSummary(activities);

        return record;
    }
}
