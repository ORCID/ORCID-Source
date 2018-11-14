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
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileEntityManagerReadOnlyImpl;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.OrcidType;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.rc2.record.Biography;
import org.orcid.jaxb.model.v3.rc2.record.CreditName;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.FamilyName;
import org.orcid.jaxb.model.v3.rc2.record.GivenNames;
import org.orcid.jaxb.model.v3.rc2.record.Name;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
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
    private RecordNameManager recordNameManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Override
    public boolean orcidExists(String orcid) {
        return profileDao.orcidExists(orcid);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        return profileDao.hasBeenGivenPermissionTo(giverOrcid, receiverOrcid);
    }

    @Override
    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        return profileDao.existsAndNotClaimedAndBelongsTo(messageOrcid, clientId);
    }

    @Override
    public String findByCreditName(String creditName) {
        Name name = recordNameManager.findByCreditName(creditName);
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
                    clearRecord(deprecatedOrcid, false);
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

                    profileDao.updateLastModifiedDateAndIndexingStatus(deprecatedOrcid, IndexingStatus.REINDEX);
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
                if (profileDao.deactivate(orcid)) {
                    clearRecord(orcid, true);
                    emailManager.hideAllEmails(orcid);
                    notificationManager.sendAmendEmail(orcid, AmendedSection.UNKNOWN, null);
                    LOGGER.info("Record {} successfully deactivated", orcid);
                    return true;
                }
                return false;
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
    public void updateLastModifed(String orcid) {
        profileLastModifiedAspect.updateLastModifiedDateAndIndexingStatus(orcid);
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
    public void disableApplication(Long tokenId, String userOrcid) {
        orcidOauth2TokenService.disableAccessToken(tokenId, userOrcid);
    }

    @Override
    public List<ApplicationSummary> getApplications(String orcid) {
        List<OrcidOauth2TokenDetail> tokenDetails = orcidOauth2TokenService.findByUserName(orcid);
        List<ApplicationSummary> applications = new ArrayList<ApplicationSummary>();
        Map<Pair<String, Set<ScopePathType>>, ApplicationSummary> existingApplications = new HashMap<Pair<String, Set<ScopePathType>>, ApplicationSummary>();
        if (tokenDetails != null && !tokenDetails.isEmpty()) {
            for (OrcidOauth2TokenDetail token : tokenDetails) {
                if (token.getTokenDisabled() == null || !token.getTokenDisabled()) {
                    ClientDetailsEntity client = clientDetailsEntityCacheManager.retrieve(token.getClientDetailsId());
                    if (client != null) {
                        ApplicationSummary applicationSummary = new ApplicationSummary();
                        // Check the scopes
                        Set<ScopePathType> scopesGrantedToClient = ScopePathType.getScopesFromSpaceSeparatedString(token.getScope());
                        Map<ScopePathType, String> scopePathMap = new HashMap<ScopePathType, String>();
                        String scopeFullPath = ScopePathType.class.getName() + ".";
                        for (ScopePathType tempScope : scopesGrantedToClient) {
                            try {
                                scopePathMap.put(tempScope, localeManager.resolveMessage(scopeFullPath + tempScope.toString()));
                            } catch (NoSuchMessageException e) {
                                LOGGER.warn("No message to display for scope " + tempScope.toString());
                            }
                        }
                        // If there is at least one scope in this token, fill
                        // the application summary element
                        if (!scopePathMap.isEmpty()) {
                            applicationSummary.setScopePaths(scopePathMap);
                            applicationSummary.setOrcidHost(orcidUrlManager.getBaseHost());
                            applicationSummary.setOrcidUri(orcidUrlManager.getBaseUrl() + "/" + client.getId());
                            applicationSummary.setOrcidPath(client.getId());
                            applicationSummary.setName(client.getClientName());
                            applicationSummary.setWebsiteValue(client.getClientWebsite());
                            applicationSummary.setApprovalDate(token.getDateCreated());
                            applicationSummary.setTokenId(String.valueOf(token.getId()));
                            // Add member information
                            if (!PojoUtil.isEmpty(client.getGroupProfileId())) {
                                ProfileEntity member = profileEntityCacheManager.retrieve(client.getGroupProfileId());
                                applicationSummary.setGroupOrcidPath(member.getId());
                                applicationSummary.setGroupName(getMemberDisplayName(member));
                            }

                            if (shouldBeAddedToTheApplicationsList(applicationSummary, scopesGrantedToClient, existingApplications)) {
                                applications.add(applicationSummary);
                            }
                        }
                    }
                }
            }
        }

        return applications;
    }

    private boolean shouldBeAddedToTheApplicationsList(ApplicationSummary application, Set<ScopePathType> scopes,
            Map<Pair<String, Set<ScopePathType>>, ApplicationSummary> existingApplications) {
        boolean result = false;
        Pair<String, Set<ScopePathType>> key = Pair.of(application.getOrcidPath(), scopes);
        if (!existingApplications.containsKey(key)) {
            result = true;
        } else {
            Date existingAppCreatedDate = existingApplications.get(key).getApprovalDate();

            // This case should never happen
            if (existingAppCreatedDate == null) {
                result = true;
            }

            if (application.getApprovalDate().before(existingAppCreatedDate)) {
                result = true;
            }
        }

        if (result) {
            existingApplications.put(key, application);
        }
        return result;
    }

    private String getMemberDisplayName(ProfileEntity member) {
        RecordNameEntity recordName = member.getRecordNameEntity();
        if (recordName == null) {
            return StringUtils.EMPTY;
        }

        // If it is a member, return the credit name
        if (OrcidType.GROUP.equals(member.getOrcidType())) {
            return recordName.getCreditName();
        }

        Visibility namesVisibilty = Visibility.fromValue(recordName.getVisibility());
        if (Visibility.PUBLIC.equals(namesVisibilty)) {
            if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                String displayName = recordName.getGivenNames();
                String familyName = recordName.getFamilyName();
                if (StringUtils.isNotBlank(familyName)) {
                    displayName += " " + familyName;
                }
                return displayName;
            }
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getHash(String string) {
        if (PojoUtil.isEmpty(string)) {
            return null;
        }
        try {
            return encryptionManager.sha256Hash(string);
        } catch(NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
        
    }

    @Override
    public String retrivePublicDisplayName(String orcid) {
        String publicName = "";
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile != null) {
            RecordNameEntity recordName = profile.getRecordNameEntity();
            if (recordName != null) {
                Visibility namesVisibility = (recordName.getVisibility() != null) ? Visibility.fromValue(recordName.getVisibility())
                        : Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
                if (Visibility.PUBLIC.equals(namesVisibility)) {
                    if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                        publicName = recordName.getCreditName();
                    } else {
                        publicName = PojoUtil.isEmpty(recordName.getGivenNames()) ? "" : recordName.getGivenNames();
                        publicName += PojoUtil.isEmpty(recordName.getFamilyName()) ? "" : " " + recordName.getFamilyName();
                    }
                }
            }
        }
        return publicName;
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
        profile.setLastModified(new Date());
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

        // Update the visibility for every bio element to the visibility
        // selected by the user
        // Update the bio
        org.orcid.jaxb.model.common_v2.Visibility defaultVisibility = org.orcid.jaxb.model.common_v2.Visibility
                .fromValue(claim.getActivitiesVisibilityDefault().getVisibility().value());
        if (profile.getBiographyEntity() != null) {
            profile.getBiographyEntity().setVisibility(defaultVisibility.name());
        }
        // Update address
        if (profile.getAddresses() != null) {
            for (AddressEntity a : profile.getAddresses()) {
                a.setVisibility(defaultVisibility.name());
            }
        }

        // Update the keywords
        if (profile.getKeywords() != null) {
            for (ProfileKeywordEntity k : profile.getKeywords()) {
                k.setVisibility(defaultVisibility.name());
            }
        }

        // Update the other names
        if (profile.getOtherNames() != null) {
            for (OtherNameEntity o : profile.getOtherNames()) {
                o.setVisibility(defaultVisibility.name());
            }
        }

        // Update the researcher urls
        if (profile.getResearcherUrls() != null) {
            for (ResearcherUrlEntity r : profile.getResearcherUrls()) {
                r.setVisibility(defaultVisibility.name());
            }
        }

        // Update the external identifiers
        if (profile.getExternalIdentifiers() != null) {
            for (ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
                e.setVisibility(defaultVisibility.name());
            }
        }
        profileDao.merge(profile);
        profileDao.flush();        
        
        if(!emailFrequencyManager.emailFrequencyExists(orcid)) {
            if(claim.getSendOrcidNews() == null) {
                emailFrequencyManager.createOnClaim(orcid, false);
            } else {
                emailFrequencyManager.createOnClaim(orcid, claim.getSendOrcidNews().getValue());
            }            
        } else {
            if(claim.getSendOrcidNews() == null) {
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
        try {
            String emailHash = encryptionManager.sha256Hash(email.trim().toLowerCase());
            return profileDao.getClaimedStatusByEmailHash(emailHash);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void reactivate(String orcid, String primaryEmail, Reactivation reactivation) {  
        ArrayList<String> emailsToNotify = new ArrayList<String>();
        // Null reactivation object means the reactivation request comes from an admin
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                LOGGER.info("About to reactivate record, orcid={}", orcid);
                String orcid = emailManager.findOrcidIdByEmail(primaryEmail);
                // Populate primary email
                emailManager.reactivatePrimaryEmail(orcid, primaryEmail, getHash(primaryEmail));
                if(reactivation == null) {
                    // Delete any non primary email
                    emailManager.clearEmailsAfterReactivation(orcid);                    
                } else {
                    // Populate additional emails
                    if(reactivation.getEmailsAdditional() != null && !reactivation.getEmailsAdditional().isEmpty()) {
                        for(Text additionalEmail : reactivation.getEmailsAdditional()) {
                            if(!PojoUtil.isEmpty(additionalEmail)) {
                                String email = additionalEmail.getValue();
                                String hash = getHash(email);
                                boolean isNewEmailOrShouldNotify = emailManager.reactivateOrCreate(orcid, email, hash, reactivation.getActivitiesVisibilityDefault().getVisibility());                                      
                                if(isNewEmailOrShouldNotify) {
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
                if(reactivation != null) {
                    profileEntity.setEncryptedPassword(encryptionManager.hashForInternalUse(reactivation.getPassword().getValue()));
                    profileEntity.setActivitiesVisibilityDefault(reactivation.getActivitiesVisibilityDefault().getVisibility().name());
                    RecordNameEntity recordNameEntity = profileEntity.getRecordNameEntity();
                    recordNameEntity.setGivenNames(reactivation.getGivenNames().getValue());
                    recordNameEntity.setFamilyName(reactivation.getFamilyNames().getValue());                    
                }
                profileDao.merge(profileEntity);               
                LOGGER.info("Record orcid={} successfully reactivated", orcid);
                return true;
            }            
        });
        
        // Notify any new email address
        if(!emailsToNotify.isEmpty()) {
            for(String emailToNotify: emailsToNotify) {
                notificationManager.sendVerificationEmail(orcid, emailToNotify);
            }
        }
        
    }

    @Override
    public void updatePassword(String orcid, String password) {
        String encryptedPassword = encryptionManager.hashForInternalUse(password);
        profileDao.updateEncryptedPassword(orcid, encryptedPassword);
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.RESET_PASSWORD, orcid);
    }

    @Override
    public void updateSecurityQuestion(String orcid, Integer questionId, String answer) {
        String encryptedAnswer = encryptionManager.encryptForInternalUse(answer);
        profileDao.updateSecurityQuestion(orcid, questionId, questionId != null ? encryptedAnswer : null);
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
    public boolean lockProfile(String orcid, String lockReason, String description) {
        boolean wasLocked = profileDao.lockProfile(orcid, lockReason, description);
        if (wasLocked) {
            notificationManager.sendOrcidLockedEmail(orcid);
        }
        return wasLocked;
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

    @Override
    public void disable2FA(String orcid) {
        profileDao.disable2FA(orcid);
    }

    @Override
    public void enable2FA(String orcid) {
        profileDao.enable2FA(orcid);
    }

    @Override
    public void update2FASecret(String orcid, String secret) {
        profileDao.update2FASecret(orcid, secret);
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

        // Remove biography
        if (biographyManager.exists(orcid)) {
            Biography deprecatedBio = new Biography();
            deprecatedBio.setContent(null);
            deprecatedBio.setVisibility(Visibility.PRIVATE);
            biographyManager.updateBiography(orcid, deprecatedBio);
        }

        // Set the deactivated names
        if (recordNameManager.exists(orcid)) {
            Name name = new Name();
            name.setCreditName(new CreditName());
            name.setGivenNames(new GivenNames("Given Names Deactivated"));
            name.setFamilyName(new FamilyName("Family Name Deactivated"));
            name.setVisibility(Visibility.PUBLIC);
            name.setPath(orcid);
            recordNameManager.updateRecordName(orcid, name);
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