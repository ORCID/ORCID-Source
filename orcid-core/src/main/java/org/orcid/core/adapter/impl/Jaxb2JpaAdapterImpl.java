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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.Triplet;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdUrl;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.Locale;
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
import org.orcid.jaxb.model.record_rc2.Relationship;
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
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.FundingExternalIdentifiers;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
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

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity) { 
        Assert.notNull(profile, "Cannot convert a null OrcidProfile");
        ProfileEntity profileEntity = existingProfileEntity == null ? new ProfileEntity() : existingProfileEntity;

        // if orcid-id exist us it
        String orcidString = profile.getOrcidIdentifier().getPath();
        if (profile.retrieveOrcidUriAsString() != null && !profile.retrieveOrcidUriAsString().isEmpty()) {
            orcidString = OrcidStringUtils.getOrcidNumber(profile.retrieveOrcidUriAsString());
        }

        profileEntity.setId(orcidString);
        profileEntity.setOrcidType(profile.getType());
        profileEntity.setGroupType(profile.getGroupType());
        setBioDetails(profileEntity, profile.getOrcidBio());            
        setHistoryDetails(profileEntity, profile.getOrcidHistory());
        setActivityDetails(profileEntity, profile.getOrcidActivities());
        setInternalDetails(profileEntity, profile.getOrcidInternal());
        setPreferencesDetails(profileEntity, profile.getOrcidPreferences());
        profileEntity.setUserLastIp(profile.getUserLastIp());
        profileEntity.setReviewed(profile.isReviewed());

        if(profileEntity.getUsedRecaptchaOnRegistration() == null) {
            profileEntity.setUsedRecaptchaOnRegistration(false);
        }
        
        return profileEntity;
    }

    private void setActivityDetails(ProfileEntity profileEntity, OrcidActivities orcidActivities) {
        Affiliations affiliations = null;
        FundingList orcidFundings = null;
        OrcidWorks orcidWorks = null;
        if (orcidActivities != null) {
            affiliations = orcidActivities.getAffiliations();
            orcidFundings = orcidActivities.getFundings();
            orcidWorks = orcidActivities.getOrcidWorks();
        }
        setOrgAffiliationRelations(profileEntity, affiliations);
        setFundings(profileEntity, orcidFundings);
        setWorks(profileEntity, orcidWorks);
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
                workEntity.setCitationType(workCitation.getWorkCitationType());
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
            workEntity.setIso2Country(orcidWork.getCountry() == null ? null : orcidWork.getCountry().getValue());
            workEntity.setWorkType(orcidWork.getWorkType());
            workEntity.setWorkUrl(orcidWork.getUrl() != null ? orcidWork.getUrl().getValue() : null);
            workEntity.setSource(getSource(orcidWork.getSource()));            
            workEntity.setVisibility(orcidWork.getVisibility() == null ? Visibility.PRIVATE : orcidWork.getVisibility());
            workEntity.setAddedToProfileDate(new Date());
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
        //Transform the external id v1.2 into an external id v2.0 
        //note uses rc1 format, rc2 no longer has WorkExternalIdentifiers, this is to maintain db compatibility
        org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers recordExternalIdentifiers = org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers.valueOf(work.getWorkExternalIdentifiers());
        
        /**
         * Transform the external identifiers according to the rules in: 
         * https://trello.com/c/pqboi7EJ/1368-activity-identifiers-add-self-or-part-of
         * */
        for(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier extId : recordExternalIdentifiers.getExternalIdentifier()) {
            if(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.ISSN.equals(extId.getWorkExternalIdentifierType())) {
                if(!work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.BOOK)){
                    extId.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.PART_OF);
                } else {
                    extId.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF);
                }                
            } else if(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.ISBN.equals(extId.getWorkExternalIdentifierType())) {
                if(work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.BOOK_CHAPTER) || work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.CONFERENCE_PAPER)) {
                    extId.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.PART_OF);
                } else {
                    extId.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF);
                }
            } else {
                extId.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF);
            }
        }
        
        return JsonUtils.convertToJsonString(recordExternalIdentifiers);
        
        /*
        if (work == null || work.getWorkExternalIdentifiers() == null) {
            return null;
        }
        
        org.orcid.jaxb.model.record_rc2.ExternalIDs recordExternalIdentifiers = org.orcid.jaxb.model.record_rc2.ExternalIDs.valueOf(work.getWorkExternalIdentifiers());
        
        for(org.orcid.jaxb.model.record_rc2.ExternalID extId : recordExternalIdentifiers.getExternalIdentifiers()) {
            if(ExternalIDType.ISSN.equals(extId.getType())) {
                if(!work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.BOOK)){
                    extId.setRelationship(Relationship.PART_OF);
                } else {
                    extId.setRelationship(Relationship.SELF);
                }                
            } else if(ExternalIDType.ISBN.equals(extId.getType())) {
                if(work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.BOOK_CHAPTER) || work.getWorkType().equals(org.orcid.jaxb.model.message.WorkType.CONFERENCE_PAPER)) {
                    extId.setRelationship(Relationship.PART_OF);
                } else {
                    extId.setRelationship(Relationship.SELF);
                }
            } else {
                extId.setRelationship(Relationship.SELF);
            }
        }
        
        return JsonUtils.convertToJsonString(recordExternalIdentifiers);
        */
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
            setDelegations(profileEntity, orcidBio.getDelegation());
            setExternalIdentifiers(profileEntity, orcidBio.getExternalIdentifiers());
            setKeywords(profileEntity, orcidBio.getKeywords());
            setPersonalDetails(profileEntity, orcidBio.getPersonalDetails());
            setResearcherUrls(profileEntity, orcidBio.getResearcherUrls());
        }
    }

    private void setResearcherUrls(ProfileEntity profileEntity, ResearcherUrls researcherUrls) {
        if (researcherUrls != null && researcherUrls.getResearcherUrl() != null && !researcherUrls.getResearcherUrl().isEmpty()) {
            List<ResearcherUrl> researcherUrlList = researcherUrls.getResearcherUrl();
            SortedSet<ResearcherUrlEntity> researcherUrlEntities = new TreeSet<ResearcherUrlEntity>();
            for (ResearcherUrl researcherUrl : researcherUrlList) {
                ResearcherUrlEntity researcherUrlEntity = new ResearcherUrlEntity();
                researcherUrlEntity.setUrl(researcherUrl.getUrl() != null ? researcherUrl.getUrl().getValue() : null);
                researcherUrlEntity.setUrlName(researcherUrl.getUrlName() != null ? researcherUrl.getUrlName().getContent() : null);
                researcherUrlEntity.setUser(profileEntity);

                if (researcherUrl.getVisibility() != null){
                    researcherUrlEntity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(researcherUrl.getVisibility().value()));                    
                } else {
                    researcherUrlEntity
                            .setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility().value()));
                }
                
                Source source = researcherUrl.getSource();
                if (source != null && !PojoUtil.isEmpty(source.retrieveSourcePath())) {
                    researcherUrlEntity.setSource(getSource(source));
                } else {
                    researcherUrlEntity.setSource(sourceManager.retrieveSourceEntity());
                }
                                
                researcherUrlEntities.add(researcherUrlEntity);
            }
            profileEntity.setResearcherUrls(researcherUrlEntities);
        }
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
        if (otherNames != null) {
            List<OtherName> otherNameList = otherNames.getOtherName();
            if (otherNameList != null && !otherNameList.isEmpty()) {
                SortedSet<OtherNameEntity> otherNameEntities = new TreeSet<OtherNameEntity>();
                for (OtherName otherName : otherNameList) {
                    OtherNameEntity otherNameEntity = new OtherNameEntity();
                    otherNameEntity.setDisplayName(otherName.getContent());
                    otherNameEntity.setProfile(profileEntity);
                    
                    if (otherName.getVisibility() != null){
                        otherNameEntity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(otherName.getVisibility().value()));                    
                    } else {
                        otherNameEntity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility().value()));
                    }
                    
                    Source source = otherName.getSource();
                    if (source != null && !PojoUtil.isEmpty(source.retrieveSourcePath())) {
                        otherNameEntity.setSource(getSource(source));
                    } else {
                        otherNameEntity.setSource(sourceManager.retrieveSourceEntity());
                    }
                                        
                    otherNameEntities.add(otherNameEntity);
                }
                profileEntity.setOtherNames(otherNameEntities);
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
            
            //TODO: remove when the names migration is done
            //Save also the profile table
            profileEntity.setGivenNames(givenNames.getContent());
        }
    }

    private void setFamilyName(ProfileEntity profileEntity, FamilyName familyName) {
        if (familyName != null && StringUtils.isNotBlank(familyName.getContent())) {
            if(profileEntity.getRecordNameEntity() == null) {
                profileEntity.setRecordNameEntity(new RecordNameEntity());
                profileEntity.getRecordNameEntity().setProfile(profileEntity);
            }
            profileEntity.getRecordNameEntity().setFamilyName(familyName.getContent());
            
            //TODO: remove when the names migration is done
            //Save also the profile table
            profileEntity.setFamilyName(familyName.getContent());
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
                recordName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(creditName.getVisibility().value()));
            } else {
                recordName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
            }
            
            recordName.setCreditName(creditName.getContent());
            
            //TODO: remove when the names migration is done
            //Save also the profile table
            profileEntity.setCreditName(creditName.getContent());
            profileEntity.setNamesVisibility(creditName.getVisibility());
        }
    }

    private void setKeywords(ProfileEntity profileEntity, Keywords keywords) {
        SortedSet<ProfileKeywordEntity> profileKeywordEntities = null;
        SortedSet<ProfileKeywordEntity> existingProfileKeywordEntities = profileEntity.getKeywords();
        Map<String, ProfileKeywordEntity> existingProfileKeywordEntitiesMap = createProfileKeyworkEntitiesMap(existingProfileKeywordEntities);
        if (existingProfileKeywordEntities == null) {
            profileKeywordEntities = new TreeSet<>();
        } else {
            // To allow for orphan deletion
            existingProfileKeywordEntities.clear();
            profileKeywordEntities = existingProfileKeywordEntities;
        }
        if (keywords != null) {
            List<Keyword> keywordList = keywords.getKeyword();
            if (keywordList != null && !keywordList.isEmpty()) {
                for (Keyword keyword : keywordList) {
                    if (StringUtils.isNotBlank(keyword.getContent())) {
                        profileKeywordEntities.add(getProfileKeywordEntity(keyword, profileEntity, existingProfileKeywordEntitiesMap, keywords.getVisibility()));                        
                    }
                }
            }
        }
        profileEntity.setKeywords(profileKeywordEntities);
    }

    private Map<String, ProfileKeywordEntity> createProfileKeyworkEntitiesMap(SortedSet<ProfileKeywordEntity> profileKeywordEntities) {
        Map<String, ProfileKeywordEntity> map = new HashMap<>();
        if (profileKeywordEntities != null) {
            for (ProfileKeywordEntity entity : profileKeywordEntities) {
                String keyword = entity.getKeywordName();
                map.put(keyword, entity);
            }
        }
        return map;
    }

    private ProfileKeywordEntity getProfileKeywordEntity(Keyword keyword, ProfileEntity profileEntity, Map<String, ProfileKeywordEntity> existingProfileKeywordEntitiesMap, Visibility requestVisibility) {
        String keywordContent = keyword.getContent();
        
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setProfile(profileEntity);
        entity.setKeywordName(keywordContent);

        if (keyword.getVisibility() != null){
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(keyword.getVisibility().value()));
        } else {
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value()));
        }
                        
        Source source = keyword.getSource();
        if (source != null && !PojoUtil.isEmpty(source.retrieveSourcePath())) {
            entity.setSource(getSource(source));
        } else {
            entity.setSource(sourceManager.retrieveSourceEntity());
        }
        
        return entity;
    }

    private void setExternalIdentifiers(ProfileEntity profileEntity, ExternalIdentifiers externalIdentifiers) {
        if (externalIdentifiers != null) {
            
            Set<ExternalIdentifierEntity> existingExternalIdentifiers = profileEntity.getExternalIdentifiers();
            Map<Triplet<String, String, String>, ExternalIdentifierEntity> existingExternalIdentifiersMap = createExternalIdentifiersMap(existingExternalIdentifiers);
            Set<ExternalIdentifierEntity> externalIdentifierEntities = null;

            if (existingExternalIdentifiers == null) {
                externalIdentifierEntities = new TreeSet<ExternalIdentifierEntity>();
            } else {
                // To allow for orphan deletion
                existingExternalIdentifiers.clear();
                externalIdentifierEntities = existingExternalIdentifiers;
            }

            List<ExternalIdentifier> externalIdentifierList = externalIdentifiers.getExternalIdentifier();

            if (externalIdentifierList != null && !externalIdentifierList.isEmpty()) {
                for (ExternalIdentifier externalIdentifier : externalIdentifierList) {
                    //Discard the ext ids that comes without external id reference, which is a required field
                    if(externalIdentifier.getExternalIdReference() != null) {
                        ExternalIdentifierEntity externalIdentifierEntity = getExternalIdentifierEntity(profileEntity, externalIdentifier, existingExternalIdentifiersMap, externalIdentifiers.getVisibility());
                        if (externalIdentifierEntity != null) {
                            externalIdentifierEntity.setOwner(profileEntity);
                            externalIdentifierEntities.add(externalIdentifierEntity);
                        }
                    }
                }
            }

            profileEntity.setExternalIdentifiers(externalIdentifierEntities);
        }
    }

    private Map<Triplet<String, String, String>, ExternalIdentifierEntity> createExternalIdentifiersMap(Set<ExternalIdentifierEntity> existingExternalIdentifiers) {
        Map<Triplet<String, String, String>, ExternalIdentifierEntity> map = new HashMap<>();
        if (existingExternalIdentifiers != null) {
            for (ExternalIdentifierEntity entity : existingExternalIdentifiers) {
                Triplet<String, String, String> triplet = createTripletForKey(entity);
                map.put(triplet, entity);
            }
        }
        return map;
    }

    private Triplet<String, String, String> createTripletForKey(ExternalIdentifierEntity entity) {
        String first = entity.getOwner().getId();
        String second = entity.getExternalIdReference();
        String third = entity.getExternalIdCommonName();
        Triplet<String, String, String> triplet = new Triplet<String, String, String>(first, second, third);        
        return triplet;
    }

    private ExternalIdentifierEntity getExternalIdentifierEntity(ProfileEntity profileEntity, ExternalIdentifier externalIdentifier,
            Map<Triplet<String, String, String>, ExternalIdentifierEntity> existingExternalIdentifiersMap, Visibility requestVisibility) {
        if (externalIdentifier != null && externalIdentifier.getExternalIdReference() != null) {
            ExternalIdCommonName externalIdCommonName = externalIdentifier.getExternalIdCommonName();
            ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
            String referenceValue = externalIdReference != null ? externalIdReference.getContent() : null;
            ExternalIdUrl externalIdUrl = externalIdentifier.getExternalIdUrl();
            Visibility externalIdVisibility = externalIdentifier.getVisibility();
            
            String first = profileEntity.getId();            
            
            String second = null;
            if(externalIdentifier.getExternalIdReference() != null)
                second = externalIdentifier.getExternalIdReference().getContent();
            
            String third = null;
            if(externalIdentifier.getExternalIdCommonName() != null)
                third = externalIdentifier.getExternalIdCommonName().getContent(); 
                    

            Triplet<String, String, String> key = new Triplet<>(first, second, third);
            ExternalIdentifierEntity existingExternalIdentifierEntity = existingExternalIdentifiersMap.get(key);
            
            ExternalIdentifierEntity externalIdentifierEntity = null;
            if (existingExternalIdentifierEntity == null) {
                externalIdentifierEntity = new ExternalIdentifierEntity();
                externalIdentifierEntity.setSource(sourceManager.retrieveSourceEntity());
                externalIdentifierEntity.setExternalIdReference(referenceValue);
            } else {
                existingExternalIdentifierEntity.clean();
                externalIdentifierEntity = existingExternalIdentifierEntity;
            }
            
            if (externalIdVisibility != null){
                externalIdentifierEntity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(externalIdVisibility.value()));            
            }else {
                externalIdentifierEntity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT.getVisibility().value()));
            }
            
            externalIdentifierEntity.setExternalIdCommonName(externalIdCommonName != null ? externalIdCommonName.getContent() : null);
            externalIdentifierEntity.setExternalIdUrl(externalIdUrl != null ? externalIdUrl.getValue() : null);
            externalIdentifierEntity.setExternalIdReference(externalIdReference != null ? externalIdReference.getContent() : null);
            
            return externalIdentifierEntity;
        }
        return null;
    }

    private void setDelegations(ProfileEntity profileEntity, Delegation delegation) {
        profileEntity.setGivenPermissionTo(getGivenPermissionsTo(profileEntity, delegation));
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
        
        Source source = null;
        if(contactDetails.getAddress() != null && contactDetails.getAddress().getSource() != null) {
            source = contactDetails.getAddress().getSource();
        }        
        
        Iso3166Country country = contactCountry != null ? contactCountry.getValue() : null;
        
        //Set the info in the address table
        if(country != null) {
            AddressEntity address = new AddressEntity();
            address.setIso2Country(org.orcid.jaxb.model.common_rc2.Iso3166Country.fromValue(country.value()));
            if(profileEntity.getAddresses() != null && !profileEntity.getAddresses().isEmpty()) {
            	Iterator<AddressEntity> iterator = profileEntity.getAddresses().iterator();
            	while (iterator.hasNext()) {
            		AddressEntity temp = iterator.next();
            	    if(temp.getPrimary()) {
            	    	address = temp;
            	    	break;
            	    }
            	}
            }
            address.setPrimary(true);
            address.setDisplayIndex(-1L);
            boolean claimed = profileEntity.getClaimed() == null ? false : profileEntity.getClaimed();
            if(claimed) {
                if(profileEntity.getActivitiesVisibilityDefault() != null) {                
                    address.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profileEntity.getActivitiesVisibilityDefault().value()));
                }else{
                    address.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value()));
                }
            } else {
                Visibility countryVisibility = contactCountry != null ? contactCountry.getVisibility() : Visibility.PRIVATE;
                if(countryVisibility != null) {
                    address.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(countryVisibility.value()));
                } else {
                    address.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
                }
            }
                                                
            address.setUser(profileEntity);
            if (source != null && !PojoUtil.isEmpty(source.retrieveSourcePath())) {
                address.setSource(getSource(source));
            } else {
                address.setSource(sourceManager.retrieveSourceEntity());
            }
            
            HashSet<AddressEntity> addresses = new HashSet<AddressEntity>();
            addresses.add(address);
            profileEntity.setAddresses(addresses);
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
                if (email.getSourceClientId() != null) {
                    SourceEntity source = new SourceEntity(email.getSourceClientId());
                    emailEntity.setSource(source);
                } else if (email.getSource() != null) {
                    SourceEntity source = new SourceEntity(email.getSource());
                    emailEntity.setSource(source);
                }
            } else {
                existingEmailEntity.clean();
                emailEntity = existingEmailEntity;
            }
            emailEntity.setPrimary(email.isPrimary());
            emailEntity.setCurrent(email.isCurrent());
            emailEntity.setVerified(email.isVerified());
            if (email.getVisibility() == null) {
                emailEntity.setVisibility(Visibility.PRIVATE);
            } else {
                emailEntity.setVisibility(email.getVisibility());
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
            if(profileEntity.getBiographyEntity() == null) {
                profileEntity.setBiographyEntity(new BiographyEntity());
                profileEntity.getBiographyEntity().setProfile(profileEntity);
            }
                        
            profileEntity.getBiographyEntity().setBiography(biography.getContent());
            if (biography.getVisibility() != null) {
                profileEntity.getBiographyEntity().setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(biography.getVisibility().value()));
            } else {
                profileEntity.getBiographyEntity().setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profileEntity.getActivitiesVisibilityDefault().value()));
            }
            
            //TODO: remove when the names migration is done
            //Save also the profile table
            if (biography.getVisibility() != null) {
                profileEntity.setBiographyVisibility(biography.getVisibility());
            } else {
                profileEntity.setBiographyVisibility(profileEntity.getActivitiesVisibilityDefault());
            }
            profileEntity.setBiography(biography.getContent());
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
                if (preferences.getActivitiesVisibilityDefault() != null) {
                    profileEntity.setActivitiesVisibilityDefault(preferences.getActivitiesVisibilityDefault().getValue());
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
                profileEntity.setLocale(orcidPreferences.getLocale());
            else
                profileEntity.setLocale(Locale.EN);
        }
    }

    private Set<GivenPermissionToEntity> getGivenPermissionsTo(ProfileEntity profileEntity, Delegation delegation) {
        if (delegation != null) {
            GivenPermissionTo givenPermissionTo = delegation.getGivenPermissionTo();
            if (givenPermissionTo != null && givenPermissionTo.getDelegationDetails() != null && !givenPermissionTo.getDelegationDetails().isEmpty()) {
                Set<GivenPermissionToEntity> givenPermissionToEntities = new HashSet<GivenPermissionToEntity>();
                for (DelegationDetails delegationDetails : givenPermissionTo.getDelegationDetails()) {
                    GivenPermissionToEntity givenPermissionToEntity = new GivenPermissionToEntity();
                    givenPermissionToEntity.setGiver(profileEntity.getId());
                    DelegateSummary profileSummary = delegationDetails.getDelegateSummary();
                    ProfileSummaryEntity profileSummaryEntity = new ProfileSummaryEntity(profileSummary.getOrcidIdentifier().getPath());
                    profileSummaryEntity.setCreditName(profileSummary.getCreditName() != null ? profileSummary.getCreditName().getContent() : null);
                    givenPermissionToEntity.setReceiver(profileSummaryEntity);
                    ApprovalDate approvalDate = delegationDetails.getApprovalDate();
                    if (approvalDate == null) {
                        givenPermissionToEntity.setApprovalDate(new Date());
                    } else {
                        givenPermissionToEntity.setApprovalDate(DateUtils.convertToDate(approvalDate.getValue()));
                    }
                    givenPermissionToEntities.add(givenPermissionToEntity);
                }
                return givenPermissionToEntities;
            }
        }
        return null;
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
            orgRelationEntity.setAffiliationType(affiliation.getType());
            orgRelationEntity.setVisibility(affiliation.getVisibility() == null ? Visibility.PRIVATE : affiliation.getVisibility());
            orgRelationEntity.setSource(getSource(affiliation.getSource()));
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
                profileFundingEntity.setSource(getSource(funding.getSource()));
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
            profileFundingEntity.setExternalIdentifiersJson(getFundingExternalIdentifiersJson(funding.getFundingExternalIdentifiers()));
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

            profileFundingEntity.setType(funding.getType() != null ? funding.getType() : null);
            profileFundingEntity.setOrganizationDefinedType(funding.getOrganizationDefinedFundingType() != null ? funding.getOrganizationDefinedFundingType()
                    .getContent() : null);
            if (funding.getUrl() != null)
                profileFundingEntity.setUrl(StringUtils.isNotBlank(funding.getUrl().getValue()) ? funding.getUrl().getValue() : null);
            profileFundingEntity.setVisibility(funding.getVisibility() != null ? funding.getVisibility() : OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility());

            if (funding.getCreatedDate() != null && funding.getCreatedDate().getValue() != null)
                profileFundingEntity.setDateCreated(funding.getCreatedDate().getValue().toGregorianCalendar().getTime());
            if (funding.getLastModifiedDate() != null && funding.getLastModifiedDate().getValue() != null)
                profileFundingEntity.setLastModified(funding.getLastModifiedDate().getValue().toGregorianCalendar().getTime());

            profileFundingEntity.setOrg(orgEntity);

            return profileFundingEntity;
        }
        return null;
    }
    
    /**
     * Transforms the list of external identifiers into a json object
     * @param fundingExternalIdentifiers
     * @return a json string containig the external identifiers
     * */
    private String getFundingExternalIdentifiersJson(org.orcid.jaxb.model.message.FundingExternalIdentifiers fundingExternalIdentifiers) {
        if (fundingExternalIdentifiers == null) {
            return null;
        }
        //Transform the message external identifiers to core external identifiers
        FundingExternalIdentifiers feis = FundingExternalIdentifiers.fromMessagePojo(fundingExternalIdentifiers);
        if(feis != null && !feis.getFundingExternalIdentifier().isEmpty()) {
            //For all external identifiers, if the relationship is empty set it to self by default
            for(org.orcid.pojo.FundingExternalIdentifier fei : feis.getFundingExternalIdentifier()) {
                if(fei.getRelationship() == null) {
                    fei.setRelationship(Relationship.SELF);
                }
            }
        }
        return JsonUtils.convertToJsonString(feis);
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

    private SourceEntity getSource(Source source) {
        if (source != null) {
            String sourcePath = source.retrieveSourcePath();
            if (StringUtils.isNotEmpty(sourcePath) && !sourcePath.equals(Source.NULL_SOURCE_PROFILE)) {
                ClientDetailsEntity cde = clientDetailsDao.find(sourcePath);
                if (cde != null && cde.getClientType() != null) {
                    return new SourceEntity(cde);
                }
                return new SourceEntity(sourcePath);
            }
        }
        return null;
    }

    private Date toDate(XMLGregorianCalendar completionDate) {
        if (completionDate != null) {
            GregorianCalendar gregorianCalendar = completionDate.toGregorianCalendar();
            return gregorianCalendar.getTime();
        }
        return null;
    }

}
