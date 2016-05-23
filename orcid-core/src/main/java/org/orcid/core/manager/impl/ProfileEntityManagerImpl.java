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
package org.orcid.core.manager.impl;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.activities.ActivitiesGroup;
import org.orcid.core.utils.activities.ActivitiesGroupGenerator;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.Educations;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.Employments;
import org.orcid.jaxb.model.record.summary_rc2.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.Fundings;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroupKey;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc2.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.GroupAble;
import org.orcid.jaxb.model.record_rc2.GroupableActivity;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 10/02/2012
 */
@Service("profileEntityManager")
public class ProfileEntityManagerImpl implements ProfileEntityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEntityManagerImpl.class);
    
    @Resource
    private ProfileDao profileDao;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private AffiliationsManager affiliationsManager;

    @Resource
    private ProfileFundingManager fundingManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private AddressManager addressManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private PersonalDetailsManager personalDetailsManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;
    
    @Resource
    private EmailManager emailManager;
    
    @Resource
    private OrgAffiliationRelationDao orgRelationAffiliationDao;    
    
    @Resource
    private OtherNameManager otherNamesManager;
    
    @Resource
    private RecordNameManager recordNameManager;
    
    @Resource
    private BiographyManager biographyManager;
    
    @Resource
    private UserConnectionDao userConnectionDao;
    
    @Resource
    private OrcidProfileManager orcidProfileManager;
    
    @Resource
    private NotificationManager notificationManager;
    
    @Resource
    private SourceManager sourceManager;
    
    /**
     * Fetch a ProfileEntity from the database Instead of calling this function,
     * use the cache profileEntityCacheManager whenever is possible
     */
    @Override
    public ProfileEntity findByOrcid(String orcid) {
        return profileDao.find(orcid);
    }

    @Override
    public String findByCreditName(String creditName) {
        RecordNameEntity recordName = recordNameManager.findByCreditName(creditName);
        if(recordName == null) {
            return null;
        }
        return recordName.getProfile().getId();
    }

    @Override
    public boolean orcidExists(String orcid) {
        return profileDao.orcidExists(orcid);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        return profileDao.hasBeenGivenPermissionTo(giverOrcid, receiverOrcid);
    }

    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        return profileDao.existsAndNotClaimedAndBelongsTo(messageOrcid, clientId);
    }

    @Override
    public Long getConfirmedProfileCount() {
        return profileDao.getConfirmedProfileCount();
    }

    /**
     * Deprecates a profile
     * 
     * @param deprecatedProfile
     *            The profile that want to be deprecated
     * @param primaryProfile
     *            The primary profile for the deprecated profile
     * @return true if the account was successfully deprecated, false otherwise
     */
    @Override
    @Transactional 
    public boolean deprecateProfile(ProfileEntity deprecatedProfile, ProfileEntity primaryProfile) {
        deprecatedProfile.setActivitiesVisibilityDefault(org.orcid.jaxb.model.message.Visibility.PRIVATE); 
        boolean wasDeprecated = profileDao.deprecateProfile(deprecatedProfile, primaryProfile.getId());        
        // If it was successfully deprecated
        if (wasDeprecated) {
            LOGGER.info("Account {} was deprecated to primary account: {}", deprecatedProfile.getId(), primaryProfile.getId());
            ProfileEntity deprecated = profileDao.find(deprecatedProfile.getId());
            
            String deprecatedOrcid = deprecatedProfile.getId();
            String primaryOrcid = primaryProfile.getId();
            
            // Remove works
            if (deprecated.getWorks() != null) {
                for (WorkEntity work : deprecated.getWorks()) {
                    List<Long> works = new ArrayList<Long>();
                    works.add(work.getId());
                    workManager.removeWorks(deprecated.getId(), works);
                }
            }
            
            // Remove funding
            if (deprecated.getProfileFunding() != null) {
                for(ProfileFundingEntity funding : deprecated.getProfileFunding()) {
                    fundingManager.removeProfileFunding(funding.getProfile().getId(), funding.getId());
                }
            }
            
            // Remove affiliations
            if (deprecated.getOrgAffiliationRelations() != null) {
                for(OrgAffiliationRelationEntity affiliation : deprecated.getOrgAffiliationRelations()) {                    
                    orgRelationAffiliationDao.removeOrgAffiliationRelation(affiliation.getProfile().getId(), affiliation.getId());
                }
            }
            
            // Remove external identifiers
            if (deprecated.getExternalIdentifiers() != null) {
                for (ExternalIdentifierEntity externalIdentifier : deprecated.getExternalIdentifiers()) {
                    externalIdentifierManager.deleteExternalIdentifier(deprecated.getId(), externalIdentifier.getId(), false);
                }
            }

            // Remove researcher urls
            if(deprecated.getResearcherUrls() != null) {
                for(ResearcherUrlEntity rUrl : deprecated.getResearcherUrls()) {
                    researcherUrlManager.deleteResearcherUrl(deprecatedOrcid, rUrl.getId(), false);
                }
            }
            
            // Remove other names
            if(deprecated.getOtherNames() != null) {
                for(OtherNameEntity otherName : deprecated.getOtherNames()) {
                    otherNamesManager.deleteOtherName(deprecatedOrcid, otherName.getId(), false);
                }                            
            }
            
            // Remove keywords
            if(deprecated.getKeywords() != null) {
                for(ProfileKeywordEntity keyword : deprecated.getKeywords()) {
                    profileKeywordManager.deleteKeyword(deprecatedOrcid, keyword.getId(), false);
                }                                                        
            }
            
            //Remove biography
            Biography deprecatedBio = new Biography();
            deprecatedBio.setContent(null);
            deprecatedBio.setVisibility(Visibility.PRIVATE);
            
            BiographyEntity bioEntity = deprecated.getBiographyEntity();
            if(bioEntity != null) {
                biographyManager.updateBiography(deprecatedOrcid,deprecatedBio);
            } else {
                biographyManager.createBiography(deprecatedOrcid, deprecatedBio);    
            }
            
            
            //Set the deactivated names
            RecordNameEntity recordName = deprecated.getRecordNameEntity();
            if(recordName != null) {
                recordName.setCreditName(null);
                recordName.setGivenNames("Given Names Deactivated");
                recordName.setFamilyName("Family Name Deactivated");
                recordName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
                recordName.setProfile(new ProfileEntity(deprecatedOrcid));
                recordNameManager.updateRecordName(recordName);                
            } 
                                                        
            // Move all emails to the primary email
            Set<EmailEntity> deprecatedAccountEmails = deprecated.getEmails();
            if (deprecatedAccountEmails != null) {
                // For each email in the deprecated profile                            
                for (EmailEntity email : deprecatedAccountEmails) {
                    // Delete each email from the deprecated
                    // profile
                    LOGGER.info("About to move email {} from profile {} to profile {}", new Object[] {email.getId(), deprecatedOrcid, primaryOrcid });
                    emailManager.moveEmailToOtherAccount(email.getId(), deprecatedOrcid, primaryOrcid);
                }
            }
            
            return true;
        }
        
        return false; 
    }

    /**
     * Return the list of profiles that belongs to the provided OrcidType
     * 
     * @param type
     *            OrcidType that indicates the profile type we want to fetch
     * @return the list of profiles that belongs to the specified type
     */
    @Override
    public List<ProfileEntity> findProfilesByOrcidType(OrcidType type) {
        if (type == null)
            return new ArrayList<ProfileEntity>();
        return profileDao.findProfilesByOrcidType(type);
    }

    /**
     * Enable developer tools
     * 
     * @param profile
     *            The profile to update
     * @return true if the developer tools where enabled on that profile
     */
    @Override
    public boolean enableDeveloperTools(OrcidProfile profile) {
        boolean result = profileDao.updateDeveloperTools(profile.getOrcidIdentifier().getPath(), true);
        return result;
    }

    /**
     * Disable developer tools
     * 
     * @param profile
     *            The profile to update
     * @return true if the developer tools where disabeled on that profile
     */
    @Override
    public boolean disableDeveloperTools(OrcidProfile profile) {
        boolean result = profileDao.updateDeveloperTools(profile.getOrcidIdentifier().getPath(), false);
        return result;
    }

    @Override
    public boolean isProfileClaimed(String orcid) {
        return profileDao.getClaimedStatus(orcid);
    }

    /**
     * Get the client type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the client type, null if it is not a client
     */
    @Override
    public ClientType getClientType(String orcid) {
        return profileDao.getClientType(orcid);
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
        return profileDao.getGroupType(orcid);
    }

    @Override
    @Transactional
    public ActivitiesSummary getActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, false);
    }

    @Override
    @Transactional
    public ActivitiesSummary getPublicActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, true);
    }

    public ActivitiesSummary getActivitiesSummary(String orcid, boolean justPublic) {
        if (!orcidExists(orcid)) {
            throw new NoResultException();
        }
        Date lastModified = profileDao.retrieveLastModifiedDate(orcid);
        long lastModifiedTime = lastModified.getTime();
        ActivitiesSummary activities = new ActivitiesSummary();

        // Set educations
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid, lastModifiedTime);
        if (!educationsList.isEmpty()) {
            Educations educations = new Educations();
            for (EducationSummary summary : educationsList) {
                if (justPublic) {
                    if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        educations.getSummaries().add(summary);
                    }
                } else {
                    educations.getSummaries().add(summary);
                }
            }
            LastModifiedDatesHelper.calculateLatest(educations);
            activities.setEducations(educations);
        }

        // Set employments
        List<EmploymentSummary> employmentList = affiliationsManager.getEmploymentSummaryList(orcid, lastModifiedTime);
        if (!employmentList.isEmpty()) {
            Employments employments = new Employments();
            for (EmploymentSummary summary : employmentList) {
                if (justPublic) {
                    if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                        employments.getSummaries().add(summary);
                    }
                } else {
                    employments.getSummaries().add(summary);
                }
            }
            LastModifiedDatesHelper.calculateLatest(employments);
            activities.setEmployments(employments);
        }

        // Set fundings
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid, lastModifiedTime);
        Fundings fundings = groupFundings(fundingSummaries, justPublic);
        LastModifiedDatesHelper.calculateLatest(fundings);
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid, lastModifiedTime);
        PeerReviews peerReviews = groupPeerReviews(peerReviewSummaries, justPublic);
        LastModifiedDatesHelper.calculateLatest(peerReviews);
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid, lastModifiedTime);
        Works works = groupWorks(workSummaries, justPublic);
        LastModifiedDatesHelper.calculateLatest(works);
        activities.setWorks(works);

        LastModifiedDatesHelper.calculateLatest(activities);
        return activities;
    }

    private Works groupWorks(List<WorkSummary> works, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        Works result = new Works();
        // Group all works
        for (WorkSummary work : works) {
            if (justPublic && !work.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                // If it is just public and the work is not public, just ignore
                // it
            } else {
                groupGenerator.group(work);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            WorkGroup workGroup = new WorkGroup();
            // Fill the work groups with the external identifiers
            for (GroupAble extId : externalIdentifiers) {
                ExternalID workExtId = (ExternalID) extId;
                workGroup.getIdentifiers().getExternalIdentifier().add(workExtId.clone());
            }

            // Fill the work group with the list of activities
            for (GroupableActivity activity : activities) {
                WorkSummary workSummary = (WorkSummary) activity;
                workGroup.getWorkSummary().add(workSummary);
            }

            // Sort the works
            Collections.sort(workGroup.getWorkSummary(), new GroupableActivityComparator());
            result.getWorkGroup().add(workGroup);
        }
        return result;
    }

    private Fundings groupFundings(List<FundingSummary> fundings, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        Fundings result = new Fundings();
        for (FundingSummary funding : fundings) {
            if (justPublic && !funding.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                // If it is just public and the funding is not public, just
                // ignore it
            } else {
                groupGenerator.group(funding);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            FundingGroup fundingGroup = new FundingGroup();

            // Fill the funding groups with the external identifiers
            for (GroupAble extId : externalIdentifiers) {
                ExternalID fundingExtId = (ExternalID) extId;
                fundingGroup.getIdentifiers().getExternalIdentifier().add(fundingExtId.clone());
            }

            // Fill the funding group with the list of activities
            for (GroupableActivity activity : activities) {
                FundingSummary fundingSummary = (FundingSummary) activity;
                fundingGroup.getFundingSummary().add(fundingSummary);
            }

            // Sort the fundings
            Collections.sort(fundingGroup.getFundingSummary(), new GroupableActivityComparator());

            result.getFundingGroup().add(fundingGroup);
        }

        return result;
    }

    private PeerReviews groupPeerReviews(List<PeerReviewSummary> peerReviews, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        PeerReviews result = new PeerReviews();
        for (PeerReviewSummary peerReview : peerReviews) {
            if (justPublic && !peerReview.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                // If it is just public and the funding is not public, just
                // ignore it
            } else {
                groupGenerator.group(peerReview);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> groupKeys = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            PeerReviewGroup peerReviewGroup = new PeerReviewGroup();
            // Fill the peer review groups with the external identifiers
            for (GroupAble groupKey : groupKeys) {
                PeerReviewGroupKey key = (PeerReviewGroupKey) groupKey;
                ExternalID id = new ExternalID();
                id.setType(PeerReviewGroupKey.KEY_NAME);//TODO: this is not nice
                id.setValue(key.getGroupId());
                peerReviewGroup.getIdentifiers().getExternalIdentifier().add(id);
            }

            // Fill the peer review group with the list of activities
            for (GroupableActivity activity : activities) {
                PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
                peerReviewGroup.getPeerReviewSummary().add(peerReviewSummary);
            }

            // Sort the peer reviews
            Collections.sort(peerReviewGroup.getPeerReviewSummary(), new GroupableActivityComparator());

            result.getPeerReviewGroup().add(peerReviewGroup);
        }

        return result;
    }

    public Date getLastModified(String orcid) {
        return profileDao.retrieveLastModifiedDate(orcid);
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
    public List<ApplicationSummary> getApplications(List<OrcidOauth2TokenDetail> tokenDetails) {
        return jpa2JaxbAdapter.getApplications(tokenDetails);
    }

    @Override
    public String getOrcidHash(String orcid) throws NoSuchAlgorithmException {
        if (PojoUtil.isEmpty(orcid)) {
            return null;
        }
        return encryptionManager.sha256Hash(orcid);
    }

    @Override
    public String retrivePublicDisplayName(String orcid) {
        String publicName = "";
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile != null) {
            RecordNameEntity recordName = profile.getRecordNameEntity();
            if(recordName != null) {
                Visibility namesVisibility = (recordName.getVisibility() != null) ? Visibility.fromValue(recordName.getVisibility().value())
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
    public Person getPersonDetails(String orcid) {        
        Date lastModified = getLastModified(orcid);
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        Person person = new Person();
        Biography biography = biographyManager.getBiography(orcid);
        if(biography != null) {
            person.setBiography(biography);
        } 
        
        person.setName(personalDetailsManager.getName(orcid));
        
        person.setAddresses(addressManager.getAddresses(orcid, lastModifiedTime));
        LastModifiedDate latest = person.getAddresses().getLastModifiedDate();
        
        person.setExternalIdentifiers(externalIdentifierManager.getExternalIdentifiers(orcid, lastModifiedTime));
        LastModifiedDate temp = person.getExternalIdentifiers().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setKeywords(profileKeywordManager.getKeywords(orcid, lastModifiedTime));
        temp = person.getKeywords().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);                
        
        person.setOtherNames(otherNameManager.getOtherNames(orcid, lastModifiedTime));
        temp = person.getOtherNames().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setResearcherUrls(researcherUrlManager.getResearcherUrls(orcid, lastModifiedTime));  
        temp = person.getResearcherUrls().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setEmails(emailManager.getEmails(orcid, lastModifiedTime));
        temp = person.getEmails().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setLastModifiedDate(latest);    
        return person;
    }

    @Override
    @Transactional
    public Person getPublicPersonDetails(String orcid) {
        Person person = new Person();
        
        Biography bio = biographyManager.getPublicBiography(orcid);        
        if(bio != null) {
            person.setBiography(bio);
        } 
        
        Name name = personalDetailsManager.getName(orcid);
        if(Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }
		
        Date lastModified = getLastModified(orcid);
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        person.setAddresses(addressManager.getPublicAddresses(orcid, lastModifiedTime));
        LastModifiedDate latest = person.getAddresses().getLastModifiedDate();
        
        person.setExternalIdentifiers(externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime));
        LastModifiedDate temp = person.getExternalIdentifiers().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setKeywords(profileKeywordManager.getPublicKeywords(orcid, lastModifiedTime));
        temp = person.getKeywords().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setOtherNames(otherNameManager.getPublicOtherNames(orcid, lastModifiedTime));
        temp = person.getOtherNames().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setResearcherUrls(researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime));
        temp = person.getResearcherUrls().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);

        person.setEmails(emailManager.getPublicEmails(orcid, lastModifiedTime));
        temp = person.getEmails().getLastModifiedDate();
        latest = LastModifiedDatesHelper.returnLatestLastModifiedDate(latest, temp);
        
        person.setLastModifiedDate(latest);
        return person;
    }

    @Override
    @Transactional
    public boolean claimProfileAndUpdatePreferences(String orcid, String email, Locale locale, Claim claim) {
        //Verify the email
        boolean emailVerified = emailManager.verifySetCurrentAndPrimary(orcid, email);
        if(!emailVerified) {
            throw new InvalidParameterException("Unable to claim and verify email: " + email + " for user: " + orcid);
        }
        
        //Update the profile entity fields
        ProfileEntity profile = profileDao.find(orcid);
        profile.setLastModified(new Date());
        profile.setIndexingStatus(IndexingStatus.REINDEX);
        profile.setClaimed(true);
        profile.setCompletedDate(new Date());
        profile.setLocale(locale);
        if(claim != null) {
            profile.setSendChangeNotifications(claim.getSendChangeNotifications().getValue());
            profile.setSendOrcidNews(claim.getSendOrcidNews().getValue());
            profile.setActivitiesVisibilityDefault(claim.getActivitiesVisibilityDefault().getVisibility());
        }
                
        //Update the visibility for every bio element to the visibility selected by the user
        //Update the bio
        org.orcid.jaxb.model.common_rc2.Visibility defaultVisibility = org.orcid.jaxb.model.common_rc2.Visibility.fromValue(claim.getActivitiesVisibilityDefault().getVisibility().value());
        if(profile.getBiographyEntity() != null) {
            profile.getBiographyEntity().setVisibility(defaultVisibility);
        }
        //Update address
        if(profile.getAddresses() != null) {
            for(AddressEntity a : profile.getAddresses()) {
                a.setVisibility(defaultVisibility);
            }
        }
        
        //Update the keywords
        if(profile.getKeywords() != null) {
            for(ProfileKeywordEntity k : profile.getKeywords()) {
                k.setVisibility(defaultVisibility);
            }
        }
        
        //Update the other names
        if(profile.getOtherNames() != null) {
            for(OtherNameEntity o : profile.getOtherNames()) {
                o.setVisibility(defaultVisibility);
            }
        }
        
        //Update the researcher urls
        if(profile.getResearcherUrls() != null) {
            for(ResearcherUrlEntity r : profile.getResearcherUrls()) {
                r.setVisibility(defaultVisibility);
            }
        }
        
        //Update the external identifiers
        if(profile.getExternalIdentifiers() != null) {
            for(ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
                e.setVisibility(defaultVisibility);
            }
        }
        profileDao.merge(profile);
        profileDao.flush();
        return true;
    }

    @Override
    @Transactional
    public boolean deactivateRecord(String orcid) {
        //Clear the record
        ProfileEntity toClear = profileDao.find(orcid);
        toClear.setLastModified(new Date());
        toClear.setDeactivationDate(new Date());
        toClear.setActivitiesVisibilityDefault(org.orcid.jaxb.model.message.Visibility.PRIVATE);
        toClear.setIndexingStatus(IndexingStatus.REINDEX);
        
        // Remove works
        if (toClear.getWorks() != null) {
            toClear.getWorks().clear();
        }
        
        // Remove funding
        if (toClear.getProfileFunding() != null) {
            toClear.getProfileFunding().clear();
        }
        
        // Remove affiliations
        if (toClear.getOrgAffiliationRelations() != null) {
            toClear.getOrgAffiliationRelations().clear();
        }
        
        // Remove external identifiers
        if (toClear.getExternalIdentifiers() != null) {
            toClear.getExternalIdentifiers().clear();
        }

        // Remove researcher urls
        if(toClear.getResearcherUrls() != null) {
            toClear.getResearcherUrls().clear();
        }
        
        // Remove other names
        if(toClear.getOtherNames() != null) {
            toClear.getOtherNames().clear();      
        }
        
        // Remove keywords
        if(toClear.getKeywords() != null) {
            toClear.getKeywords().clear();                                                        
        }        
        
        // Remove address
        if(toClear.getAddresses() != null) {
            toClear.getAddresses().clear();
        }
        
        BiographyEntity bioEntity = toClear.getBiographyEntity();
        if(bioEntity != null) {
            bioEntity.setBiography("");
            bioEntity.setVisibility(Visibility.PRIVATE);
        } 
                
        //Set the deactivated names
        RecordNameEntity recordName = toClear.getRecordNameEntity();
        if(recordName != null) {
            recordName.setCreditName(null);
            recordName.setGivenNames("Given Names Deactivated");
            recordName.setFamilyName("Family Name Deactivated");
            recordName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);                                      
        }
        
        Set<EmailEntity> emails = toClear.getEmails();
        if (emails != null) {
            // For each email in the deprecated profile                            
            for (EmailEntity email : emails) {
                email.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);
            }        
        }
        
        profileDao.merge(toClear);
        profileDao.flush();
        
        //Delete all connections
        userConnectionDao.deleteByOrcid(orcid);                
        
        OrcidProfile deactivated = orcidProfileManager.retrieveOrcidProfile(orcid);
        
        notificationManager.sendAmendEmail(deactivated, AmendedSection.UNKNOWN);
        return false;
    }    
}

class GroupableActivityComparator implements Comparator<GroupableActivity> {

    @Override
    public int compare(GroupableActivity o1, GroupableActivity o2) {
        return o1.compareTo(o2);
    }

}