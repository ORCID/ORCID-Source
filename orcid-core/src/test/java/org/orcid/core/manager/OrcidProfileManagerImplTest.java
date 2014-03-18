/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.DeveloperToolsEnabled;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.utils.DateUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
@DirtiesContext
public class OrcidProfileManagerImplTest extends OrcidProfileManagerBaseTest {

    protected static final String APPLICATION_ORCID = "2222-2222-2222-2228";

    protected static final String DELEGATE_ORCID = "1111-1111-1111-1115";

    protected static final String TEST_ORCID = "4444-4444-4444-4447";

    protected static final String TEST_ORCID_WITH_WORKS = "4444-4444-4444-4443";

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private GenericDao<SubjectEntity, String> subjectDao;

    @Resource
    private GenericDao<WorkEntity, Long> workDao;

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Mock
    private NotificationManager notificationManager;

    @Before
    @Transactional
    @Rollback
    public void before() throws Exception {

        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setNotificationManager(notificationManager);

        if (profileDao.find(TEST_ORCID) != null) {
            profileDao.remove(TEST_ORCID);
        }
        subjectDao.merge(new SubjectEntity("Computer Science"));
        subjectDao.merge(new SubjectEntity("Dance"));

        OrcidProfile delegateProfile = new OrcidProfile();
        delegateProfile.setOrcidIdentifier(DELEGATE_ORCID);
        OrcidBio delegateBio = new OrcidBio();
        delegateProfile.setOrcidBio(delegateBio);
        PersonalDetails delegatePersonalDetails = new PersonalDetails();
        delegateBio.setPersonalDetails(delegatePersonalDetails);
        CreditName delegateCreditName = new CreditName("H. Shearer");
        delegateCreditName.setVisibility(Visibility.PUBLIC);
        delegatePersonalDetails.setCreditName(delegateCreditName);
        orcidProfileManager.createOrcidProfile(delegateProfile);

        OrcidProfile applicationProfile = new OrcidProfile();
        applicationProfile.setOrcidIdentifier(APPLICATION_ORCID);
        OrcidBio applicationBio = new OrcidBio();
        applicationProfile.setOrcidBio(applicationBio);
        PersonalDetails applicationPersonalDetails = new PersonalDetails();
        applicationBio.setPersonalDetails(applicationPersonalDetails);
        applicationPersonalDetails.setCreditName(new CreditName("Brown University"));
        orcidProfileManager.createOrcidProfile(applicationProfile);

        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(applicationProfile.getOrcidIdentifier().getPath());
        ProfileEntity applicationProfileEntity = profileDao.find(applicationProfile.getOrcidIdentifier().getPath());
        profileDao.refresh(applicationProfileEntity);
        clientDetails.setProfileEntity(applicationProfileEntity);
        clientDetailsDao.merge(clientDetails);

        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setTokenValue("1234");
        token.setClientDetailsEntity(clientDetails);
        token.setProfile(profileDao.find(delegateProfile.getOrcidIdentifier().getPath()));
        token.setScope(StringUtils.join(new String[] { ScopePathType.ORCID_BIO_READ_LIMITED.value(), ScopePathType.ORCID_BIO_UPDATE.value() }, " "));
        SortedSet<OrcidOauth2TokenDetail> tokens = new TreeSet<>();
        tokens.add(token);
        ProfileEntity delegateProfileEntity = profileDao.find(delegateProfile.getOrcidIdentifier().getPath());
        delegateProfileEntity.setTokenDetails(tokens);
        profileDao.merge(delegateProfileEntity);

        SecurityQuestionEntity existingSecurityQuestionEntity = securityQuestionDao.find(3);
        if (existingSecurityQuestionEntity == null) {
            SecurityQuestionEntity securityQuestionEntity = new SecurityQuestionEntity();
            securityQuestionEntity.setId(3);
            securityQuestionEntity.setQuestion("What?");
            securityQuestionDao.persist(securityQuestionEntity);
        }
    }

    @After
    public void after() {
        profileDao.remove(DELEGATE_ORCID);
        profileDao.remove(APPLICATION_ORCID);
        orcidProfileManager.clearOrcidProfileCache();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfile() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        String originalPutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidProfile profile2 = createBasicProfile();
        profile2.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setPutCode(originalPutCode);
        profile2 = orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        String resultPutCode = resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("Put code should not change", originalPutCode, resultPutCode);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileButRemoveActivities() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidActivities(null);
        profile2 = orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertNull("There should be no activities", resultProfile.getOrcidActivities());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileButRemoveWorkExternalIdentifier() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);

        List<WorkExternalIdentifier> workExternalIdentifiers = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers()
                .getWorkExternalIdentifier();
        assertEquals(2, workExternalIdentifiers.size());
        Iterator<WorkExternalIdentifier> workExternalIdentifiersIterator = workExternalIdentifiers.iterator();
        while (workExternalIdentifiersIterator.hasNext()) {
            if (WorkExternalIdentifierType.PMID.equals(workExternalIdentifiersIterator.next().getWorkExternalIdentifierType())) {
                workExternalIdentifiersIterator.remove();
            }
        }

        profile1 = orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers().getWorkExternalIdentifier().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileButRemoveWorkContributor() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);

        List<Contributor> contributors = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor();
        assertEquals(2, contributors.size());
        Iterator<Contributor> contributorsIterator = contributors.iterator();
        while (contributorsIterator.hasNext()) {
            if (SequenceType.ADDITIONAL.equals(contributorsIterator.next().getContributorAttributes().getContributorSequence())) {
                contributorsIterator.remove();
            }
        }

        profile1 = orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWhenTokenPresent() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(profile1);
        assertNotNull(profile1.getOrcidBio().getApplications());
        assertEquals(1, profile1.getOrcidBio().getApplications().getApplicationSummary().size());

        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidIdentifier(DELEGATE_ORCID);

        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(resultProfile);
        assertNotNull(resultProfile.getOrcidBio().getApplications());
        assertEquals(1, resultProfile.getOrcidBio().getApplications().getApplicationSummary().size());
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithEmailVerified() {
        OrcidProfile profile = createBasicProfile();
        profile = orcidProfileManager.createOrcidProfile(profile);
        assertNotNull(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertFalse(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());

        profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVerified(true);

        orcidProfileManager.updateOrcidProfile(profile);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertTrue(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testPreventDuplicatedWorks() {
        OrcidWork work1 = createWork1();
        OrcidWork work2 = createWork2();
        OrcidWork work3 = createWork3();
        OrcidProfile profile = createBasicProfile();
        profile = orcidProfileManager.createOrcidProfile(profile);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidActivities());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(1, profile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertEquals(work1.getWorkTitle().getTitle().getContent(), profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle()
                .getContent());

        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);
        orcidProfileManager.addOrcidWorks(profile);

        profile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(profile);
        assertNotNull(profile.getOrcidActivities());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(2, profile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work1.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work1.getWorkTitle().getTitle().getContent()));
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work2.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work2.getWorkTitle().getTitle().getContent()));
        assertFalse(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work3.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));

        // Add work # 3 and duplicate other works
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work1);
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work3);
        orcidProfileManager.addOrcidWorks(profile);

        profile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        // Work 3 was added and work 1 and 2 where not added twice
        assertNotNull(profile);
        assertNotNull(profile.getOrcidActivities());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, profile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work1.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work1.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work2.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work2.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work3.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));

        // Duplicate all works
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work1);
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile);

        profile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        // No new works are added and no duplicated was allowed
        assertNotNull(profile);
        assertNotNull(profile.getOrcidActivities());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(3, profile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work1.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work1.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work2.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work2.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));
        assertTrue(profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent()
                .equals(work3.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent())
                || profile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(2).getWorkTitle().getTitle().getContent()
                        .equals(work3.getWorkTitle().getTitle().getContent()));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testOrcidWorksHashCodeAndEquals() {
        OrcidWork workA = createWork1();
        OrcidWork workB = createWork1();
        assertEquals(workA, workB);
        assertEquals(workA.hashCode(), workB.hashCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDedupeWorks() {
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        assertEquals(2, orcidWorks.getOrcidWork().size());

        OrcidWorks dedupedOrcidWorks = orcidProfileManager.dedupeWorks(orcidWorks);

        assertEquals(1, dedupedOrcidWorks.getOrcidWork().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithDupeWork() {
        OrcidProfile profile = createBasicProfile();
        OrcidProfile createdProfile = orcidProfileManager.createOrcidProfile(profile);
        List<OrcidWork> orcidWorkList = createdProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, orcidWorkList.size());

        orcidWorkList.add(createWork1());
        assertEquals(2, orcidWorkList.size());
        OrcidProfile updatedProfile = orcidProfileManager.updateOrcidProfile(createdProfile);
        List<OrcidWork> updatedOrcidWorkList = updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, updatedOrcidWorkList.size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePersonalInformation() {

        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = createFullOrcidProfile();

        orcidProfileManager.updatePersonalInformation(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("William", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals("Simpson", resultProfile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertEquals("W. J. R. Simpson", resultProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals("Will is a software developer at Semantico", resultProfile.getOrcidBio().getBiography().getContent());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePersonalInformationRemovesOrcidIndexFields() throws Exception {

        // re-use the createFull method but add some extra criteria so we can
        // use our specific orcidAllSolrFieldsPopulatedForSave matcher
        OrcidProfile profile = createFullOrcidProfile();
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherName().add(new OtherName("Stan"));
        otherNames.getOtherName().add(new OtherName("Willis"));

        profile.getOrcidBio().getPersonalDetails().setOtherNames(otherNames);

        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);
        WorkTitle singleWorkTitle = new WorkTitle();
        singleWorkTitle.setTitle(new Title("Single works"));
        singleWorkTitle.setSubtitle(new Subtitle("Single works"));
        OrcidWork orcidWork = createWork1(singleWorkTitle);
        // TODO JB some doi testing here?
        // orcidWork.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));

        orcidWorks.getOrcidWork().add(orcidWork);

        orcidProfileManager.createOrcidProfile(profile);

        // now negate all fields that form part of a solr query, leaving only
        // the orcid itself
        // we do this by passing through an orcid missing the fields from an
        // OrcidSolrDocument
        OrcidProfile negatedProfile = createBasicProfile();
        negatedProfile.getOrcidBio().getPersonalDetails().setFamilyName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setGivenNames(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setCreditName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setOtherNames(null);
        negatedProfile.getOrcidActivities().setAffiliations(null);
        orcidProfileManager.updateOrcidBio(negatedProfile);
        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddOrcidWorks() {

        OrcidProfile profile1 = createBasicProfile();
        OrcidHistory history = new OrcidHistory();
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile1.setOrcidHistory(history);
        history.setClaimed(new Claimed(true));
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        String originalPutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("Another Title"));
        workTitle1.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work1 = createWork1(workTitle1);
        orcidWorks.getOrcidWork().add(work1);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("New Title"));
        workTitle2.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work2 = createWork2(workTitle2);
        orcidWorks.getOrcidWork().add(work2);

        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("New Title"));
        workTitle3.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work3 = createWork2(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            assertEquals(Visibility.PRIVATE, work.getVisibility());
        }
        assertEquals("Put code of original work should not have changed", originalPutCode, works.get(2).getPutCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddOrcidWorksWhenDefaultVisibilityIsPublic() {

        OrcidProfile profile1 = createBasicProfile();
        OrcidHistory history = new OrcidHistory();
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile1.setOrcidHistory(history);
        history.setClaimed(new Claimed(true));
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("Another Title"));
        workTitle1.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work1 = createWork1(workTitle1);
        orcidWorks.getOrcidWork().add(work1);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("New Title"));
        workTitle2.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work2 = createWork2(workTitle2);
        orcidWorks.getOrcidWork().add(work2);
        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("Further Title"));
        workTitle3.setSubtitle(new Subtitle("Further subtitle"));
        OrcidWork work3 = createWork3(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(4, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            if ("Test Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
            } else if ("Further Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
            } else {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
            }
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddOrcidWorkToUnclaimedProfile() {

        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("Another Title"));
        workTitle1.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work1 = createWork1(workTitle1);
        orcidWorks.getOrcidWork().add(work1);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("New Title"));
        workTitle2.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work2 = createWork2(workTitle2);
        orcidWorks.getOrcidWork().add(work2);

        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("Further Title"));
        workTitle3.setSubtitle(new Subtitle("Further subtitle"));
        OrcidWork work3 = createWork3(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(4, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            if ("Further Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
            } else {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
            }
        }
    }

    /*
     * @Test
     * 
     * @Transactional
     * 
     * @Rollback(true) public void testDeleteOrcidWorks() { OrcidProfile profile
     * = createBasicProfile(); OrcidWork work1 = createWork("Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work1); OrcidWork work2 =
     * createWork("Yet Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work2);
     * 
     * orcidProfileManager.createOrcidProfile(profile);
     * orcidProfileManager.deleteOrcidWorks(TEST_ORCID, new int[] { 0, 2 });
     * 
     * OrcidProfile resultProfile =
     * orcidProfileManager.retrieveOrcidProfile(TEST_ORCID); assertEquals(1,
     * resultProfile.getOrcidWorks().getOrcidWork().size()); //
     * assertEquals("Test Title", //
     * resultProfile.getOrcidWorks().getOrcidWork()
     * .get(0).getTitles().getTitle().getContent());
     * 
     * assertEquals(IndexingStatus.PENDING,
     * profileDao.find(TEST_ORCID).getIndexingStatus()); }
     * 
     * @Test
     * 
     * @Transactional
     * 
     * @Rollback(true) public void testUpdateOrcidWorkVisibility() {
     * OrcidProfile profile = createBasicProfile(); OrcidWork work1 =
     * createWork("Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work1); OrcidWork work2 =
     * createWork("Yet Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work2);
     * 
     * orcidProfileManager.createOrcidProfile(profile);
     * orcidProfileManager.processProfilesPendingIndexing();
     * orcidProfileManager.updateOrcidWorkVisibility(TEST_ORCID, new int[] { 0,
     * 2 }, Visibility.LIMITED);
     * 
     * OrcidProfile resultProfile =
     * orcidProfileManager.retrieveOrcidProfile(TEST_ORCID); assertEquals(3,
     * resultProfile.getOrcidWorks().getOrcidWork().size());
     * assertEquals(Visibility.LIMITED,
     * resultProfile.getOrcidWorks().getOrcidWork().get(0).getVisibility());
     * assertNull
     * (resultProfile.getOrcidWorks().getOrcidWork().get(1).getVisibility());
     * assertEquals(Visibility.LIMITED,
     * resultProfile.getOrcidWorks().getOrcidWork().get(2).getVisibility()); }
     */

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeApplication() {
        OrcidProfile userProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getOrcidBio().getApplications());
        assertEquals(1, userProfile.getOrcidBio().getApplications().getApplicationSummary().size());

        orcidProfileManager.revokeApplication(DELEGATE_ORCID, APPLICATION_ORCID,
                Arrays.asList(new ScopePathType[] { ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE }));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(retrievedProfile);
        assertNull(retrievedProfile.getOrcidBio().getApplications());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeApplicationWithWrongScope() {
        OrcidProfile userProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getOrcidBio().getApplications());
        assertEquals(1, userProfile.getOrcidBio().getApplications().getApplicationSummary().size());

        // Shouldn't remove the token because scopes different
        orcidProfileManager.revokeApplication(DELEGATE_ORCID, APPLICATION_ORCID, Arrays.asList(new ScopePathType[] { ScopePathType.ORCID_BIO_READ_LIMITED }));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(retrievedProfile);
        assertNotNull(retrievedProfile.getOrcidBio().getApplications());
        assertEquals(1, retrievedProfile.getOrcidBio().getApplications().getApplicationSummary().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordResultsInEncypytedProfile() {
        OrcidProfile basicProfile = createBasicProfile();
        OrcidProfile derivedProfile = orcidProfileManager.createOrcidProfile(basicProfile);
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", encryptionManager.decryptForInternalUse(derivedProfile.getSecurityQuestionAnswer()));
        assertEquals("1234", encryptionManager.decryptForInternalUse(derivedProfile.getVerificationCode()));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(derivedProfile.getOrcidIdentifier().getPath());
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", retrievedProfile.getSecurityQuestionAnswer());
        assertEquals("1234", retrievedProfile.getVerificationCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePreferences() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        assertEquals(Visibility.PRIVATE, profile1.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue());

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidInternal internal = new OrcidInternal();
        profile2.setOrcidInternal(internal);
        Preferences preferences = new Preferences();
        internal.setPreferences(preferences);
        preferences.setSendChangeNotifications(new SendChangeNotifications(false));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        preferences.setDeveloperToolsEnabled(new DeveloperToolsEnabled(true));
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));

        orcidProfileManager.updatePreferences(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(false, retrievedProfile.getOrcidInternal().getPreferences().getSendChangeNotifications().isValue());
        assertEquals(true, retrievedProfile.getOrcidInternal().getPreferences().getSendOrcidNews().isValue());
        assertEquals(true, retrievedProfile.getOrcidInternal().getPreferences().getDeveloperToolsEnabled().isValue());
        assertEquals(Visibility.PUBLIC, retrievedProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAffiliations() throws DatatypeConfigurationException {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setPersonalDetails(new PersonalDetails());

        OrcidActivities orcidActivities = new OrcidActivities();
        profile2.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);

        Affiliation affiliation1 = getAffiliation();
        Affiliation affiliation2 = getAffiliation();
        affiliation2.setType(AffiliationType.EDUCATION);
        affiliation2.getOrganization().setName("Past Institution 2");

        affiliations.getAffiliation().add(affiliation1);
        affiliations.getAffiliation().add(affiliation2);

        profile2.setOrcidBio(orcidBio);
        orcidProfileManager.addAffiliations(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(retrievedProfile);
        assertEquals(2, retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation().size());
        for (Affiliation affiliation : retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation()) {
            assertNotNull(affiliation.getPutCode());
        }

        // Remove an affiliation
        profile2 = createFullOrcidProfile();
        affiliations.getAffiliation().clear();
        affiliations.getAffiliation().add(affiliation1);
        profile2.setOrcidActivities(orcidActivities);

        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile profile3 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile3.getOrcidActivities().getAffiliations().getAffiliation().size());

        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testOrgReuse() {
        OrcidProfile profile1 = createBasicProfile();
        OrcidHistory history = new OrcidHistory();
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile1.setOrcidHistory(history);
        history.setClaimed(new Claimed(true));
        OrcidActivities orcidActivities = profile1.getOrcidActivities();
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliations.getAffiliation().add(affiliation);
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName("New College");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);

        orcidProfileManager.createOrcidProfile(profile1);

        ProfileEntity profileEntity = profileDao.find(TEST_ORCID);
        assertEquals(1, profileEntity.getOrgAffiliationRelations().size());
        OrgEntity orgEntity = profileEntity.getOrgAffiliationRelations().iterator().next().getOrg();
        assertNotNull(orgEntity);

        // Now create another profile with the same affiliation and check that
        // the org is reused;

        String otherOrcid = "4444-4444-4444-4448";
        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidIdentifier(otherOrcid);
        List<Email> emailList2 = profile2.getOrcidBio().getContactDetails().getEmail();
        emailList2.clear();
        emailList2.add(new Email("another@semantico.com"));
        profile2.getOrcidActivities().setAffiliations(affiliations);
        orcidProfileManager.createOrcidProfile(profile2);

        ProfileEntity profileEntity2 = profileDao.find(otherOrcid);
        assertEquals(1, profileEntity2.getOrgAffiliationRelations().size());
        OrgEntity orgEntity2 = profileEntity2.getOrgAffiliationRelations().iterator().next().getOrg();
        assertNotNull(orgEntity);

        assertEquals(orgEntity.getId(), orgEntity2.getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    @Rollback(true)
    public void testAddDelegates() {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidBio orcidBio = new OrcidBio();
        profile2.setOrcidBio(orcidBio);
        Delegation delegation = new Delegation();
        orcidBio.setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16")));
        DelegateSummary delegateSummary = new DelegateSummary(new OrcidIdentifier(DELEGATE_ORCID));
        delegationDetails.setDelegateSummary(delegateSummary);
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        orcidProfileManager.addDelegates(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(retrievedProfile);
        GivenPermissionTo retrievedGivenPermissionTo = retrievedProfile.getOrcidBio().getDelegation().getGivenPermissionTo();
        assertNotNull(retrievedGivenPermissionTo);
        assertEquals(1, retrievedGivenPermissionTo.getDelegationDetails().size());
        DelegationDetails retrievedDelegationDetails = retrievedGivenPermissionTo.getDelegationDetails().get(0);
        DelegateSummary retrievedDelegateSummary = retrievedDelegationDetails.getDelegateSummary();
        assertNotNull(retrievedDelegateSummary);
        assertEquals(DELEGATE_ORCID, retrievedDelegateSummary.getOrcidIdentifier().getPath());
        assertEquals("H. Shearer", retrievedDelegateSummary.getCreditName().getContent());
        assertEquals(DateUtils.convertToDate("2011-03-14T02:34:16"), DateUtils.convertToDate(retrievedDelegationDetails.getApprovalDate().getValue()));
        verify(notificationManager, times(1)).sendNotificationToAddedDelegate(any(OrcidProfile.class), any(List.class));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeDelegate() {
        OrcidProfile profile1 = createBasicProfile();
        Delegation delegation = new Delegation();
        profile1.getOrcidBio().setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16")));
        DelegateSummary profileSummary = new DelegateSummary(new OrcidIdentifier(DELEGATE_ORCID));
        delegationDetails.setDelegateSummary(profileSummary);
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        orcidProfileManager.createOrcidProfile(profile1);

        orcidProfileManager.revokeDelegate(TEST_ORCID, DELEGATE_ORCID);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNull(retrievedProfile.getOrcidBio().getDelegation());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordInformationLeavesSecurityQuestionsUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertEquals("Should have security question", 3, retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("Should have decrypted security answer", "random answer", retrievedProfile.getSecurityQuestionAnswer());

        retrievedProfile.setPassword("A new password");

        orcidProfileManager.updatePasswordInformation(retrievedProfile);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String updatedPassword = updatedProfile.getPassword();
        assertEquals("Password should be hashed", 108, updatedPassword.length());
        assertFalse("Password should have changed but was still: " + updatedPassword, hashedPasswordValue.equals(updatedPassword));
        assertEquals("Should have security question", 3, updatedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("Should have decrypted security answer", "random answer", updatedProfile.getSecurityQuestionAnswer());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSecurityQuestionsUpdateLeavePasswordInformationUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertEquals("Should have security question", 3, retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertTrue("Should have decrypted security answer", "random answer".equals(retrievedProfile.getSecurityQuestionAnswer()));

        retrievedProfile.setSecurityQuestionAnswer("A new random answer");

        orcidProfileManager.updateSecurityQuestionInformation(retrievedProfile);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        assertTrue("Password should not have changed", hashedPasswordValue.equals(updatedProfile.getPassword()));
        assertEquals("Should have security question", 3, updatedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("A new random answer", updatedProfile.getSecurityQuestionAnswer());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateLastModifiedDate() throws InterruptedException {
        Date start = new Date();
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        Date profile1LastModified = profile1.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        assertNotNull(profile1LastModified);
        assertFalse(start.after(profile1LastModified));

        Thread.sleep(100);
        orcidProfileManager.updateLastModifiedDate(TEST_ORCID);

        OrcidProfile profile2 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        Date profile2LastModified = profile2.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        assertTrue(profile2LastModified.after(profile1LastModified));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDeactivateProfile() {
        OrcidProfile profile1 = createBasicProfile();
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdCommonName(new ExternalIdCommonName("External body"));
        extId.setExternalIdReference(new ExternalIdReference("abc123"));
        extIds.getExternalIdentifier().add(extId);
        profile1.getOrcidBio().setExternalIdentifiers(extIds);
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        assertEquals(1, profile1.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());

        orcidProfileManager.deactivateOrcidProfile(profile1);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());
        assertTrue(retrievedProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().isEmpty());
        assertNull(retrievedProfile.getOrcidBio().getBiography());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRetrieveProfileWhenNonExistant() {
        OrcidProfile orcidProfile = orcidProfileManager.retrievePublicOrcidProfile("1234-5678-8765-4321");
        assertNull(orcidProfile);
    }

}
