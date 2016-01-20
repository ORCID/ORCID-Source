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
import org.orcid.core.adapter.JpaJaxbGivenPermissionByAdapter;
import org.orcid.core.adapter.JpaJaxbGivenPermissionToAdapter;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.activities.ActivitiesGroup;
import org.orcid.core.utils.activities.ActivitiesGroupGenerator;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.Educations;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.Employments;
import org.orcid.jaxb.model.record.summary_rc2.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.Fundings;
import org.orcid.jaxb.model.record.summary_rc2.Identifier;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroupKey;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc2.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.FundingExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.GroupAble;
import org.orcid.jaxb.model.record_rc2.GroupableActivity;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Delegation;
import org.orcid.jaxb.model.record_rc2.DelegationDetails;
import org.orcid.jaxb.model.record_rc2.GivenPermissionBy;
import org.orcid.jaxb.model.record_rc2.GivenPermissionTo;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 10/02/2012
 */
@Service("profileEntityManager")
public class ProfileEntityManagerImpl implements ProfileEntityManager {

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
    private JpaJaxbGivenPermissionToAdapter jpaJaxbGivenPermissionToAdapter;

    @Resource
    private JpaJaxbGivenPermissionByAdapter jpaJaxbGivenPermissionByAdapter;
    
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
        return profileDao.findOrcidByCreditName(creditName);
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
     * Updates a profile with the given OrcidProfile object
     * 
     * @param orcidProfile
     *            The object that will be used to update the database profile
     * @return true if the profile was successfully updated on database, false
     *         otherwise
     */
    @Override
    public boolean updateProfile(OrcidProfile orcidProfile) {
        ProfileEntity profile = generateProfileEntityWithBio(orcidProfile);
        return profileDao.updateProfile(profile);
    }

    /**
     * Updates a profile entity object on database.
     * 
     * @param profile
     *            The profile object to update
     * @return true if the profile was successfully updated.
     */
    @Override
    public boolean updateProfile(ProfileEntity profile) {
        return profileDao.updateProfile(profile);
    }

    /**
     * Generate a ProfileEntity object with the bio information populated from
     * the info that comes from the OrcidProfile parameter
     * 
     * @param orcidProfile
     * @return A Profile Entity containing the bio information that comes in the
     *         OrcidProfile parameter
     */
    private ProfileEntity generateProfileEntityWithBio(OrcidProfile orcidProfile) {
        ProfileEntity profile = new ProfileEntity();
        profile.setCreditName(orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        profile.setFamilyName(orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        profile.setGivenNames(orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        profile.setBiography(orcidProfile.getOrcidBio().getBiography().getContent());
        profile.setIso2Country(orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue());
        profile.setBiographyVisibility(orcidProfile.getOrcidBio().getBiography().getVisibility());
        profile.setKeywordsVisibility(orcidProfile.getOrcidBio().getKeywords().getVisibility());
        profile.setResearcherUrlsVisibility(orcidProfile.getOrcidBio().getResearcherUrls().getVisibility());
        profile.setOtherNamesVisibility(orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        profile.setNamesVisibility(orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility());
        profile.setProfileAddressVisibility(orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility());
        profile.setId(orcidProfile.getOrcidIdentifier().getPath());
        return profile;
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
    public boolean deprecateProfile(ProfileEntity deprecatedProfile, ProfileEntity primaryProfile) {
        return profileDao.deprecateProfile(deprecatedProfile.getId(), primaryProfile.getId());
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
    public Iso3166Country getCountry(String orcid) {
        return profileDao.getCountry(orcid);
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

    /**
     * Set the locked status of an account to true
     * 
     * @param orcid
     *            the id of the profile that should be locked
     * @return true if the account was locked
     */
    @Override
    public boolean lockProfile(String orcid) {
        return profileDao.lockProfile(orcid);
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

    /**
     * Check if a profile is locked
     * 
     * @param orcid
     *            the id of the profile to check
     * @return true if the account is locked
     */
    @Override
    public boolean isLocked(String orcid) {
        if (PojoUtil.isEmpty(orcid))
            return false;
        return profileDao.isLocked(orcid);
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

            activities.setEmployments(employments);
        }

        // Set fundings
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid, lastModifiedTime);
        Fundings fundings = groupFundings(fundingSummaries, justPublic);
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid, lastModifiedTime);
        PeerReviews peerReviews = groupPeerReviews(peerReviewSummaries, justPublic);
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid, lastModifiedTime);
        Works works = groupWorks(workSummaries, justPublic);
        activities.setWorks(works);

        LastModifiedDatesHelper.calculateLatest(activities);
        return activities;
    }

    private Works groupWorks(List<WorkSummary> works, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        Works result = new Works();
        // Group all works
        for (WorkSummary work : works) {
            if (justPublic && !work.getVisibility().equals(org.orcid.jaxb.model.common.Visibility.PUBLIC)) {
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
                WorkExternalIdentifier workExtId = (WorkExternalIdentifier) extId;
                workGroup.getIdentifiers().getIdentifier().add(Identifier.fromWorkExternalIdentifier(workExtId));
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
            if (justPublic && !funding.getVisibility().equals(org.orcid.jaxb.model.common.Visibility.PUBLIC)) {
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
                FundingExternalIdentifier fundingExtId = (FundingExternalIdentifier) extId;
                fundingGroup.getIdentifiers().getIdentifier().add(Identifier.fromFundingExternalIdentifier(fundingExtId));
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
            if (justPublic && !peerReview.getVisibility().equals(org.orcid.jaxb.model.common.Visibility.PUBLIC)) {
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
                peerReviewGroup.getIdentifiers().getIdentifier().add(Identifier.fromPeerReviewGroupKey(key));
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
    public Visibility getResearcherUrlDefaultVisibility(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Visibility result = profile.getResearcherUrlsVisibility() == null ? Visibility.fromValue(OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility().value())
                : Visibility.fromValue(profile.getResearcherUrlsVisibility().value());
        return result;
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
            Visibility namesVisibility = (profile.getNamesVisibility() != null) ? Visibility.fromValue(profile.getNamesVisibility().value())
                    : Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
            if (Visibility.PUBLIC.equals(namesVisibility)) {
                if (!PojoUtil.isEmpty(profile.getCreditName())) {
                    publicName = profile.getCreditName();
                } else {
                    publicName = PojoUtil.isEmpty(profile.getGivenNames()) ? "" : profile.getGivenNames();
                    publicName += PojoUtil.isEmpty(profile.getFamilyName()) ? "" : " " + profile.getFamilyName();
                }
            }
        }
        return publicName;
    }

    @Override
    public Biography getBiography(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Biography bio = new Biography();
        bio.setVisibility(Visibility.fromValue(
                profile.getBiographyVisibility() == null ? OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value() : profile.getBiographyVisibility().value()));
        bio.setContent(profile.getBiography());
        return bio;
    }

    @Override
    @Transactional
    public Person getPersonDetails(String orcid) {
        Person person = new Person();
        person.setBiography(getBiography(orcid));
        person.setAddresses(addressManager.getAddresses(orcid));
        person.setExternalIdentifiers(externalIdentifierManager.getExternalIdentifiersV2(orcid));
        person.setKeywords(profileKeywordManager.getKeywords(orcid));
        person.setName(personalDetailsManager.getName(orcid));
        person.setOtherNames(otherNameManager.getOtherNames(orcid)); 
        person.setResearcherUrls(researcherUrlManager.getResearcherUrls(orcid));             
        person.setEmails(emailManager.getEmails(orcid));
        
        //The rest should come from the ProfileEntity object
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);       
        Delegation delegation = null;
        Set<GivenPermissionToEntity> givenPermissionTo = profile.getGivenPermissionTo();
        Set<GivenPermissionByEntity> givenPermissionBy = profile.getGivenPermissionBy();
        
        if(givenPermissionTo != null || givenPermissionBy != null) {
            delegation = new Delegation();
            if(givenPermissionTo != null) {
                List<DelegationDetails> detailsList = jpaJaxbGivenPermissionToAdapter.toDelegationDetailsList(givenPermissionTo);
                List<GivenPermissionTo> givenPermissionToList = new ArrayList<GivenPermissionTo>();
                for(DelegationDetails details : detailsList) {
                    GivenPermissionTo to = new GivenPermissionTo();
                    to.setDelegationDetails(details);
                    givenPermissionToList.add(to);
                }
                delegation.setGivenPermissionTo(givenPermissionToList);
            }
            
            if(givenPermissionBy != null) {
                List<DelegationDetails> detailsList = jpaJaxbGivenPermissionByAdapter.toDelegationDetailsList(givenPermissionBy);
                List<GivenPermissionBy> givenPermissionByList = new ArrayList<GivenPermissionBy>();
                for(DelegationDetails details : detailsList) {
                    GivenPermissionBy by = new GivenPermissionBy();
                    by.setDelegationDetails(details);
                    givenPermissionByList.add(by);
                }
                delegation.setGivenPermissionBy(givenPermissionByList);
            }            
        }
        
        person.setDelegation(delegation);
                                
        // TODO: implement
        person.setApplications(null);
              
        return person;
    }

    @Override
    @Transactional
    public Person getPublicPersonDetails(String orcid) {
        Person person = new Person();
        
        Biography bio = getBiography(orcid);
        if(Visibility.PUBLIC.equals(bio.getVisibility())) {
            person.setBiography(bio);
        }
        
        Name name = personalDetailsManager.getName(orcid);
        if(Visibility.PUBLIC.equals(name.getVisibility())) {
            person.setName(name);
        }
        
        person.setAddresses(addressManager.getPublicAddresses(orcid));
        person.setExternalIdentifiers(externalIdentifierManager.getPublicExternalIdentifiersV2(orcid));
        person.setKeywords(profileKeywordManager.getPublicKeywords(orcid));
        person.setOtherNames(otherNameManager.getPublicOtherNames(orcid));
        person.setResearcherUrls(researcherUrlManager.getPublicResearcherUrls(orcid));             
        person.setEmails(emailManager.getPublicEmails(orcid));
        
        //The rest should come from the ProfileEntity object
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);       
        Delegation delegation = null;
        Set<GivenPermissionToEntity> givenPermissionTo = profile.getGivenPermissionTo();
        Set<GivenPermissionByEntity> givenPermissionBy = profile.getGivenPermissionBy();
        
        if(givenPermissionTo != null || givenPermissionBy != null) {
            delegation = new Delegation();
            if(givenPermissionTo != null) {
                List<DelegationDetails> detailsList = jpaJaxbGivenPermissionToAdapter.toDelegationDetailsList(givenPermissionTo);
                List<GivenPermissionTo> givenPermissionToList = new ArrayList<GivenPermissionTo>();
                for(DelegationDetails details : detailsList) {
                    GivenPermissionTo to = new GivenPermissionTo();
                    to.setDelegationDetails(details);
                    givenPermissionToList.add(to);
                }
                delegation.setGivenPermissionTo(givenPermissionToList);
            }
            
            if(givenPermissionBy != null) {
                List<DelegationDetails> detailsList = jpaJaxbGivenPermissionByAdapter.toDelegationDetailsList(givenPermissionBy);
                List<GivenPermissionBy> givenPermissionByList = new ArrayList<GivenPermissionBy>();
                for(DelegationDetails details : detailsList) {
                    GivenPermissionBy by = new GivenPermissionBy();
                    by.setDelegationDetails(details);
                    givenPermissionByList.add(by);
                }
                delegation.setGivenPermissionBy(givenPermissionByList);
            }            
        }
        
        person.setDelegation(delegation);
                                
        // TODO: implement
        person.setApplications(null);
              
        return person;
    }
            
}

class GroupableActivityComparator implements Comparator<GroupableActivity> {

    @Override
    public int compare(GroupableActivity o1, GroupableActivity o2) {
        return o1.compareTo(o2);
    }

}