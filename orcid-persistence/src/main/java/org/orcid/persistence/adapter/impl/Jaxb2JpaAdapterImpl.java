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
package org.orcid.persistence.adapter.impl;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;
import org.orcid.jaxb.model.message.*;
import org.orcid.persistence.adapter.Jaxb2JpaAdapter;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.*;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapterImpl
 * 
 * @author Declan Newman (declan)
 */

public class Jaxb2JpaAdapterImpl implements Jaxb2JpaAdapter {

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity) {
        Assert.notNull(profile, "Cannot convert a null OrcidProfile");
        ProfileEntity profileEntity = existingProfileEntity == null ? new ProfileEntity() : existingProfileEntity;

        // if orci d-id exist us it
        String orcidString = profile.getOrcid().getValue();
        if (profile.getOrcidId() != null && !profile.getOrcidId().isEmpty()) {
            orcidString = OrcidStringUtils.getOrcidNumber(profile.getOrcidId());
        }

        profileEntity.setId(orcidString);
        profileEntity.setOrcidType(profile.getType());
        setBioDetails(profileEntity, profile.getOrcidBio());
        setHistoryDetails(profileEntity, profile.getOrcidHistory());
        setActivityDetails(profileEntity, profile.getOrcidActivities());
        setInternalDetails(profileEntity, profile.getOrcidInternal());

        return profileEntity;
    }

    private void setActivityDetails(ProfileEntity profileEntity, OrcidActivities orcidActivities) {
        if (orcidActivities != null) {
            setPatents(profileEntity, orcidActivities.getOrcidPatents());
            setGrants(profileEntity, orcidActivities.getOrcidGrants());
            setWorks(profileEntity, orcidActivities.getOrcidWorks());
        }
    }

    private void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        if (orcidWorks != null && orcidWorks.getOrcidWork() != null && !orcidWorks.getOrcidWork().isEmpty()) {
            List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
            SortedSet<ProfileWorkEntity> profileWorkEntities = new TreeSet<ProfileWorkEntity>();
            for (OrcidWork orcidWork : orcidWorkList) {
                ProfileWorkEntity profileWorkEntity = getProfileWorkEntity(orcidWork);
                if (profileWorkEntity != null) {
                    profileWorkEntity.setProfile(profileEntity);
                    profileWorkEntities.add(profileWorkEntity);
                }
            }
            profileEntity.setProfileWorks(profileWorkEntities);
        }
    }

    private ProfileWorkEntity getProfileWorkEntity(OrcidWork orcidWork) {
        if (orcidWork != null) {
            ProfileWorkEntity profileWorkEntity = new ProfileWorkEntity();
            profileWorkEntity.setWork(getWorkEntity(orcidWork));
            profileWorkEntity.setVisibility(orcidWork.getVisibility() == null ? Visibility.PRIVATE : orcidWork.getVisibility());
            profileWorkEntity.setSources(getWorkSources(orcidWork.getWorkSources()));
            return profileWorkEntity;
        }
        return null;
    }

    private Set<WorkSourceEntity> getWorkSources(WorkSources workSources) {
        if (workSources != null && workSources.getSource() != null && !workSources.getSource().isEmpty()) {
            List<Source> sources = workSources.getSource();
            Set<WorkSourceEntity> workSourceEntities = new HashSet<WorkSourceEntity>();
            for (Source source : sources) {
                WorkSourceEntity workSourceEntity = new WorkSourceEntity();
                workSourceEntity.setSponsorOrcid(source.getSourceOrcid() != null ? new ProfileEntity(source.getSourceOrcid().getValue()) : null);
                workSourceEntity.setDepositedDate(source.getSourceDate() != null ? toDate(source.getSourceDate().getValue()) : null);
                workSourceEntities.add(workSourceEntity);
            }
            return workSourceEntities;
        }
        return null;
    }

    private WorkEntity getWorkEntity(OrcidWork orcidWork) {
        if (orcidWork != null) {
            WorkEntity workEntity = new WorkEntity();
            Citation workCitation = orcidWork.getWorkCitation();
            if (workCitation != null && StringUtils.isNotBlank(workCitation.getCitation()) && workCitation.getWorkCitationType() != null) {
                workEntity.setCitation(workCitation.getCitation());
                workEntity.setCitationType(workCitation.getWorkCitationType());
            }
            workEntity.setContributors(getWorkContributors(workEntity, orcidWork.getWorkContributors()));
            workEntity.setDescription(orcidWork.getShortDescription() != null ? orcidWork.getShortDescription() : null);
            workEntity.setExternalIdentifiers(getWorkExternalIdentififiers(workEntity, orcidWork.getWorkExternalIdentifiers()));
            workEntity.setPublicationDate(getWorkPublicationDate(orcidWork));
            WorkTitle workTitle = orcidWork.getWorkTitle();
            if (workTitle != null) {
                workEntity.setSubtitle(workTitle.getSubtitle() != null ? workTitle.getSubtitle().getContent() : null);
                workEntity.setTitle(workTitle.getTitle() != null ? workTitle.getTitle().getContent() : null);
            }
            // TODO this code will be phased out when schema 1.0.6.XSD is
            workEntity.setWorkType(WorkType.BIBLE.equals(orcidWork.getWorkType()) ? WorkType.RELIGIOUS_TEXT : orcidWork.getWorkType());
            workEntity.setWorkUrl(orcidWork.getUrl() != null ? orcidWork.getUrl().getValue() : null);
            return workEntity;
        }
        return null;
    }

    private SortedSet<WorkExternalIdentifierEntity> getWorkExternalIdentififiers(WorkEntity workEntity, WorkExternalIdentifiers workExternalIdentifiers) {
        if (workExternalIdentifiers != null && workExternalIdentifiers.getWorkExternalIdentifier() != null
                && !workExternalIdentifiers.getWorkExternalIdentifier().isEmpty()) {
            List<WorkExternalIdentifier> workExternalIdentifierList = workExternalIdentifiers.getWorkExternalIdentifier();
            SortedSet<WorkExternalIdentifierEntity> workExternalIdentifierEntities = new TreeSet<WorkExternalIdentifierEntity>();
            for (WorkExternalIdentifier workExternalIdentifier : workExternalIdentifierList) {
                WorkExternalIdentifierEntity workExternalIdentifierEntity = new WorkExternalIdentifierEntity();
                workExternalIdentifierEntity.setIdentifier(workExternalIdentifier.getWorkExternalIdentifierId() != null ? workExternalIdentifier
                        .getWorkExternalIdentifierId().getContent() : null);
                workExternalIdentifierEntity.setIdentifierType(workExternalIdentifier.getWorkExternalIdentifierType());
                workExternalIdentifierEntity.setWork(workEntity);
                workExternalIdentifierEntities.add(workExternalIdentifierEntity);
            }
            return workExternalIdentifierEntities;
        }
        return null;
    }

    private FuzzyDate getWorkPublicationDate(OrcidWork orcidWork) {
        if (orcidWork != null && orcidWork.getPublicationDate() != null) {
            PublicationDate publicationDate = orcidWork.getPublicationDate();
            Integer year = publicationDate.getYear() != null ? toInteger(publicationDate.getYear().getValue()) : null;
            Integer month = publicationDate.getMonth() != null ? toInteger(publicationDate.getMonth().getValue()) : null;
            Integer day = publicationDate.getDay() != null ? toInteger(publicationDate.getDay().getValue()) : null;
            return new FuzzyDate(year, month, day);
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

    private SortedSet<WorkContributorEntity> getWorkContributors(WorkEntity workEntity, WorkContributors workContributors) {
        if (workContributors != null && workContributors.getContributor() != null && !workContributors.getContributor().isEmpty()) {
            SortedSet<WorkContributorEntity> workContributorEntities = new TreeSet<WorkContributorEntity>();
            List<Contributor> contributorList = workContributors.getContributor();
            for (Contributor contributor : contributorList) {
                WorkContributorEntity workContributorEntity = new WorkContributorEntity();
                workContributorEntity.setContributorEmail(contributor.getContributorEmail() != null ? contributor.getContributorEmail().getValue() : null);
                workContributorEntity.setProfile(contributor.getContributorOrcid() != null ? new ProfileEntity(contributor.getContributorOrcid().getValue()) : null);
                workContributorEntity.setWork(workEntity);
                ContributorAttributes contributorAttributes = contributor.getContributorAttributes();
                if (contributorAttributes != null) {
                    ContributorRole contributorRole = contributorAttributes.getContributorRole();
                    SequenceType contributorSequence = contributorAttributes.getContributorSequence();
                    workContributorEntity.setContributorRole(contributorRole);
                    workContributorEntity.setSequence(contributorSequence);
                }
                workContributorEntity.setCreditName(contributor.getCreditName() != null ? contributor.getCreditName().getContent() : null);
                workContributorEntities.add(workContributorEntity);
            }
            return workContributorEntities;
        }
        return null;
    }

    private void setGrants(ProfileEntity profileEntity, OrcidGrants orcidGrants) {
        if (orcidGrants != null && orcidGrants.getOrcidGrant() != null && !orcidGrants.getOrcidGrant().isEmpty()) {
            List<OrcidGrant> orcidGrantList = orcidGrants.getOrcidGrant();
            SortedSet<ProfileGrantEntity> profileGrantEntities = new TreeSet<ProfileGrantEntity>();
            for (OrcidGrant orcidGrant : orcidGrantList) {
                ProfileGrantEntity profileGrantEntity = getProfileGrantEntity(orcidGrant);
                if (profileGrantEntity != null) {
                    profileGrantEntity.setProfile(profileEntity);
                    profileGrantEntities.add(profileGrantEntity);
                }
            }
            profileEntity.setProfileGrants(profileGrantEntities);
        }
    }

    private ProfileGrantEntity getProfileGrantEntity(OrcidGrant orcidGrant) {
        if (orcidGrant != null) {
            ProfileGrantEntity profileGrantEntity = new ProfileGrantEntity();
            profileGrantEntity.setVisibility(orcidGrant.getVisibility());
            profileGrantEntity.setGrant(getGrant(orcidGrant));
            profileGrantEntity.setSources(getGrantSources(profileGrantEntity, orcidGrant.getGrantSources()));
            return profileGrantEntity;
        }
        return null;
    }

    private Set<GrantSourceEntity> getGrantSources(ProfileGrantEntity profileGrantEntity, GrantSources grantSources) {
        if (grantSources != null && grantSources.getSource() != null && !grantSources.getSource().isEmpty()) {
            List<Source> sources = grantSources.getSource();
            Set<GrantSourceEntity> grantSourceEntities = new HashSet<GrantSourceEntity>();
            for (Source source : sources) {
                GrantSourceEntity grantSourceEntity = new GrantSourceEntity();
                grantSourceEntity.setProfileGrant(profileGrantEntity);
                grantSourceEntity.setDepositedDate(source.getSourceDate() != null ? toDate(source.getSourceDate().getValue()) : null);
                grantSourceEntity.setSponsorOrcid(source.getSourceOrcid() != null ? new ProfileEntity(source.getSourceOrcid().getValue()) : null);
                grantSourceEntities.add(grantSourceEntity);
            }
            return grantSourceEntities;
        }
        return null;
    }

    private GrantEntity getGrant(OrcidGrant orcidGrant) {
        if (orcidGrant != null) {
            GrantEntity grantEntity = new GrantEntity();
            setFundingAgenctDetails(grantEntity, orcidGrant.getFundingAgency());
            setGrantExternalIdentifier(grantEntity, orcidGrant.getGrantExternalIdentifier());

            grantEntity.setContributors(getGrantContributors(grantEntity, orcidGrant));
            grantEntity.setGrantDate(orcidGrant.getGrantDate() != null ? toDate(orcidGrant.getGrantDate().getValue()) : null);
            grantEntity.setGrantNo(orcidGrant.getGrantNumber() != null ? orcidGrant.getGrantNumber().getContent() : null);
            grantEntity.setShortDescription(orcidGrant.getShortDescription() != null ? orcidGrant.getShortDescription() : null);
            return grantEntity;
        }
        return null;
    }

    private void setGrantExternalIdentifier(GrantEntity grantEntity, GrantExternalIdentifier grantExternalIdentifier) {
        if (grantExternalIdentifier != null) {
            grantEntity.setGrantExternalId(grantExternalIdentifier.getGrantExternalId() != null ? grantExternalIdentifier.getGrantExternalId().getContent() : null);
            grantEntity.setGrantExternalProgram(grantExternalIdentifier.getGrantExternalProgram() != null ? grantExternalIdentifier.getGrantExternalProgram()
                    .getContent() : null);
        }
    }

    private void setFundingAgenctDetails(GrantEntity grantEntity, FundingAgency fundingAgency) {
        if (fundingAgency != null) {
            AgencyName agencyName = fundingAgency.getAgencyName();
            AgencyOrcid agencyOrcid = fundingAgency.getAgencyOrcid();
            grantEntity.setAgencyName(agencyName != null ? agencyName.getContent() : null);
            grantEntity.setAgencyOrcid(agencyOrcid != null ? new ProfileEntity(agencyOrcid.getValue()) : null);
        }
    }

    private SortedSet<GrantContributorEntity> getGrantContributors(GrantEntity grantEntity, OrcidGrant orcidGrant) {
        if (orcidGrant != null && orcidGrant.getGrantContributors() != null && orcidGrant.getGrantContributors().getContributor() != null
                && !orcidGrant.getGrantContributors().getContributor().isEmpty()) {
            SortedSet<GrantContributorEntity> grantContributorEntities = new TreeSet<GrantContributorEntity>();
            List<Contributor> contributorList = orcidGrant.getGrantContributors().getContributor();
            for (Contributor contributor : contributorList) {
                GrantContributorEntity grantContributorEntity = getGrantContributorEntity(contributor);
                if (grantContributorEntity != null) {
                    grantContributorEntity.setGrant(grantEntity);
                    grantContributorEntities.add(grantContributorEntity);
                }

            }
            return grantContributorEntities;
        }
        return null;
    }

    private GrantContributorEntity getGrantContributorEntity(Contributor contributor) {
        if (contributor != null) {
            GrantContributorEntity contributorEntity = new GrantContributorEntity();
            contributorEntity.setContributorEmail(contributor.getContributorEmail() != null ? contributor.getContributorEmail().getValue() : null);
            ContributorAttributes contributorAttributes = contributor.getContributorAttributes();
            if (contributorAttributes != null) {
                contributorEntity.setContributorRole(contributorAttributes.getContributorRole());
                contributorEntity.setSequence(contributorAttributes.getContributorSequence());
            }
            contributorEntity.setCreditName(contributor.getCreditName() != null ? contributor.getCreditName().getContent() : null);
            return contributorEntity;
        }
        return null;
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
            patentEntity.setCountryOfIssue(orcidPatent.getCountry() != null ? orcidPatent.getCountry().getContent() : null);
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
                patentSourceEntity.setSponsorOrcid(source.getSourceOrcid() != null ? new ProfileEntity(source.getSourceOrcid().getValue()) : null);
                patentSourceEntities.add(patentSourceEntity);
            }
            return patentSourceEntities;
        }
        return null;
    }

    private void setBioDetails(ProfileEntity profileEntity, OrcidBio orcidBio) {
        if (orcidBio != null) {
            setAffiliations(profileEntity, orcidBio.getAffiliations());
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
        if (profileEntity != null) {
            profileKeywordEntities = profileEntity.getKeywords();
            if (profileKeywordEntities != null) {
                profileKeywordEntities.clear();
            }
        }
        if (keywords != null && keywords.getKeyword() != null && !keywords.getKeyword().isEmpty()) {
            profileEntity.setKeywordsVisibility(keywords.getVisibility());
            List<Keyword> keywordList = keywords.getKeyword();
            if (keywordList != null && !keywordList.isEmpty()) {
                if (profileKeywordEntities == null) {
                    profileKeywordEntities = new TreeSet<ProfileKeywordEntity>();
                }
                for (Keyword keyword : keywordList) {
                    if (StringUtils.isNotBlank(keyword.getContent())) {
                        profileKeywordEntities.add(new ProfileKeywordEntity(profileEntity, keyword.getContent()));
                    }
                }

            }
        }
        profileEntity.setKeywords(profileKeywordEntities);
    }

    private void setExternalIdentifiers(ProfileEntity profileEntity, ExternalIdentifiers externalIdentifiers) {
        if (externalIdentifiers != null) {
            profileEntity.setExternalIdentifiersVisibility(externalIdentifiers.getVisibility());

            Set<ExternalIdentifierEntity> existingExternalIdentifiers = profileEntity.getExternalIdentifiers();
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
                    ExternalIdentifierEntity externalIdentifierEntity = getExternalIdentifierEntity(externalIdentifier);
                    if (externalIdentifierEntity != null) {
                        externalIdentifierEntity.setOwner(profileEntity);
                        externalIdentifierEntities.add(externalIdentifierEntity);
                    }
                }
            }

            profileEntity.setExternalIdentifiers(externalIdentifierEntities);
        }
    }

    private ExternalIdentifierEntity getExternalIdentifierEntity(ExternalIdentifier externalIdentifier) {
        if (externalIdentifier != null && externalIdentifier.getExternalIdReference() != null) {
            ExternalIdentifierEntity externalIdentifierEntity = new ExternalIdentifierEntity();
            ExternalIdCommonName externalIdCommonName = externalIdentifier.getExternalIdCommonName();
            ExternalIdOrcid externalIdOrcid = externalIdentifier.getExternalIdOrcid();
            ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
            ExternalIdUrl externalIdUrl = externalIdentifier.getExternalIdUrl();

            externalIdentifierEntity.setExternalIdCommonName(externalIdCommonName != null ? externalIdCommonName.getContent() : null);
            externalIdentifierEntity.setExternalIdOrcid(externalIdOrcid != null ? new ProfileEntity(externalIdOrcid.getValue()) : null);
            externalIdentifierEntity.setExternalIdReference(externalIdReference != null ? externalIdReference.getContent() : null);
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
            clearUpOldWayOfDoingEmails(profileEntity);
            setCountry(profileEntity, contactDetails);
        }
    }

    private void setCountry(ProfileEntity profileEntity, ContactDetails contactDetails) {
        Country contactCountry = contactDetails.getAddress() != null && contactDetails.getAddress().getCountry() != null ? contactDetails.getAddress().getCountry()
                : null;
        String country = contactCountry != null ? contactCountry.getContent() : null;
        profileEntity.setProfileAddressVisibility(contactCountry != null ? contactCountry.getVisibility() : null);
        profileEntity.setIso2Country(country);
    }

    private void clearUpOldWayOfDoingEmails(ProfileEntity profileEntity) {
        // Clear up old way of doing emails
        profileEntity.setEmail(null);
        profileEntity.setEmailVerified(null);
        profileEntity.setEmailVisibility(null);
        if (profileEntity.getAlternateEmails() != null) {
            profileEntity.getAlternateEmails().clear();
        }
        profileEntity.setAlternativeEmailsVisibility(null);
    }

    private void setEmails(ProfileEntity profileEntity, ContactDetails contactDetails) {
        Set<EmailEntity> existingEmailsEntities = profileEntity.getEmails();
        Set<EmailEntity> emailEntities = null;
        if (existingEmailsEntities == null) {
            emailEntities = new HashSet<>();
        } else {
            // To allow for orphan deletion
            existingEmailsEntities.clear();
            emailEntities = existingEmailsEntities;
        }
        for (Email email : contactDetails.getEmail()) {
            EmailEntity emailEntity = new EmailEntity();
            emailEntity.setId(email.getValue());
            emailEntity.setPrimary(email.isPrimary());
            emailEntity.setCurrent(email.isCurrent());
            emailEntity.setVerified(email.isVerified());
            if (email.getVisibility() == null) {
                emailEntity.setVisibility(Visibility.PRIVATE);
            } else {
                emailEntity.setVisibility(email.getVisibility());
            }
            emailEntity.setProfile(profileEntity);
            if (email.getSource() != null) {
                ProfileEntity source = new ProfileEntity();
                source.setId(email.getSource());
                emailEntity.setSource(source);
            }
            emailEntities.add(emailEntity);
        }
        profileEntity.setEmails(emailEntities);
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
                source.setId(orcidHistory.getSource().getSourceOrcid().getValue());
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

    private void setAffiliations(ProfileEntity profileEntity, List<Affiliation> affiliations) {
        if (affiliations != null && !affiliations.isEmpty()) {
            SortedSet<AffiliationEntity> affiliationEntities = new TreeSet<AffiliationEntity>();
            for (Affiliation affiliation : affiliations) {
                AffiliationEntity affiliationEntity = getAffiliationEntity(affiliation);
                affiliationEntity.setProfile(profileEntity);
                affiliationEntities.add(affiliationEntity);
            }
            profileEntity.setAffiliations(affiliationEntities);
        }
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

    private Set<GivenPermissionToEntity> getGivenPermissionsTo(ProfileEntity profileEntity, Delegation delegation) {
        if (delegation != null) {
            GivenPermissionTo givenPermissionTo = delegation.getGivenPermissionTo();
            if (givenPermissionTo != null && givenPermissionTo.getDelegationDetails() != null && !givenPermissionTo.getDelegationDetails().isEmpty()) {
                Set<GivenPermissionToEntity> givenPermissionToEntities = new HashSet<GivenPermissionToEntity>();
                for (DelegationDetails delegationDetails : givenPermissionTo.getDelegationDetails()) {
                    GivenPermissionToEntity givenPermissionToEntity = new GivenPermissionToEntity();
                    givenPermissionToEntity.setGiver(profileEntity.getId());
                    DelegateSummary profileSummary = delegationDetails.getDelegateSummary();
                    ProfileSummaryEntity profileSummaryEntity = new ProfileSummaryEntity(profileSummary.getOrcid().getValue());
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
                    ProfileSummaryEntity profileSummaryEntity = new ProfileSummaryEntity(profileSummary.getOrcid().getValue());
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

    private AffiliationEntity getAffiliationEntity(Affiliation affiliation) {
        if (affiliation != null) {
            AffiliationEntity affiliationEntity = new AffiliationEntity();
            StartDate startDate = affiliation.getStartDate();
            EndDate endDate = affiliation.getEndDate();

            affiliationEntity.setAffiliationType(affiliation.getAffiliationType());
            affiliationEntity.setAffiliationVisibility(affiliation.getVisibility());
            affiliationEntity.setDepartmentName(affiliation.getDepartmentName());
            affiliationEntity.setEndDate(endDate != null ? toDate(endDate.getValue()) : null);
            affiliationEntity.setInstitutionEntity(getInstitutionEntity(affiliation));
            affiliationEntity.setRoleTitle(affiliation.getRoleTitle());
            affiliationEntity.setStartDate(startDate != null ? toDate(startDate.getValue()) : null);

            if (affiliation.getAddress() != null && affiliation.getAddress().getCountry() != null) {
                affiliationEntity.setAffiliationAddressVisibility(affiliation.getAddress().getCountry().getVisibility());
            }

            return affiliationEntity;
        }
        return null;
    }

    private InstitutionEntity getInstitutionEntity(Affiliation affiliation) {
        if (affiliation != null) {
            InstitutionEntity institutionEntity = new InstitutionEntity();
            institutionEntity.setAddress(getInstitutionAddressEntity(affiliation.getAddress()));
            institutionEntity.setName(affiliation.getAffiliationName());
            return institutionEntity;
        }
        return null;
    }

    private AddressEntity getInstitutionAddressEntity(Address address) {
        if (address != null) {
            AddressEntity addressEntity = new AddressEntity();
            Country country = address.getCountry();
            addressEntity.setCountry(country == null ? null : country.getContent());
            return addressEntity;
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
