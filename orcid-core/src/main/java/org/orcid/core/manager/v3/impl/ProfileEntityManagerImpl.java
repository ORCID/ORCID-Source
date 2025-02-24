package org.orcid.core.manager.v3.impl;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.*;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.ProfileEntityManagerReadOnlyImpl;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.dao.BackupCodeDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.*;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Declan Newman (declan) Date: 10/02/2012
 */
public class ProfileEntityManagerImpl extends ProfileEntityManagerReadOnlyImpl implements ProfileEntityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEntityManagerImpl.class);

    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

    @Resource(name = "profileFundingManagerV3")
    private ProfileFundingManager fundingManager;

    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "workManagerV3")
    private WorkManager workManager;

    @Resource(name = "researchResourceManagerV3")
    private ResearchResourceManager researchResourceManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "addressManagerV3")
    private AddressManager addressManager;

    @Resource(name = "externalIdentifierManagerV3")
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource(name = "profileKeywordManagerV3")
    private ProfileKeywordManager profileKeywordManager;

    @Resource(name = "otherNameManagerV3")
    private OtherNameManager otherNameManager;

    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;

    @Resource(name = "biographyManagerV3")
    private BiographyManager biographyManager;

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManagerV3;

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;
    
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    @Resource
    private EmailFrequencyManager emailFrequencyManager;

    @Resource
    private GivenPermissionToManager givenPermissionToManager;
    
    @Resource
    protected BackupCodeDao backupCodeDao;
    
    @Resource
    private ProfileLastModifiedDao profileLastModifiedDao;

    @Resource
    private ProfileEmailDomainManager profileEmailDomainManager;

    @Override
    public boolean orcidExists(String orcid) {
        return profileDao.orcidExists(orcid);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        return profileDao.hasBeenGivenPermissionTo(giverOrcid, receiverOrcid);
    }

    @Override
    public String findByCreditName(String creditName) {
        Name name = recordNameManagerV3.findByCreditName(creditName);
        if (name == null) {
            return null;
        }
        return name.getPath();
    }

    @Override
    public boolean deprecateProfile(String deprecatedOrcid, String primaryOrcid, String deprecatedMethod, String adminUser) {
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            public Boolean doInTransaction(TransactionStatus status) {
                boolean wasDeprecated = profileDao.deprecateProfile(deprecatedOrcid, primaryOrcid, deprecatedMethod, adminUser);
                // If it was successfully deprecated
                if (wasDeprecated) {
                    LOGGER.info("Account {} was deprecated to primary account: {}", deprecatedOrcid, primaryOrcid);
                    // Move all email's to the primary record
                    Emails deprecatedAccountEmails = emailManager.getEmails(deprecatedOrcid);
                    if (deprecatedAccountEmails != null) {
                        // For each email in the deprecated profile
                        for (Email email : deprecatedAccountEmails.getEmails()) {
                            // Delete each email from the deprecated
                            // profile
                            LOGGER.info("About to move email {} from profile {} to profile {}", new Object[] { email.getEmail(), deprecatedOrcid, primaryOrcid });
                            emailManager.moveEmailToOtherAccount(email.getEmail(), deprecatedOrcid, primaryOrcid);
                        }
                    }
                    List<ProfileEmailDomainEntity> deprecatedEmailDomains = profileEmailDomainManager.getEmailDomains(deprecatedOrcid);
                    if (deprecatedEmailDomains != null && !deprecatedEmailDomains.isEmpty()) {
                        for (ProfileEmailDomainEntity emailDomain : deprecatedEmailDomains) {
                            profileEmailDomainManager.moveEmailDomainToAnotherAccount(emailDomain.getEmailDomain(), deprecatedOrcid, primaryOrcid);
                        }
                    }
                    // important to run this after moving domains, as this function will delete the domains from the database
                    clearRecord(deprecatedOrcid, false);

                    profileLastModifiedDao.updateLastModifiedDateAndIndexingStatus(deprecatedOrcid, IndexingStatus.REINDEX);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean deactivateRecord(String orcid) {
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            public Boolean doInTransaction(TransactionStatus status) {
                LOGGER.info("About to deactivate record {}", orcid);                
                clearRecord(orcid, true);
                emailManager.hideAllEmails(orcid);
                notificationManager.sendAmendEmail(orcid, AmendedSection.UNKNOWN, null);
                profileDao.deactivate(orcid);
                LOGGER.info("Record {} successfully deactivated", orcid);
                return true;                
            }
        });
    }

    @Override
    public boolean enableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, true);
    }

    /**
     * Disable developer tools
     * 
     * @param orcid
     *            The orcid to update
     * @return true if the developer tools where disabeled on that profile
     */
    @Override
    public boolean disableDeveloperTools(String orcid) {
        return profileDao.updateDeveloperTools(orcid, false);
    }

    @Override
    public boolean isProfileClaimed(String orcid) {
        return profileDao.getClaimedStatus(orcid);
    }

    /**
     * Get the group type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the group type, null if it is not a client
     */
    @Override
    public MemberType getGroupType(String orcid) {
        String memberType = profileDao.getGroupType(orcid);
        return MemberType.valueOf(memberType);
    }

    /**
     * Updates the DB and the cached value in the request scope.
     * 
     */
    @Override
    public void updateLastModifedAndIndexingStatus(String orcid) {
        profileLastModifiedAspect.updateLastModifiedDateAndIndexingStatus(orcid);
    }

    /**
     * Updates the DB and the cached value in the request scope.
     * 
     */
    @Override
    public void updateLastModifed(String orcid) {
        profileLastModifiedAspect.updateLastModifiedDate(orcid);
    }

    
    @Override
    public boolean isDeactivated(String orcid) {
        return profileDao.isDeactivated(orcid);
    }

    @Override
    public boolean reviewProfile(String orcid) {
        return profileDao.reviewProfile(orcid);
    }

    @Override
    public boolean unreviewProfile(String orcid) {
        return profileDao.unreviewProfile(orcid);
    }

    @Override
    public void disableClientAccess(String clientDetailsId, String userOrcid) {
        orcidOauth2TokenService.disableClientAccess(clientDetailsId, userOrcid);
    }

    @Override
    public List<ApplicationSummary> getApplications(String orcid) {
        List<OrcidOauth2TokenDetail> tokenDetails = orcidOauth2TokenService.findByUserName(orcid);
        Map<String, ApplicationSummary> distinctApplications = new HashMap<>();
        for (OrcidOauth2TokenDetail token : tokenDetails) {
            if ((token.getTokenDisabled() == null || !token.getTokenDisabled()) && token.getOboClientDetailsId() == null) {
                addApplicationToMap(token, distinctApplications);
            }
        }
        List<ApplicationSummary> applications = new ArrayList<>();
        for (String key : distinctApplications.keySet()) {
            applications.add(distinctApplications.get(key));
        }
        
        applications.sort((a1, a2) -> a1.getName().toLowerCase().compareTo(a2.getName().toLowerCase()));
        return applications;
    }

    private void addApplicationToMap(OrcidOauth2TokenDetail token, Map<String, ApplicationSummary> distinctApplications) {
        ClientDetailsEntity client = clientDetailsEntityCacheManager.retrieve(token.getClientDetailsId());
        if (client != null) {
            ApplicationSummary applicationSummary = distinctApplications.get(client.getId());
            if (applicationSummary != null) {
                if (token.getDateCreated().before(applicationSummary.getApprovalDate())) {
                    applicationSummary.setApprovalDate(token.getDateCreated());
                }
            } else {
                applicationSummary = new ApplicationSummary();
                distinctApplications.put(client.getId(), applicationSummary);
                applicationSummary.setScopePaths(new HashMap<String, String>());
                applicationSummary.setName(client.getClientName());
                applicationSummary.setClientId(client.getId());
                applicationSummary.setWebsiteValue(client.getClientWebsite());
                applicationSummary.setApprovalDate(token.getDateCreated());
            }

            Set<ScopePathType> scopesGrantedToClient = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
            String scopeFullPath = ScopePathType.class.getName() + ".";
            for (ScopePathType tempScope : scopesGrantedToClient) {
                try {
                    String label = localeManager.resolveMessage(scopeFullPath + tempScope.toString());
                    applicationSummary.getScopePaths().put(tempScope.toString(), label);
                } catch (NoSuchMessageException e) {
                    LOGGER.warn("No message to display for scope " + tempScope.toString());
                }
            }
        }
    }    

    @Override
    public String getOrcidHash(String string) {
        if (PojoUtil.isEmpty(string)) {
            return null;
        }
        try {
            return encryptionManager.sha256Hash(string);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    }

    @Override
    public String retrivePublicDisplayName(String orcid) {
        String publicDisplayName = recordNameManagerReadOnlyV3.fetchDisplayablePublicName(orcid);        
        return PojoUtil.isEmpty(publicDisplayName) ? StringUtils.EMPTY : publicDisplayName;
    }

    @Override
    @Transactional
    public boolean claimProfileAndUpdatePreferences(String orcid, String email, AvailableLocales locale, Claim claim) {
        // Verify the email
        boolean emailVerified = emailManager.verifySetCurrentAndPrimary(orcid, email);
        if (!emailVerified) {
            throw new InvalidParameterException("Unable to claim and verify email: " + email + " for user: " + orcid);
        }

        // Update the profile entity fields
        ProfileEntity profile = profileDao.find(orcid);
        profile.setIndexingStatus(IndexingStatus.REINDEX);
        profile.setClaimed(true);
        profile.setCompletedDate(new Date());
        profile.setEncryptedPassword(encryptionManager.hashForInternalUse(claim.getPassword().getValue()));
        if (locale != null) {
            profile.setLocale(locale.name());
        }
        if (claim != null) {
            profile.setActivitiesVisibilityDefault(claim.getActivitiesVisibilityDefault().getVisibility().name());
        }
        // Update profile entity in the DB
        profileDao.merge(profile);
        profileDao.flush();
        
        // Update the visibility for every bio element to the visibility
        // selected by the user
        // Update the bio
        Visibility defaultVisibility = claim.getActivitiesVisibilityDefault().getVisibility();
        
        // Update address
        Addresses addresses = addressManager.getAddresses(orcid);
        for(Address address : addresses.getAddress()) {
            address.setVisibility(defaultVisibility);
            addressManager.updateAddress(orcid, address.getPutCode(), address, false);
        }        

        // Update the keywords
        Keywords keywords = profileKeywordManager.getKeywords(orcid); 
        for(Keyword keyword : keywords.getKeywords()) {
            keyword.setVisibility(defaultVisibility);
            profileKeywordManager.updateKeyword(orcid, keyword.getPutCode(), keyword, false);
        }

        // Update the other names
        OtherNames otherNames = otherNameManager.getOtherNames(orcid);
        for(OtherName otherName : otherNames.getOtherNames()) {
            otherName.setVisibility(defaultVisibility);
            otherNameManager.updateOtherName(orcid, otherName.getPutCode(), otherName, false);
        }

        // Update the researcher urls
        ResearcherUrls researcherUrls = researcherUrlManager.getResearcherUrls(orcid);
        for(ResearcherUrl researcherUrl : researcherUrls.getResearcherUrls()) {
            researcherUrl.setVisibility(defaultVisibility);
            researcherUrlManager.updateResearcherUrl(orcid, researcherUrl, false);
        }

        // Update the external identifiers
        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid);
        for(PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            extId.setVisibility(defaultVisibility);
            externalIdentifierManager.updateExternalIdentifier(orcid, extId, false);
        }        

        // Update the biography
        if (biographyManager.exists(orcid)) {
            Biography bio = biographyManager.getBiography(orcid);
            bio.setVisibility(Visibility.fromValue(defaultVisibility.value()));
            biographyManager.updateBiography(orcid, bio);
        }
        
        if (!emailFrequencyManager.emailFrequencyExists(orcid)) {
            if (claim.getSendOrcidNews() == null) {
                emailFrequencyManager.createOnClaim(orcid, false);
            } else {
                emailFrequencyManager.createOnClaim(orcid, claim.getSendOrcidNews().getValue());
            }
        } else {
            if (claim.getSendOrcidNews() == null) {
                emailFrequencyManager.updateSendQuarterlyTips(orcid, false);
            } else {
                emailFrequencyManager.updateSendQuarterlyTips(orcid, claim.getSendOrcidNews().getValue());
            }
        }

        return true;
    }

    @Override
    public void updateLocale(String orcid, AvailableLocales locale) {
        profileDao.updateLocale(orcid, locale.name());
    }

    @Override
    public boolean isProfileClaimedByEmail(String email) {
        Map<String, String> emailKeys = emailManager.getEmailKeys(email);
        return profileDao.getClaimedStatusByEmailHash(emailKeys.get(EmailManager.HASH));
    }

    @Override
    public List<String> reactivate(String orcid, String primaryEmail, Reactivation reactivation) {
        ArrayList<String> emailsToNotify = new ArrayList<String>();
        // Null reactivation object means the reactivation request comes from an
        // admin
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                LOGGER.info("About to reactivate record, orcid={}", orcid);
                // Populate primary email
                String primaryEmailTrim = primaryEmail.trim();
                emailManager.reactivatePrimaryEmail(orcid, primaryEmailTrim);
                profileEmailDomainManager.processDomain(orcid, primaryEmailTrim);
                if (reactivation == null) {
                    // Delete any non primary email
                    emailManager.clearEmailsAfterReactivation(orcid);
                } else {
                    // Populate additional emails
                    if (reactivation.getEmailsAdditional() != null && !reactivation.getEmailsAdditional().isEmpty()) {
                        for (Text additionalEmail : reactivation.getEmailsAdditional()) {
                            if (!PojoUtil.isEmpty(additionalEmail)) {
                                String email = additionalEmail.getValue().trim();
                                boolean isNewEmailOrShouldNotify = emailManager.reactivateOrCreate(orcid, email,
                                        reactivation.getActivitiesVisibilityDefault().getVisibility());
                                if (isNewEmailOrShouldNotify) {
                                    emailsToNotify.add(email);
                                }
                            }
                        }
                    }
                    // Delete any non populated email
                    emailManager.clearEmailsAfterReactivation(orcid);
                }

                // Reactivate user
                ProfileEntity profileEntity = profileDao.find(orcid);
                profileEntity.setDeactivationDate(null);
                profileEntity.setClaimed(true);
                profileEntity.setIndexingStatus(IndexingStatus.PENDING);
                if (reactivation != null) {
                    profileEntity.setEncryptedPassword(encryptionManager.hashForInternalUse(reactivation.getPassword().getValue()));
                    profileEntity.setActivitiesVisibilityDefault(reactivation.getActivitiesVisibilityDefault().getVisibility().name());                    
                }
                profileDao.merge(profileEntity);
                if(reactivation != null) {
                    Name name = recordNameManagerReadOnlyV3.getRecordName(orcid);
                    if(reactivation.getGivenNames() != null)
                        name.setGivenNames(new GivenNames(reactivation.getGivenNames().getValue()));
                    if(reactivation.getFamilyNames() != null)
                        name.setFamilyName(new FamilyName(reactivation.getFamilyNames().getValue()));
                    recordNameManagerV3.updateRecordName(orcid, name);
                    LOGGER.info("Name for orcid={} successfully set", orcid);                    
                }
                
                LOGGER.info("Record orcid={} successfully reactivated", orcid);
                return true;
            }
        });

        return emailsToNotify;
    }

    @Override
    public void updatePassword(String orcid, String password) {
        String encryptedPassword = encryptionManager.hashForInternalUse(password);
        profileDao.changeEncryptedPassword(orcid, encryptedPassword);
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.RESET_PASSWORD, orcid);
    }

    @Override
    public boolean isProfileDeprecated(String orcid) {
        return profileDao.isProfileDeprecated(orcid);
    }

    @Override
    public void updateLastLoginDetails(String orcid, String ipAddress) {
        profileDao.updateLastLoginDetails(orcid, ipAddress);
    }

    @Override
    public AvailableLocales retrieveLocale(String orcid) {
        return AvailableLocales.valueOf(profileDao.retrieveLocale(orcid));
    }

    /**
     * Set the locked status of an account to true
     * 
     * @param orcid
     *            the id of the profile that should be locked
     * @return true if the account was locked
     */
    @Override
    public boolean lockProfile(String orcid, String lockReason, String description, String adminUser) {
        return profileDao.lockProfile(orcid, lockReason, description, adminUser);
    }

    /**
     * Set the locked status of an account to false
     * 
     * @param orcid
     *            the id of the profile that should be unlocked
     * @return true if the account was unlocked
     */
    @Override
    public boolean unlockProfile(String orcid) {
        return profileDao.unlockProfile(orcid);
    }

    @Override
    public Date getLastLogin(String orcid) {
        return profileDao.getLastLogin(orcid);
    }

    public void startSigninLock(String orcid) {
        profileDao.startSigninLock(orcid);
    }
    
    public void resetSigninLock(String orcid) {
        profileDao.resetSigninLock(orcid);
    }
    
    public void updateSigninLock(String orcid, Integer count) {
        profileDao.updateSigninLock(orcid, count);
    }
    
    public List<Object[]> getSigninLock(String orcid) {
        return profileDao.getSigninLock(orcid);
    }

    /**
     * Clears all record info but the email addresses, that stay unmodified
     */
    private void clearRecord(String orcid, Boolean disableTokens) {
        // Remove works
        workManager.removeAllWorks(orcid);

        // Remove funding
        fundingManager.removeAllFunding(orcid);

        // Remove affiliations
        affiliationsManager.removeAllAffiliations(orcid);

        // Remove peer reviews
        peerReviewManager.removeAllPeerReviews(orcid);

        // Research resource
        researchResourceManager.removeAllResearchResources(orcid);

        // Remove addresses
        addressManager.removeAllAddress(orcid);

        // Remove external identifiers
        externalIdentifierManager.removeAllExternalIdentifiers(orcid);

        // Remove researcher urls
        researcherUrlManager.removeAllResearcherUrls(orcid);

        // Remove other names
        otherNameManager.removeAllOtherNames(orcid);

        // Remove keywords
        profileKeywordManager.removeAllKeywords(orcid);

        // Admin disabling 2FA, so, we should not notify the user
        profileDao.disable2FA(orcid);
        backupCodeDao.removedUsedBackupCodes(orcid);

        // delete notifications
        notificationManager.deleteNotificationsForRecord(orcid);

        // remove trusted individuals
        givenPermissionToManager.removeAllForProfile(orcid);

        // remove email domains
        // NOTE: when deprecating, the domains get moved to the other record before this code is executed
        profileEmailDomainManager.removeAllEmailDomains(orcid);

        // Remove biography
        if (biographyManager.exists(orcid)) {
            Biography deprecatedBio = new Biography();
            deprecatedBio.setContent(null);
            deprecatedBio.setVisibility(Visibility.PRIVATE);
            biographyManager.updateBiography(orcid, deprecatedBio);
        }

        // Set the deactivated names
        if (recordNameManagerV3.exists(orcid)) {
            Name name = new Name();
            name.setCreditName(new CreditName());
            name.setGivenNames(new GivenNames("Given Names Deactivated"));
            name.setFamilyName(new FamilyName("Family Name Deactivated"));
            name.setVisibility(Visibility.PUBLIC);
            name.setPath(orcid);
            recordNameManagerV3.updateRecordName(orcid, name);
        }

        //
        userConnectionDao.deleteByOrcid(orcid);

        if (disableTokens) {
            // Disable any token that belongs to this record
            orcidOauth2TokenDetailService.disableAccessTokenByUserOrcid(orcid, RevokeReason.RECORD_DEACTIVATED);
        }

        // Change default visibility to private
        boolean updated = profileDao.updateDefaultVisibility(orcid, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        if (updated) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, orcid, "deactivated/deprecated");
        }
    }
}
