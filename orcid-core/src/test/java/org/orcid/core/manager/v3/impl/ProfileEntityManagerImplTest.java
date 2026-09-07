package org.orcid.core.manager.v3.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.GivenPermissionToManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEmailDomainManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.manager.v3.read_only.impl.ProfileEntityManagerReadOnlyImpl;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.CreditName;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.BackupCodeDao;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ProfileEntityManagerImplTest {

    private ProfileEntityManagerImpl manager;

    @Mock
    private ProfileDao profileDao;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private EmailManager emailManager;
    @Mock
    private ProfileEmailDomainManager profileEmailDomainManager;
    @Mock
    private WorkManager workManager;
    @Mock
    private ProfileFundingManager fundingManager;
    @Mock
    private AffiliationsManager affiliationsManager;
    @Mock
    private PeerReviewManager peerReviewManager;
    @Mock
    private ResearchResourceManager researchResourceManager;
    @Mock
    private AddressDao addressDao;
    @Mock
    private ExternalIdentifierDao externalIdentifierDao;
    @Mock
    private ResearcherUrlDao researcherUrlDao;
    @Mock
    private OtherNameDao otherNameDao;
    @Mock
    private ProfileKeywordDao profileKeywordDao;
    @Mock
    private BackupCodeDao backupCodeDao;
    @Mock
    private NotificationManager notificationManager;
    @Mock
    private GivenPermissionToManager givenPermissionToManager;
    @Mock
    private UserConnectionDao userConnectionDao;
    @Mock
    private ProfileHistoryEventManager profileHistoryEventManager;
    @Mock
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    @Mock
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDaoReadOnly;
    @Mock
    private ProfileLastModifiedDao profileLastModifiedDao;
    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    @Mock
    private LocaleManager localeManager;
    @Mock
    private EncryptionManager encryptionManager;
    @Mock
    private RecordNameManager recordNameManagerV3;
    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;
    @Mock
    private BiographyManager biographyManager;
    @Mock
    private EmailFrequencyManager emailFrequencyManager;
    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    @Mock
    private AddressManager addressManager;
    @Mock
    private ExternalIdentifierManager externalIdentifierManager;
    @Mock
    private ProfileKeywordManager profileKeywordManager;
    @Mock
    private OtherNameManager otherNameManager;
    @Mock
    private ResearcherUrlManager researcherUrlManager;
    @Mock
    private RedisClient redisClient;

    @Before
    public void setUp() {
        manager = new ProfileEntityManagerImpl();

        inject(ProfileEntityManagerReadOnlyImpl.class, "profileDao", profileDao);
        inject(ProfileEntityManagerReadOnlyImpl.class, "orcidOauth2TokenDetailDaoReadOnly", orcidOauth2TokenDetailDaoReadOnly);
        inject(ManagerReadOnlyBaseImpl.class, "profileLastModifiedAspect", profileLastModifiedAspect);

        inject(ProfileEntityManagerImpl.class, "transactionTemplate", transactionTemplate);
        inject(ProfileEntityManagerImpl.class, "emailManager", emailManager);
        inject(ProfileEntityManagerImpl.class, "profileEmailDomainManager", profileEmailDomainManager);
        inject(ProfileEntityManagerImpl.class, "workManager", workManager);
        inject(ProfileEntityManagerImpl.class, "fundingManager", fundingManager);
        inject(ProfileEntityManagerImpl.class, "affiliationsManager", affiliationsManager);
        inject(ProfileEntityManagerImpl.class, "peerReviewManager", peerReviewManager);
        inject(ProfileEntityManagerImpl.class, "researchResourceManager", researchResourceManager);
        inject(ProfileEntityManagerImpl.class, "addressDao", addressDao);
        inject(ProfileEntityManagerImpl.class, "externalIdentifierDao", externalIdentifierDao);
        inject(ProfileEntityManagerImpl.class, "researcherUrlDao", researcherUrlDao);
        inject(ProfileEntityManagerImpl.class, "otherNameDao", otherNameDao);
        inject(ProfileEntityManagerImpl.class, "profileKeywordDao", profileKeywordDao);
        inject(ProfileEntityManagerImpl.class, "backupCodeDao", backupCodeDao);
        inject(ProfileEntityManagerImpl.class, "notificationManager", notificationManager);
        inject(ProfileEntityManagerImpl.class, "givenPermissionToManager", givenPermissionToManager);
        inject(ProfileEntityManagerImpl.class, "userConnectionDao", userConnectionDao);
        inject(ProfileEntityManagerImpl.class, "profileHistoryEventManager", profileHistoryEventManager);
        inject(ProfileEntityManagerImpl.class, "orcidOauth2TokenDetailDao", orcidOauth2TokenDetailDao);
        inject(ProfileEntityManagerImpl.class, "orcidOauth2TokenDetailDaoReadOnly", orcidOauth2TokenDetailDaoReadOnly);
        inject(ProfileEntityManagerImpl.class, "profileLastModifiedDao", profileLastModifiedDao);
        inject(ProfileEntityManagerImpl.class, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        inject(ProfileEntityManagerImpl.class, "localeManager", localeManager);
        inject(ProfileEntityManagerImpl.class, "encryptionManager", encryptionManager);
        inject(ProfileEntityManagerImpl.class, "recordNameManagerV3", recordNameManagerV3);
        inject(ProfileEntityManagerImpl.class, "recordNameManagerReadOnlyV3", recordNameManagerReadOnlyV3);
        inject(ProfileEntityManagerImpl.class, "biographyManager", biographyManager);
        inject(ProfileEntityManagerImpl.class, "emailFrequencyManager", emailFrequencyManager);
        inject(ProfileEntityManagerImpl.class, "redisClient", redisClient);

        doAnswer(invocation -> {
            TransactionCallback<?> callback = (TransactionCallback<?>) invocation.getArguments()[0];
            return callback.doInTransaction(null);
        }).when(transactionTemplate).execute(any(TransactionCallback.class));

        lenient().when(profileLastModifiedAspect.retrieveLastModifiedDate(anyString())).thenReturn(new Date());
    }

    @Test
    public void findByOrcidDelegates() {
        ProfileEntity profile = new ProfileEntity();
        profile.setId("0000-0000-0000-0001");
        when(profileDao.find("0000-0000-0000-0001")).thenReturn(profile);

        assertEquals(profile, manager.findByOrcid("0000-0000-0000-0001"));
    }

    @Test
    public void isLockedDelegates() {
        when(profileDao.isLocked("orcid")).thenReturn(true);
        assertTrue(manager.isLocked("orcid"));
    }

    @Test
    public void getLockedReasonFormatsAllParts() {
        ProfileEntity profile = new ProfileEntity();
        profile.setReasonLocked("spam");
        profile.setReasonLockedDescription("details");
        profile.setRecordLockingAdmin("admin");
        profile.setRecordLockedDate(new Date(0));
        when(profileDao.getLockedReason("orcid")).thenReturn(profile);

        assertEquals(" by admin on 01-01-1970 for: spam - details", manager.getLockedReason("orcid"));
    }

    @Test
    public void getLockedReasonReturnsEmptyWhenNoReason() {
        ProfileEntity profile = new ProfileEntity();
        profile.setReasonLocked("");
        when(profileDao.getLockedReason("orcid")).thenReturn(profile);

        assertEquals("", manager.getLockedReason("orcid"));
    }

    @Test
    public void isOrcidValidAsDelegateDelegates() {
        when(profileDao.isOrcidValidAsDelegate("orcid")).thenReturn(true);
        assertTrue(manager.isOrcidValidAsDelegate("orcid"));
    }

    @Test
    public void haveMemberPushedWorksOrAffiliationsToRecordReturnsFalseForBlankInput() {
        assertFalse(manager.haveMemberPushedWorksOrAffiliationsToRecord("", "client"));
        assertFalse(manager.haveMemberPushedWorksOrAffiliationsToRecord("orcid", ""));
    }

    @Test
    public void haveMemberPushedWorksOrAffiliationsToRecordDelegatesWhenValid() {
        when(profileDao.haveMemberPushedWorksOrAffiliationsToRecord("orcid", "client")).thenReturn(true);
        assertTrue(manager.haveMemberPushedWorksOrAffiliationsToRecord("orcid", "client"));
    }

    @Test
    public void hasTokenDelegatesToReadOnlyDao() {
        when(orcidOauth2TokenDetailDaoReadOnly.hasToken("orcid")).thenReturn(true);
        assertTrue(manager.hasToken("orcid", 123L));
    }

    @Test
    public void orcidExistsDelegates() {
        when(profileDao.orcidExists("orcid")).thenReturn(true);
        assertTrue(manager.orcidExists("orcid"));
    }

    @Test
    public void hasBeenGivenPermissionToDelegates() {
        when(profileDao.hasBeenGivenPermissionTo("g", "r")).thenReturn(true);
        assertTrue(manager.hasBeenGivenPermissionTo("g", "r"));
    }

    @Test
    public void findByCreditNameReturnsNullWhenNameMissing() {
        when(recordNameManagerV3.findByCreditName("credit")).thenReturn(null);
        assertNull(manager.findByCreditName("credit"));
    }

    @Test
    public void findByCreditNameReturnsPathWhenFound() {
        Name name = new Name();
        name.setPath("0000-0000-0000-0001");
        when(recordNameManagerV3.findByCreditName("credit")).thenReturn(name);
        assertEquals("0000-0000-0000-0001", manager.findByCreditName("credit"));
    }

    @Test
    public void deprecateProfileReturnsFalseWhenDaoDoesNotDeprecate() {
        when(profileDao.deprecateProfile("d", "p", "method", "admin")).thenReturn(false);
        assertFalse(manager.deprecateProfile("d", "p", "method", "admin"));
    }

    @Test
    public void deprecateProfileMovesEmailsDomainsClearsRecordAndRecordsHistoryEvent() {
        when(profileDao.deprecateProfile("d", "p", "method", "admin")).thenReturn(true);
        Emails emails = new Emails();
        emails.setEmails(Arrays.asList(email("a@test.org"), email("b@test.org")));
        when(emailManager.getEmails("d")).thenReturn(emails);
        when(profileEmailDomainManager.getEmailDomains("d")).thenReturn(Arrays.asList(emailDomain("test.org")));
        when(profileDao.updateDefaultVisibility("d", org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name())).thenReturn(true);
        when(recordNameManagerV3.exists("d")).thenReturn(true);
        when(biographyManager.exists("d")).thenReturn(false);

        assertTrue(manager.deprecateProfile("d", "p", "method", "admin"));

        verify(emailManager).moveEmailToOtherAccount("a@test.org", "d", "p");
        verify(emailManager).moveEmailToOtherAccount("b@test.org", "d", "p");
        verify(profileEmailDomainManager).moveEmailDomainToAnotherAccount("test.org", "d", "p");
        verify(orcidOauth2TokenDetailDao, never()).disableAccessTokenByUserOrcid(anyString(), anyString());
        verify(profileHistoryEventManager).recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, "d", "deactivated/deprecated");
        verify(profileLastModifiedDao).updateLastModifiedDateAndIndexingStatus("d", IndexingStatus.REINDEX);
    }

    @Test
    public void deprecateProfileSkipsHistoryEventWhenDefaultVisibilityNotUpdated() {
        when(profileDao.deprecateProfile("d", "p", "method", "admin")).thenReturn(true);
        when(emailManager.getEmails("d")).thenReturn(new Emails());
        when(profileEmailDomainManager.getEmailDomains("d")).thenReturn(new ArrayList<ProfileEmailDomainEntity>());
        when(profileDao.updateDefaultVisibility("d", org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name())).thenReturn(false);
        when(recordNameManagerV3.exists("d")).thenReturn(false);
        when(biographyManager.exists("d")).thenReturn(false);

        manager.deprecateProfile("d", "p", "method", "admin");

        verify(profileHistoryEventManager, never()).recordEvent(any(ProfileHistoryEventType.class), anyString(), anyString());
    }

    @Test
    public void deactivateRecordClearsRecordHidesEmailsAndDisablesTokens() {
        when(profileDao.updateDefaultVisibility("orcid", org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name())).thenReturn(true);
        when(recordNameManagerV3.exists("orcid")).thenReturn(false);
        when(biographyManager.exists("orcid")).thenReturn(false);

        assertTrue(manager.deactivateRecord("orcid"));

        verify(emailManager).hideAllEmails("orcid");
        verify(profileDao).deactivate("orcid");
        verify(orcidOauth2TokenDetailDao).disableAccessTokenByUserOrcid("orcid", RevokeReason.RECORD_DEACTIVATED.name());
        verify(profileHistoryEventManager).recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, "orcid", "deactivated/deprecated");
    }

    @Test
    public void enableDeveloperToolsDelegates() {
        when(profileDao.updateDeveloperTools("orcid", true)).thenReturn(true);
        assertTrue(manager.enableDeveloperTools("orcid"));
    }

    @Test
    public void disableDeveloperToolsDelegates() {
        when(profileDao.updateDeveloperTools("orcid", false)).thenReturn(true);
        assertTrue(manager.disableDeveloperTools("orcid"));
    }

    @Test
    public void isProfileClaimedDelegates() {
        when(profileDao.getClaimedStatus("orcid")).thenReturn(true);
        assertTrue(manager.isProfileClaimed("orcid"));
    }

    @Test
    public void getGroupTypeDelegates() {
        MemberType expected = MemberType.values()[0];
        when(profileDao.getGroupType("orcid")).thenReturn(expected.name());
        assertEquals(expected, manager.getGroupType("orcid"));
    }

    @Test
    public void getGroupTypeThrowsForInvalidType() {
        when(profileDao.getGroupType("orcid")).thenReturn("INVALID");
        assertThrows(IllegalArgumentException.class, () -> manager.getGroupType("orcid"));
    }

    @Test
    public void updateLastModifedAndIndexingStatusDelegatesToAspect() {
        manager.updateLastModifedAndIndexingStatus("orcid");
        verify(profileLastModifiedAspect).updateLastModifiedDateAndIndexingStatus("orcid");
    }

    @Test
    public void updateLastModifedDelegatesToAspect() {
        manager.updateLastModifed("orcid");
        verify(profileLastModifiedAspect).updateLastModifiedDate("orcid");
    }

    @Test
    public void isDeactivatedDelegates() {
        when(profileDao.isDeactivated("orcid")).thenReturn(true);
        assertTrue(manager.isDeactivated("orcid"));
    }

    @Test
    public void isReviewedDelegates() {
        when(profileDao.isReviewed("orcid")).thenReturn(true);
        assertTrue(manager.isReviewed("orcid"));
    }

    @Test
    public void reviewProfileDelegates() {
        when(profileDao.reviewProfile("orcid")).thenReturn(true);
        assertTrue(manager.reviewProfile("orcid"));
    }

    @Test
    public void unreviewProfileDelegates() {
        when(profileDao.unreviewProfile("orcid")).thenReturn(true);
        assertTrue(manager.unreviewProfile("orcid"));
    }

    @Test
    public void getApplicationsFiltersDisabledOboAndNullClientsAndSorts() {
        OrcidOauth2TokenDetail t1 = token("clientB", "/read-limited", new Date(2000), false, null);
        OrcidOauth2TokenDetail t2 = token("clientA", "/activities/update", new Date(3000), false, null);
        OrcidOauth2TokenDetail t3 = token("clientA", "/person/read-limited", new Date(1000), false, null);
        OrcidOauth2TokenDetail disabled = token("clientA", "/orcid-profile/read-limited", new Date(1000), true, null);
        OrcidOauth2TokenDetail obo = token("clientB", "/orcid-profile/read-limited", new Date(1000), false, "obo-client");
        OrcidOauth2TokenDetail noClient = token("missing", "/read-limited", new Date(1000), false, null);
        when(orcidOauth2TokenDetailDaoReadOnly.findByUserName("orcid")).thenReturn(Arrays.asList(t1, t2, t3, disabled, obo, noClient));

        when(clientDetailsEntityCacheManager.retrieve("clientA")).thenReturn(client("clientA", "Alpha", "https://a.test"));
        when(clientDetailsEntityCacheManager.retrieve("clientB")).thenReturn(client("clientB", "beta", "https://b.test"));
        when(clientDetailsEntityCacheManager.retrieve("missing")).thenReturn(null);
        when(localeManager.resolveMessage(anyString())).thenAnswer(i -> (String) i.getArguments()[0]);

        List<ApplicationSummary> apps = manager.getApplications("orcid");

        assertEquals(2, apps.size());
        assertEquals("clientA", apps.get(0).getClientId());
        assertEquals("clientB", apps.get(1).getClientId());
        assertEquals(new Date(1000), apps.get(0).getApprovalDate());
        assertTrue(apps.get(0).getScopePaths().containsKey(ScopePathType.ACTIVITIES_UPDATE.toString()));
        assertTrue(apps.get(0).getScopePaths().containsKey(ScopePathType.PERSON_READ_LIMITED.toString()));
    }

    @Test
    public void getApplicationsSkipsScopeLabelsMissingFromMessages() {
        OrcidOauth2TokenDetail token = token("clientA", "/activities/update", new Date(1000), false, null);
        when(orcidOauth2TokenDetailDaoReadOnly.findByUserName("orcid")).thenReturn(Arrays.asList(token));
        when(clientDetailsEntityCacheManager.retrieve("clientA")).thenReturn(client("clientA", "Alpha", "https://a.test"));
        when(localeManager.resolveMessage(anyString())).thenThrow(new NoSuchMessageException("missing"));

        List<ApplicationSummary> apps = manager.getApplications("orcid");

        assertEquals(1, apps.size());
        assertTrue(apps.get(0).getScopePaths().isEmpty());
    }

    @Test
    public void getOrcidHashReturnsNullForBlankInput() {
        assertNull(manager.getOrcidHash(null));
        assertNull(manager.getOrcidHash(" "));
    }

    @Test
    public void getOrcidHashDelegatesToEncryptionManager() throws Exception {
        when(encryptionManager.sha256Hash("value")).thenReturn("hash");
        assertEquals("hash", manager.getOrcidHash("value"));
    }

    @Test
    public void getOrcidHashWrapsNoSuchAlgorithmException() throws Exception {
        when(encryptionManager.sha256Hash("value")).thenThrow(new NoSuchAlgorithmException("nope"));
        assertThrows(RuntimeException.class, () -> manager.getOrcidHash("value"));
    }

    @Test
    public void retrivePublicDisplayNameReturnsEmptyWhenNoPublicName() {
        when(recordNameManagerReadOnlyV3.fetchDisplayablePublicName("orcid")).thenReturn(null);
        assertEquals("", manager.retrivePublicDisplayName("orcid"));
    }

    @Test
    public void retrivePublicDisplayNameReturnsNameWhenPresent() {
        when(recordNameManagerReadOnlyV3.fetchDisplayablePublicName("orcid")).thenReturn("Public Name");
        assertEquals("Public Name", manager.retrivePublicDisplayName("orcid"));
    }

    @Test
    public void claimProfileAndUpdatePreferencesThrowsWhenEmailVerificationFails() {
        when(emailManager.verifySetCurrentAndPrimary("orcid", "a@b.com")).thenReturn(false);
        assertThrows(InvalidParameterException.class, () -> manager.claimProfileAndUpdatePreferences("orcid", "a@b.com", AvailableLocales.EN, claim(false)));
    }

    @Test
    public void claimProfileAndUpdatePreferencesUpdatesProfileAndVisibilitiesAndCreatesFrequency() {
        Claim claim = claim(false);
        when(emailManager.verifySetCurrentAndPrimary("orcid", "a@b.com")).thenReturn(true);
        ProfileEntity profile = new ProfileEntity();
        when(profileDao.find("orcid")).thenReturn(profile);
        when(encryptionManager.hashForInternalUse("password#1")).thenReturn("enc");
        when(biographyManager.exists("orcid")).thenReturn(true);
        Biography bio = new Biography();
        bio.setVisibility(Visibility.PUBLIC);
        when(biographyManager.getBiography("orcid")).thenReturn(bio);
        when(emailFrequencyManager.emailFrequencyExists("orcid")).thenReturn(false);

        assertTrue(manager.claimProfileAndUpdatePreferences("orcid", "a@b.com", AvailableLocales.EN, claim));

        assertTrue(profile.getClaimed());
        assertEquals(IndexingStatus.REINDEX, profile.getIndexingStatus());
        assertEquals("enc", profile.getEncryptedPassword());
        assertEquals(AvailableLocales.EN.name(), profile.getLocale());
        assertEquals(Visibility.PRIVATE.name(), profile.getActivitiesVisibilityDefault());
        verify(addressDao).updateVisibility("orcid", Visibility.PRIVATE);
        verify(profileKeywordDao).updateVisibility("orcid", Visibility.PRIVATE);
        verify(otherNameDao).updateVisibility("orcid", Visibility.PRIVATE);
        verify(researcherUrlDao).updateVisibility("orcid", Visibility.PRIVATE);
        verify(externalIdentifierDao).updateVisibility("orcid", Visibility.PRIVATE);
        verify(biographyManager).updateBiography(eq("orcid"), any(Biography.class));
        verify(emailFrequencyManager).createOnClaim("orcid", false);
    }

    @Test
    public void claimProfileAndUpdatePreferencesUpdatesExistingFrequencySetting() {
        Claim claim = claim(true);
        when(emailManager.verifySetCurrentAndPrimary("orcid", "a@b.com")).thenReturn(true);
        when(profileDao.find("orcid")).thenReturn(new ProfileEntity());
        when(encryptionManager.hashForInternalUse("password#1")).thenReturn("enc");
        when(biographyManager.exists("orcid")).thenReturn(false);
        when(emailFrequencyManager.emailFrequencyExists("orcid")).thenReturn(true);

        manager.claimProfileAndUpdatePreferences("orcid", "a@b.com", null, claim);

        verify(emailFrequencyManager).updateSendQuarterlyTips("orcid", true);
        verify(emailFrequencyManager, never()).createOnClaim(anyString(), anyBoolean());
    }

    @Test
    public void updateLocaleDelegates() {
        manager.updateLocale("orcid", AvailableLocales.EN);
        verify(profileDao).updateLocale("orcid", AvailableLocales.EN.name());
    }

    @Test
    public void isProfileClaimedByEmailUsesEmailHashFromManager() {
        Map<String, String> keys = new HashMap<String, String>();
        keys.put(EmailManager.HASH, "email-hash");
        when(emailManager.getEmailKeys("a@b.com")).thenReturn(keys);
        when(profileDao.getClaimedStatusByEmailHash("email-hash")).thenReturn(true);

        assertTrue(manager.isProfileClaimedByEmail("a@b.com"));
    }

    @Test
    public void reactivateFromAdminPathClearsAdditionalEmails() {
        ProfileEntity profile = new ProfileEntity();
        profile.setDeactivationDate(new Date());
        when(profileDao.find("orcid")).thenReturn(profile);

        List<String> toNotify = manager.reactivate("orcid", " primary@test.org ", null);

        assertTrue(toNotify.isEmpty());
        verify(emailManager).reactivatePrimaryEmail("orcid", "primary@test.org");
        verify(profileEmailDomainManager).processDomain("orcid", "primary@test.org");
        verify(emailManager).clearEmailsAfterReactivation("orcid");
        assertNull(profile.getDeactivationDate());
        assertTrue(profile.getClaimed());
        assertEquals(IndexingStatus.PENDING, profile.getIndexingStatus());
    }

    @Test
    public void reactivateFromUserPathRecreatesAdditionalEmailsUpdatesNameAndReturnsNotifyList() {
        Reactivation reactivation = reactivation();
        ProfileEntity profile = new ProfileEntity();
        profile.setDeactivationDate(new Date());
        when(profileDao.find("orcid")).thenReturn(profile);
        when(encryptionManager.hashForInternalUse("reactivation#1")).thenReturn("encrypted");
        Name existingName = new Name();
        when(recordNameManagerReadOnlyV3.getRecordName("orcid")).thenReturn(existingName);
        when(emailManager.reactivateOrCreate("orcid", "add1@test.org", Visibility.PUBLIC)).thenReturn(true);
        when(emailManager.reactivateOrCreate("orcid", "add2@test.org", Visibility.PUBLIC)).thenReturn(false);

        List<String> toNotify = manager.reactivate("orcid", "primary@test.org", reactivation);

        assertEquals(1, toNotify.size());
        assertEquals("add1@test.org", toNotify.get(0));
        assertEquals("encrypted", profile.getEncryptedPassword());
        assertEquals(Visibility.PUBLIC.name(), profile.getActivitiesVisibilityDefault());
        assertEquals("Given", existingName.getGivenNames().getContent());
        assertEquals("Family", existingName.getFamilyName().getContent());
        verify(recordNameManagerV3).updateRecordName("orcid", existingName);
        verify(emailManager).clearEmailsAfterReactivation("orcid");
    }

    @Test
    public void updatePasswordHashesAndPersists() {
        when(encryptionManager.hashForInternalUse("password")).thenReturn("encrypted");

        manager.updatePassword("orcid", "password");

        verify(profileDao).changeEncryptedPassword("orcid", "encrypted");
        verify(profileHistoryEventManager, never()).recordEvent(any(ProfileHistoryEventType.class), anyString(), anyString());
    }

    @Test
    public void isProfileDeprecatedDelegates() {
        when(profileDao.isProfileDeprecated("orcid")).thenReturn(true);
        assertTrue(manager.isProfileDeprecated("orcid"));
    }

    @Test
    public void updateLastLoginDetailsDelegates() {
        manager.updateLastLoginDetails("orcid", "127.0.0.1");
        verify(profileDao).updateLastLoginDetails("orcid", "127.0.0.1");
    }

    @Test
    public void retrieveLocaleConvertsStringToEnum() {
        when(profileDao.retrieveLocale("orcid")).thenReturn("EN");
        assertEquals(AvailableLocales.EN, manager.retrieveLocale("orcid"));
    }

    @Test
    public void lockProfileDelegates() {
        when(profileDao.lockProfile("orcid", "reason", "desc", "admin")).thenReturn(true);
        assertTrue(manager.lockProfile("orcid", "reason", "desc", "admin"));
    }

    @Test
    public void unlockProfileDelegates() {
        when(profileDao.unlockProfile("orcid")).thenReturn(true);
        assertTrue(manager.unlockProfile("orcid"));
    }

    @Test
    public void getLastLoginDelegates() {
        Date now = new Date();
        when(profileDao.getLastLogin("orcid")).thenReturn(now);
        assertEquals(now, manager.getLastLogin("orcid"));
    }

    @Test
    public void startSigninLockDelegates() {
        manager.startSigninLock("orcid");
        verify(profileDao).startSigninLock("orcid");
    }

    @Test
    public void resetSigninLockDelegates() {
        manager.resetSigninLock("orcid");
        verify(profileDao).resetSigninLock("orcid");
    }

    @Test
    public void updateSigninLockDelegates() {
        manager.updateSigninLock("orcid", 3);
        verify(profileDao).updateSigninLock("orcid", 3);
    }

    @Test
    public void getSigninLockDelegates() {
        List<Object[]> values = new ArrayList<Object[]>();
        values.add(new Object[] { "orcid", 3 });
        when(profileDao.getSigninLock("orcid")).thenReturn(values);
        assertEquals(values, manager.getSigninLock("orcid"));
    }

    @Test
    public void updateDeprecationDelegates() {
        when(profileDao.updateDeprecation("deprecated", "primary")).thenReturn(true);
        assertTrue(manager.updateDeprecation("deprecated", "primary"));
    }

    private void inject(Class<?> declaringClass, String field, Object value) {
        try {
            Field f = declaringClass.getDeclaredField(field);
            f.setAccessible(true);
            f.set(manager, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field " + field + " in " + declaringClass.getName(), e);
        }
    }

    private Email email(String value) {
        Email e = new Email();
        e.setEmail(value);
        return e;
    }

    private ProfileEmailDomainEntity emailDomain(String domain) {
        ProfileEmailDomainEntity entity = new ProfileEmailDomainEntity();
        entity.setEmailDomain(domain);
        return entity;
    }

    private Claim claim(boolean sendOrcidNews) {
        Claim claim = new Claim();
        claim.getPassword().setValue("password#1");
        org.orcid.pojo.ajaxForm.Visibility defaultVisibility = org.orcid.pojo.ajaxForm.Visibility.valueOf(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        claim.setActivitiesVisibilityDefault(defaultVisibility);
        Checkbox sendNews = new Checkbox();
        sendNews.setValue(sendOrcidNews);
        claim.setSendOrcidNews(sendNews);
        return claim;
    }

    private Reactivation reactivation() {
        Reactivation r = new Reactivation();
        r.getPassword().setValue("reactivation#1");
        r.getGivenNames().setValue("Given");
        r.getFamilyNames().setValue("Family");
        org.orcid.pojo.ajaxForm.Visibility defaultVisibility = org.orcid.pojo.ajaxForm.Visibility.valueOf(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        r.setActivitiesVisibilityDefault(defaultVisibility);
        r.setEmailsAdditional(Arrays.asList(Text.valueOf(" add1@test.org "), Text.valueOf("add2@test.org"), new Text()));
        return r;
    }

    private OrcidOauth2TokenDetail token(String clientId, String scope, Date created, boolean disabled, String oboClientId) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId(clientId);
        token.setScope(scope);
        token.setTokenDisabled(disabled);
        token.setOboClientDetailsId(oboClientId);
        setEntityDateCreated(token, created);
        return token;
    }

    private ClientDetailsEntity client(String id, String name, String website) {
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setId(id);
        client.setClientName(name);
        client.setClientWebsite(website);
        return client;
    }

    private void setEntityDateCreated(Object entity, Date created) {
        try {
            Field f = BaseEntity.class.getDeclaredField("dateCreated");
            f.setAccessible(true);
            f.set(entity, created);
        } catch (Exception e) {
            throw new RuntimeException("Unable to set dateCreated", e);
        }
    }
}
