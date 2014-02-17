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
package org.orcid.core.adapter.impl;

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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdUrl;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GivenPermissionBy;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidPatents;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PatentContributors;
import org.orcid.jaxb.model.message.PatentSources;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.PatentContributorEntity;
import org.orcid.persistence.jpa.entities.PatentEntity;
import org.orcid.persistence.jpa.entities.PatentSourceEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ProfilePatentEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.keys.WorkExternalIdentifierEntityPk;
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
    private OrgManager orgManager;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

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
        profileEntity.setClientType(profile.getClientType());
        setBioDetails(profileEntity, profile.getOrcidBio());
        setHistoryDetails(profileEntity, profile.getOrcidHistory());
        setActivityDetails(profileEntity, profile.getOrcidActivities());
        setInternalDetails(profileEntity, profile.getOrcidInternal());
        setPreferencesDetails(profileEntity, profile.getOrcidPreferences());

        return profileEntity;
    }

    private void setActivityDetails(ProfileEntity profileEntity, OrcidActivities orcidActivities) {
        Affiliations affiliations = null;        
        OrcidPatents orcidPatents = null;
        FundingList orcidFundings = null;
        OrcidWorks orcidWorks = null;
        if (orcidActivities != null) {
            affiliations = orcidActivities.getAffiliations();            
            orcidPatents = orcidActivities.getOrcidPatents();
            orcidFundings = orcidActivities.getFundings();
            orcidWorks = orcidActivities.getOrcidWorks();
        }
        setOrgAffiliationRelations(profileEntity, affiliations);        
        setPatents(profileEntity, orcidPatents);        
        setFundings(profileEntity, orcidFundings);
        setWorks(profileEntity, orcidWorks);
    }

    private void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        SortedSet<ProfileWorkEntity> profileWorkEntities = getProfileWorkEntities(profileEntity, orcidWorks);
        profileEntity.setProfileWorks(profileWorkEntities);
    }

    private SortedSet<ProfileWorkEntity> getProfileWorkEntities(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        SortedSet<ProfileWorkEntity> existingProfileWorkEntities = profileEntity.getProfileWorks();
        Map<String, ProfileWorkEntity> existingProfileWorkEntitiesMap = createProfileWorkEntitiesMap(existingProfileWorkEntities);
        SortedSet<ProfileWorkEntity> profileWorkEntities = null;
        if (existingProfileWorkEntities == null) {
            profileWorkEntities = new TreeSet<ProfileWorkEntity>();
        } else {
            // To allow for orphan deletion
            existingProfileWorkEntities.clear();
            profileWorkEntities = existingProfileWorkEntities;
        }
        if (orcidWorks != null && orcidWorks.getOrcidWork() != null && !orcidWorks.getOrcidWork().isEmpty()) {
            List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
            for (OrcidWork orcidWork : orcidWorkList) {
                ProfileWorkEntity profileWorkEntity = getProfileWorkEntity(orcidWork, existingProfileWorkEntitiesMap.get(orcidWork.getPutCode()));
                if (profileWorkEntity != null) {
                    profileWorkEntity.setProfile(profileEntity);
                    profileWorkEntities.add(profileWorkEntity);
                }
            }
        }
        return profileWorkEntities;
    }

    private Map<String, ProfileWorkEntity> createProfileWorkEntitiesMap(SortedSet<ProfileWorkEntity> profileWorkEntities) {
        Map<String, ProfileWorkEntity> map = new HashMap<>();
        if (profileWorkEntities != null) {
            for (ProfileWorkEntity profileWorkEntity : profileWorkEntities) {
                map.put(String.valueOf(profileWorkEntity.getWork().getId()), profileWorkEntity);
            }
        }
        return map;
    }

    @Override
    public ProfileWorkEntity getNewProfileWorkEntity(OrcidWork orcidWork, ProfileEntity profileEntity) {
        ProfileWorkEntity profileWorkEntity = getProfileWorkEntity(orcidWork, null);
        profileWorkEntity.setProfile(profileEntity);
        return profileWorkEntity;
    }

    private ProfileWorkEntity getProfileWorkEntity(OrcidWork orcidWork, ProfileWorkEntity existingProfileWorkEntity) {
        if (orcidWork != null) {
            ProfileWorkEntity profileWorkEntity = null;
            WorkEntity workEntity = null;
            if (existingProfileWorkEntity == null) {
                String putCode = orcidWork.getPutCode();
                if (StringUtils.isNotBlank(putCode) && !"-1".equals(putCode)) {
                    throw new IllegalArgumentException("Invalid put-code was supplied: " + putCode);
                }
                profileWorkEntity = new ProfileWorkEntity();
                workEntity = new WorkEntity();
            } else {
                profileWorkEntity = existingProfileWorkEntity;
                workEntity = existingProfileWorkEntity.getWork();
                workEntity.clean();
            }
            profileWorkEntity.setWork(getWorkEntity(orcidWork, workEntity));
            profileWorkEntity.setVisibility(orcidWork.getVisibility() == null ? Visibility.PRIVATE : orcidWork.getVisibility());
            profileWorkEntity.setSourceProfile(getWorkSource(orcidWork.getWorkSource()));
            return profileWorkEntity;
        }
        return null;
    }

    private ProfileEntity getWorkSource(WorkSource workSource) {
        if (workSource != null && StringUtils.isNotEmpty(workSource.getPath()) && !workSource.getPath().equals(WorkSource.NULL_SOURCE_PROFILE)) {
            return new ProfileEntity(workSource.getPath());
        }
        return null;
    }

    private WorkEntity getWorkEntity(OrcidWork orcidWork, WorkEntity workEntity) {
        if (orcidWork != null) {
            Citation workCitation = orcidWork.getWorkCitation();
            if (workCitation != null && StringUtils.isNotBlank(workCitation.getCitation()) && workCitation.getWorkCitationType() != null) {
                workEntity.setCitation(workCitation.getCitation());
                workEntity.setCitationType(workCitation.getWorkCitationType());
            }
            // New way of doing work contributors
            workEntity.setContributorsJson(getWorkContributorsJson(orcidWork.getWorkContributors()));
            // Old way of doing work contributors
            // workEntity.setContributors(getWorkContributors(workEntity,
            // orcidWork.getWorkContributors()));
            workEntity.setDescription(orcidWork.getShortDescription() != null ? orcidWork.getShortDescription() : null);
            workEntity.setExternalIdentifiers(getWorkExternalIdentifiers(workEntity, orcidWork.getWorkExternalIdentifiers()));
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
            return workEntity;
        }
        return null;
    }

    private SortedSet<WorkExternalIdentifierEntity> getWorkExternalIdentifiers(WorkEntity workEntity, WorkExternalIdentifiers workExternalIdentifiers) {
        SortedSet<WorkExternalIdentifierEntity> existingWorkExternalIdentifierEntities = workEntity.getExternalIdentifiers();
        Map<WorkExternalIdentifierEntityPk, WorkExternalIdentifierEntity> existingWorkExternalIdentifierEntitiesMap = createWorkExternalIdentifierEntitiesMap(existingWorkExternalIdentifierEntities);
        SortedSet<WorkExternalIdentifierEntity> workExternalIdentifierEntities = null;
        if (existingWorkExternalIdentifierEntities == null) {
            workExternalIdentifierEntities = new TreeSet<WorkExternalIdentifierEntity>();
        } else {
            // To allow for orphan deletion
            existingWorkExternalIdentifierEntities.clear();
            workExternalIdentifierEntities = existingWorkExternalIdentifierEntities;
        }
        if (workExternalIdentifiers != null && workExternalIdentifiers.getWorkExternalIdentifier() != null
                && !workExternalIdentifiers.getWorkExternalIdentifier().isEmpty()) {
            List<WorkExternalIdentifier> workExternalIdentifierList = workExternalIdentifiers.getWorkExternalIdentifier();
            for (WorkExternalIdentifier workExternalIdentifier : workExternalIdentifierList) {
                WorkExternalIdentifierEntity existingWorkExternalIdentifierEntity = null;
                if (workEntity.getId() != null) {
                    existingWorkExternalIdentifierEntity = existingWorkExternalIdentifierEntitiesMap.get(new WorkExternalIdentifierEntityPk(workExternalIdentifier
                            .getWorkExternalIdentifierId().getContent(), workExternalIdentifier.getWorkExternalIdentifierType(), workEntity.getId()));
                }
                WorkExternalIdentifierEntity workExternalIdentifierEntity = getWorkExternalIdentifier(workEntity, workExternalIdentifier,
                        existingWorkExternalIdentifierEntity);
                workExternalIdentifierEntities.add(workExternalIdentifierEntity);
            }
        }
        return workExternalIdentifierEntities;
    }

    private WorkExternalIdentifierEntity getWorkExternalIdentifier(WorkEntity workEntity, WorkExternalIdentifier workExternalIdentifier,
            WorkExternalIdentifierEntity existingWorkExternalIdentifierEntity) {
        WorkExternalIdentifierEntity workExternalIdentifierEntity = null;
        if (existingWorkExternalIdentifierEntity == null) {
            workExternalIdentifierEntity = new WorkExternalIdentifierEntity();
        } else {
            workExternalIdentifierEntity = existingWorkExternalIdentifierEntity;
        }
        workExternalIdentifierEntity.setIdentifier(workExternalIdentifier.getWorkExternalIdentifierId() != null ? workExternalIdentifier.getWorkExternalIdentifierId()
                .getContent() : null);
        workExternalIdentifierEntity.setIdentifierType(workExternalIdentifier.getWorkExternalIdentifierType());
        workExternalIdentifierEntity.setWork(workEntity);
        return workExternalIdentifierEntity;
    }

    private Map<WorkExternalIdentifierEntityPk, WorkExternalIdentifierEntity> createWorkExternalIdentifierEntitiesMap(
            SortedSet<WorkExternalIdentifierEntity> workExternalIdentifierEntities) {
        Map<WorkExternalIdentifierEntityPk, WorkExternalIdentifierEntity> map = new HashMap<>();
        if (workExternalIdentifierEntities != null) {
            for (WorkExternalIdentifierEntity workExternalIdentifierEntity : workExternalIdentifierEntities) {
                map.put(workExternalIdentifierEntity.getId(), workExternalIdentifierEntity);
            }
        }
        return map;
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

    private void setFundings(ProfileEntity profileEntity, FundingList orcidFundings) {
    	SortedSet<ProfileFundingEntity> existingProfileFundingEntities = profileEntity.getProfileFunding();
    	if(existingProfileFundingEntities == null) {
    		existingProfileFundingEntities = new TreeSet<>();
    	}
    	
    	// Create a map containing the existing profileGrant entities
    	Map<String, ProfileFundingEntity> existingProfileFundingEntitiesMap = createProfileFundingEntitiesMap(existingProfileFundingEntities);    	
    	
    	// A set that will contain the updated profileGrant entities that comes from the orcidGrant object
    	SortedSet<ProfileFundingEntity> updatedProfileFundingEntities = new TreeSet<>();
        
    	// Populate a list of the updated profileGrant entities that comes from the orcidGrant object
    	if(orcidFundings != null && orcidFundings.getFundings() != null && !orcidFundings.getFundings().isEmpty()) {
        	for(Funding orcidFunding : orcidFundings.getFundings()) {
        		ProfileFundingEntity newProfileGrantEntity = getProfileFundingEntity(orcidFunding, existingProfileFundingEntitiesMap.get(orcidFunding.getPutCode()));
        		newProfileGrantEntity.setProfile(profileEntity);
        		updatedProfileFundingEntities.add(newProfileGrantEntity);
        	}
        }
    	    	
    	// Create a map containing the ProfileFundingEntity that comes in the orcidGrant object and that will be persisted
    	Map<String, ProfileFundingEntity> updatedProfileGrantEntitiesMap = createProfileFundingEntitiesMap(updatedProfileFundingEntities);
    	
    	// Remove orphans
    	for(Iterator<ProfileFundingEntity> iterator = updatedProfileFundingEntities.iterator(); iterator.hasNext();){
    		ProfileFundingEntity existingEntity = iterator.next();
    		if(existingEntity.getId() != null && !updatedProfileGrantEntitiesMap.containsKey(Long.toString(existingEntity.getId()))){
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
    
    private void setPatents(ProfileEntity profileEntity, OrcidPatents orcidPatents) {
        if (orcidPatents != null && orcidPatents.getOrcidPatent() != null && !orcidPatents.getOrcidPatent().isEmpty()) {
            List<OrcidPatent> orcidPatentList = orcidPatents.getOrcidPatent();
            SortedSet<ProfilePatentEntity> profilePatentEntities = new TreeSet<ProfilePatentEntity>();
            for (OrcidPatent orcidPatent : orcidPatentList) {
                ProfilePatentEntity profilePatentEntity = getProfilePatentEntity(orcidPatent);
                if (profilePatentEntity != null) {
                    profilePatentEntity.setProfile(profileEntity);
                    profilePatentEntities.add(profilePatentEntity);
                }
            }
            profileEntity.setProfilePatents(profilePatentEntities);
        }
    }

    private ProfilePatentEntity getProfilePatentEntity(OrcidPatent orcidPatent) {
        if (orcidPatent != null) {
            ProfilePatentEntity profilePatentEntity = new ProfilePatentEntity();
            profilePatentEntity.setSources(getPatentSources(profilePatentEntity, orcidPatent.getPatentSources()));
            profilePatentEntity.setPatent(getPatent(orcidPatent));
            profilePatentEntity.setVisibility(orcidPatent.getVisibility());
            return profilePatentEntity;
        }
        return null;
    }

    private PatentEntity getPatent(OrcidPatent orcidPatent) {
        if (orcidPatent != null) {
            PatentEntity patentEntity = new PatentEntity();
            patentEntity.setContributors(getPatentContributors(patentEntity, orcidPatent.getPatentContributors()));
            patentEntity.setCountryOfIssue(orcidPatent.getCountry() != null ? orcidPatent.getCountry().getValue() : null);
            patentEntity.setIssueDate(orcidPatent.getPatentIssueDate() != null ? toDate(orcidPatent.getPatentIssueDate().getValue()) : null);
            patentEntity.setPatentNo(orcidPatent.getPatentNumber() != null ? orcidPatent.getPatentNumber().getContent() : null);
            patentEntity.setShortDescription(orcidPatent.getShortDescription() != null ? orcidPatent.getShortDescription() : null);
            return patentEntity;
        }
        return null;
    }

    private SortedSet<PatentContributorEntity> getPatentContributors(PatentEntity patentEntity, PatentContributors patentContributors) {
        if (patentContributors != null && patentContributors.getContributor() != null && !patentContributors.getContributor().isEmpty()) {
            List<Contributor> contributors = patentContributors.getContributor();
            SortedSet<PatentContributorEntity> patentContributorEntities = new TreeSet<PatentContributorEntity>();
            for (Contributor contributor : contributors) {
                PatentContributorEntity patentContributorEntity = new PatentContributorEntity();
                patentContributorEntity.setContributorEmail(contributor.getContributorEmail() != null ? contributor.getContributorEmail().getValue() : null);
                patentContributorEntity.setCreditName(contributor.getCreditName() != null ? contributor.getCreditName().getContent() : null);
                patentContributorEntity.setPatent(patentEntity);
                ContributorAttributes contributorAttributes = contributor.getContributorAttributes();
                ContributorRole contributorRole = contributorAttributes.getContributorRole();
                SequenceType contributorSequence = contributorAttributes.getContributorSequence();
                patentContributorEntity.setContributorRole(contributorRole != null ? contributorRole : null);
                patentContributorEntity.setSequence(contributorSequence != null ? contributorSequence : null);
                patentContributorEntities.add(patentContributorEntity);
            }
            return patentContributorEntities;
        }
        return null;
    }

    private Set<PatentSourceEntity> getPatentSources(ProfilePatentEntity profilePatentEntity, PatentSources patentSources) {
        if (patentSources != null && patentSources.getSource() != null && !patentSources.getSource().isEmpty()) {
            Set<PatentSourceEntity> patentSourceEntities = new HashSet<PatentSourceEntity>();
            List<Source> sources = patentSources.getSource();
            for (Source source : sources) {
                PatentSourceEntity patentSourceEntity = new PatentSourceEntity();
                patentSourceEntity.setProfilePatent(profilePatentEntity);
                patentSourceEntity.setDepositedDate(source.getSourceDate() != null ? toDate(source.getSourceDate().getValue()) : null);
                patentSourceEntity.setSponsorOrcid(source.getSourceOrcid() != null ? new ProfileEntity(source.getSourceOrcid().getPath()) : null);
                patentSourceEntities.add(patentSourceEntity);
            }
            return patentSourceEntities;
        }
        return null;
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
        profileEntity.setResearcherUrlsVisibility(researcherUrls != null ? researcherUrls.getVisibility() : null);
        if (researcherUrls != null && researcherUrls.getResearcherUrl() != null && !researcherUrls.getResearcherUrl().isEmpty()) {
            List<ResearcherUrl> researcherUrlList = researcherUrls.getResearcherUrl();
            SortedSet<ResearcherUrlEntity> researcherUrlEntities = new TreeSet<ResearcherUrlEntity>();
            for (ResearcherUrl researcherUrl : researcherUrlList) {
                ResearcherUrlEntity researcherUrlEntity = new ResearcherUrlEntity();
                researcherUrlEntity.setUrl(researcherUrl.getUrl() != null ? researcherUrl.getUrl().getValue() : null);
                researcherUrlEntity.setUrlName(researcherUrl.getUrlName() != null ? researcherUrl.getUrlName().getContent() : null);
                researcherUrlEntity.setUser(profileEntity);
                researcherUrlEntities.add(researcherUrlEntity);
            }
            profileEntity.setResearcherUrls(researcherUrlEntities);
        }
    }

    private void setPersonalDetails(ProfileEntity profileEntity, PersonalDetails personalDetails) {
        if (personalDetails != null) {
            setCreditNameDetails(profileEntity, personalDetails.getCreditName());
            setFamilyName(profileEntity, personalDetails.getFamilyName());
            setGivenNames(profileEntity, personalDetails.getGivenNames());
            setOtherNames(profileEntity, personalDetails.getOtherNames());
        }
    }

    private void setOtherNames(ProfileEntity profileEntity, OtherNames otherNames) {
        if (otherNames != null) {
            profileEntity.setOtherNamesVisibility(otherNames.getVisibility());
            List<OtherName> otherNameList = otherNames.getOtherName();
            if (otherNameList != null && !otherNameList.isEmpty()) {
                SortedSet<OtherNameEntity> otherNameEntities = new TreeSet<OtherNameEntity>();
                for (OtherName otherName : otherNameList) {
                    OtherNameEntity otherNameEntity = new OtherNameEntity();
                    otherNameEntity.setDisplayName(otherName.getContent());
                    otherNameEntity.setProfile(profileEntity);
                    otherNameEntities.add(otherNameEntity);
                }
                profileEntity.setOtherNames(otherNameEntities);
            }
        }
    }

    private void setGivenNames(ProfileEntity profileEntity, GivenNames givenNames) {
        if (givenNames != null && StringUtils.isNotBlank(givenNames.getContent())) {
            profileEntity.setGivenNames(givenNames.getContent());
        }
    }

    private void setFamilyName(ProfileEntity profileEntity, FamilyName familyName) {
        if (familyName != null) {
            profileEntity.setFamilyName(familyName.getContent());
        }
    }

    private void setCreditNameDetails(ProfileEntity profileEntity, CreditName creditName) {
        if (creditName != null) {
            profileEntity.setCreditNameVisibility(creditName.getVisibility());
            profileEntity.setCreditName(creditName.getContent());
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
            profileEntity.setKeywordsVisibility(keywords.getVisibility());
            List<Keyword> keywordList = keywords.getKeyword();
            if (keywordList != null && !keywordList.isEmpty()) {
                for (Keyword keyword : keywordList) {
                    if (StringUtils.isNotBlank(keyword.getContent())) {
                        profileKeywordEntities.add(getProfileKeywordEntity(keyword, profileEntity, existingProfileKeywordEntitiesMap));
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
                String keyword = entity.getKeyword();
                map.put(keyword, entity);
            }
        }
        return map;
    }

    private ProfileKeywordEntity getProfileKeywordEntity(Keyword keyword, ProfileEntity profileEntity, Map<String, ProfileKeywordEntity> existingProfileKeywordEntitiesMap) {
        String keywordContent = keyword.getContent();
        ProfileKeywordEntity existingProfileKeywordEntity = existingProfileKeywordEntitiesMap.get(keywordContent);
        if (existingProfileKeywordEntity != null) {
            return existingProfileKeywordEntity;
        }
        return new ProfileKeywordEntity(profileEntity, keywordContent);
    }

    private void setExternalIdentifiers(ProfileEntity profileEntity, ExternalIdentifiers externalIdentifiers) {
        if (externalIdentifiers != null) {
            profileEntity.setExternalIdentifiersVisibility(externalIdentifiers.getVisibility());

            Set<ExternalIdentifierEntity> existingExternalIdentifiers = profileEntity.getExternalIdentifiers();
            Map<Pair<String, String>, ExternalIdentifierEntity> existingExternalIdentifiersMap = createExternalIdentifiersMap(existingExternalIdentifiers);
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
                    ExternalIdentifierEntity externalIdentifierEntity = getExternalIdentifierEntity(externalIdentifier, existingExternalIdentifiersMap);
                    if (externalIdentifierEntity != null) {
                        externalIdentifierEntity.setOwner(profileEntity);
                        externalIdentifierEntities.add(externalIdentifierEntity);
                    }
                }
            }

            profileEntity.setExternalIdentifiers(externalIdentifierEntities);
        }
    }

    private Map<Pair<String, String>, ExternalIdentifierEntity> createExternalIdentifiersMap(Set<ExternalIdentifierEntity> existingExternalIdentifiers) {
        Map<Pair<String, String>, ExternalIdentifierEntity> map = new HashMap<>();
        if (existingExternalIdentifiers != null) {
            for (ExternalIdentifierEntity entity : existingExternalIdentifiers) {
                Pair<String, String> pair = createPairForKey(entity);
                map.put(pair, entity);
            }
        }
        return map;
    }

    private Pair<String, String> createPairForKey(ExternalIdentifierEntity entity) {
        ProfileEntity profileEntity = entity.getExternalIdOrcid();
        String id = null;
        if (profileEntity != null) {
            id = profileEntity.getId();
        }
        Pair<String, String> pair = new ImmutablePair<>(entity.getExternalIdReference(), id);
        return pair;
    }

    private ExternalIdentifierEntity getExternalIdentifierEntity(ExternalIdentifier externalIdentifier,
            Map<Pair<String, String>, ExternalIdentifierEntity> existingExternalIdentifiersMap) {
        if (externalIdentifier != null && externalIdentifier.getExternalIdReference() != null) {
            ExternalIdCommonName externalIdCommonName = externalIdentifier.getExternalIdCommonName();
            ExternalIdOrcid externalIdOrcid = externalIdentifier.getExternalIdOrcid();
            String externalIdOrcidValue = externalIdOrcid != null ? externalIdOrcid.getPath() : null;
            ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
            String referenceValue = externalIdReference != null ? externalIdReference.getContent() : null;
            ExternalIdUrl externalIdUrl = externalIdentifier.getExternalIdUrl();

            Pair<String, String> key = new ImmutablePair<>(referenceValue, externalIdOrcidValue);
            ExternalIdentifierEntity existingExternalIdentifierEntity = existingExternalIdentifiersMap.get(key);

            ExternalIdentifierEntity externalIdentifierEntity = null;
            if (existingExternalIdentifierEntity == null) {
                externalIdentifierEntity = new ExternalIdentifierEntity();
                ProfileEntity profileEntity = externalIdOrcid != null ? new ProfileEntity(externalIdOrcidValue) : null;
                externalIdentifierEntity.setExternalIdOrcid(profileEntity);
                externalIdentifierEntity.setExternalIdReference(referenceValue);
            } else {
                existingExternalIdentifierEntity.clean();
                externalIdentifierEntity = existingExternalIdentifierEntity;
            }

            externalIdentifierEntity.setExternalIdCommonName(externalIdCommonName != null ? externalIdCommonName.getContent() : null);
            externalIdentifierEntity.setExternalIdUrl(externalIdUrl != null ? externalIdUrl.getValue() : null);
            return externalIdentifierEntity;
        }
        return null;
    }

    private void setDelegations(ProfileEntity profileEntity, Delegation delegation) {
        profileEntity.setGivenPermissionTo(getGivenPermissionsTo(profileEntity, delegation));
        profileEntity.setGivenPermissionBy(getGivenPermissionsBy(profileEntity, delegation));
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
        profileEntity.setProfileAddressVisibility(contactCountry != null ? contactCountry.getVisibility() : null);
        profileEntity.setIso2Country(country);
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
                if (email.getSource() != null) {
                    ProfileEntity source = new ProfileEntity();
                    source.setId(email.getSource());
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
            if (orcidHistory.getSource() != null) {
                ProfileEntity source = new ProfileEntity();
                source.setId(orcidHistory.getSource().getSourceOrcid().getPath());
                profileEntity.setSource(source);
            }
        }
    }

    private void setBiographyDetails(ProfileEntity profileEntity, Biography biography) {
        if (biography != null) {
            if (biography.getVisibility() != null) {
                profileEntity.setBiographyVisibility(biography.getVisibility());
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
            if (orcidInternal.getGroupOrcidIdentifier() != null) {
                profileEntity.setGroupOrcid(orcidInternal.getGroupOrcidIdentifier().getPath());
            }
            Preferences preferences = orcidInternal.getPreferences();
            if (preferences != null) {
                profileEntity.setSendChangeNotifications(preferences.getSendChangeNotifications() == null ? null : preferences.getSendChangeNotifications().isValue());
                profileEntity.setSendOrcidNews(preferences.getSendOrcidNews() == null ? null : preferences.getSendOrcidNews().isValue());
                // Use the default value in the ProfileEntity class if work
                // visibility default is not given
                if (preferences.getWorkVisibilityDefault() != null) {
                    profileEntity.setWorkVisibilityDefault(preferences.getWorkVisibilityDefault().getValue());
                }
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

    private Set<GivenPermissionByEntity> getGivenPermissionsBy(ProfileEntity profileEntity, Delegation delegation) {
        if (delegation != null) {
            GivenPermissionBy givenPermissionBy = delegation.getGivenPermissionBy();
            if (givenPermissionBy != null && givenPermissionBy.getDelegationDetails() != null && !givenPermissionBy.getDelegationDetails().isEmpty()) {
                Set<GivenPermissionByEntity> givenPermissionByEntities = new HashSet<GivenPermissionByEntity>();
                for (DelegationDetails delegationDetails : givenPermissionBy.getDelegationDetails()) {
                    GivenPermissionByEntity givenPermissionByEntity = new GivenPermissionByEntity();
                    DelegateSummary profileSummary = delegationDetails.getDelegateSummary();
                    ProfileSummaryEntity profileSummaryEntity = new ProfileSummaryEntity(profileSummary.getOrcidIdentifier().getPath());
                    profileSummaryEntity.setCreditName(profileSummary.getCreditName().getContent());
                    givenPermissionByEntity.setGiver(profileSummaryEntity);
                    givenPermissionByEntity.setReceiver(profileEntity.getId());
                    ApprovalDate approvalDate = delegationDetails.getApprovalDate();
                    if (approvalDate == null) {
                        givenPermissionByEntity.setApprovalDate(new Date());
                    } else {
                        givenPermissionByEntity.setApprovalDate(DateUtils.convertToDate(approvalDate.getValue()));
                    }
                    givenPermissionByEntities.add(givenPermissionByEntity);
                }
                return givenPermissionByEntities;
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
    
    /**
     * Transforms a OrcidGrant object into a ProfileFundingEntity object
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

    private OrgAffiliationRelationEntity getOrgAffiliationRelationEntity(Affiliation affiliation, OrgAffiliationRelationEntity exisitingOrgAffiliationEntity) {
        if (affiliation != null) {
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
            orgRelationEntity.setOrg(getOrgEntity(affiliation));
            orgRelationEntity.setTitle(affiliation.getRoleTitle());
            orgRelationEntity.setStartDate(startDate != null ? new StartDateEntity(startDate) : null);

            return orgRelationEntity;
        }
        return null;
    }
    
    /**
     * Get a ProfileFundingEntity based on a Grant object
     * @param funding
     * @param exisitingProfileFundingEntity
     * @return a ProfileFundingEntity created from the provided funding 
     * */
    private ProfileFundingEntity getProfileFundingEntity(Funding funding, ProfileFundingEntity exisitingProfileFundingEntity) {
        if (funding != null) {
        	ProfileFundingEntity profileFundingEntity = null;
            if (exisitingProfileFundingEntity == null) {
                String putCode = funding.getPutCode();
                if (StringUtils.isNotBlank(putCode) && !"-1".equals(putCode)) {
                    throw new IllegalArgumentException("Invalid put-code was supplied for a funding: " + putCode);
                }
                profileFundingEntity = new ProfileFundingEntity();
                profileFundingEntity.setSource(getSource(funding.getSource()));
                profileFundingEntity.setOrg(getOrgEntity(funding));
            } else {
            	profileFundingEntity = exisitingProfileFundingEntity;
            	profileFundingEntity.clean();
            }
            FuzzyDate startDate = funding.getStartDate();
            FuzzyDate endDate = funding.getEndDate();
            if(funding.getAmount() != null) {
            	profileFundingEntity.setAmount(StringUtils.isNotBlank(funding.getAmount().getContent()) ? funding.getAmount().getContent() : null);            
            	profileFundingEntity.setCurrencyCode(funding.getAmount().getCurrencyCode() != null ? funding.getAmount().getCurrencyCode() : null);
            }
            
            profileFundingEntity.setContributorsJson(getFundingContributorsJson(funding.getFundingContributors()));
            profileFundingEntity.setDescription(StringUtils.isNotBlank(funding.getDescription()) ? funding.getDescription() : null);
            profileFundingEntity.setEndDate(endDate != null ? new EndDateEntity(endDate) : null);
            profileFundingEntity.setExternalIdentifiers(getGrantExternalIdentifiers(profileFundingEntity, funding.getFundingExternalIdentifiers()));            
            profileFundingEntity.setStartDate(startDate != null ? new StartDateEntity(startDate) : null);
            
            FundingTitle fundingTitle = funding.getTitle();
            if(fundingTitle != null) {
            	String title = null, translatedTitle = null, languageCode = null;            	
            	if(fundingTitle.getTitle() != null)
            		title = fundingTitle.getTitle().getContent();
            	if(fundingTitle.getTranslatedTitle() != null) {
            		translatedTitle = fundingTitle.getTranslatedTitle().getContent();
            		languageCode = fundingTitle.getTranslatedTitle().getLanguageCode();
            	}
            	
            	profileFundingEntity.setTitle(StringUtils.isNotBlank(title) ? title : null);
            	profileFundingEntity.setTranslatedTitle(StringUtils.isNotBlank(translatedTitle) ? translatedTitle : null);
            	profileFundingEntity.setTranslatedTitleLanguageCode(StringUtils.isNotBlank(languageCode) ? languageCode : null);            	
            }
                        
            profileFundingEntity.setType(funding.getType() != null ? funding.getType() : null);
            if(funding.getUrl() != null)
            	profileFundingEntity.setUrl(StringUtils.isNotBlank(funding.getUrl().getValue()) ? funding.getUrl().getValue() : null);            
            profileFundingEntity.setVisibility(funding.getVisibility() != null ? funding.getVisibility() : OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility());

            return profileFundingEntity;
        }
        return null;
    }
    
    /**
     * Get a list of GrantExternalIdentifierEntity from the external identifiers
     * @param profileGrantEntity
     * @param externalIdentifiers
     * @return a list of GrantExternalIdentifierEntity based on the provided OrcidGrantExternalIdentifiers
     * */
    private SortedSet<FundingExternalIdentifierEntity> getGrantExternalIdentifiers(ProfileFundingEntity profileFundingEntity, FundingExternalIdentifiers externalIdentifiers) {
    	if(externalIdentifiers == null || externalIdentifiers.getFundingExternalIdentifier() == null || externalIdentifiers.getFundingExternalIdentifier().isEmpty())
    		return null;
    	TreeSet<FundingExternalIdentifierEntity> result = new TreeSet<>();
    	List<FundingExternalIdentifier> externalIdentifierList = externalIdentifiers.getFundingExternalIdentifier();
    	for(FundingExternalIdentifier externalIdentifier : externalIdentifierList){
    		FundingExternalIdentifierEntity entity = new FundingExternalIdentifierEntity();    		
    		entity.setProfileFunding(profileFundingEntity);
    		entity.setType(StringUtils.isNotEmpty(externalIdentifier.getType()) ? externalIdentifier.getType() : null);
    		entity.setValue(StringUtils.isNotEmpty(externalIdentifier.getValue()) ? externalIdentifier.getValue() : null);
    		if(externalIdentifier.getUrl() != null)
    			entity.setUrl(StringUtils.isNotEmpty(externalIdentifier.getUrl().getValue()) ? externalIdentifier.getUrl().getValue() : null);    		    		
    		result.add(entity);
    		if(StringUtils.isNotEmpty(externalIdentifier.getPutCode()))
    			entity.setId(Long.valueOf(externalIdentifier.getPutCode()));
    	}
    	return result;
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
     * @param orcidGrant
     * @return a OrgEntity based on the provided OrcidGrant
     * */
    private OrgEntity getOrgEntity(Funding orcidGrant) {
        if (orcidGrant != null) {
            OrgEntity orgEntity = new OrgEntity();
            Organization organization = orcidGrant.getOrganization();
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
    
    private ProfileEntity getSource(Source source) {
        if (source != null && StringUtils.isNotEmpty(source.getSourceOrcid().getPath()) && !source.getSourceOrcid().getPath().equals(WorkSource.NULL_SOURCE_PROFILE)) {
            return new ProfileEntity(source.getSourceOrcid().getPath());
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
