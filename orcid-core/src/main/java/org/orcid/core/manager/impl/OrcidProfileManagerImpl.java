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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.manager.OrcidProfileCleaner;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.security.visibility.aop.VisibilityControl;
import org.orcid.core.utils.OrcidJaxbCopyUtils;
import org.orcid.core.utils.ReleaseNameUtils;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EncryptedPassword;
import org.orcid.jaxb.model.message.EncryptedSecurityAnswer;
import org.orcid.jaxb.model.message.EncryptedVerificationCode;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The profile manager is responsible for passing onto the persistence layer
 * after performing any data manipulation necessary before doing so.
 * <p/>
 * One of its functions is to persist visibilty options set by the user and/or
 * client. As this is VERY sensitive it is implied that any security has been
 * performed already, thus if a method is called within this class it is will
 * first check for visibility within the updated object, and persist them. If
 * none are found in the updated object, the existing visibility options are
 * used. If neither are present, the defaults are used.
 * 
 * @author Declan Newman and Will Simpson
 */

public class OrcidProfileManagerImpl implements OrcidProfileManager {

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    private JpaJaxbEntityAdapter adapter;

    @Resource
    private OrcidIndexManager orcidIndexManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidProfileCleaner orcidProfileCleaner;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "profileCache")
    private Cache profileCache;

    private int claimWaitPeriodDays = 10;

    private int claimReminderAfterDays = 8;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private ConcurrentMap<String, Object> readLocks = new ConcurrentHashMap<>();

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    private int numberOfIndexingThreads;

    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileManagerImpl.class);

    private static final int INDEXING_BATCH_SIZE = 100;

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setOrcidIndexManager(OrcidIndexManager orcidIndexManager) {
        this.orcidIndexManager = orcidIndexManager;
    }

    public void setNumberOfIndexingThreads(int numberOfIndexingThreads) {
        this.numberOfIndexingThreads = numberOfIndexingThreads;
    }

    public void setClaimWaitPeriodDays(int claimWaitPeriodDays) {
        this.claimWaitPeriodDays = claimWaitPeriodDays;
    }

    public void setClaimReminderAfterDays(int claimReminderAfterDays) {
        this.claimReminderAfterDays = claimReminderAfterDays;
    }

    @Override
    @Transactional
    public OrcidProfile createOrcidProfile(OrcidProfile orcidProfile) {
        if (orcidProfile.getOrcid() == null) {
            orcidProfile.setOrcid(orcidGenerationManager.createNewOrcid());
        }

        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile);
        encryptAndMapFieldsForProfileEntityPersistence(orcidProfile, profileEntity);
        profileEntity.setAuthorities(getGrantedAuthorities(profileEntity));

        profileDao.persist(profileEntity);
        profileDao.flush();
        OrcidProfile updatedTranslatedOrcid = adapter.toOrcidProfile(profileEntity);
        return updatedTranslatedOrcid;
    }

    @Override
    public OrcidProfile createOrcidProfileAndNotify(OrcidProfile orcidProfile) {
        OrcidProfile createdOrcidProfile = createOrcidProfile(orcidProfile);
        notificationManager.sendApiRecordCreationEmail(
                orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), orcidProfile);
        return createdOrcidProfile;
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidProfile(OrcidProfile orcidProfile) {
        String amenderOrcid = retrieveAmenderOrcid();
        ProfileEntity existingProfileEntity = profileDao.find(orcidProfile.getOrcid().getValue());
        if (existingProfileEntity != null) {
            profileDao.removeChildrenWithGeneratedIds(existingProfileEntity);
            setWorkPrivacy(orcidProfile, existingProfileEntity.getWorkVisibilityDefault());
        }
        dedupeProfileWorks(orcidProfile);
        addSourceToEmails(orcidProfile, existingProfileEntity, amenderOrcid);
        addSourceToWorks(orcidProfile, amenderOrcid);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile, existingProfileEntity);
        profileEntity.setLastModified(new Date());
        profileEntity.setIndexingStatus(IndexingStatus.PENDING);
        ProfileEntity updatedProfileEntity = profileDao.merge(profileEntity);
        profileDao.flush();
        profileDao.refresh(updatedProfileEntity);
        OrcidProfile updatedOrcidProfile = convertToOrcidProfile(updatedProfileEntity, LoadOptions.ALL);
        putInCache(updatedOrcidProfile);
        notificationManager.sendAmendEmail(updatedOrcidProfile, amenderOrcid);
        return updatedOrcidProfile;
    }

    private String retrieveAmenderOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
            return authorizationRequest.getClientId();
        }
        if (OrcidProfileUserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            return ((OrcidProfileUserDetails) authentication.getPrincipal()).getRealOrcid();
        }
        return null;
    }

    /**
     * Preserves existing source for existing emails, and adds specified source
     * for new emails
     * 
     * @param orcidProfile
     *            The incoming profile
     * @param existingProfileEntity
     *            The existing profile entity from the DB
     * @param amenderOrcid
     *            The source of new emails (from the security context)
     */
    private void addSourceToEmails(OrcidProfile orcidProfile, ProfileEntity existingProfileEntity, String amenderOrcid) {
        Map<String, EmailEntity> existingMap = new HashMap<>();
        Set<EmailEntity> existingEmails = existingProfileEntity.getEmails();
        if (existingEmails != null) {
            existingMap = EmailEntity.mapByLowerCaseEmail(existingEmails);
        }
        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        if (orcidBio != null) {
            ContactDetails contactDetails = orcidBio.getContactDetails();
            if (contactDetails != null) {
                for (Email email : contactDetails.getEmail()) {
                    EmailEntity existingEmail = existingMap.get(email.getValue().toLowerCase());
                    if (existingEmail == null) {
                        email.setSource(amenderOrcid);
                    } else {
                        ProfileEntity existingSource = existingEmail.getSource();
                        if (existingSource != null) {
                            email.setSource(existingSource.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * Add source to the profile works
     * 
     * @param orcidProfile
     *            The profile
     * @param amenderOrcid
     *            The orcid of the user or client that add the work to the
     *            profile user
     * */
    private void addSourceToWorks(OrcidProfile orcidProfile, String amenderOrcid) {
        OrcidWorks orcidWorks = orcidProfile.getOrcidActivities() == null ? null : orcidProfile.getOrcidActivities().getOrcidWorks();

        if (orcidWorks != null && !orcidWorks.getOrcidWork().isEmpty()) {
            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                if (orcidWork.getWorkSource() == null || StringUtils.isEmpty(orcidWork.getWorkSource().getContent()))
                    orcidWork.setWorkSource(new WorkSource(amenderOrcid));
            }
        }

    }

    private void setWorkPrivacy(OrcidProfile updatedOrcidProfile, Visibility defaultWorkVisibility) {
        OrcidHistory orcidHistory = updatedOrcidProfile.getOrcidHistory();
        boolean isClaimed = orcidHistory != null ? orcidHistory.getClaimed().isValue() : false;
        OrcidActivities incomingActivities = updatedOrcidProfile.getOrcidActivities();
        if (incomingActivities != null) {
            OrcidWorks incomingWorks = incomingActivities.getOrcidWorks();
            if (incomingWorks != null) {
                for (OrcidWork incomingWork : incomingWorks.getOrcidWork()) {
                    if (StringUtils.isBlank(incomingWork.getPutCode())) {
                        Visibility incomingWorkVisibility = incomingWork.getVisibility();
                        if (isClaimed) {
                            if (defaultWorkVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                                incomingWork.setVisibility(defaultWorkVisibility);
                            }
                        } else if (incomingWorkVisibility == null) {
                            incomingWork.setVisibility(Visibility.PRIVATE);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the orcid external identifiers given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedExternalIdentifiers(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToExternalIdentifiersOnly();
        }
        return profile;
    }

    /**
     * Retrieves the orcid bio given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the bio populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidBio(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToBioOnly();
        }
        return profile;
    }

    /**
     * Retrieves the orcid works given an identifier
     * 
     * @param orcid
     *            the identifier
     * @return the orcid profile with only the works populated
     */
    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidWorks(String orcid) {
        OrcidProfile profile = retrieveClaimedOrcidProfile(orcid);
        if (profile != null) {
            profile.downgradeToWorksOnly();
        }
        return profile;
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfile(String orcid) {
        return retrieveOrcidProfile(orcid, LoadOptions.ALL);
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfile(String orcid, LoadOptions loadOptions) {
        OrcidProfile cachedProfile = lockAndRetrieveFromCache(orcid, loadOptions);
        Date cachedProfileLastModified = extractLastModifiedDateFromObject(cachedProfile);
        if (cachedProfileLastModified == null) {
            return clearCacheAndRetrieve(orcid, loadOptions);
        }
        Date actualLastModified = retrieveLastModifiedDate(orcid);
        if (actualLastModified == null) {
            return clearCacheAndRetrieve(orcid, loadOptions);
        }
        if (actualLastModified.after(cachedProfileLastModified)) {
            return clearCacheAndRetrieve(orcid, loadOptions);
        }
        return cachedProfile;
    }

    private OrcidProfile lockAndRetrieveFromCache(String orcid, LoadOptions loadOptions) {
        Element element = getFromCache(orcid);
        if (element == null) {
            try {
                Object lock = obtainReadLock(orcid);
                synchronized (lock) {
                    // Might be in the cache by now!
                    element = getFromCache(orcid);
                    if (element == null) {
                        OrcidProfile freshOrcidProfile = retrieveFreshOrcidProfile(orcid, loadOptions);
                        return freshOrcidProfile;
                    }
                }
            } finally {
                releaseReadLock(orcid);
            }
        }
        OrcidProfile cachedProfile = (OrcidProfile) element.getObjectValue();
        return cachedProfile;
    }

    private Object obtainReadLock(String orcid) {
        Object newLock = new Object();
        Object existingLock = readLocks.putIfAbsent(orcid, newLock);
        return existingLock == null ? newLock : existingLock;
    }

    private void releaseReadLock(String orcid) {
        readLocks.remove(orcid);
    }

    private OrcidProfile clearCacheAndRetrieve(String orcid, LoadOptions loadOptions) {
        profileCache.remove(createCacheKey(orcid));
        return lockAndRetrieveFromCache(orcid, loadOptions);
    }

    private OrcidProfile retrieveFreshOrcidProfile(String orcid, LoadOptions loadOptions) {
        profileDao.flush();
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            OrcidProfile freshOrcidProfile = convertToOrcidProfile(profileEntity, loadOptions);
            if (LoadOptions.ALL.equals(loadOptions)) {
                putInCache(orcid, freshOrcidProfile);
            }
            return freshOrcidProfile;
        }
        return null;
    }

    private Date extractLastModifiedDateFromObject(OrcidProfile orcidProfile) {
        OrcidHistory orcidHistory = orcidProfile.getOrcidHistory();
        if (orcidHistory == null) {
            return null;
        }
        LastModifiedDate lastModifiedDate = orcidHistory.getLastModifiedDate();
        if (lastModifiedDate == null) {
            return null;
        }
        return lastModifiedDate.getValue().toGregorianCalendar().getTime();
    }

    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidProfile(String orcid) {
        OrcidProfile orcidProfile = retrieveOrcidProfile(orcid);
        if (orcidProfile != null) {
            if (Boolean.TRUE.equals(orcidProfile.getOrcidHistory().getClaimed().isValue()) || isOldEnough(orcidProfile) || isBeingAccessedByCreator(orcidProfile)
                    || haveSystemRole()) {
                return orcidProfile;
            } else {
                return createReservedForClaimOrcidProfile(orcid);
            }
        }
        return null;
    }

    private boolean isOldEnough(OrcidProfile orcidProfile) {
        return DateUtils.olderThan(orcidProfile.getOrcidHistory().getSubmissionDate().getValue().toGregorianCalendar().getTime(), claimWaitPeriodDays);
    }

    private boolean isBeingAccessedByCreator(OrcidProfile orcidProfile) {
        String amenderOrcid = retrieveAmenderOrcid();
        Source source = orcidProfile.getOrcidHistory().getSource();
        if (NullUtils.noneNull(amenderOrcid, source)) {
            return amenderOrcid.equals(source.getSourceOrcid().getValue());
        }
        return false;
    }

    private boolean haveSystemRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                return authorities.contains(new SimpleGrantedAuthority("ROLE_SYSTEM"));
            }
        }
        return false;
    }

    private OrcidProfile createReservedForClaimOrcidProfile(String orcid) {
        OrcidProfile op = new OrcidProfile();
        op.setOrcid(orcid);
        OrcidHistory oh = new OrcidHistory();
        oh.setClaimed(new Claimed(false));
        op.setOrcidHistory(oh);
        GivenNames gn = new GivenNames();
        PersonalDetails pd = new PersonalDetails();
        gn.setContent(localeManager.resolveMessage("orcid.reserved_for_claim"));
        pd.setGivenNames(gn);
        OrcidBio ob = new OrcidBio();
        ob.setPersonalDetails(pd);
        op.setOrcidBio(ob);
        return op;
    }

    private OrcidProfile convertToOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions) {
        profileDao.refresh(profileEntity);
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, loadOptions);
        String verificationCode = profileEntity.getEncryptedVerificationCode();
        String securityAnswer = profileEntity.getEncryptedSecurityAnswer();
        orcidProfile.setVerificationCode(decrypt(verificationCode));
        orcidProfile.setSecurityQuestionAnswer(decrypt(securityAnswer));
        return orcidProfile;
    }

    @Override
    @VisibilityControl(removeAttributes = false, visibilities = Visibility.PUBLIC)
    public OrcidProfile retrievePublicOrcidProfile(String orcid) {
        return retrieveClaimedOrcidProfile(orcid);
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfileByEmail(String email) {
        EmailEntity emailEntity = emailDao.findCaseInsensitive(email);
        if (emailEntity != null) {
            ProfileEntity profileEntity = emailEntity.getProfile();
            OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
            String verificationCode = profileEntity.getEncryptedVerificationCode();
            String securityAnswer = profileEntity.getEncryptedSecurityAnswer();
            orcidProfile.setVerificationCode(decrypt(verificationCode));
            orcidProfile.setSecurityQuestionAnswer(decrypt(securityAnswer));
            return orcidProfile;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the orcid profile given an identifier, without any personal
     * internal data.
     * <p/>
     * Specifically, this is for use by tier 1
     * 
     * @param orcid
     *            the identifier
     * @return the full orcid profile
     */
    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfileWithNoInternal(String orcid) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            return adapter.toOrcidProfile(profileEntity);
        } else {
            return null;
        }
    }

    /**
     * Updates the ORCID works only
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile updateOrcidWorks(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());

        if (existingProfile == null) {
            return null;
        }

        existingProfile.setOrcidWorks(updatedOrcidProfile.retrieveOrcidWorks());
        return updateOrcidProfile(existingProfile);
    }

    /**
     * Add new external identifiers to an existing profile
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile addExternalIdentifiers(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());

        if (existingProfile != null && existingProfile.getOrcidBio() != null) {
            OrcidBio orcidBio = existingProfile.getOrcidBio();
            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();

            if (externalIdentifiers == null) {
                orcidBio.setExternalIdentifiers(new ExternalIdentifiers());
            }
            ExternalIdentifiers externalIdentifier = updatedOrcidProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> updatedExternalIdentifiers = externalIdentifier.getExternalIdentifier();
            List<ExternalIdentifier> existingExternalIdentifiers = orcidBio.getExternalIdentifiers().getExternalIdentifier();

            // Copy all the existing external identifiers to the updated profile
            for (ExternalIdentifier ei : existingExternalIdentifiers) {
                updatedExternalIdentifiers.add(ei);
            }

            OrcidJaxbCopyUtils.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(orcidBio, updatedOrcidProfile.getOrcidBio());

            return updateOrcidProfile(existingProfile);
        } else {
            return null;
        }
    }

    /**
     * Updates the ORCID bio data
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile updateOrcidBio(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }
        // preserve the visibility settings
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingProfile.getOrcidBio(), updatedOrcidProfile.getOrcidBio());
        existingProfile.setOrcidBio(updatedOrcidProfile.getOrcidBio());
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updatePersonalInformation(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());

        if (existingProfile == null) {
            return null;
        }

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingProfile.getOrcidBio(), updatedOrcidProfile.getOrcidBio());
        OrcidJaxbCopyUtils.copyUpdatedWorksVisibilityInformationOnlyPreservingVisbility(existingProfile.retrieveOrcidWorks(), updatedOrcidProfile.retrieveOrcidWorks());
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidHistory(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());

        if (existingProfile == null) {
            return null;
        }
        OrcidJaxbCopyUtils.copyRelevantUpdatedHistoryElements(existingProfile.getOrcidHistory(), updatedOrcidProfile.getOrcidHistory());
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingProfile.getOrcidBio(), updatedOrcidProfile.getOrcidBio());
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updateAffiliations(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingProfile.getOrcidBio(), updatedOrcidProfile.getOrcidBio());
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updatePasswordInformation(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null || existingProfile.getOrcidInternal() == null) {
            // Nothing we can do with this. Just return null
            return null;
        }
        OrcidInternal orcidInternal = updatedOrcidProfile.getOrcidInternal();
        if (orcidInternal.getSecurityDetails() == null) {
            orcidInternal.setSecurityDetails(new SecurityDetails());
        }
        orcidInternal.getSecurityDetails().setEncryptedPassword(new EncryptedPassword(hash(updatedOrcidProfile.getPassword())));
        existingProfile = updatePasswordSecurityQuestionsInformation(updatedOrcidProfile);
        existingProfile.setOrcidInternal(orcidInternal);
        return updateOrcidProfile(existingProfile);
    }

    @Override
    public OrcidProfile updatePasswordSecurityQuestionsInformation(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null || existingProfile.getOrcidInternal() == null) {
            // Nothing we can do with this. Just return null
            return null;
        }
        OrcidInternal orcidInternal = updatedOrcidProfile.getOrcidInternal();
        if (orcidInternal.getSecurityDetails() == null) {
            orcidInternal.setSecurityDetails(new SecurityDetails());
        }

        orcidInternal.getSecurityDetails().setEncryptedSecurityAnswer(new EncryptedSecurityAnswer(encrypt(updatedOrcidProfile.getSecurityQuestionAnswer())));
        orcidInternal.getSecurityDetails().setEncryptedVerificationCode(new EncryptedVerificationCode(encrypt(updatedOrcidProfile.getVerificationCode())));
        existingProfile.setOrcidInternal(orcidInternal);
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updatePreferences(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }
        org.orcid.jaxb.model.message.Preferences preferences = updatedOrcidProfile.getOrcidInternal().getPreferences();
        existingProfile.getOrcidInternal().setPreferences(preferences);
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidPreferences(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }

        existingProfile.setOrcidPreferences(updatedOrcidProfile.getOrcidPreferences());
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile addOrcidWorks(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }
        OrcidWorks existingOrcidWorks = existingProfile.retrieveOrcidWorks();
        OrcidWorks updatedOrcidWorks = updatedOrcidProfile.retrieveOrcidWorks();

        // Get each of the works and check the orcid and email parameters
        // against existing profile information.
        for (OrcidWork work : updatedOrcidWorks.getOrcidWork()) {
            WorkContributors contributors = work.getWorkContributors();

            if (contributors != null) {
                for (Contributor contributor : contributors.getContributor()) {
                    // If contributor orcid is available, look for the profile
                    // associated with that orcid
                    if (contributor.getContributorOrcid() != null) {
                        ProfileEntity profile = profileDao.find(contributor.getContributorOrcid().getValue());
                        if (profile != null) {
                            contributor.setContributorEmail(new ContributorEmail(profile.getPrimaryEmail().getId()));
                            contributor.setCreditName(new CreditName(profile.getCreditName()));
                        }
                    } else if (contributor.getContributorEmail() != null) {
                        // Else, if email is available, get the profile
                        // associated with that email
                        String email = contributor.getContributorEmail().getValue();

                        EmailEntity emailEntity = emailDao.findCaseInsensitive(email);
                        if (emailEntity != null) {
                            ProfileEntity profileEntity = emailEntity.getProfile();
                            contributor.setContributorOrcid(new ContributorOrcid(profileEntity.getId()));
                            contributor.setCreditName(new CreditName(profileEntity.getCreditName()));
                        }
                    }
                }
            }
        }

        if (existingOrcidWorks == null) {
            existingProfile.setOrcidWorks(updatedOrcidWorks);
        } else {
            existingOrcidWorks.getOrcidWork().addAll(updatedOrcidWorks.getOrcidWork());
            existingProfile.setOrcidWorks(existingOrcidWorks);
        }
        return updateOrcidProfile(existingProfile);
    }

    private void dedupeProfileWorks(OrcidProfile orcidProfile) {
        OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
        if (orcidActivities != null) {
            OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
            if (orcidWorks != null) {
                OrcidWorks dedupedOrcidWorks = dedupeWorks(orcidWorks);
                orcidActivities.setOrcidWorks(dedupedOrcidWorks);
            }
        }
    }

    @Override
    public OrcidWorks dedupeWorks(OrcidWorks orcidWorks) {
        Set<OrcidWork> workSet = new LinkedHashSet<OrcidWork>();
        for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
            orcidProfileCleaner.clean(orcidWork);
            workSet.add(orcidWork);
        }
        OrcidWorks dedupedOrcidWorks = new OrcidWorks();
        dedupedOrcidWorks.getOrcidWork().addAll(workSet);
        return dedupedOrcidWorks;
    }

    @Override
    @Transactional
    public OrcidProfile deleteOrcidWorks(String orcid, int[] positionsToDelete) {
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            return null;
        }
        // XXX Probably should be some optimistic locking around here somewhere,
        // or something, because deleting by position isn't cool if the list has
        // changed.
        Arrays.sort(positionsToDelete);
        List<OrcidWork> orcidWorks = existingProfile.retrieveOrcidWorks().getOrcidWork();
        List<OrcidWork> remainingWorks = new ArrayList<OrcidWork>(orcidWorks.size() - positionsToDelete.length);
        existingProfile.retrieveOrcidWorks().setOrcidWork(remainingWorks);
        int startPosition = 0;
        for (int positionToDelete : positionsToDelete) {
            remainingWorks.addAll(orcidWorks.subList(startPosition, positionToDelete));
            startPosition = positionToDelete + 1;
        }
        remainingWorks.addAll(orcidWorks.subList(startPosition, orcidWorks.size()));
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile updateOrcidWorkVisibility(String orcid, int[] positionsToUpdate, Visibility visibility) {
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            return null;
        }
        // XXX Probably should be some optimistic locking around here somewhere,
        // or something, because updating by position isn't cool if the list has
        // changed.
        Arrays.sort(positionsToUpdate);
        List<OrcidWork> orcidWorks = existingProfile.retrieveOrcidWorks().getOrcidWork();
        for (int positionToUpdate : positionsToUpdate) {
            OrcidWork work = orcidWorks.get(positionToUpdate);
            work.setVisibility(visibility);
        }
        return updateOrcidProfile(existingProfile);
    }

    @Override
    @Transactional
    public OrcidProfile deactivateOrcidProfile(OrcidProfile existingOrcidProfile) {
        OrcidProfile blankedOrcidProfile = new OrcidProfile();

        OrcidBio existingBio = existingOrcidProfile.getOrcidBio();
        PersonalDetails existingPersonalDetails = existingBio.getPersonalDetails();

        OrcidBio minimalBio = new OrcidBio();

        ContactDetails minimalContactDetails = new ContactDetails();
        minimalContactDetails.getEmail().addAll(existingBio.getContactDetails().getEmail());

        OrcidInternal minimalOrcidInternal = new OrcidInternal();
        minimalOrcidInternal.setSecurityDetails(existingOrcidProfile.getOrcidInternal().getSecurityDetails());

        OrcidHistory deactivatedOrcidHistory = existingOrcidProfile.getOrcidHistory();
        deactivatedOrcidHistory.setDeactivationDate(new DeactivationDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        blankedOrcidProfile.setOrcidHistory(deactivatedOrcidHistory);

        // only names names from bio with a visibility setting
        PersonalDetails minimalPersonalDetails = new PersonalDetails();
        minimalPersonalDetails.setCreditName(existingPersonalDetails != null ? existingPersonalDetails.getCreditName() : null);
        minimalPersonalDetails.setOtherNames(existingPersonalDetails != null ? existingPersonalDetails.getOtherNames() : null);
        minimalPersonalDetails.setGivenNames(new GivenNames("Given Names Deactivated"));
        minimalPersonalDetails.setFamilyName(new FamilyName("Family Name Deactivated"));

        for (Email email : minimalContactDetails.getEmail()) {
            setVisibilityToPrivate(email);
        }
        setVisibilityToPrivate(minimalPersonalDetails.getCreditName());
        setVisibilityToPrivate(minimalPersonalDetails.getOtherNames());

        minimalBio.setPersonalDetails(minimalPersonalDetails);
        minimalBio.setContactDetails(minimalContactDetails);
        minimalBio.setBiography(new Biography());
        minimalBio.setExternalIdentifiers(new ExternalIdentifiers());
        blankedOrcidProfile.setOrcidBio(minimalBio);
        blankedOrcidProfile.setOrcid(existingOrcidProfile.getOrcid().getValue());

        return this.updateOrcidProfile(blankedOrcidProfile);
    }

    private void setVisibilityToPrivate(VisibilityType visibilityType) {
        if (visibilityType != null) {
            visibilityType.setVisibility(Visibility.PRIVATE);
        }
    }

    @Override
    @Transactional
    public OrcidProfile revokeApplication(String userOrcid, String applicationOrcid, Collection<ScopePathType> scopes) {
        ProfileEntity existingProfile = profileDao.find(userOrcid);
        if (existingProfile == null) {
            return null;
        }
        Set<OrcidOauth2TokenDetail> tokenDetails = existingProfile.getTokenDetails();
        if (tokenDetails != null) {
            Iterator<OrcidOauth2TokenDetail> tokenDetailIterator = tokenDetails.iterator();
            while (tokenDetailIterator.hasNext()) {
                OrcidOauth2TokenDetail tokenDetail = tokenDetailIterator.next();
                if (tokenDetail.getClientDetailsEntity().getId().equals(applicationOrcid)) {
                    String tokenScope = tokenDetail.getScope();
                    if (tokenScope != null) {
                        Set<ScopePathType> tokenScopes = ScopePathType.getScopesFromSpaceSeparatedString(tokenScope);
                        if (new HashSet<ScopePathType>(scopes).equals(tokenScopes)) {
                            // Remove from DB
                            orcidOauth2TokenDetailDao.remove(tokenDetail.getId());
                            // Remove from in memory object cached by Hibernate
                            tokenDetailIterator.remove();
                        }
                    }
                }
            }
        }
        orcidOauth2TokenDetailDao.flush();
        OrcidProfile updatedOrcidProfile = convertToOrcidProfile(existingProfile, LoadOptions.ALL);
        putInCache(userOrcid, updatedOrcidProfile);
        return updatedOrcidProfile;
    }

    /**
     * Checks that the email is not already being used
     * 
     * @param email
     *            the value to be used to check for an existing record
     */
    @Override
    public boolean emailExists(String email) {
        return profileDao.emailExists(email) || emailDao.emailExists(email);
    }

    /**
     * Adds a new {@link List&lt;org.orcid.jaxb.model.message.Affiliation&lt;}
     * to the {@link} OrcidProfile} and returns the updated values
     * 
     * @param updatedOrcidProfile
     * @return
     */
    @Override
    @Transactional
    public OrcidProfile addAffiliations(OrcidProfile updatedOrcidProfile) {
        if (updatedOrcidProfile.getOrcidBio() == null || updatedOrcidProfile.getOrcidBio().getAffiliations() == null
                || updatedOrcidProfile.getOrcidBio().getAffiliations().isEmpty()) {
            return null;
        }
        String orcid = updatedOrcidProfile.getOrcid().getValue();
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            return null;
        }
        OrcidBio orcidBio = existingProfile.getOrcidBio();
        List<Affiliation> affiliations = updatedOrcidProfile.getOrcidBio().getAffiliations();
        for (Affiliation affiliation : affiliations) {
            affiliation.setVisibility(OrcidVisibilityDefaults.AFFILIATE_DETAIL_DEFAULT.getVisibility());
            orcidBio.getAffiliations().add(affiliation);
        }
        updatedOrcidProfile.getOrcidBio();
        OrcidProfile persistedProfile = updateOrcidProfile(existingProfile);
        return persistedProfile;
    }

    @Override
    @Transactional
    public OrcidProfile addDelegates(OrcidProfile updatedOrcidProfile) {
        String orcid = updatedOrcidProfile.getOrcid().getValue();
        OrcidProfile existingProfile = retrieveOrcidProfile(orcid);
        if (existingProfile == null) {
            return null;
        }
        OrcidBio existingOrcidBio = existingProfile.getOrcidBio();
        Delegation existingDelegation = existingOrcidBio.getDelegation();
        // if existing delegation doesn't exist then use this as the definitive
        List<DelegationDetails> newlyAddedDelegates = new ArrayList<DelegationDetails>();
        if (existingDelegation == null) {
            Delegation updatedDelegation = updatedOrcidProfile.getOrcidBio().getDelegation();
            existingOrcidBio.setDelegation(updatedDelegation);
            // all delegate for this user can be considered new
            if (updatedDelegation.getGivenPermissionTo() != null && updatedDelegation.getGivenPermissionTo().getDelegationDetails() != null) {
                newlyAddedDelegates.addAll(updatedDelegation.getGivenPermissionTo().getDelegationDetails());
            }

        } else {
            GivenPermissionTo existingPermissionsTo = existingDelegation.getGivenPermissionTo();
            GivenPermissionTo updatedPermissionTo = updatedOrcidProfile.getOrcidBio().getDelegation().getGivenPermissionTo();
            if (existingPermissionsTo == null) {
                // any delegates in this list of GivenPermissionTo are to be
                // added to mailing list, first time in..
                if (updatedPermissionTo.getDelegationDetails() != null && updatedPermissionTo.getDelegationDetails() != null) {
                    newlyAddedDelegates.addAll(updatedPermissionTo.getDelegationDetails());
                }
                existingDelegation.setGivenPermissionTo(updatedPermissionTo);
            } else {
                // anything being passed in the new list of delegation details
                // is to be added to existing, but email the
                // delta
                for (DelegationDetails delegationDetails : updatedPermissionTo.getDelegationDetails()) {
                    // add all to correctly update object graph for persistence
                    // but don't send repeated emails
                    if (!existingPermissionsTo.getDelegationDetails().contains(delegationDetails)) {
                        newlyAddedDelegates.add(delegationDetails);
                    }
                    existingPermissionsTo.getDelegationDetails().add(delegationDetails);

                }
            }
        }

        OrcidProfile persistedProfile = updateOrcidProfile(existingProfile);
        if (!newlyAddedDelegates.isEmpty()) {
            notificationManager.sendNotificationToAddedDelegate(existingProfile, newlyAddedDelegates);
        }

        return persistedProfile;
    }

    @Override
    @Transactional
    public OrcidProfile revokeDelegate(String giverOrcid, String receiverOrcid) {
        profileDao.remove(giverOrcid, receiverOrcid);
        givenPermissionToDao.remove(giverOrcid, receiverOrcid);
        return retrieveOrcidProfile(giverOrcid);
    }

    @Override
    @Transactional
    public OrcidProfile deleteProfile(String orcid) {
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity == null) {
            LOG.debug("Asked to delete profile {}, but not found in DB", orcid);
            return null;
        }
        profileDao.remove(profileEntity);
        profileDao.flush();
        orcidIndexManager.deleteOrcidProfile(orcid);
        removeFromCache(orcid);
        // There seems to be a Hibernate problem relating
        // OrcidOauth2TokenDetail, when getting and deleting in same
        // transaction. So not possible to return deleted profile, and probably
        // not really necessary either.
        // OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        return null;
    }

    static ExecutorService executorService = null;
    static Object executorServiceLock = new Object();
    static ConcurrentHashMap<String, FutureTask<String>> futureHM = new ConcurrentHashMap<String, FutureTask<String>>();

    @Override
    public void processProfilesPendingIndexing() {
        // XXX There are some concurrency related edge cases to fix here.
        LOG.info("About to process profiles pending indexing");
        if (executorService == null || executorService.isShutdown()) {
            synchronized (executorServiceLock) {
                if (executorService == null || executorService.isShutdown()) {
                    executorService = createThreadPoolForIndexing();
                } else {
                    // already running
                    return;
                }
            }
        } else {
            // already running
            return;
        }

        List<String> orcidsForIndexing = new ArrayList<>();
        List<String> orcidFailures = new ArrayList<>();
        do {
            orcidsForIndexing = profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, INDEXING_BATCH_SIZE, orcidFailures);
            LOG.info("Got batch of {} profiles for indexing", orcidsForIndexing.size());
            for (final String orcid : orcidsForIndexing) {
                FutureTask<String> task = new FutureTask<String>(new GetPendingOrcid(orcid));
                executorService.execute(task);
                futureHM.put(orcid, task);
            }
            for (final String orcid : orcidsForIndexing) {
                try {
                    futureHM.remove(orcid).get(15, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " InterruptedException ", e);
                } catch (ExecutionException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " ExecutionException ", e);
                } catch (TimeoutException e) {
                    orcidFailures.add(orcid);
                    LOG.error(orcid + " TimeoutException ", e);
                }
            }
        } while (!orcidsForIndexing.isEmpty());
        if (!executorService.isShutdown()) {
            synchronized (executorServiceLock) {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOG.warn("Received an interupt exception whilst waiting for the indexing to complete", e);
                    }
                    LOG.info("Finished processing profiles pending indexing");
                }
            }
        }
    }

    class GetPendingOrcid implements Callable<String> {
        String orcid = null;

        public GetPendingOrcid(String orcid) {
            this.orcid = orcid;
        }

        @Override
        public String call() throws Exception {
            processProfilePendingIndexingInTransaction(orcid);
            return "was successful " + orcid;
        }

    }

    private ExecutorService createThreadPoolForIndexing() {
        return new ThreadPoolExecutor(numberOfIndexingThreads, numberOfIndexingThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(INDEXING_BATCH_SIZE), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void processProfilePendingIndexingInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.info("About to index profile: {}", orcid);
                OrcidProfile orcidProfile = retrieveClaimedOrcidProfile(orcid);
                if (orcidProfile == null) {
                    LOG.debug("Null profile found during indexing: {}", orcid);
                } else {
                    LOG.debug("Got profile to index: {}", orcid);
                    orcidIndexManager.persistProfileInformationForIndexing(orcidProfile);
                    profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
                }
            }
        });
    }

    @Override
    synchronized public void processUnclaimedProfilesToFlagForIndexing() {
        LOG.info("About to process unclaimed profiles to flag for indexing");
        List<String> orcidsToFlag = Collections.<String> emptyList();
        do {
            orcidsToFlag = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(claimWaitPeriodDays, INDEXING_BATCH_SIZE, orcidsToFlag);
            LOG.info("Got batch of {} unclaimed profiles to flag for indexing", orcidsToFlag.size());
            for (String orcid : orcidsToFlag) {
                LOG.info("About to flag unclaimed profile for indexing: {}", orcid);
                profileDao.updateIndexingStatus(orcid, IndexingStatus.PENDING);
            }
        } while (!orcidsToFlag.isEmpty());
    }

    @Override
    synchronized public void processUnclaimedProfilesForReminder() {
        LOG.info("About to process unclaimed profiles for reminder");
        List<String> orcidsToRemind = Collections.<String> emptyList();
        do {
            orcidsToRemind = profileDao.findUnclaimedNeedingReminder(claimReminderAfterDays, INDEXING_BATCH_SIZE, orcidsToRemind);
            LOG.info("Got batch of {} unclaimed profiles for reminder", orcidsToRemind.size());
            for (final String orcid : orcidsToRemind) {
                processUnclaimedProfileForReminderInTransaction(orcid);
            }
        } while (!orcidsToRemind.isEmpty());
    }

    private void processUnclaimedProfileForReminderInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                processUnclaimedProfileForReminder(orcid);
            }
        });
    }

    private void processUnclaimedProfileForReminder(final String orcid) {
        LOG.info("About to process unclaimed profile for reminder: {}", orcid);
        OrcidProfile orcidProfile = retrieveOrcidProfile(orcid);
        notificationManager.sendClaimReminderEmail(orcidProfile, claimWaitPeriodDays - claimReminderAfterDays);
    }

    private Set<OrcidGrantedAuthority> getGrantedAuthorities(ProfileEntity profileEntity) {
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setProfileEntity(profileEntity);
        if(profileEntity.getOrcidType() == null || profileEntity.getOrcidType().equals(OrcidType.USER))
            authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());        
        else if(profileEntity.getOrcidType() == null || profileEntity.getOrcidType().equals(OrcidType.GROUP)){
            switch(profileEntity.getGroupType()){
            case BASIC:
                authority.setAuthority(OrcidWebRole.ROLE_BASIC.getAuthority());
                break;
            case PREMIUM:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM.getAuthority());
                break;
            case BASIC_INSTITUTION:
                authority.setAuthority(OrcidWebRole.ROLE_BASIC_INSTITUTION.getAuthority());
                break;            
            case PREMIUM_INSTITUTION:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM_INSTITUTION.getAuthority());
                break;
            }            
        } else if(profileEntity.getOrcidType() == null || profileEntity.getOrcidType().equals(OrcidType.CLIENT)){
            switch(profileEntity.getClientType()){
            case CREATOR:
                authority.setAuthority(OrcidWebRole.ROLE_CREATOR.getAuthority());
                break;
            case UPDATER:
                authority.setAuthority(OrcidWebRole.ROLE_UPDATER.getAuthority());
                break;
            case PREMIUM_CREATOR:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM_CREATOR.getAuthority());
                break;
            case PREMIUM_UPDATER:
                authority.setAuthority(OrcidWebRole.ROLE_PREMIUM_UPDATER.getAuthority());
                break;            
            }
        }
                
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
        authorities.add(authority);
        return authorities;
    }

    private String hash(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.hashForInternalUse(unencrypted);
        } else {
            return null;
        }
    }

    private String encrypt(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.encryptForInternalUse(unencrypted);
        } else {
            return null;
        }
    }

    private String decrypt(String encrypted) {
        if (StringUtils.isNotBlank(encrypted)) {
            return encryptionManager.decryptForInternalUse(encrypted);
        } else {
            return null;
        }
    }

    private void encryptAndMapFieldsForProfileEntityPersistence(OrcidProfile orcidProfile, ProfileEntity profileEntity) {
        String password = orcidProfile.getPassword();
        profileEntity.setEncryptedPassword(password == null ? null : encryptionManager.hashForInternalUse(password));
        String verificationCode = orcidProfile.getVerificationCode();
        profileEntity.setEncryptedVerificationCode(verificationCode == null ? null : encryptionManager.encryptForInternalUse(verificationCode));
        String securityAnswer = orcidProfile.getSecurityQuestionAnswer();
        profileEntity.setEncryptedSecurityAnswer(securityAnswer == null ? null : encryptionManager.encryptForInternalUse(securityAnswer));
    }

    @Override
    public Date retrieveLastModifiedDate(String orcid) {
        return profileDao.retrieveLastModifiedDate(orcid);
    }

    @Override
    public Date updateLastModifiedDate(String orcid) {
        return profileDao.updateLastModifiedDate(orcid);
    }

    private Element getFromCache(String orcid) {
        Element element = profileCache.get(createCacheKey(orcid));
        return element;
    }

    private void putInCache(OrcidProfile orcidProfile) {
        putInCache(orcidProfile.getOrcid().getValue(), orcidProfile);
    }

    private void putInCache(String orcid, OrcidProfile orcidProfile) {
        profileCache.put(new Element(createCacheKey(orcid), orcidProfile));
    }

    private void removeFromCache(String orcid) {
        profileCache.remove(createCacheKey(orcid));
    }

    private Object createCacheKey(String orcid) {
        return new OrcidCacheKey(orcid, releaseName);
    }

    @Override
    public void clearOrcidProfileCache() {
        profileCache.removeAll();
    }

    public void addLocale(OrcidProfile orcidProfile, Locale locale) {
        if (orcidProfile.getOrcidPreferences() == null)
            orcidProfile.setOrcidPreferences(new OrcidPreferences());
        orcidProfile.getOrcidPreferences().setLocale(org.orcid.jaxb.model.message.Locale.fromValue(locale.toString()));
    }

}
