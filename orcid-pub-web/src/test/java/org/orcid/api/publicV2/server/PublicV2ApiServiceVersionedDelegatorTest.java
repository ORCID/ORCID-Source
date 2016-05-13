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
package org.orcid.api.publicV2.server;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicV2ApiServiceVersionedDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", 
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", 
            "/data/OrgAffiliationEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml");
    
    @Resource(name = "publicV2ApiServiceDelegatorRc2")
    PublicV2ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegator;
    
    @Resource
    private ProfileDao profileDao;
    
    private String nonExistingUser = "0000-0000-0000-000X";    
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0005";
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);        
    }
    
    @Before
    public void before() {
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication auth = new AnonymousAuthenticationToken("anonymous", "anonymous", roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
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
     * */
    @Test(expected = NoResultException.class)
    public void test00ViewRecord() {
        serviceDelegator.viewRecord(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewActivities() {
        serviceDelegator.viewActivities(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewWork() {
        serviceDelegator.viewWork(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewWorkSummary() {
        serviceDelegator.viewWorkSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewFunding() {
        serviceDelegator.viewFunding(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewFundingSummary() {
        serviceDelegator.viewFundingSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEducation() {
        serviceDelegator.viewEducation(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEducationSummary() {
        serviceDelegator.viewEducationSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmployment() {
        serviceDelegator.viewEmployment(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmploymentSummary() {
        serviceDelegator.viewEmploymentSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPeerReview() {
        serviceDelegator.viewPeerReview(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPeerReviewSummary() {
        serviceDelegator.viewPeerReviewSummary(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewResearcherUrls() {
        serviceDelegator.viewResearcherUrls(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewResearcherUrl() {
        serviceDelegator.viewResearcherUrl(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmails() {
        serviceDelegator.viewEmails(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewOtherNames() {
        serviceDelegator.viewOtherNames(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewOtherName() {
        serviceDelegator.viewOtherName(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPersonalDetails() {
        serviceDelegator.viewPersonalDetails(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewExternalIdentifiers() {
        serviceDelegator.viewExternalIdentifiers(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewExternalIdentifier() {
        serviceDelegator.viewExternalIdentifier(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewBiography() {
        serviceDelegator.viewBiography(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewKeywords() {
        serviceDelegator.viewKeywords(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewKeyword() {
        serviceDelegator.viewKeyword(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewAddresses() {
        serviceDelegator.viewAddresses(nonExistingUser);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void test00ViewAddress() {
        serviceDelegator.viewAddress(nonExistingUser, 0L);
        fail();
    }

    @Test(expected = NoResultException.class)
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
    public void test03ViewPerson() {
        updateProfileSubmissionDate(unclaimedUserOrcid, 0);
        serviceDelegator.viewPerson(unclaimedUserOrcid);
        fail();
    }

    private void updateProfileSubmissionDate(String orcid, int increment) {
        // Update the submission date so it is long enough
        ProfileEntity profileEntity = profileDao.find(orcid);
        profileEntity.setSubmissionDate(DateUtils.addDays(new Date(), increment));
        profileDao.merge(profileEntity);
        profileDao.flush();
    }
}
