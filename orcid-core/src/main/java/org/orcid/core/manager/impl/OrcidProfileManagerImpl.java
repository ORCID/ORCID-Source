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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LocaleManager;
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
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
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
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.VisibilityType;
import org.orcid.persistence.adapter.JpaJaxbEntityAdapter;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
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

    private int claimWaitPeriodDays = 10;

    private int claimReminderAfterDays = 8;

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
        notificationManager.sendApiRecordCreationEmail(orcidProfile);
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
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile, existingProfileEntity);
        profileEntity.setIndexingStatus(IndexingStatus.PENDING);
        ProfileEntity updatedProfileEntity = profileDao.merge(profileEntity);
        profileDao.flush();
        OrcidProfile updatedOrcidProfile = adapter.toOrcidProfile(updatedProfileEntity);
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
            return ((OrcidProfileUserDetails) authentication.getPrincipal()).getRealProfile().getOrcid().getValue();
        }
        return null;
    }

    private void setWorkPrivacy(OrcidProfile updatedOrcidProfile, Visibility defaultWorkVisibility) {
        OrcidActivities incomingActivities = updatedOrcidProfile.getOrcidActivities();
        if (incomingActivities != null) {
            OrcidWorks incomingWorks = incomingActivities.getOrcidWorks();
            if (incomingWorks != null) {
                for (OrcidWork incomingWork : incomingWorks.getOrcidWork()) {
                    if (StringUtils.isBlank(incomingWork.getPutCode())) {
                        Visibility incomingWorkVisibility = incomingWork.getVisibility();
                        if (defaultWorkVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                            incomingWork.setVisibility(defaultWorkVisibility);
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
            OrcidBio orcidBio = profile.getOrcidBio();
            if (orcidBio != null) {
                orcidBio.getAffiliations().clear();
                orcidBio.setContactDetails(null);
                orcidBio.setKeywords(null);
                orcidBio.setPersonalDetails(null);
                orcidBio.setScope(null);
                orcidBio.setBiography(null);
            }
            profile.setOrcidWorks(null);
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
            profile.setOrcidActivities(null);
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
            profile.setOrcidBio(null);
        }
        return profile;
    }

    @Override
    @Transactional
    public OrcidProfile retrieveOrcidProfile(String orcid) {
        profileDao.flush();
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            return convertToOrcidProfile(profileEntity);
        } else {
            return null;
        }

    }

    @Override
    @Transactional
    public OrcidProfile retrieveClaimedOrcidProfile(String orcid) {
        profileDao.flush();
        ProfileEntity profileEntity = profileDao.find(orcid);
        if (profileEntity != null) {
            if (Boolean.TRUE.equals(profileEntity.getClaimed()) || isOldEnough(profileEntity) || isBeingAccessedByCreator(profileEntity) || haveSystemRole()) {
                return convertToOrcidProfile(profileEntity);
            } else {
                return createReservedForClaimOrcidProfile(profileEntity);
            }
        }
        return null;
    }

    private boolean isOldEnough(ProfileEntity profileEntity) {
        return DateUtils.olderThan(profileEntity.getDateCreated(), claimWaitPeriodDays);
    }

    private boolean isBeingAccessedByCreator(ProfileEntity profileEntity) {
        String amenderOrcid = retrieveAmenderOrcid();
        ProfileEntity sourceEntity = profileEntity.getSource();
        if (NullUtils.noneNull(amenderOrcid, sourceEntity)) {
            return amenderOrcid.equals(sourceEntity.getId());
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

    private OrcidProfile createReservedForClaimOrcidProfile(ProfileEntity profileEntity) {
        OrcidProfile op = new OrcidProfile();
        op.setOrcid(profileEntity.getId());
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

    private OrcidProfile convertToOrcidProfile(ProfileEntity profileEntity) {
        profileDao.refresh(profileEntity);
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        String verificationCode = profileEntity.getEncryptedVerificationCode();
        String securityAnswer = profileEntity.getEncryptedSecurityAnswer();
        orcidProfile.setVerificationCode(decrypt(verificationCode));
        orcidProfile.setSecurityQuestionAnswer(decrypt(securityAnswer));
        dedupeProfileWorks(orcidProfile);
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
        ProfileEntity profileEntity = profileDao.findByEmail(email);
        if (profileEntity == null) {
            EmailEntity emailEntity = emailDao.find(email);
            if (emailEntity != null) {
                profileEntity = emailEntity.getProfile();
            }
        }
        if (profileEntity != null) {
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
            ExternalIdentifiers externalIdentifiers = existingProfile.getOrcidBio().getExternalIdentifiers();
            OrcidBio orcidBio = existingProfile.getOrcidBio();
            if (externalIdentifiers == null) {
                orcidBio.setExternalIdentifiers(new ExternalIdentifiers());
            }
            ExternalIdentifiers externalIdentifier = updatedOrcidProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> updatedExternalIdentifiers = externalIdentifier.getExternalIdentifier();
            List<ExternalIdentifier> existingExternalIdentifiers = orcidBio.getExternalIdentifiers().getExternalIdentifier();

            for (ExternalIdentifier ei : updatedExternalIdentifiers) {
                existingExternalIdentifiers.add(ei);
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
    public OrcidProfile addOrcidWorks(OrcidProfile updatedOrcidProfile) {
        OrcidProfile existingProfile = retrieveOrcidProfile(updatedOrcidProfile.getOrcid().getValue());
        if (existingProfile == null) {
            return null;
        }
        OrcidWorks existingOrcidWorks = existingProfile.retrieveOrcidWorks();
        OrcidWorks updatedOrcidWorks = updatedOrcidProfile.retrieveOrcidWorks();
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
        return adapter.toOrcidProfile(existingProfile);
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
        // There seems to be a Hibernate problem relating
        // OrcidOauth2TokenDetail, when getting and deleting in same
        // transaction. So not possible to return deleted profile, and probably
        // not really necessary either.
        // OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        profileDao.remove(profileEntity);
        orcidIndexManager.deleteOrcidProfile(orcid);
        // See comment above
        return null;
    }

    @Override
    synchronized public void processProfilesPendingIndexing() {
        // XXX There are some concurrency related edge cases to fix here.
        LOG.info("About to process profiles pending indexing");
        ExecutorService executorService = createThreadPoolForIndexing();
        List<String> orcidsForIndexing = Collections.<String> emptyList();
        do {
            orcidsForIndexing = profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, INDEXING_BATCH_SIZE, orcidsForIndexing);
            LOG.info("Got batch of {} profiles for indexing", orcidsForIndexing.size());
            for (final String orcid : orcidsForIndexing) {
                executorService.execute(new Runnable() {

                    public void run() {
                        processProfilePendingIndexingInTransaction(orcid);
                    }
                });
            }
        } while (!orcidsForIndexing.isEmpty());
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("Received an interupt exception whilst waiting for the indexing to complete", e);
        }
        LOG.info("Finished processing profiles pending indexing");
    }

    private ExecutorService createThreadPoolForIndexing() {
        return new ThreadPoolExecutor(numberOfIndexingThreads, numberOfIndexingThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(INDEXING_BATCH_SIZE), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void processProfilePendingIndexingInTransaction(final String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                LOG.debug("About to index profile: {}", orcid);
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
        authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());
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

}
