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
package org.orcid.core.adapter.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.impl.jsonidentifiers.FundingExternalIdentifiers;
import org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIdentifiers;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.UpdateOptions;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.Triplet;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.record_v2.AffiliationType;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.util.Assert;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapterImpl
 * 
 * @author Declan Newman (declan)
 */

public class Jaxb2JpaAdapterImpl implements Jaxb2JpaAdapter {

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrgManager orgManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource
    private ProfileFundingManager profileFundingManager;

    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource
    protected SourceManager sourceManager;
    
    @Resource
    protected RecordNameManager recordNameManager;
    
    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity) {
        return toProfileEntity(profile, existingProfileEntity, UpdateOptions.ALL);
    }

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity, UpdateOptions updateOptions) { 
        Assert.notNull(profile, "Cannot convert a null OrcidProfile");
        ProfileEntity profileEntity = existingProfileEntity == null ? new ProfileEntity() : existingProfileEntity;
        
        // if orcid-id exist us it
        String orcidString = profile.getOrcidIdentifier().getPath();
        if (profile.retrieveOrcidUriAsString() != null && !profile.retrieveOrcidUriAsString().isEmpty()) {
            orcidString = OrcidStringUtils.getOrcidNumber(profile.retrieveOrcidUriAsString());
        }

        profileEntity.setId(orcidString);
        profileEntity.setOrcidType(org.orcid.jaxb.model.common_v2.OrcidType.fromValue(profile.getType().value()));
        profileEntity.setGroupType(profile.getGroupType());
        setBioDetails(profileEntity, profile.getOrcidBio());            
        setHistoryDetails(profileEntity, profile.getOrcidHistory());
        setActivityDetails(profileEntity, profile.getOrcidActivities(), updateOptions);
        setInternalDetails(profileEntity, profile.getOrcidInternal());
        setPreferencesDetails(profileEntity, profile.getOrcidPreferences());
        profileEntity.setUserLastIp(profile.getUserLastIp());
        profileEntity.setReviewed(profile.isReviewed());

        if(profileEntity.getUsedRecaptchaOnRegistration() == null) {
            profileEntity.setUsedRecaptchaOnRegistration(false);
        }
        
        return profileEntity;
    }

    private void setActivityDetails(ProfileEntity profileEntity, OrcidActivities orcidActivities, UpdateOptions updateOptions) {
        Affiliations affiliations = null;
        FundingList orcidFundings = null;
        OrcidWorks orcidWorks = null;
        if (orcidActivities != null) {
            affiliations = orcidActivities.getAffiliations();
            orcidFundings = orcidActivities.getFundings();
            orcidWorks = orcidActivities.getOrcidWorks();
        }
        if (updateOptions.isUpdateAffiliations()) {
            setOrgAffiliationRelations(profileEntity, affiliations);
        }
        if (updateOptions.isUpdateFundings()) {
            setFundings(profileEntity, orcidFundings);
        }
        if (updateOptions.isUpdateWorks()) {
            setWorks(profileEntity, orcidWorks);
        }
    }

    private void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        SortedSet<WorkEntity> workEntities = getWorkEntities(profileEntity, orcidWorks);
        profileEntity.setWorks(workEntities);
    }

    private SortedSet<WorkEntity> getWorkEntities(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        SortedSet<WorkEntity> existingWorkEntities = profileEntity.getWorks();        
        Map<String, WorkEntity> existingWorkEntitiesMap = createWorkEntitiesMap(existingWorkEntities);
        SortedSet<WorkEntity> workEntities = null;
        if (existingWorkEntities == null) {
            workEntities = new TreeSet<WorkEntity>();
        } else {
            // To allow for orphan deletion
            existingWorkEntities.clear();
            workEntities = existingWorkEntities;
        }
        if (orcidWorks != null && orcidWorks.getOrcidWork() != null && !orcidWorks.getOrcidWork().isEmpty()) {
            List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
            for (OrcidWork orcidWork : orcidWorkList) {
                WorkEntity workEntity = getWorkEntity(orcidWork, existingWorkEntitiesMap.get(orcidWork.getPutCode()));
                if (workEntity != null) {
                    workEntity.setProfile(profileEntity);
                    workEntities.add(workEntity);
                }
            }
        }
        return workEntities;
    }

    private Map<String, WorkEntity> createWorkEntitiesMap(SortedSet<WorkEntity> workEntities) {
        Map<String, WorkEntity> map = new HashMap<>();
        if (workEntities != null) {
            for (WorkEntity workEntity : workEntities) {
                map.put(String.valueOf(workEntity.getId()), workEntity);
            }
        }
        return map;
    }
    
    public WorkEntity getWorkEntity(OrcidWork orcidWork, WorkEntity workEntity) {
        if (orcidWork != null) {
            if(workEntity == null) {
                String putCode = orcidWork.getPutCode();
                if (StringUtils.isNotBlank(putCode) && !"-1".equals(putCode)) {
                    throw new IllegalArgumentException("Invalid put-code was supplied: " + putCode);
                }
                workEntity = new WorkEntity();
            } else {
                workEntity.clean();
            }
            Citation workCitation = orcidWork.getWorkCitation();
            if (workCitation != null && StringUtils.isNotBlank(workCitation.getCitation()) && workCitation.getWorkCitationType() != null) {
                workEntity.setCitation(workCitation.getCitation());
                workEntity.setCitationType(CitationType.fromValue(workCitation.getWorkCitationType().value()));
            }
            // New way of doing work contributors
            workEntity.setContributorsJson(getWorkContributorsJson(orcidWork.getWorkContributors()));
            workEntity.setDescription(orcidWork.getShortDescription() != null ? orcidWork.getShortDescription() : null);
            // New way of doing work external ids
            workEntity.setExternalIdentifiersJson(getWorkExternalIdsJson(orcidWork));
            workEntity.setPublicationDate(getWorkPublicationDate(orcidWork));
            WorkTitle workTitle = orcidWork.getWorkTitle();
            if (workTitle != null) {
                workEntity.setSubtitle(workTitle.getSubtitle() != null ? workTitle.getSubtitle().getContent() : null);
                workEntity.setTitle(workTitle.getTitle() != null ? workTitle.getTitle().getContent().trim() : null);
                TranslatedTitle translatedTitle = workTitle.getTranslatedTitle();
                if (translatedTitle != null) {
                    workEntity.setTranslatedTitle(StringUtils.isEmpty(translatedTitle.getContent()) ? null : translatedTitle.getContent());
                    workEntity.setTranslatedTitleLanguageCode(StringUtils.isEmpty(translatedTitle.getLanguageCode()) ? null : translatedTitle.getLanguageCode());
                }
            }
            workEntity.setJournalTitle(orcidWork.getJournalTitle() != null ? orcidWork.getJournalTitle().getContent() : null);
            workEntity.setLanguageCode(orcidWork.getLanguageCode() != null ? orcidWork.getLanguageCode() : null);
            if(orcidWork.getCountry() != null && orcidWork.getCountry().getValue() != null) {
                workEntity.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.fromValue(orcidWork.getCountry().getValue().value()));
            }            
            workEntity.setWorkUrl(orcidWork.getUrl() != null ? orcidWork.getUrl().getValue() : null);
            if(orcidWork.getWorkType() != null) {
                workEntity.setWorkType(org.orcid.jaxb.model.record_v2.WorkType.fromValue(orcidWork.getWorkType().value()));   
            }                        
            
            if(orcidWork.getVisibility() != null) {
                workEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(orcidWork.getVisibility().value()));
            } else {
                workEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
            }
            
            workEntity.setAddedToProfileDate(new Date());
            //Set source
            setSource(orcidWork.getSource(), workEntity);
            if(workEntity.getDisplayIndex() == null) {
                workEntity.setDisplayIndex(0L);
            }
            return workEntity;
        }
        return null;
    }
    
    private PublicationDateEntity getWorkPublicationDate(OrcidWork orcidWork) {
        if (orcidWork != null && orcidWork.getPublicationDate() != null) {
            PublicationDate publicationDate = orcidWork.getPublicationDate();
            Integer year = publicationDate.getYear() != null ? toInteger(publicationDate.getYear().getValue()) : null;
            Integer month = publicationDate.getMonth() != null ? toInteger(publicationDate.getMonth().getValue()) : null;
            Integer day = publicationDate.getDay() != null ? toInteger(publicationDate.getDay().getValue()) : null;
            return new PublicationDateEntity(year, month, day);
        }
        return null;
    }

    private Integer toInteger(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getWorkContributorsJson(WorkContributors workContributors) {
        if (workContributors == null) {
            return null;
        }
        return JsonUtils.convertToJsonString(workContributors);
    }

    private String getFundingContributorsJson(FundingContributors fundingContributors) {
        if (fundingContributors == null) {
            return null;
        }
        return JsonUtils.convertToJsonString(fundingContributors);
    }

    private String getWorkExternalIdsJson(OrcidWork work) {
        if (work == null || work.getWorkExternalIdentifiers() == null) {
            return null;
        }        
        WorkExternalIdentifiers recordExternalIdentifiers = new WorkExternalIdentifiers(work.getWorkExternalIdentifiers(), work.getWorkType());        
        return recordExternalIdentifiers.toDBJSONString();
    }
    
    
    private void setFundings(ProfileEntity profileEntity, FundingList orcidFundings) {
        SortedSet<ProfileFundingEntity> existingProfileFundingEntities = profileEntity.getProfileFunding();
        if (existingProfileFundingEntities == null) {
            existingProfileFundingEntities = new TreeSet<>();
        }

        // Create a map containing the existing profile funding entities
        Map<String, ProfileFundingEntity> existingProfileFundingEntitiesMap = createProfileFundingEntitiesMap(existingProfileFundingEntities);

        // A set that will contain the updated profile funding entities that
        // comes from the orcidGrant object
        SortedSet<ProfileFundingEntity> updatedProfileFundingEntities = new TreeSet<>();

        // Populate a list of the updated profile funding entities that comes
        // from the fundingList object
        if (orcidFundings != null && orcidFundings.getFundings() != null && !orcidFundings.getFundings().isEmpty()) {
            for (Funding orcidFunding : orcidFundings.getFundings()) {
                ProfileFundingEntity newProfileGrantEntity = getProfileFundingEntity(orcidFunding, existingProfileFundingEntitiesMap.get(orcidFunding.getPutCode()));
                newProfileGrantEntity.setProfile(profileEntity);
                updatedProfileFundingEntities.add(newProfileGrantEntity);
            }
        }

        // Create a map containing the profile funding that comes in the
        // orcidGrant object and that will be persisted
        Map<String, ProfileFundingEntity> updatedProfileGrantEntitiesMap = createProfileFundingEntitiesMap(updatedProfileFundingEntities);

        // Remove orphans
        for (Iterator<ProfileFundingEntity> iterator = existingProfileFundingEntities.iterator(); iterator.hasNext();) {
            ProfileFundingEntity existingEntity = iterator.next();
            if (!updatedProfileGrantEntitiesMap.containsKey(String.valueOf(existingEntity.getId()))) {
                iterator.remove();
            }
        }

        // Add new
        for (ProfileFundingEntity updatedEntity : updatedProfileFundingEntities) {
            if (updatedEntity.getId() == null) {
                existingProfileFundingEntities.add(updatedEntity);
            }
        }
        profileEntity.setProfileFunding(existingProfileFundingEntities);
    }

    private void setBioDetails(ProfileEntity profileEntity, OrcidBio orcidBio) {
        if (orcidBio != null) {
            setBiographyDetails(profileEntity, orcidBio.getBiography());
            setContactDetails(profileEntity, orcidBio.getContactDetails());
            setExternalIdentifiers(profileEntity, orcidBio.getExternalIdentifiers());
            setKeywords(profileEntity, orcidBio.getKeywords());
            setPersonalDetails(profileEntity, orcidBio.getPersonalDetails());
            setResearcherUrls(profileEntity, orcidBio.getResearcherUrls());
        }
    }

    private void setResearcherUrls(ProfileEntity profileEntity, ResearcherUrls researcherUrls) {
        String sourceId = getSourceId();
        SortedSet<ResearcherUrlEntity> existingResearcherUrlEntities = profileEntity.getResearcherUrls();
        
        Iterator<ResearcherUrlEntity> existingIt = null;
        if(existingResearcherUrlEntities != null) {
            existingIt = existingResearcherUrlEntities.iterator();
        } 
        
        //Iterate over the list of existing elements, to see which ones still exists but preserving all the ones where the calling client is not the source of
        if(existingIt != null) {
            while(existingIt.hasNext()) {
                ResearcherUrlEntity existing = existingIt.next();
                String existingElementSource = existing.getElementSourceId();
                if(sourceId != null && !sourceId.equals(existingElementSource)) {
                    //If am not the source of this element, do nothing
                } else {
                    //If am the source, check if the element exists in the list of incoming elements
                    Pair<String, String> existingPair = createResearcherUrlPair(existing);
                    boolean found = false;
                    if(researcherUrls != null && researcherUrls.getResearcherUrl() != null) {
                        for(ResearcherUrl newResearcherUrl : researcherUrls.getResearcherUrl()){
                            Pair<String, String> newResearcherUrlPair = createResearcherUrlPair(newResearcherUrl);
                            if(Objects.equals(existingPair, newResearcherUrlPair)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    
                    //If it doesn't exists, remove it from the existing elements
                    if(!found) {
                        existingIt.remove();
                    }
                }
            }
        }
        
        //Iterate over the list of all new ones and add the ones that doesn't exists yet
        if(researcherUrls != null && researcherUrls.getResearcherUrl() != null) {
            for(ResearcherUrl newResearcherUrl : researcherUrls.getResearcherUrl()) {
                boolean exists = false;
                Pair<String, String> newResearcherUrlPair = createResearcherUrlPair(newResearcherUrl);
                if(existingResearcherUrlEntities != null) {
                    for(ResearcherUrlEntity existingEntity : existingResearcherUrlEntities) {
                        Pair<String, String> existingPair = createResearcherUrlPair(existingEntity);
                        if(Objects.equals(newResearcherUrlPair, existingPair)) {
                            exists = true;
                            //If the profile is not claimed, you can update the visibility
                            if(profileEntity.getClaimed() == null || !profileEntity.getClaimed()) {
                                //Update the visibility of existing elements if the profile is not claimed
                                String existingVisibilityValue = existingEntity.getVisibility() == null ? null : existingEntity.getVisibility().value();                                 
                                String listVisibilityValue = researcherUrls.getVisibility() == null ? null : researcherUrls.getVisibility().value();                                                                
                                if(listVisibilityValue != null && !Objects.equals(existingVisibilityValue, listVisibilityValue)) {
                                    existingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(listVisibilityValue));
                                }                                                                
                            }
                            break;
                        }
                    }
                }
                
                if(!exists) {
                    if (existingResearcherUrlEntities == null) {
                        existingResearcherUrlEntities = new TreeSet<ResearcherUrlEntity>(); 
                        profileEntity.setResearcherUrls(existingResearcherUrlEntities);
                    }
                    ResearcherUrlEntity newEntity = new ResearcherUrlEntity();
                    newEntity.setUser(profileEntity);
                    //Set source
                    SourceEntity source = sourceManager.retrieveSourceEntity();                    
                    setSource(source, newEntity);
                    if(newResearcherUrl.getUrl() != null) {
                        newEntity.setUrl(newResearcherUrl.getUrl().getValue());
                    }
                    if(newResearcherUrl.getUrlName() != null) {
                        newEntity.setUrlName(newResearcherUrl.getUrlName().getContent());
                    }                    
                    newEntity.setVisibility(getDefaultVisibility(profileEntity, researcherUrls.getVisibility(), OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT));
                    newEntity.setDisplayIndex(0L);
                    for (ResearcherUrlEntity tempEntity:existingResearcherUrlEntities)
                        tempEntity.setDisplayIndex(tempEntity.getDisplayIndex()+1);
                    existingResearcherUrlEntities.add(newEntity);
                }
            }
        }
    }

    private Pair<String, String> createResearcherUrlPair(ResearcherUrl rUrl) {
        String url = rUrl.getUrl() == null ? "" : rUrl.getUrl().getValue();
        String urlName = rUrl.getUrlName() == null ? "" : rUrl.getUrlName().getContent();
        Pair<String, String> pair = Pair.of(url, urlName);
        return pair;
    }

    private Pair<String, String> createResearcherUrlPair(ResearcherUrlEntity entity) {
        String url = entity.getUrl() == null ? "" : entity.getUrl();
        String urlName = entity.getUrlName() == null ? "" : entity.getUrlName();
        Pair<String, String> pair = Pair.of(url, urlName);
        return pair;
    }                                                            
    
    private void setPersonalDetails(ProfileEntity profileEntity, PersonalDetails personalDetails) {
        if (personalDetails != null) {
            if(profileEntity.getRecordNameEntity() == null) {
                profileEntity.setRecordNameEntity(new RecordNameEntity());
            }
            profileEntity.getRecordNameEntity().setProfile(profileEntity);
            setCreditNameDetails(profileEntity, personalDetails.getCreditName());
            setFamilyName(profileEntity, personalDetails.getFamilyName());
            setGivenNames(profileEntity, personalDetails.getGivenNames());            
            setOtherNames(profileEntity, personalDetails.getOtherNames());
        }
    }

    private void setOtherNames(ProfileEntity profileEntity, OtherNames otherNames) {        
        String sourceId = getSourceId();
        
        SortedSet<OtherNameEntity> existingOtherNameEntities = profileEntity.getOtherNames();
        
        Iterator<OtherNameEntity> existingIt = null;
        if(existingOtherNameEntities != null) {
            existingIt = existingOtherNameEntities.iterator();
        } 
        
        //Iterate over the list of existing elements, to see which ones still exists but preserving all the ones where the calling client is not the source of
        if(existingIt != null) {
            while(existingIt.hasNext()) {
                OtherNameEntity existing = existingIt.next();
                String existingElementSource = existing.getElementSourceId();
                if(sourceId != null && !sourceId.equals(existingElementSource)) {
                    //If am not the source of this element, do nothing
                } else {
                    //If am the source, check if the element exists in the list of incoming elements
                    String value = existing.getDisplayName();
                    boolean found = false;
                    if(otherNames != null && otherNames.getOtherName() != null) {
                        for(OtherName newOtherName : otherNames.getOtherName()){
                            if(Objects.equals(value, newOtherName.getContent())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    
                    //If it doesn't exists, remove it from the existing elements
                    if(!found) {
                        existingIt.remove();
                    }
                }
            }
        }
        
        //Iterate over the list of all new ones and add the ones that doesn't exists yet
        if(otherNames != null && otherNames.getOtherName() != null) {
            for(OtherName newOtherName : otherNames.getOtherName()) {
                boolean exists = false;
                if(existingOtherNameEntities != null) {
                    for(OtherNameEntity existingEntity : existingOtherNameEntities) {
                        if(Objects.equals(newOtherName.getContent(), existingEntity.getDisplayName())) {
                            exists = true;
                            //If the profile is not claimed, you can update the visibility
                            if(profileEntity.getClaimed() == null || !profileEntity.getClaimed()) {
                                //Update the visibility of existing elements if the profile is not claimed
                                String existingVisibilityValue = existingEntity.getVisibility() == null ? null : existingEntity.getVisibility().value();                                 
                                String listVisibilityValue = otherNames.getVisibility() == null ? null : otherNames.getVisibility().value();                                                                
                                if(listVisibilityValue != null && !Objects.equals(existingVisibilityValue, listVisibilityValue)) {
                                    existingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(listVisibilityValue));
                                }                                                                
                            }
                            break;
                        }
                    }
                }
                
                if(!exists) {
                    if(existingOtherNameEntities == null) {
                        existingOtherNameEntities = new TreeSet<OtherNameEntity>();
                        profileEntity.setOtherNames(existingOtherNameEntities);
                    }
                    OtherNameEntity newEntity = new OtherNameEntity();
                    newEntity.setProfile(profileEntity);
                    
                    //Set source
                    SourceEntity source = sourceManager.retrieveSourceEntity();
                    setSource(source, newEntity);
                    
                    newEntity.setDisplayName(newOtherName.getContent());
                    newEntity.setVisibility(getDefaultVisibility(profileEntity, otherNames.getVisibility(), OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT));
                    newEntity.setDisplayIndex(0L);
                    for (OtherNameEntity tempEntity:existingOtherNameEntities)
                        tempEntity.setDisplayIndex(tempEntity.getDisplayIndex()+1);
                    existingOtherNameEntities.add(newEntity);
                }                
            }
        }
    }   
    
    private void setGivenNames(ProfileEntity profileEntity, GivenNames givenNames) {
        if (givenNames != null && StringUtils.isNotBlank(givenNames.getContent())) {
            if(profileEntity.getRecordNameEntity() == null) {
                profileEntity.setRecordNameEntity(new RecordNameEntity());
                profileEntity.getRecordNameEntity().setProfile(profileEntity);
            }
            profileEntity.getRecordNameEntity().setGivenNames(givenNames.getContent());            
        }
    }

    private void setFamilyName(ProfileEntity profileEntity, FamilyName familyName) {
        if (familyName != null && StringUtils.isNotBlank(familyName.getContent())) {
            if(profileEntity.getRecordNameEntity() == null) {
                profileEntity.setRecordNameEntity(new RecordNameEntity());
                profileEntity.getRecordNameEntity().setProfile(profileEntity);
            }
            profileEntity.getRecordNameEntity().setFamilyName(familyName.getContent());                        
        }
    }

    private void setCreditNameDetails(ProfileEntity profileEntity, CreditName creditName) {
        if (creditName != null) {
            if(profileEntity.getRecordNameEntity() == null) {
                profileEntity.setRecordNameEntity(new RecordNameEntity());
                profileEntity.getRecordNameEntity().setProfile(profileEntity);
            }
            
            RecordNameEntity recordName = profileEntity.getRecordNameEntity();                        
            //Save the record name entity
            if(creditName.getVisibility() != null) {
                recordName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(creditName.getVisibility().value()));
            } else {
                recordName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
            }            
            recordName.setCreditName(creditName.getContent());            
        }
    }

    private void setKeywords(ProfileEntity profileEntity, Keywords keywords) {
        String sourceId = getSourceId();
        
        SortedSet<ProfileKeywordEntity> existingProfileKeywordEntities = profileEntity.getKeywords();
        
        Iterator<ProfileKeywordEntity> existingIt = null;
        if(existingProfileKeywordEntities != null){
            existingIt = existingProfileKeywordEntities.iterator();
        }
        
        //Iterate over the list of existing elements, to see which ones still exists but preserving all the ones where the calling client is not the source of
        if(existingIt != null) {
            while(existingIt.hasNext()) {
                ProfileKeywordEntity existing = existingIt.next();
                String existingElementSource = existing.getElementSourceId();
                if(sourceId != null && !sourceId.equals(existingElementSource)){
                    //If am not the source of this element, do nothing
                } else {
                    //If am the source, check if the element exists in the list of incoming elements
                    String value = existing.getKeywordName();
                    boolean found = false;
                    if(keywords != null && keywords.getKeyword() != null) {
                        for(Keyword newKeyword : keywords.getKeyword()) {
                            if(Objects.equals(value, newKeyword.getContent())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    
                    //If it doesn't exists, remove it from the existing elements
                    if(!found) {
                        existingIt.remove();
                    }
                }
            }
        }
        
        //Iterate over the list of all new ones and add the ones that doesn't exists yet
        if(keywords != null && keywords.getKeyword() != null) {
            for(Keyword newKeyword : keywords.getKeyword()) {
                boolean exists = false;
                if(existingProfileKeywordEntities != null) {
                    for(ProfileKeywordEntity existingEntity : existingProfileKeywordEntities) {
                        if(Objects.equals(newKeyword.getContent(), existingEntity.getKeywordName())) {
                            exists = true;
                            //If the profile is not claimed, you can update the visibility
                            if(profileEntity.getClaimed() == null || !profileEntity.getClaimed()) {
                                //Update the visibility of existing elements if the profile is not claimed
                                String existingVisibilityValue = existingEntity.getVisibility() == null ? null : existingEntity.getVisibility().value();                                 
                                String listVisibilityValue = keywords.getVisibility() == null ? null : keywords.getVisibility().value();                                                                
                                if(listVisibilityValue != null && !Objects.equals(existingVisibilityValue, listVisibilityValue)) {
                                    existingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(listVisibilityValue));
                                }                                                                
                            }
                            break;
                        }
                    }
                }
                
                if(!exists) {
                    if(existingProfileKeywordEntities == null) {
                        existingProfileKeywordEntities = new TreeSet<ProfileKeywordEntity>();
                        profileEntity.setKeywords(existingProfileKeywordEntities);
                    }
                    ProfileKeywordEntity newEntity = new ProfileKeywordEntity();
                    newEntity.setProfile(profileEntity);
                    
                    //Set source
                    SourceEntity source = sourceManager.retrieveSourceEntity();
                    setSource(source, newEntity);
                    
                    newEntity.setKeywordName(newKeyword.getContent());
                    newEntity.setVisibility(getDefaultVisibility(profileEntity, keywords.getVisibility(), OrcidVisibilityDefaults.KEYWORD_DEFAULT));
                    newEntity.setDisplayIndex(0L);
                    for (ProfileKeywordEntity tempEntity:existingProfileKeywordEntities)
                        tempEntity.setDisplayIndex(tempEntity.getDisplayIndex()+1);
                    existingProfileKeywordEntities.add(newEntity);
                }
            }
        }                 
    }
    
    private void setExternalIdentifiers(ProfileEntity profileEntity, ExternalIdentifiers externalIdentifiers) {
        String sourceId = getSourceId();

        Set<ExternalIdentifierEntity> existingExternalIdentifierEntities = profileEntity.getExternalIdentifiers();
        Iterator<ExternalIdentifierEntity> existingIt = null;
        if(existingExternalIdentifierEntities != null){
            existingIt = existingExternalIdentifierEntities.iterator();
        } 
        
        //Iterate over the list of existing elements, to see which ones still exists but preserving all the ones where the calling client is not the source of
        if(existingIt != null) {
            while(existingIt.hasNext()) {
                ExternalIdentifierEntity existing = existingIt.next();
                String existingElementSource = existing.getElementSourceId();                
                if(sourceId != null && !sourceId.equals(existingElementSource)){
                    //If am not the source of this element, do nothing
                } else {
                    //If am the source, check if the element exists in the list of incoming elements
                    Triplet<String, String, String> existingTriplet = createTripletForExternalIdentifier(existing);
                    boolean found = false;
                    if(externalIdentifiers != null && externalIdentifiers.getExternalIdentifier() != null) {
                        for(ExternalIdentifier newExternalIdentifier : externalIdentifiers.getExternalIdentifier()){
                            Triplet<String, String, String> newExternalIdentifierTriplet = createTripletForExternalIdentifier(newExternalIdentifier);
                            if(Objects.equals(existingTriplet, newExternalIdentifierTriplet)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    //If it doesn't exists, remove it from the existing elements
                    if(!found) {
                            existingIt.remove();
                    }
                }
            }
        }
        
        
        //Iterate over the list of all new ones and add the ones that doesn't exists yet
        if(externalIdentifiers != null && externalIdentifiers.getExternalIdentifier() != null) {
            for(ExternalIdentifier newExternalIdentifier : externalIdentifiers.getExternalIdentifier()) {
                boolean exists = false;
                Triplet<String, String, String> newExternalIdentifierTriplet = createTripletForExternalIdentifier(newExternalIdentifier);
                String sourceOfNewElement = newExternalIdentifier.getSource() == null ? null : newExternalIdentifier.getSource().retrieveSourcePath();
                if(existingExternalIdentifierEntities != null) {
                    for(ExternalIdentifierEntity existingEntity : existingExternalIdentifierEntities) {
                        Triplet<String, String, String> existingTriplet = createTripletForExternalIdentifier(existingEntity);
                        String sourceOfExistingElement = existingEntity.getElementSourceId();
                        if (Objects.equals(sourceOfNewElement, sourceOfExistingElement) && Objects.equals(newExternalIdentifierTriplet, existingTriplet)) {
                            exists = true;
                            // If the profile is not claimed, you can update the
                            // visibility
                            if (profileEntity.getClaimed() == null || !profileEntity.getClaimed()) {
                                // Update the visibility of existing elements if
                                // the profile is not claimed
                                String existingVisibilityValue = existingEntity.getVisibility() == null ? null : existingEntity.getVisibility().value();
                                String listVisibilityValue = externalIdentifiers.getVisibility() == null ? null : externalIdentifiers.getVisibility().value();
                                if (listVisibilityValue != null && !Objects.equals(existingVisibilityValue, listVisibilityValue)) {
                                    existingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(listVisibilityValue));
                                }
                            }
                            break;
                        }
                    }
                }
                
                if(!exists) {
                    if(existingExternalIdentifierEntities == null) {
                        existingExternalIdentifierEntities = new TreeSet<ExternalIdentifierEntity>();
                        profileEntity.setExternalIdentifiers(existingExternalIdentifierEntities);
                    }
                    ExternalIdentifierEntity newEntity = new ExternalIdentifierEntity();
                    newEntity.setOwner(profileEntity);
                    
                    //Set source
                    SourceEntity source = sourceManager.retrieveSourceEntity();
                    setSource(source, newEntity);
                    
                    if(newExternalIdentifier.getExternalIdCommonName() != null) {
                        newEntity.setExternalIdCommonName(newExternalIdentifier.getExternalIdCommonName().getContent());
                    }
                    if(newExternalIdentifier.getExternalIdReference() != null) {
                        newEntity.setExternalIdReference(newExternalIdentifier.getExternalIdReference().getContent());
                    }
                    if(newExternalIdentifier.getExternalIdUrl() != null) {
                        newEntity.setExternalIdUrl(newExternalIdentifier.getExternalIdUrl().getValue());
                    }
                    
                    newEntity.setVisibility(getDefaultVisibility(profileEntity, externalIdentifiers.getVisibility(), OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT));
                    newEntity.setDisplayIndex(0L);
                    for (ExternalIdentifierEntity tempEntity:existingExternalIdentifierEntities)
                        tempEntity.setDisplayIndex(tempEntity.getDisplayIndex()+1);
                    existingExternalIdentifierEntities.add(newEntity);
                }
            }               
        }                
    }

    private Triplet<String, String, String> createTripletForExternalIdentifier(ExternalIdentifierEntity entity) {
        String first = entity.getExternalIdUrl() == null ? "" : entity.getExternalIdUrl();
        String second = entity.getExternalIdReference() == null ? "" : entity.getExternalIdReference();
        String third = entity.getExternalIdCommonName() == null ? "" : entity.getExternalIdCommonName();
        Triplet<String, String, String> triplet = new Triplet<String, String, String>(first, second, third);        
        return triplet;
    }
    
    private Triplet<String, String, String> createTripletForExternalIdentifier(ExternalIdentifier entity) {
        String first = entity.getExternalIdUrl() == null ? "" : entity.getExternalIdUrl().getValue();
        String second = entity.getExternalIdReference() == null ? "" : entity.getExternalIdReference().getContent();
        String third = entity.getExternalIdCommonName() == null ? "" : entity.getExternalIdCommonName().getContent();
        Triplet<String, String, String> triplet = new Triplet<String, String, String>(first, second, third);        
        return triplet;
    }

    private void setContactDetails(ProfileEntity profileEntity, ContactDetails contactDetails) {
        if (contactDetails != null) {
            setEmails(profileEntity, contactDetails);
            setCountry(profileEntity, contactDetails);
        }
    }
    
    private void setCountry(ProfileEntity profileEntity, ContactDetails contactDetails) {        
        Country contactCountry = contactDetails.getAddress() != null && contactDetails.getAddress().getCountry() != null ? contactDetails.getAddress().getCountry()
                : null;        
        Iso3166Country country = contactCountry != null ? contactCountry.getValue() : null;
        
        if(country != null) {
            Set<AddressEntity> addresses = profileEntity.getAddresses();
            if(addresses == null) {
                addresses = new HashSet<AddressEntity>();
                profileEntity.setAddresses(addresses);
            }
            
            boolean addIt = true;
                                   
            //If the address exists, don't add it
            for(AddressEntity address : addresses) {
                if(Objects.equals(country.value(), address.getIso2Country().value())) {
                    addIt = false;                    
                }
            }            
            
            if(addIt) {
                AddressEntity newAddress = new AddressEntity();
                newAddress.setDateCreated(new Date());
                //The default country is the smallest one, so, lets add this one as the biggest display index possible for the record
                newAddress.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.fromValue(country.value()));
                newAddress.setLastModified(new Date());
                newAddress.setUser(profileEntity);
                newAddress.setVisibility(getDefaultVisibility(profileEntity, contactCountry.getVisibility(), OrcidVisibilityDefaults.COUNTRY_DEFAULT));
                //Set source
                SourceEntity source = sourceManager.retrieveSourceEntity();
                setSource(source, newAddress);
                newAddress.setDisplayIndex(0L);
                for(AddressEntity address : addresses)
                    address.setDisplayIndex(address.getDisplayIndex()+1L);
                addresses.add(newAddress);
            }                        
        }
    }

    private void setEmails(ProfileEntity profileEntity, ContactDetails contactDetails) {
        Set<EmailEntity> existingEmailEntities = profileEntity.getEmails();
        Map<String, EmailEntity> existingEmailEntitiesMap = createEmailEntitiesMap(existingEmailEntities);
        Set<EmailEntity> emailEntities = null;
        if (existingEmailEntities == null) {
            emailEntities = new HashSet<>();
        } else {
            // To allow for orphan deletion
            existingEmailEntities.clear();
            emailEntities = existingEmailEntities;
        }
        for (Email email : contactDetails.getEmail()) {
            String emailId = email.getValue().trim();
            EmailEntity emailEntity = null;
            EmailEntity existingEmailEntity = existingEmailEntitiesMap.get(emailId);
            if (existingEmailEntity == null) {
                emailEntity = new EmailEntity();
                emailEntity.setId(emailId);
                emailEntity.setProfile(profileEntity);
                
                emailEntity.setSourceId(email.getSource());
                emailEntity.setClientSourceId(email.getSourceClientId());                                
            } else {
                existingEmailEntity.clean();
                emailEntity = existingEmailEntity;
            }
            emailEntity.setPrimary(email.isPrimary());
            emailEntity.setCurrent(email.isCurrent());
            emailEntity.setVerified(email.isVerified());
            if (email.getVisibility() == null) {
                emailEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
            } else {
                emailEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(email.getVisibility().value()));
            }
            emailEntities.add(emailEntity);
        }
        profileEntity.setEmails(emailEntities);
    }

    private Map<String, EmailEntity> createEmailEntitiesMap(Set<EmailEntity> existingEmailsEntities) {
        if (existingEmailsEntities == null) {
            return new HashMap<>();
        }
        return EmailEntity.mapById(existingEmailsEntities);
    }

    private void setHistoryDetails(ProfileEntity profileEntity, OrcidHistory orcidHistory) {
        if (orcidHistory != null) {
            CompletionDate completionDate = orcidHistory.getCompletionDate();
            profileEntity.setCompletedDate(completionDate == null ? null : toDate(completionDate.getValue()));
            SubmissionDate submissionDate = orcidHistory.getSubmissionDate();
            profileEntity.setSubmissionDate(submissionDate == null ? null : toDate(submissionDate.getValue()));
            DeactivationDate deactivationDate = orcidHistory.getDeactivationDate();
            profileEntity.setDeactivationDate(deactivationDate == null ? null : toDate(deactivationDate.getValue()));
            profileEntity.setClaimed(orcidHistory.isClaimed());
            CreationMethod creationMethod = orcidHistory.getCreationMethod();
            profileEntity.setCreationMethod(creationMethod != null ? creationMethod.value() : null);
            Source source = orcidHistory.getSource();
            if (source != null) {
                SourceEntity sourceEntity = new SourceEntity();
                ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
                clientDetailsEntity.setId(source.retrieveSourcePath());
                sourceEntity.setSourceClient(clientDetailsEntity);
                profileEntity.setSource(sourceEntity);
            }
        }
    }

    private void setBiographyDetails(ProfileEntity profileEntity, Biography biography) {
        if (biography != null) {
            Visibility defaultVisibility = profileEntity.getActivitiesVisibilityDefault() == null ? OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility() : Visibility.fromValue(profileEntity.getActivitiesVisibilityDefault().value());
            if(profileEntity.getBiographyEntity() == null) {
                profileEntity.setBiographyEntity(new BiographyEntity());
                profileEntity.getBiographyEntity().setProfile(profileEntity);                
            }
                        
            profileEntity.getBiographyEntity().setBiography(biography.getContent());
            if(profileEntity.getClaimed() == null || !profileEntity.getClaimed()) {                
                if (biography.getVisibility() != null) {
                    profileEntity.getBiographyEntity().setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(biography.getVisibility().value()));
                } else {                    
                    profileEntity.getBiographyEntity().setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(defaultVisibility.value()));
                }                
            }  
            
            //Fill the visibility in case it is still null
            if(profileEntity.getBiographyEntity().getVisibility() == null) {
                profileEntity.getBiographyEntity().setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(defaultVisibility.value()));
            }
        }
    }

    private void setOrgAffiliationRelations(ProfileEntity profileEntity, Affiliations affiliations) {
        SortedSet<OrgAffiliationRelationEntity> existingOrgAffiliationEntities = profileEntity.getOrgAffiliationRelations();
        if (existingOrgAffiliationEntities == null) {
            existingOrgAffiliationEntities = new TreeSet<>();
        }
        Map<String, OrgAffiliationRelationEntity> existingOrgAffiliationsEntitiesMap = createOrgAffiliationEntitiesMap(existingOrgAffiliationEntities);
        SortedSet<OrgAffiliationRelationEntity> updatedOrgAffiliationEntities = new TreeSet<>();
        if (affiliations != null && !affiliations.getAffiliation().isEmpty()) {
            for (Affiliation affiliation : affiliations.getAffiliation()) {
                OrgAffiliationRelationEntity orgRelationEntity = getOrgAffiliationRelationEntity(affiliation,
                        existingOrgAffiliationsEntitiesMap.get(affiliation.getPutCode()));
                orgRelationEntity.setProfile(profileEntity);
                updatedOrgAffiliationEntities.add(orgRelationEntity);
            }
        }
        Map<String, OrgAffiliationRelationEntity> updatedOrgAffiliationEntitiesMap = createOrgAffiliationEntitiesMap(updatedOrgAffiliationEntities);
        // Remove orphans
        for (Iterator<OrgAffiliationRelationEntity> iterator = existingOrgAffiliationEntities.iterator(); iterator.hasNext();) {
            OrgAffiliationRelationEntity existingEntity = iterator.next();
            if (!updatedOrgAffiliationEntitiesMap.containsKey(String.valueOf(existingEntity.getId()))) {
                iterator.remove();
            }
        }
        // Add new
        for (OrgAffiliationRelationEntity updatedEntity : updatedOrgAffiliationEntities) {
            if (updatedEntity.getId() == null) {
                existingOrgAffiliationEntities.add(updatedEntity);
            }
        }
        profileEntity.setOrgAffiliationRelations(existingOrgAffiliationEntities);
    }

    private Map<String, OrgAffiliationRelationEntity> createOrgAffiliationEntitiesMap(Set<OrgAffiliationRelationEntity> orgAffiliationEntities) {
        Map<String, OrgAffiliationRelationEntity> map = new HashMap<>();
        if (orgAffiliationEntities != null) {
            for (OrgAffiliationRelationEntity orgAffiliationEntity : orgAffiliationEntities) {
                map.put(String.valueOf(orgAffiliationEntity.getId()), orgAffiliationEntity);
            }
        }
        return map;

    }

    /**
     * Create a map with the provided profileGrantEntities
     * 
     * @param profileGrantEntities
     * @return Map<String, ProfileFundingEntity>
     * */
    private Map<String, ProfileFundingEntity> createProfileFundingEntitiesMap(Set<ProfileFundingEntity> profileFundingEntities) {
        Map<String, ProfileFundingEntity> map = new HashMap<>();
        if (profileFundingEntities != null) {
            for (ProfileFundingEntity profileFundingEntity : profileFundingEntities) {
                map.put(String.valueOf(profileFundingEntity.getId()), profileFundingEntity);
            }
        }
        return map;

    }

    private void setInternalDetails(ProfileEntity profileEntity, OrcidInternal orcidInternal) {
        if (orcidInternal != null) {
            SecurityDetails securityDetails = orcidInternal.getSecurityDetails();
            if (securityDetails != null) {
                String encryptedPassword = securityDetails.getEncryptedPassword() != null ? securityDetails.getEncryptedPassword().getContent() : null;
                profileEntity.setEncryptedPassword(encryptedPassword);
                profileEntity.setSecurityQuestion(securityDetails.getSecurityQuestionId() == null ? null : securityQuestionDao.find((int) securityDetails
                        .getSecurityQuestionId().getValue()));

                String encryptedAnswer = securityDetails.getEncryptedSecurityAnswer() != null ? securityDetails.getEncryptedSecurityAnswer().getContent() : null;
                profileEntity.setEncryptedSecurityAnswer(encryptedAnswer);

                String verificationCode = securityDetails.getEncryptedVerificationCode() != null ? securityDetails.getEncryptedVerificationCode().getContent() : null;
                profileEntity.setEncryptedVerificationCode(verificationCode);

            }

            if (orcidInternal.getReferredBy() != null) {
                profileEntity.setReferredBy(orcidInternal.getReferredBy().getPath());
            }

            Preferences preferences = orcidInternal.getPreferences();
            if (preferences != null) {
                String sendEmailFrequencyDays = preferences.getSendEmailFrequencyDays();
                profileEntity.setSendEmailFrequencyDays(Float.valueOf(sendEmailFrequencyDays == null ? DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS
                        : sendEmailFrequencyDays));
                profileEntity.setSendChangeNotifications(preferences.getSendChangeNotifications() == null ? null : preferences.getSendChangeNotifications().isValue());
                profileEntity.setSendOrcidNews(preferences.getSendOrcidNews() == null ? null : preferences.getSendOrcidNews().isValue());
                profileEntity.setSendMemberUpdateRequests(preferences.getSendMemberUpdateRequests() == null ? null : preferences
                        .getSendMemberUpdateRequests());
                // ActivitiesVisibilityDefault default is WorkVisibilityDefault
                if (preferences.getActivitiesVisibilityDefault() != null && preferences.getActivitiesVisibilityDefault().getValue() != null) {
                    profileEntity.setActivitiesVisibilityDefault(org.orcid.jaxb.model.common_v2.Visibility.fromValue(preferences.getActivitiesVisibilityDefault().getValue().value()));
                }

                if (preferences.getDeveloperToolsEnabled() != null) {
                    profileEntity.setEnableDeveloperTools(preferences.getDeveloperToolsEnabled().isValue());
                }
                
                profileEntity.setEnableNotifications(preferences.getNotificationsEnabled() == null ? DefaultPreferences.NOTIFICATIONS_ENABLED : preferences.getNotificationsEnabled());
            }
            if (orcidInternal.getSalesforceId() != null) {
                profileEntity.setSalesforeId(orcidInternal.getSalesforceId().getContent());
            }
        }
    }

    private void setPreferencesDetails(ProfileEntity profileEntity, OrcidPreferences orcidPreferences) {
        if (orcidPreferences != null) {
            if (orcidPreferences.getLocale() != null)
                profileEntity.setLocale(org.orcid.jaxb.model.common_v2.Locale.fromValue(orcidPreferences.getLocale().value()));
            else
                profileEntity.setLocale(org.orcid.jaxb.model.common_v2.Locale.EN);
        }
    }    

    @Override
    public OrgAffiliationRelationEntity getNewOrgAffiliationRelationEntity(Affiliation affiliation, ProfileEntity profileEntity) {
        OrgAffiliationRelationEntity orgAffiliationRelationEntity = getOrgAffiliationRelationEntity(affiliation, null);
        orgAffiliationRelationEntity.setProfile(profileEntity);
        return orgAffiliationRelationEntity;
    }

    @Override
    public OrgAffiliationRelationEntity getUpdatedAffiliationRelationEntity(Affiliation updatedAffiliation) {
        if (PojoUtil.isEmpty(updatedAffiliation.getPutCode()))
            throw new IllegalArgumentException("Affiliation must contain a put code to be edited");
        long affiliationId = Long.valueOf(updatedAffiliation.getPutCode());
        OrgAffiliationRelationEntity exisitingOrgAffiliationEntity = orgAffiliationRelationDao.find(affiliationId);
        OrgAffiliationRelationEntity orgAffiliationRelationEntity = getOrgAffiliationRelationEntity(updatedAffiliation, exisitingOrgAffiliationEntity);
        return orgAffiliationRelationEntity;
    }

    /**
     * Transforms a OrcidGrant object into a ProfileFundingEntity object
     * 
     * @param updatedFunding
     * @param profileEntity
     * @return ProfileFundingEntity
     * */
    @Override
    public ProfileFundingEntity getNewProfileFundingEntity(Funding updatedFunding, ProfileEntity profileEntity) {
        ProfileFundingEntity profileFundingEntity = getProfileFundingEntity(updatedFunding, null);
        profileFundingEntity.setProfile(profileEntity);
        return profileFundingEntity;
    }

    /**
     * Transforms a OrcidGrant object into a ProfileFundingEntity object
     * 
     * @param updatedFunding
     * @param profileEntity
     * @return ProfileFundingEntity
     * */
    @Override
    public ProfileFundingEntity getUpdatedProfileFundingEntity(Funding updatedFunding) {
        ProfileFundingEntity existingProfileFundingEntity = profileFundingManager.getProfileFundingEntity(Long.valueOf(updatedFunding.getPutCode()));
        ProfileFundingEntity profileFundingEntity = getProfileFundingEntity(updatedFunding, existingProfileFundingEntity);
        return profileFundingEntity;
    }

    private OrgAffiliationRelationEntity getOrgAffiliationRelationEntity(Affiliation affiliation, OrgAffiliationRelationEntity exisitingOrgAffiliationEntity) {
        if (affiliation != null) {

            // Get the org
            OrgEntity orgEntity = getOrgEntity(affiliation);

            OrgAffiliationRelationEntity orgRelationEntity = null;
            if (exisitingOrgAffiliationEntity == null) {
                String putCode = affiliation.getPutCode();
                if (StringUtils.isNotBlank(putCode) && !"-1".equals(putCode)) {
                    throw new IllegalArgumentException("Invalid put-code was supplied for an affiliation: " + putCode);
                }
                orgRelationEntity = new OrgAffiliationRelationEntity();
            } else {
                orgRelationEntity = exisitingOrgAffiliationEntity;
                orgRelationEntity.clean();
            }
            FuzzyDate startDate = affiliation.getStartDate();
            FuzzyDate endDate = affiliation.getEndDate();
            if(affiliation.getType() != null) {
                orgRelationEntity.setAffiliationType(AffiliationType.fromValue(affiliation.getType().value()));    
            }
            
            if(affiliation.getVisibility() != null) {
                orgRelationEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(affiliation.getVisibility().value()));
            } else {
                orgRelationEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
            }
                        
            //Set source
            setSource(affiliation.getSource(), orgRelationEntity);
            
            orgRelationEntity.setDepartment(affiliation.getDepartmentName());
            orgRelationEntity.setEndDate(endDate != null ? new EndDateEntity(endDate) : null);
            orgRelationEntity.setOrg(orgEntity);
            orgRelationEntity.setTitle(affiliation.getRoleTitle());
            orgRelationEntity.setStartDate(startDate != null ? new StartDateEntity(startDate) : null);

            if (affiliation.getCreatedDate() != null && affiliation.getCreatedDate().getValue() != null)
                orgRelationEntity.setDateCreated(affiliation.getCreatedDate().getValue().toGregorianCalendar().getTime());
            if (affiliation.getLastModifiedDate() != null && affiliation.getLastModifiedDate().getValue() != null)
                orgRelationEntity.setLastModified(affiliation.getLastModifiedDate().getValue().toGregorianCalendar().getTime());

            return orgRelationEntity;
        }
        return null;
    }

    /**
     * Get a ProfileFundingEntity based on a Grant object
     * 
     * @param funding
     * @param exisitingProfileFundingEntity
     * @return a ProfileFundingEntity created from the provided funding
     * */
    private ProfileFundingEntity getProfileFundingEntity(Funding funding, ProfileFundingEntity exisitingProfileFundingEntity) {
        if (funding != null) {
            // Get the org
            OrgEntity orgEntity = getOrgEntity(funding);

            ProfileFundingEntity profileFundingEntity = null;
            if (exisitingProfileFundingEntity == null) {
                String putCode = funding.getPutCode();
                if (StringUtils.isNotBlank(putCode) && !"-1".equals(putCode)) {
                    throw new IllegalArgumentException("Invalid put-code was supplied for a funding: " + putCode);
                }
                profileFundingEntity = new ProfileFundingEntity();
                //Set source
                setSource(funding.getSource(), profileFundingEntity);                
            } else {
                profileFundingEntity = exisitingProfileFundingEntity;
                profileFundingEntity.clean();
            }
            FuzzyDate startDate = funding.getStartDate();
            FuzzyDate endDate = funding.getEndDate();

            if (funding.getAmount() != null) {
                String amount = StringUtils.isNotBlank(funding.getAmount().getContent()) ? funding.getAmount().getContent() : null;
                String currencyCode = funding.getAmount().getCurrencyCode() != null ? funding.getAmount().getCurrencyCode() : null;
                if (StringUtils.isNotBlank(amount)) {
                    try {
                        BigDecimal bigDecimalAmount = getAmountAsBigDecimal(amount);
                        profileFundingEntity.setNumericAmount(bigDecimalAmount);
                    } catch (Exception e) {
                        String sample = getSampleAmountInProperFormat(localeManager.getLocale());
                        throw new IllegalArgumentException("Cannot cast amount: " + amount + " proper format is: " + sample);
                    }

                    profileFundingEntity.setCurrencyCode(currencyCode);
                }
            }

            profileFundingEntity.setContributorsJson(getFundingContributorsJson(funding.getFundingContributors()));
            profileFundingEntity.setDescription(StringUtils.isNotBlank(funding.getDescription()) ? funding.getDescription() : null);
            profileFundingEntity.setEndDate(endDate != null ? new EndDateEntity(endDate) : null);
            FundingExternalIdentifiers recordExternalIdentifiers = new FundingExternalIdentifiers(funding.getFundingExternalIdentifiers());        
            profileFundingEntity.setExternalIdentifiersJson(recordExternalIdentifiers.toDBJSONString());
            profileFundingEntity.setStartDate(startDate != null ? new StartDateEntity(startDate) : null);

            FundingTitle fundingTitle = funding.getTitle();
            if (fundingTitle != null) {
                String title = null, translatedTitle = null, languageCode = null;
                if (fundingTitle.getTitle() != null)
                    title = fundingTitle.getTitle().getContent();
                if (fundingTitle.getTranslatedTitle() != null) {
                    translatedTitle = fundingTitle.getTranslatedTitle().getContent();
                    languageCode = fundingTitle.getTranslatedTitle().getLanguageCode();
                }

                profileFundingEntity.setTitle(StringUtils.isNotBlank(title) ? title : null);
                profileFundingEntity.setTranslatedTitle(StringUtils.isNotBlank(translatedTitle) ? translatedTitle : null);
                profileFundingEntity.setTranslatedTitleLanguageCode(StringUtils.isNotBlank(languageCode) ? languageCode : null);
            }

            if(funding.getType() != null) {
                profileFundingEntity.setType(org.orcid.jaxb.model.record_v2.FundingType.fromValue(funding.getType().value()));
            }            
            profileFundingEntity.setOrganizationDefinedType(funding.getOrganizationDefinedFundingType() != null ? funding.getOrganizationDefinedFundingType()
                    .getContent() : null);
            if (funding.getUrl() != null)
                profileFundingEntity.setUrl(StringUtils.isNotBlank(funding.getUrl().getValue()) ? funding.getUrl().getValue() : null);
            
            if(funding.getVisibility() != null) {
                profileFundingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(funding.getVisibility().value()));
            } else {
                profileFundingEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility().value()));
            }
            

            if (funding.getCreatedDate() != null && funding.getCreatedDate().getValue() != null)
                profileFundingEntity.setDateCreated(funding.getCreatedDate().getValue().toGregorianCalendar().getTime());
            if (funding.getLastModifiedDate() != null && funding.getLastModifiedDate().getValue() != null)
                profileFundingEntity.setLastModified(funding.getLastModifiedDate().getValue().toGregorianCalendar().getTime());

            profileFundingEntity.setOrg(orgEntity);

            if(profileFundingEntity.getDisplayIndex() == null) {
                profileFundingEntity.setDisplayIndex(0L);
            }
            
            return profileFundingEntity;
        }
        return null;
    }

    /**
     * Transforms a string into a BigDecimal, it assumes the amount comes
     * already formatted
     * 
     * @param amount
     * @return a BigDecimal containing the given amount
     * @throws Exception
     *             if the amount cannot be correctly parse into a BigDecimal
     * */
    public BigDecimal getAmountAsBigDecimal(String amount) throws Exception {
        return new BigDecimal(amount);
    }

    /**
     * Get a string with the proper amount format
     * 
     * @param local
     * @return an example string showing how the amount should be entered
     * */
    private String getSampleAmountInProperFormat(java.util.Locale locale) {
        double example = 1234567.89;
        NumberFormat numberFormatExample = NumberFormat.getNumberInstance(locale);
        return numberFormatExample.format(example);
    }
        
    private OrgEntity getOrgEntity(Affiliation affiliation) {
        if (affiliation != null) {
            OrgEntity orgEntity = new OrgEntity();
            Organization organization = affiliation.getOrganization();
            orgEntity.setName(organization.getName());
            OrganizationAddress address = organization.getAddress();
            orgEntity.setCity(address.getCity());
            orgEntity.setRegion(address.getRegion());
            orgEntity.setCountry(address.getCountry());
            if (organization.getDisambiguatedOrganization() != null && organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
                orgEntity.setOrgDisambiguated(orgDisambiguatedDao.findBySourceIdAndSourceType(organization.getDisambiguatedOrganization()
                        .getDisambiguatedOrganizationIdentifier(), organization.getDisambiguatedOrganization().getDisambiguationSource()));
            }
            return orgManager.createUpdate(orgEntity);
        }
        return null;
    }

    /**
     * Get an OrgEntity object based on the provided orcidGrant
     * 
     * @param orcidGrant
     * @return a OrgEntity based on the provided OrcidGrant
     * */
    private OrgEntity getOrgEntity(Funding orcidFunding) {
        if (orcidFunding != null) {
            OrgEntity orgEntity = new OrgEntity();
            Organization organization = orcidFunding.getOrganization();
            orgEntity.setName(organization.getName());
            OrganizationAddress address = organization.getAddress();
            orgEntity.setCity(address.getCity());
            orgEntity.setRegion(address.getRegion());
            orgEntity.setCountry(address.getCountry());
            if (organization.getDisambiguatedOrganization() != null && organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() != null) {
                orgEntity.setOrgDisambiguated(orgDisambiguatedDao.findBySourceIdAndSourceType(organization.getDisambiguatedOrganization()
                        .getDisambiguatedOrganizationIdentifier(), organization.getDisambiguatedOrganization().getDisambiguationSource()));
            }
            return orgManager.createUpdate(orgEntity);
        }
        return null;
    }

    private void setSource(Source source, SourceAwareEntity<?> entity) {
        if (source != null) {
            if(source.getSourceOrcid() != null) {
                entity.setSourceId(source.getSourceOrcid().getPath());
            }
            
            if(source.getSourceClientId() != null) {
                entity.setClientSourceId(source.getSourceClientId().getPath());
            }
        }
    }
    
    private void setSource(SourceEntity source, SourceAwareEntity<?> entity) {
        if(source != null){
            if(source.getSourceProfile() != null) {
                entity.setSourceId(source.getSourceProfile().getId());
            }
            if(source.getSourceClient() != null) {
                entity.setClientSourceId(source.getSourceClient().getId());
            }
        }
    }

    private Date toDate(XMLGregorianCalendar completionDate) {
        if (completionDate != null) {
            GregorianCalendar gregorianCalendar = completionDate.toGregorianCalendar();
            return gregorianCalendar.getTime();
        }
        return null;
    }
    
    private String getSourceId() {
        SourceEntity source = sourceManager.retrieveSourceEntity();
        String sourceId = null;
        //source mananger should never return null, but for some unit tests it does, so, we return the user id
        if(source != null) {
            sourceId = source.getSourceId();
        }         
        return sourceId;        
    }
    
    private org.orcid.jaxb.model.common_v2.Visibility getDefaultVisibility(ProfileEntity profile, Visibility listVisibility, OrcidVisibilityDefaults elementDefault) {
        Visibility defaultVisibility = elementDefault.getVisibility();
        
        //If the profile is not claimed, set the list visibility
        if(profile.getClaimed() == null || !profile.getClaimed()) {
            if(listVisibility != null) {
                defaultVisibility = listVisibility;
            } else {
                defaultVisibility = OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility();
            }
        } else {
            //If it is claimed, set the default profile visibility             
            return profile.getActivitiesVisibilityDefault();             
        }
        
        return org.orcid.jaxb.model.common_v2.Visibility.fromValue(defaultVisibility.value());
    }
}
