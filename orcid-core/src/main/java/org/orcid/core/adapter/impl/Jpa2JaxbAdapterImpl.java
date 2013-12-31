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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.security.DefaultPermissionChecker;
import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.*;
import org.orcid.persistence.jpa.entities.BaseContributorEntity;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.FuzzyDateEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.PatentContributorEntity;
import org.orcid.persistence.jpa.entities.PatentEntity;
import org.orcid.persistence.jpa.entities.PatentSourceEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ProfilePatentEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceAware;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p/>
 * Adapter for converting a JPA {@link ProfileEntity} entity to the
 * corresponding JAXB {@link OrcidProfile}
 * <p/>
 * This is a complex transformation that does not take into consideration any
 * scope or permissions.
 * <p/>
 * orcid-persistence - Dec 7, 2011 - Jpa2JaxbAdapterImpl
 * 
 * @author Declan Newman (declan)
 */

public class Jpa2JaxbAdapterImpl implements Jpa2JaxbAdapter {

    @Value("${org.orcid.core.baseUri:http://orcid.org}")
    private String baseUri = null;

    private DatatypeFactory datatypeFactory = null;

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    public Jpa2JaxbAdapterImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // We're in serious trouble and can't carry on
            throw new IllegalStateException("Cannot create new DatatypeFactory");
        }
    }

    @Override
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity) {
        return toOrcidProfile(profileEntity, LoadOptions.ALL);
    }

    @Override
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions) {
        if (profileEntity == null) {
            throw new IllegalArgumentException("Cannot convert a null profileEntity");
        }

        OrcidProfile profile = new OrcidProfile();
        OrcidType type = profileEntity.getOrcidType();
        profile.setOrcidIdentifier(new OrcidIdentifier(getOrcidIdBase(profileEntity.getId())));
        // load deprecation info
        profile.setOrcidDeprecated(getOrcidDeprecated(profileEntity));

        if (loadOptions.isLoadActivities()) {
            profile.setOrcidActivities(getOrcidActivities(profileEntity));
        }
        if (loadOptions.isLoadBio()) {
            profile.setOrcidBio(getOrcidBio(profileEntity));
        }
        profile.setOrcidHistory(getOrcidHistory(profileEntity));
        if (loadOptions.isLoadInternal()) {
            profile.setOrcidInternal(getOrcidInternal(profileEntity));
        }
        profile.setOrcidPreferences(getOrcidPreferences(profileEntity));
        profile.setPassword(profileEntity.getEncryptedPassword());
        profile.setSecurityQuestionAnswer(profileEntity.getEncryptedSecurityAnswer());
        profile.setType(type == null ? OrcidType.USER : type);
        profile.setClientType(profileEntity.getClientType());
        profile.setGroupType(profileEntity.getGroupType());
        profile.setVerificationCode(profileEntity.getEncryptedVerificationCode());
        return profile;
    }

    @Override
    public OrcidClient toOrcidClient(ProfileEntity profileEntity) {
        OrcidClient client = new OrcidClient();
        client.setDisplayName(profileEntity.getCreditName());
        client.setClientId(profileEntity.getId());
        client.setShortDescription(profileEntity.getBiography());
        client.setType(profileEntity.getClientType());
        Set<ResearcherUrlEntity> researcherUrls = profileEntity.getResearcherUrls();
        if (researcherUrls != null && !researcherUrls.isEmpty()) {
            client.setWebsite(researcherUrls.iterator().next().getUrl());
        }
        ClientDetailsEntity clientDetailsEntity = profileEntity.getClientDetails();
        if (clientDetailsEntity != null) {
            client.setClientSecret(clientDetailsEntity.getClientSecretForJpa());
            Set<ClientRedirectUriEntity> redirectUriEntities = clientDetailsEntity.getClientRegisteredRedirectUris();
            RedirectUris redirectUris = new RedirectUris();
            client.setRedirectUris(redirectUris);
            for (ClientRedirectUriEntity redirectUriEntity : redirectUriEntities) {
                RedirectUri redirectUri = new RedirectUri(redirectUriEntity.getRedirectUri());
                redirectUri.setType(RedirectUriType.fromValue(redirectUriEntity.getRedirectUriType()));
                String predefinedScope = redirectUriEntity.getPredefinedClientScope();
                if (StringUtils.isNotBlank(predefinedScope)) {
                    List<ScopePathType> scopePathType = new ArrayList<ScopePathType>(ScopePathType.getScopesFromSpaceSeparatedString(predefinedScope));
                    redirectUri.setScope(scopePathType);
                }
                redirectUris.getRedirectUri().add(redirectUri);
            }
        }

        return client;
    }

    @Override
    public OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity) {
        OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupOrcid(profileEntity.getId());
        group.setGroupName(profileEntity.getCreditName());
        group.setType(profileEntity.getGroupType());
        Set<EmailEntity> emailEntities = profileEntity.getEmails();
        for (EmailEntity emailEntity : emailEntities) {
            group.setEmail(emailEntity.getId());
        }
        for (ProfileEntity clientProfileEntity : profileEntity.getClientProfiles()) {
            OrcidClient client = toOrcidClient(clientProfileEntity);
            group.getOrcidClient().add(client);
        }
        return group;
    }

    private OrcidHistory getOrcidHistory(ProfileEntity profileEntity) {
        OrcidHistory history = new OrcidHistory();

        if (profileEntity.getCompletedDate() != null) {
            history.setCompletionDate(new CompletionDate(toXMLGregorianCalendar(profileEntity.getCompletedDate())));
        }

        Boolean confirmed = profileEntity.getClaimed() != null ? profileEntity.getClaimed() : Boolean.FALSE;
        history.setClaimed(new Claimed(confirmed));
        String creationMethod = profileEntity.getCreationMethod();
        history.setCreationMethod(CreationMethod.isValid(creationMethod) ? CreationMethod.fromValue(creationMethod) : CreationMethod.WEBSITE);
        history.setSource(getSponsor(profileEntity));

        if (profileEntity.getSubmissionDate() != null) {
            history.setSubmissionDate(new SubmissionDate(toXMLGregorianCalendar(profileEntity.getSubmissionDate())));
        }

        if (profileEntity.getDeactivationDate() != null) {
            history.setDeactivationDate(new DeactivationDate(toXMLGregorianCalendar(profileEntity.getDeactivationDate())));
        }

        if (profileEntity.getLastModified() != null) {
            history.setLastModifiedDate(new LastModifiedDate(toXMLGregorianCalendar(profileEntity.getLastModified())));
        }
        return history;
    }

    private OrcidDeprecated getOrcidDeprecated(ProfileEntity profileEntity) {
        OrcidDeprecated orcidDeprecated = null;
        if (profileEntity.getPrimaryRecord() != null) {
            orcidDeprecated = new OrcidDeprecated();
            orcidDeprecated.setDate(new DeprecatedDate(toXMLGregorianCalendar(profileEntity.getDeprecatedDate())));
            PrimaryRecord primaryRecord = new PrimaryRecord();
            primaryRecord.setOrcid(new Orcid(profileEntity.getPrimaryRecord().getId()));
            primaryRecord.setOrcidId(new Url(baseUri.replace("https", "http") + "/" + profileEntity.getPrimaryRecord().getId()));
            orcidDeprecated.setPrimaryRecord(primaryRecord);
        }
        return orcidDeprecated;
    }

    private OrcidIdBase getOrcidIdBase(String id) {
        OrcidIdBase orcidId = new OrcidIdBase();
        String correctedBaseUri = baseUri.replace("https", "http");
        try {
            URI uri = new URI(correctedBaseUri);
            orcidId.setHost(uri.getHost());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing base uri", e);
        }
        orcidId.setUri(correctedBaseUri + "/" + id);
        orcidId.setPath(id);
        return orcidId;
    }

    private OrcidActivities getOrcidActivities(ProfileEntity profileEntity) {
        Affiliations affiliations = getAffiliations(profileEntity);        
        OrcidFundingList orcidGrants = getOrcidGrants(profileEntity);
        OrcidPatents orcidPatents = getOrcidPatents(profileEntity);
        OrcidWorks orcidWorks = getOrcidWorks(profileEntity);
        if (NullUtils.allNull(orcidGrants, orcidPatents, orcidWorks, affiliations)) {
            return null;
        }
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidActivities.setOrcidFundings(orcidGrants);
        orcidActivities.setOrcidPatents(orcidPatents);
        orcidActivities.setOrcidWorks(orcidWorks);
        orcidActivities.setAffiliations(affiliations);
        return orcidActivities;
    }

    private OrcidFundingList getOrcidGrants(ProfileEntity profileEntity) {
    	Set<ProfileFundingEntity> profileGrants = profileEntity.getProfileFunding();
    	if(profileGrants != null && !profileGrants.isEmpty()) {
    		OrcidFundingList grants = new OrcidFundingList();
    		List<OrcidFunding> grantList = grants.getOrcidFunding();
    		for(ProfileFundingEntity profileFundingEntity : profileGrants) {
    			grantList.add(getOrcidFunding(profileFundingEntity));
    		}
    		
    		return grants;
    	}
    	return null;
    }

    private ContributorAttributes getContributorAttributes(BaseContributorEntity contributorEntity) {
        if (contributorEntity == null) {
            return null;
        }
        ContributorAttributes contributorAttributes = new ContributorAttributes();
        contributorAttributes.setContributorRole(contributorEntity.getContributorRole());
        contributorAttributes.setContributorSequence(contributorEntity.getSequence());
        return contributorAttributes;
    }

    private OrcidPatents getOrcidPatents(ProfileEntity profileEntity) {
        Set<ProfilePatentEntity> profilePatents = profileEntity.getProfilePatents();
        if (profilePatents == null || profilePatents.isEmpty()) {
            return null;
        }
        OrcidPatents orcidPatents = new OrcidPatents();
        for (ProfilePatentEntity profilePatentEntity : profilePatents) {
            OrcidPatent orcidPatent = getOrcidPatent(profilePatentEntity);
            if (orcidPatent != null) {
                orcidPatent.setVisibility(profilePatentEntity.getVisibility());
                orcidPatents.getOrcidPatent().add(orcidPatent);
            }
        }
        return orcidPatents;
    }

    private OrcidPatent getOrcidPatent(ProfilePatentEntity profilePatentEntity) {
        if (profilePatentEntity == null || profilePatentEntity.getPatent() == null) {
            return null;
        }
        OrcidPatent orcidPatent = new OrcidPatent();
        PatentEntity patentEntity = profilePatentEntity.getPatent();
        orcidPatent.setCountry(patentEntity.getCountryOfIssue() != null ? new Country(patentEntity.getCountryOfIssue()) : null);
        orcidPatent.setPatentContributors(getPatentContributors(patentEntity));
        orcidPatent.setPatentIssueDate(patentEntity.getIssueDate() != null ? new PatentIssueDate(toXMLGregorianCalendar(patentEntity.getIssueDate())) : null);
        orcidPatent.setPatentNumber(StringUtils.isNotBlank(patentEntity.getPatentNo()) ? new PatentNumber(patentEntity.getPatentNo()) : null);
        orcidPatent.setPatentSources(getPatentSources(profilePatentEntity.getSources()));
        orcidPatent.setPutCode(Long.toString(patentEntity.getId()));
        orcidPatent.setShortDescription(patentEntity.getShortDescription());
        orcidPatent.setVisibility(profilePatentEntity.getVisibility());
        return orcidPatent;
    }

    private PatentSources getPatentSources(Set<PatentSourceEntity> sourceEntities) {
        if (sourceEntities == null || sourceEntities.isEmpty()) {
            return null;
        }
        PatentSources patentSources = new PatentSources();
        for (PatentSourceEntity patentSourceEntity : sourceEntities) {
            Source source = new Source();
            source.setSourceDate(getSourceDate(patentSourceEntity.getDepositedDate()));
            ProfileEntity sponsorOrcid = patentSourceEntity.getSponsorOrcid();
            if (sponsorOrcid != null) {
                source.setSourceOrcid(new SourceOrcid(sponsorOrcid.getId()));
                source.setSourceName(StringUtils.isNotBlank(sponsorOrcid.getCreditName()) ? new SourceName(sponsorOrcid.getCreditName()) : null);
            }
            patentSources.getSource().add(source);
        }
        return patentSources;
    }

    private PatentContributors getPatentContributors(PatentEntity patentEntity) {
        if (patentEntity == null || patentEntity.getContributors() == null || patentEntity.getContributors().isEmpty()) {
            return null;
        }
        Set<PatentContributorEntity> contributorEntities = patentEntity.getContributors();
        PatentContributors patentContributors = new PatentContributors();
        for (PatentContributorEntity patentContributorEntity : contributorEntities) {
            Contributor patentContributor = getPatentContributor(patentContributorEntity);
            if (patentContributor != null) {
                patentContributors.getContributor().add(patentContributor);
            }
        }
        return patentContributors;
    }

    private Contributor getPatentContributor(PatentContributorEntity patentContributorEntity) {
        if (patentContributorEntity == null) {
            return null;
        }
        Contributor contributor = new Contributor();
        contributor.setContributorEmail(StringUtils.isNotBlank(patentContributorEntity.getContributorEmail()) ? new ContributorEmail(patentContributorEntity
                .getContributorEmail()) : null);
        contributor.setContributorAttributes(getContributorAttributes(patentContributorEntity));
        ProfileEntity profile = patentContributorEntity.getProfile();
        if (profile != null) {
            contributor.setContributorOrcid(new ContributorOrcid(profile.getId()));
            contributor.setCreditName(StringUtils.isNotBlank(profile.getCreditName()) ? new CreditName(profile.getCreditName()) : null);
        } else {
            contributor.setCreditName(StringUtils.isNotBlank(patentContributorEntity.getCreditName()) ? new CreditName(patentContributorEntity.getCreditName()) : null);
        }
        return contributor;
    }

    private OrcidWorks getOrcidWorks(ProfileEntity profileEntity) {
        Set<ProfileWorkEntity> profileWorks = profileEntity.getProfileWorks();
        if (profileWorks != null && !profileWorks.isEmpty()) {
            OrcidWorks works = new OrcidWorks();
            for (ProfileWorkEntity profileWorkEntity : profileWorks) {
                OrcidWork orcidWork = getOrcidWork(profileWorkEntity);
                orcidWork.setVisibility(profileWorkEntity.getVisibility());                
                works.getOrcidWork().add(orcidWork);
            }
            return works;
        }
        return null;
    }

    private OrcidBio getOrcidBio(ProfileEntity profileEntity) {
        OrcidBio orcidBio = new OrcidBio();

        orcidBio.setContactDetails(getContactDetails(profileEntity));
        orcidBio.setExternalIdentifiers(getExternalIdentifiers(profileEntity));
        orcidBio.setDelegation(getDelegation(profileEntity));
        orcidBio.setPersonalDetails(getPersonalDetails(profileEntity));
        orcidBio.setKeywords(getKeywords(profileEntity));
        orcidBio.setBiography(getBiography(profileEntity));
        orcidBio.setApplications(getApplications(profileEntity));
        orcidBio.setResearcherUrls(getResearcherUrls(profileEntity));
        return orcidBio;
    }

    private PersonalDetails getPersonalDetails(ProfileEntity profileEntity) {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setGivenNames(getGivenNames(profileEntity.getGivenNames()));
        personalDetails.setFamilyName(getFamilyName(profileEntity.getFamilyName()));
        personalDetails.setCreditName(getCreditName(profileEntity));
        personalDetails.setOtherNames(getOtherNames(profileEntity));
        return personalDetails;
    }

    private Affiliation getAffiliation(OrgAffiliationRelationEntity orgAffiliationRelationEntity) {
        Affiliation affiliation = new Affiliation();
        affiliation.setPutCode(Long.toString(orgAffiliationRelationEntity.getId()));
        affiliation.setType(orgAffiliationRelationEntity.getAffiliationType());
        affiliation.setRoleTitle(orgAffiliationRelationEntity.getTitle());

        FuzzyDateEntity startDate = orgAffiliationRelationEntity.getStartDate();
        FuzzyDateEntity endDate = orgAffiliationRelationEntity.getEndDate();
        affiliation.setStartDate(startDate != null ? new FuzzyDate(startDate.getYear(), startDate.getMonth(), startDate.getDay()) : null);
        affiliation.setEndDate(endDate != null ? new FuzzyDate(endDate.getYear(), endDate.getMonth(), endDate.getDay()) : null);
        affiliation.setVisibility(orgAffiliationRelationEntity.getVisibility());
        affiliation.setDepartmentName(orgAffiliationRelationEntity.getDepartment());
        affiliation.setSource(getSource(orgAffiliationRelationEntity));

        Organization organization = new Organization();
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgAffiliationRelationEntity.getOrg().getOrgDisambiguated();
        if (orgDisambiguatedEntity != null) {
            organization.setDisambiguatedOrganization(getDisambiguatedOrganization(orgDisambiguatedEntity));
        }
        organization.setAddress(getAddress(orgAffiliationRelationEntity.getOrg()));
        organization.setName(orgAffiliationRelationEntity.getOrg().getName());
        affiliation.setOrganization(organization);

        return affiliation;
    }
    
    /**
     * Transforms a profileFundingEntity into a OrcidGrant
     * @param profileFundingEntity
     * @return OrcidGrant
     * */
    public OrcidFunding getOrcidFunding(ProfileFundingEntity profileFundingEntity){
    	OrcidFunding funding = new OrcidFunding();    	
    	if(StringUtils.isNotEmpty(profileFundingEntity.getAmount())) {
    		Amount orcidAmount = new Amount();
    		orcidAmount.setContent(StringUtils.isNotEmpty(profileFundingEntity.getAmount()) ? profileFundingEntity.getAmount() : null );
    		orcidAmount.setCurrencyCode(profileFundingEntity.getCurrencyCode() != null ? profileFundingEntity.getCurrencyCode() : null);    		
    		funding.setAmount(orcidAmount);
    	}    	    	
    	funding.setDescription(StringUtils.isNotEmpty(profileFundingEntity.getDescription()) ? profileFundingEntity.getDescription() : null); 
        FundingTitle title = new FundingTitle();
        title.setTitle(StringUtils.isNotEmpty(profileFundingEntity.getTitle()) ? new Title(profileFundingEntity.getTitle()) : null);
    	if(StringUtils.isNotEmpty(profileFundingEntity.getTranslatedTitle())) {
    		String translatedTitleValue = profileFundingEntity.getTranslatedTitle();
    		String code = profileFundingEntity.getTranslatedTitleLanguageCode();
    		TranslatedTitle translatedTitle = new TranslatedTitle(translatedTitleValue, code);
    		title.setTranslatedTitle(translatedTitle);
    	}
    	funding.setTitle(title);
    	funding.setType(profileFundingEntity.getType() != null ? profileFundingEntity.getType() : null);
    	funding.setUrl(StringUtils.isNotEmpty(profileFundingEntity.getUrl()) ? new Url(profileFundingEntity.getUrl()) : new Url(new String()));
    	funding.setVisibility(profileFundingEntity.getVisibility() != null ? profileFundingEntity.getVisibility() : Visibility.PRIVATE);
    	funding.setPutCode(Long.toString(profileFundingEntity.getId()));    	
    	funding.setFundingContributors(getGrantContributors(profileFundingEntity));
    	funding.setFundingExternalIdentifiers(getGrantExternalIdentifiers(profileFundingEntity));
    	
        // Set organization
    	Organization organization = new Organization();
        OrgDisambiguatedEntity orgDisambiguatedEntity = profileFundingEntity.getOrg().getOrgDisambiguated();
        if (orgDisambiguatedEntity != null) {
            organization.setDisambiguatedOrganization(getDisambiguatedOrganization(orgDisambiguatedEntity));
        }
        organization.setAddress(getAddress(profileFundingEntity.getOrg()));
        organization.setName(profileFundingEntity.getOrg().getName());
        funding.setOrganization(organization);
        
        // Set start and end date
        FuzzyDateEntity startDate = profileFundingEntity.getStartDate();
        FuzzyDateEntity endDate = profileFundingEntity.getEndDate();
        funding.setStartDate(startDate != null ? new FuzzyDate(startDate.getYear(), startDate.getMonth(), startDate.getDay()) : null);
        funding.setEndDate(endDate != null ? new FuzzyDate(endDate.getYear(), endDate.getMonth(), endDate.getDay()) : null);
        
        // Set source
        funding.setSource(getSource(profileFundingEntity));
    	return funding;
    }

    /**
     * Get external identifiers from a profileFundingEntity object
     * @param profileFundingEntity
     * @return The external identifiers in the form of a OrcidGrantExternalIdentifiers object
     * */
    private FundingExternalIdentifiers getGrantExternalIdentifiers(ProfileFundingEntity profileFundingEntity) {
        if (profileFundingEntity == null || profileFundingEntity.getExternalIdentifiers() == null || profileFundingEntity.getExternalIdentifiers().isEmpty()) {
            return null;
        }
        SortedSet<FundingExternalIdentifierEntity> fundingExternalIdentifierEntitys = profileFundingEntity.getExternalIdentifiers();
        FundingExternalIdentifiers fundingExternalIdentifiers = new FundingExternalIdentifiers();        
        
        for (FundingExternalIdentifierEntity fundingExternalIdentifierEntity : fundingExternalIdentifierEntitys) {
            FundingExternalIdentifier fundingExternalIdentifier = getGrantExternalIdentifier(fundingExternalIdentifierEntity);
            if (fundingExternalIdentifier != null) {
                fundingExternalIdentifiers.getFundingExternalIdentifier().add(fundingExternalIdentifier);
            }
        }        
        
        return fundingExternalIdentifiers;
    }
    
    /**
     * Transforms a GrantExternalIdentifierEntity into a GrantExternalIdentifier object
     * @param grantExternalIdentifierEntity
     * @return GrantExternalIdentifier
     * */
    private FundingExternalIdentifier getGrantExternalIdentifier(FundingExternalIdentifierEntity grantExternalIdentifierEntity) {
        if (grantExternalIdentifierEntity == null) {
            return null;
        }
        FundingExternalIdentifier grantExternalIdentifier = new FundingExternalIdentifier();
        
        grantExternalIdentifier.setType(grantExternalIdentifierEntity.getType());
        grantExternalIdentifier.setUrl(new Url(grantExternalIdentifierEntity.getUrl()));
        grantExternalIdentifier.setValue(grantExternalIdentifierEntity.getValue());
        grantExternalIdentifier.setPutCode(String.valueOf(grantExternalIdentifierEntity.getId()));
        
        return grantExternalIdentifier;
    }
    
    /**
     * Get the grant contributors from a profileFundingEntity
     * @param profileFundingEntity
     * @return the contributors in a form of GrantContributors object
     * */
    private FundingContributors getGrantContributors(ProfileFundingEntity profileFundingEntity) {        
    	FundingContributors grantContributors = new FundingContributors();
        // New way of doing work contributors
        String jsonString = profileFundingEntity.getContributorsJson();
        if (jsonString != null) {
        	grantContributors = JsonUtils.readObjectFromJsonString(jsonString, FundingContributors.class);
            for (Contributor contributor : grantContributors.getContributor()) {
                // Make sure contributor credit name has the same visibility as
                // the grant relation
                CreditName creditName = contributor.getCreditName();
                if (creditName != null) {
                    creditName.setVisibility(profileFundingEntity.getVisibility());
                }
                // Strip out any contributor emails
                contributor.setContributorEmail(null);
                // Make sure orcid-id in new format
                ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                if (contributorOrcid != null) {
                    String uri = contributorOrcid.getUri();
                    if (uri == null) {
                        String orcid = contributorOrcid.getValueAsString();
                        if(orcid == null){
                            orcid = contributorOrcid.getPath();
                        }
                        contributor.setContributorOrcid(new ContributorOrcid(getOrcidIdBase(orcid)));
                    }
                }
            }
        }        
        return grantContributors;
    }  
    
    /**
     * Get the source of a sowrceAware object
     * @param sourceAwareEntity
     * 		The entity to obtain the source
     * @return the source of the object
     * */
    private Source getSource(SourceAware sourceAwareEntity) {
        ProfileEntity sourceEntity = sourceAwareEntity.getSource();
        if (sourceEntity == null) {
            return null;
        }
        Source source = new Source(sourceEntity.getId());
        
        //Set the source name
        //If it is a client, lets use the source_name if it is public
        if(OrcidType.CLIENT.equals(sourceEntity.getOrcidType())){
        	Visibility affiliationSourceVisibility = (sourceEntity.getCreditNameVisibility() == null) ? OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility() : sourceEntity.getCreditNameVisibility();  
        	if(Visibility.PUBLIC.equals(affiliationSourceVisibility)) {
        		source.setSourceName(new SourceName(sourceEntity.getCreditName()));
        	}
        } else {
        	//If it is a user, check if it have a credit name and is visible
            if(Visibility.PUBLIC.equals(sourceEntity.getCreditNameVisibility())){
            	source.setSourceName(new SourceName(sourceEntity.getCreditName()));
            } else {
                //If it doesnt, lets use the give name + family name
                String name = sourceEntity.getGivenNames() + (StringUtils.isEmpty(sourceEntity.getFamilyName()) ? "" : " " + sourceEntity.getFamilyName());
                source.setSourceName(new SourceName(name));
            }
        }        
        if(sourceAwareEntity instanceof BaseEntity) {
        	Date createdDate = ((BaseEntity) sourceAwareEntity).getDateCreated();
        	source.setSourceDate(new SourceDate(DateUtils.convertToXMLGregorianCalendar(createdDate)));
        }
        return source;
    }

    private DisambiguatedOrganization getDisambiguatedOrganization(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        DisambiguatedOrganization disambiguatedOrganization = new DisambiguatedOrganization();
        disambiguatedOrganization.setDisambiguatedOrganizationIdentifier(orgDisambiguatedEntity.getSourceId());
        disambiguatedOrganization.setDisambiguationSource(orgDisambiguatedEntity.getSourceType());
        return disambiguatedOrganization;
    }

    private Affiliations getAffiliations(ProfileEntity profileEntity) {
        Set<OrgAffiliationRelationEntity> orgRelationEntities = profileEntity.getOrgAffiliationRelations();
        if (orgRelationEntities != null && !orgRelationEntities.isEmpty()) {
            Affiliations affiliations = new Affiliations();
            List<Affiliation> affiliationList = affiliations.getAffiliation();
            for (OrgAffiliationRelationEntity orgRelationEntity : orgRelationEntities) {
                affiliationList.add(getAffiliation(orgRelationEntity));
            }
            return affiliations;
        }
        return null;
    }   
    
    private Keywords getKeywords(ProfileEntity profileEntity) {
        Set<ProfileKeywordEntity> profileEntityKeywords = profileEntity.getKeywords();
        if (profileEntityKeywords != null && !profileEntityKeywords.isEmpty()) {
            Keywords keywords = new Keywords();
            keywords.setVisibility(profileEntity.getKeywordsVisibility());
            for (ProfileKeywordEntity keywordEntity : profileEntityKeywords) {
                keywords.getKeyword().add(new Keyword(keywordEntity.getKeyword()));
            }
            return keywords;
        }
        return null;
    }

    private Biography getBiography(ProfileEntity profileEntity) {
        String biography = profileEntity.getBiography();
        Visibility shortDescriptionVisibility = profileEntity.getBiographyVisibility();
        return (biography == null && shortDescriptionVisibility == null) ? null : new Biography(biography, shortDescriptionVisibility);
    }

    private ResearcherUrls getResearcherUrls(ProfileEntity profileEntity) {
        Set<ResearcherUrlEntity> researcherUrlEntities = profileEntity.getResearcherUrls();
        if (researcherUrlEntities != null) {
            ResearcherUrls researcherUrls = new ResearcherUrls();
            researcherUrls.setVisibility(profileEntity.getResearcherUrlsVisibility());
            for (ResearcherUrlEntity researcherUrl : researcherUrlEntities) {
                ResearcherUrl url = new ResearcherUrl(new Url(researcherUrl.getUrl()));
                if (!StringUtils.isBlank(researcherUrl.getUrlName()))
                    url.setUrlName(new UrlName(researcherUrl.getUrlName()));
                researcherUrls.getResearcherUrl().add(url);
            }
            return researcherUrls;
        }
        return null;
    }

    private ExternalIdentifiers getExternalIdentifiers(ProfileEntity profileEntity) {
        Set<ExternalIdentifierEntity> externalIdentifierEntities = profileEntity.getExternalIdentifiers();
        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setVisibility(profileEntity.getExternalIdentifiersVisibility());
        if (externalIdentifierEntities != null) {
            for (ExternalIdentifierEntity externalIdentifierEntity : externalIdentifierEntities) {
                ExternalIdentifier externalIdentifier = new ExternalIdentifier();
                ProfileEntity externalIdEntity = externalIdentifierEntity.getExternalIdOrcid();
                externalIdentifier.setExternalIdOrcid(externalIdEntity != null ? new ExternalIdOrcid(getOrcidIdBase(externalIdEntity.getId())) : null);
                externalIdentifier.setExternalIdReference(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdReference()) ? new ExternalIdReference(
                        externalIdentifierEntity.getExternalIdReference()) : null);
                externalIdentifier.setExternalIdCommonName(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdCommonName()) ? new ExternalIdCommonName(
                        externalIdentifierEntity.getExternalIdCommonName()) : null);
                externalIdentifier.setExternalIdUrl(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdUrl()) ? new ExternalIdUrl(externalIdentifierEntity
                        .getExternalIdUrl()) : null);
                externalIdentifiers.getExternalIdentifier().add(externalIdentifier);
            }
        }
        return externalIdentifiers;
    }

    private Delegation getDelegation(ProfileEntity profileEntity) {
        Set<GivenPermissionToEntity> givenPermissionToEntities = profileEntity.getGivenPermissionTo();
        Set<GivenPermissionByEntity> givenPermissionByEntities = profileEntity.getGivenPermissionBy();
        Delegation delegation = null;
        if (givenPermissionToEntities != null && !givenPermissionToEntities.isEmpty()) {
            delegation = new Delegation();
            GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
            delegation.setGivenPermissionTo(givenPermissionTo);
            for (GivenPermissionToEntity givenPermissionToEntity : givenPermissionToEntities) {
                DelegationDetails delegationDetails = new DelegationDetails();
                DelegateSummary delegateSummary = new DelegateSummary(new OrcidIdentifier(getOrcidIdBase(givenPermissionToEntity.getReceiver().getId())));
                String receiverCreditName = givenPermissionToEntity.getReceiver().getCreditName();
                delegateSummary.setCreditName(StringUtils.isNotBlank(receiverCreditName) ? new CreditName(receiverCreditName) : null);
                delegationDetails.setDelegateSummary(delegateSummary);
                delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(givenPermissionToEntity.getApprovalDate())));
                givenPermissionTo.getDelegationDetails().add(delegationDetails);
            }
        }
        if (givenPermissionByEntities != null && !givenPermissionByEntities.isEmpty()) {
            if (delegation == null) {
                delegation = new Delegation();
            }
            GivenPermissionBy givenPermissionBy = new GivenPermissionBy();
            delegation.setGivenPermissionBy(givenPermissionBy);
            for (GivenPermissionByEntity givenPermissionByEntity : givenPermissionByEntities) {
                DelegationDetails delegationDetails = new DelegationDetails();
                DelegateSummary delegateSummary = new DelegateSummary(new OrcidIdentifier(getOrcidIdBase((givenPermissionByEntity.getGiver().getId()))));
                String creditName = givenPermissionByEntity.getGiver().getCreditName();
                delegateSummary.setCreditName(StringUtils.isNotBlank(creditName) ? new CreditName(creditName) : null);
                delegationDetails.setDelegateSummary(delegateSummary);
                delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(givenPermissionByEntity.getApprovalDate())));
                givenPermissionBy.getDelegationDetails().add(delegationDetails);
            }
        }
        return delegation;
    }

    private ContactDetails getContactDetails(ProfileEntity profileEntity) {
        ContactDetails contactDetails = new ContactDetails();
        setEmails(profileEntity, contactDetails);
        setCountry(profileEntity, contactDetails);
        return contactDetails;
    }

    private void setCountry(ProfileEntity profileEntity, ContactDetails contactDetails) {
        Iso3166Country iso2Country = profileEntity.getIso2Country();
        if (iso2Country != null) {
            Address address = new Address();
            Country country = new Country(iso2Country);
            country.setVisibility(profileEntity.getProfileAddressVisibility());
            address.setCountry(country);
            contactDetails.setAddress(address);

        }
    }

    private void setEmails(ProfileEntity profileEntity, ContactDetails contactDetails) {
        // The new way of doing emails.
        Set<EmailEntity> emailEntities = profileEntity.getEmails();
        List<Email> emailList = contactDetails.getEmail();
        if (emailEntities != null) {
            for (EmailEntity emailEntity : emailEntities) {
                Email email = new Email(emailEntity.getId());
                email.setPrimary(emailEntity.getPrimary());
                email.setCurrent(emailEntity.getCurrent());
                email.setVerified(emailEntity.getVerified());
                email.setVisibility(emailEntity.getVisibility());
                ProfileEntity source = emailEntity.getSource();
                if (source != null) {
                    email.setSource(source.getId());
                }
                emailList.add(email);
            }
        }
    }

    private OrganizationAddress getAddress(OrgEntity orgEntity) {
        if (orgEntity != null) {
            String city = orgEntity.getCity();
            String region = orgEntity.getRegion();
            Iso3166Country country = orgEntity.getCountry();
            if (!NullUtils.allNull(city, region, country)) {
                OrganizationAddress address = new OrganizationAddress();
                address.setCity(city);
                address.setRegion(region);
                address.setCountry(country);
                return address;
            }
        }
        return null;
    }

    private OrganizationAddress getAddress(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        if (orgDisambiguatedEntity != null) {
            String city = orgDisambiguatedEntity.getCity();
            String region = orgDisambiguatedEntity.getRegion();
            Iso3166Country country = orgDisambiguatedEntity.getCountry();
            if (!NullUtils.allNull(city, region, country)) {
                OrganizationAddress address = new OrganizationAddress();
                address.setCity(city);
                address.setRegion(region);
                address.setCountry(country);
                return address;
            }
        }
        return null;
    }

    private Source getSponsor(ProfileEntity profileEntity) {
        ProfileEntity sponsorProfileEntity = profileEntity.getSource();
        if (sponsorProfileEntity != null) {
            Source sponsor = new Source();
            SourceName sponsorName = StringUtils.isNotBlank(sponsorProfileEntity.getCreditName()) ? new SourceName(sponsorProfileEntity.getCreditName()) : null;
            SourceOrcid sponsorOrcid = StringUtils.isNotBlank(sponsorProfileEntity.getId()) ? new SourceOrcid(getOrcidIdBase(sponsorProfileEntity.getId())) : null;
            sponsor.setSourceName(sponsorName);
            sponsor.setSourceOrcid(sponsorOrcid);
            return sponsor;
        }
        return null;
    }

    private Applications getApplications(ProfileEntity profileEntity) {
        Set<OrcidOauth2TokenDetail> tokenDetails = profileEntity.getTokenDetails();

        if (tokenDetails != null && !tokenDetails.isEmpty()) {
            // verify tokens don't need scopes removed.
            DefaultPermissionChecker defaultPermissionChecker = (DefaultPermissionChecker) permissionChecker;

            for (OrcidOauth2TokenDetail tokenDetail : tokenDetails)
                defaultPermissionChecker.removeUserGrantWriteScopePastValitity(tokenDetail);
            
            Applications applications = new Applications();
            for (OrcidOauth2TokenDetail tokenDetail : tokenDetails) {
                if (tokenDetail.getTokenDisabled() == null || !tokenDetail.getTokenDisabled()) {
                    ApplicationSummary applicationSummary = new ApplicationSummary();
                    ClientDetailsEntity acceptedClient = tokenDetail.getClientDetailsEntity();

                    ProfileEntity acceptedClientProfileEntity = acceptedClient != null ? acceptedClient.getProfileEntity() : null;
                    if (acceptedClientProfileEntity != null) {
                        applicationSummary.setApplicationOrcid(new ApplicationOrcid(getOrcidIdBase(acceptedClient.getClientId())));
                        applicationSummary.setApplicationName(new ApplicationName(acceptedClientProfileEntity.getCreditName()));
                        SortedSet<ResearcherUrlEntity> researcherUrls = acceptedClient.getProfileEntity().getResearcherUrls();
                        if (researcherUrls != null && !researcherUrls.isEmpty()) {
                            applicationSummary.setApplicationWebsite(new ApplicationWebsite(researcherUrls.first().getUrl()));
                        }
                        applicationSummary.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(tokenDetail.getDateCreated())));

                        // Scopes
                        Set<ScopePathType> scopesGrantedToClient = ScopePathType.getScopesFromSpaceSeparatedString(tokenDetail.getScope());
                        if (scopesGrantedToClient != null && !scopesGrantedToClient.isEmpty()) {
                            ScopePaths scopePaths = new ScopePaths();
                            for (ScopePathType scopesForClient : scopesGrantedToClient) {
                                scopePaths.getScopePath().add(new ScopePath(scopesForClient));
                            }

                            applicationSummary.setScopePaths(scopePaths);
                            // Only add to list if there is a scope (if no
                            // scopes then has been used and is defunct)
                            applications.getApplicationSummary().add(applicationSummary);
                        }
                    }

                }
            }
            return applications;
        }
        return null;
    }

    public OrcidWork getOrcidWork(ProfileWorkEntity profileWorkEntity) {
        WorkEntity work = profileWorkEntity.getWork();
        if (work == null) {
            return null;
        }
        OrcidWork orcidWork = new OrcidWork();
        PublicationDateEntity publicationDate = work.getPublicationDate();
        orcidWork.setPublicationDate(getPublicationDateFromEntity(publicationDate));
        orcidWork.setPutCode(Long.toString(work.getId()));
        orcidWork.setShortDescription(work.getDescription());
        orcidWork.setUrl(StringUtils.isNotBlank(work.getWorkUrl()) ? new Url(work.getWorkUrl()) : null);
        orcidWork.setWorkCitation(getWorkCitation(work));
        orcidWork.setWorkContributors(getWorkContributors(profileWorkEntity));
        orcidWork.setWorkExternalIdentifiers(getWorkExternalIdentifiers(work));
        orcidWork.setWorkSource(getWorkSource(profileWorkEntity));
        orcidWork.setWorkTitle(getWorkTitle(work));
        orcidWork.setJournalTitle(StringUtils.isNotBlank(work.getJournalTitle()) ? new Title(work.getJournalTitle()) : null);
        orcidWork.setLanguageCode(StringUtils.isNotBlank(work.getLanguageCode()) ? work.getLanguageCode() : null);

        if (work.getIso2Country() != null) {
            Country country = new Country(work.getIso2Country());
            country.setVisibility(OrcidVisibilityDefaults.WORKS_COUNTRY_DEFAULT.getVisibility());
            orcidWork.setCountry(country);
        }
        orcidWork.setWorkType(work.getWorkType());
        orcidWork.setVisibility(profileWorkEntity.getVisibility());
        return orcidWork;
    }

    private Citation getWorkCitation(WorkEntity work) {
        if (StringUtils.isNotBlank(work.getCitation()) && work.getCitationType() != null) {
            return new Citation(work.getCitation(), work.getCitationType());
        }
        return null;
    }

    private WorkTitle getWorkTitle(WorkEntity work) {
        if (work == null || StringUtils.isBlank(work.getTitle())) {
            return null;
        }
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(StringUtils.isNotBlank(work.getTitle()) ? new Title(work.getTitle()) : null);
        workTitle.setSubtitle(StringUtils.isNotBlank(work.getSubtitle()) ? new Subtitle(work.getSubtitle()) : null);
        if (work.getTranslatedTitle() != null)
            workTitle.setTranslatedTitle(new TranslatedTitle(work.getTranslatedTitle(), work.getTranslatedTitleLanguageCode()));
        return workTitle;
    }

    private WorkSource getWorkSource(ProfileWorkEntity profileWorkEntity) {                
        if (profileWorkEntity == null || profileWorkEntity.getSourceProfile() == null) {
            return null;
        }
        ProfileEntity sourceProfile = profileWorkEntity.getSourceProfile();
        
        WorkSource workSource = new WorkSource(getOrcidIdBase(sourceProfile.getId()));
        
        //Set the source name
        //If it is a client, lets use the source_name if it is public
        if(sourceProfile.getOrcidType() != null && sourceProfile.getOrcidType().equals(OrcidType.CLIENT)) {
            Visibility workSourceVisibility = (sourceProfile.getCreditNameVisibility() == null) ? OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility() : sourceProfile.getCreditNameVisibility();            
            if(Visibility.PUBLIC.equals(workSourceVisibility)) {
                workSource.setSourceName(sourceProfile.getCreditName());
            }
        } else {
            //If it is a user, check if it have a credit name and is visible
            if(!StringUtils.isEmpty(sourceProfile.getCreditName()) && Visibility.PUBLIC.equals(sourceProfile.getCreditNameVisibility())){
                workSource.setSourceName(sourceProfile.getCreditName());
            } else {
                //If it doesnt, lets use the give name + family name
                String name = sourceProfile.getGivenNames() + (StringUtils.isEmpty(sourceProfile.getFamilyName()) ? "" : " " + sourceProfile.getFamilyName());
                workSource.setSourceName(name);
            }
        }
                            
        return workSource;
    }

    private SourceDate getSourceDate(Date depositedDate) {
        if (depositedDate == null) {
            return null;
        }
        return new SourceDate(toXMLGregorianCalendar(depositedDate));
    }

    private WorkExternalIdentifiers getWorkExternalIdentifiers(WorkEntity work) {
        if (work == null || work.getExternalIdentifiers() == null || work.getExternalIdentifiers().isEmpty()) {
            return null;
        }
        Set<WorkExternalIdentifierEntity> workExternalIdentifierEntities = work.getExternalIdentifiers();
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        for (WorkExternalIdentifierEntity workExternalIdentifierEntity : workExternalIdentifierEntities) {
            WorkExternalIdentifier workExternalIdentifier = getWorkExternalIdentifier(workExternalIdentifierEntity);
            if (workExternalIdentifier != null) {
                workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
            }
        }
        return workExternalIdentifiers;
    }

    private WorkExternalIdentifier getWorkExternalIdentifier(WorkExternalIdentifierEntity workExternalIdentifierEntity) {
        if (workExternalIdentifierEntity == null) {
            return null;
        }
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifier.setWorkExternalIdentifierType(workExternalIdentifierEntity.getIdentifierType() != null ? workExternalIdentifierEntity.getIdentifierType()
                : null);
        workExternalIdentifier.setWorkExternalIdentifierId(StringUtils.isNotBlank(workExternalIdentifierEntity.getIdentifier()) ? new WorkExternalIdentifierId(
                workExternalIdentifierEntity.getIdentifier()) : null);
        return workExternalIdentifier;
    }

    private WorkContributors getWorkContributors(ProfileWorkEntity profileWorkEntity) {
        WorkEntity work = profileWorkEntity.getWork();
        if (work == null) {
            return null;
        }
        WorkContributors workContributors = new WorkContributors();
        // New way of doing work contributors
        String jsonString = work.getContributorsJson();
        if (jsonString != null) {
            workContributors = JsonUtils.readObjectFromJsonString(jsonString, WorkContributors.class);
            for (Contributor contributor : workContributors.getContributor()) {
                // Make sure contributor credit name has the same visibility as
                // the work
                CreditName creditName = contributor.getCreditName();
                if (creditName != null) {
                    creditName.setVisibility(profileWorkEntity.getVisibility());
                }
                // Strip out any contributor emails
                contributor.setContributorEmail(null);
                // Make sure orcid-id in new format
                ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                if (contributorOrcid != null) {
                    String uri = contributorOrcid.getUri();
                    if (uri == null) {
                        String orcid = contributorOrcid.getValueAsString();
                        if(orcid == null){
                            orcid = contributorOrcid.getPath();
                        }
                        contributor.setContributorOrcid(new ContributorOrcid(getOrcidIdBase(orcid)));
                    }
                }
            }
        }        
        return workContributors;
    }    

    private PublicationDate getPublicationDateFromEntity(PublicationDateEntity fuzzyDate) {
        if (fuzzyDate == null) {
            return null;
        }
        Year year = fuzzyDate.getYear() != null ? new Year(fuzzyDate.getYear()) : null;
        Month month = fuzzyDate.getMonth() != null ? new Month(fuzzyDate.getMonth()) : null;
        Day day = fuzzyDate.getDay() != null ? new Day(fuzzyDate.getDay()) : null;

        return new PublicationDate(year, month, day);
    }

    private OtherNames getOtherNames(ProfileEntity profile) {
        OtherNames otherNames = new OtherNames();
        otherNames.setVisibility(profile.getOtherNamesVisibility());
        Set<OtherNameEntity> otherNamesEntitiy = profile.getOtherNames();
        if (otherNamesEntitiy != null && otherNamesEntitiy.size() > 0) {
            for (OtherNameEntity otherNameEntity : otherNamesEntitiy) {
                otherNames.addOtherName(otherNameEntity.getDisplayName());
            }
        }
        return otherNames;
    }

    private GivenNames getGivenNames(String givenNames) {
        if (StringUtils.isNotBlank(givenNames)) {
            GivenNames names = new GivenNames();
            names.setContent(givenNames);
            return names;
        }
        return null;
    }

    private FamilyName getFamilyName(String familyName) {
        if (StringUtils.isNotBlank(familyName)) {
            FamilyName name = new FamilyName();
            name.setContent(familyName);
            return name;
        }
        return null;
    }

    private CreditName getCreditName(ProfileEntity profileEntity) {
        String creditName = profileEntity.getCreditName();
        if (StringUtils.isNotBlank(creditName)) {
            CreditName name = new CreditName();
            name.setContent(creditName);
            name.setVisibility(profileEntity.getCreditNameVisibility());
            return name;
        }
        return null;
    }

    private OrcidPreferences getOrcidPreferences(ProfileEntity profileEntity) {
        OrcidPreferences orcidPreferences = new OrcidPreferences();
        if (profileEntity.getLocale() == null)
            orcidPreferences.setLocale(Locale.EN);
        else
            orcidPreferences.setLocale(profileEntity.getLocale());
        return orcidPreferences;
    }

    private OrcidInternal getOrcidInternal(ProfileEntity profileEntity) {
        OrcidInternal orcidInternal = new OrcidInternal();

        SecurityDetails securityDetails = new SecurityDetails();
        orcidInternal.setSecurityDetails(securityDetails);
        securityDetails.setEncryptedPassword(profileEntity.getEncryptedPassword() != null ? new EncryptedPassword(profileEntity.getEncryptedPassword()) : null);
        securityDetails.setSecurityQuestionId(profileEntity.getSecurityQuestion() == null ? null : new SecurityQuestionId(profileEntity.getSecurityQuestion().getId()));
        securityDetails.setEncryptedSecurityAnswer(profileEntity.getEncryptedSecurityAnswer() != null ? new EncryptedSecurityAnswer(profileEntity
                .getEncryptedSecurityAnswer()) : null);
        securityDetails.setEncryptedVerificationCode(profileEntity.getEncryptedVerificationCode() != null ? new EncryptedVerificationCode(profileEntity
                .getEncryptedVerificationCode()) : null);

        Preferences preferences = new Preferences();
        orcidInternal.setPreferences(preferences);
        preferences.setSendChangeNotifications(profileEntity.getSendChangeNotifications() == null ? null : new SendChangeNotifications(profileEntity
                .getSendChangeNotifications()));
        preferences.setSendOrcidNews(profileEntity.getSendOrcidNews() == null ? null : new SendOrcidNews(profileEntity.getSendOrcidNews()));
        // This column is constrained as not null in the DB so don't have to
        // worry about null!
        preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(profileEntity.getWorkVisibilityDefault()));

        return orcidInternal;
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            return datatypeFactory.newXMLGregorianCalendar(c);
        } else {
            return null;
        }
    }

}
