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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.impl.jsonidentifiers.FundingExternalIdentifiers;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.PermissionChecker;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreatedDate;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.DeprecatedDate;
import org.orcid.jaxb.model.message.DeveloperToolsEnabled;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EncryptedPassword;
import org.orcid.jaxb.model.message.EncryptedSecurityAnswer;
import org.orcid.jaxb.model.message.EncryptedVerificationCode;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdUrl;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributor;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GivenPermissionBy;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdBase;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OrganizationDefinedFundingSubType;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.PrimaryRecord;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.ReferredBy;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SalesforceId;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SendAdministrativeChangeNotifications;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceDate;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.VerifiedEmail;
import org.orcid.jaxb.model.message.VerifiedPrimaryEmail;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.Year;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.FuzzyDateEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Jpa2JaxbAdapterImpl.class);

    @Value("${org.orcid.core.baseUri:http://orcid.org}")
    private String baseUri = null;

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;    
    
    @Resource(name = "workEntityCacheManager")
    private WorkEntityCacheManager workEntityCacheManager;
    
    public void setWorkEntityCacheManager(WorkEntityCacheManager workEntityCacheManager) {
        this.workEntityCacheManager = workEntityCacheManager;
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
        profile.setGroupType(profileEntity.getGroupType());
        profile.setVerificationCode(profileEntity.getEncryptedVerificationCode());
        profile.setLocked(profileEntity.getRecordLocked());
        profile.setReviewed(profileEntity.isReviewed());

        Date lastModified = profileEntity.getLastModified() == null? new Date() : profileEntity.getLastModified();
        
        int countTokens = orcidOauth2TokenService.findCountByUserName(profileEntity.getId(), lastModified.getTime());
        profile.setCountTokens(countTokens);
        return profile;
    }

    @Override
    public OrcidClient toOrcidClient(ClientDetailsEntity clientDetailsEntity) {
        OrcidClient client = new OrcidClient();
        client.setClientId(clientDetailsEntity.getId());
        client.setType(clientDetailsEntity.getClientType());
        if (clientDetailsEntity != null) {
            client.setClientSecret(clientDetailsEntity.getClientSecretForJpa());
            client.setDisplayName(clientDetailsEntity.getClientName());
            client.setShortDescription(clientDetailsEntity.getClientDescription());
            client.setWebsite(clientDetailsEntity.getClientWebsite());
            client.setPersistentTokenEnabled(clientDetailsEntity.isPersistentTokensEnabled());
            client.setIdp(clientDetailsEntity.getAuthenticationProviderId());
            client.setAllowAutoDeprecate(clientDetailsEntity.getAllowAutoDeprecate());
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
                redirectUri.setActType(redirectUriEntity.getUriActType());
                redirectUri.setGeoArea(redirectUriEntity.getUriGeoArea());
                redirectUris.getRedirectUri().add(redirectUri);
            }
        }
        return client;
    }

    @Override
    public OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity) {
        OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupOrcid(profileEntity.getId());
        if(profileEntity.getRecordNameEntity() != null) {
            group.setGroupName(profileEntity.getRecordNameEntity().getCreditName());
        } 
        
        group.setType(profileEntity.getGroupType());
        Set<EmailEntity> emailEntities = profileEntity.getEmails();
        for (EmailEntity emailEntity : emailEntities) {
            group.setEmail(emailEntity.getId());
        }
        for (ClientDetailsEntity clientDetailsEntity : profileEntity.getClients()) {
            OrcidClient client = toOrcidClient(clientDetailsEntity);
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

        boolean verfiedEmail = false;
        boolean verfiedPrimaryEmail = false;
        if (profileEntity.getEmails() != null) {
            for (EmailEntity emailEntity : profileEntity.getEmails()) {
                if (emailEntity != null && emailEntity.getVerified()) {
                    verfiedEmail = true;
                    if (emailEntity.getPrimary()) {
                        verfiedPrimaryEmail = true;
                        break;
                    }
                }
            }
        }
        history.setVerifiedEmail(new VerifiedEmail(verfiedEmail));
        history.setVerifiedPrimaryEmail(new VerifiedPrimaryEmail(verfiedPrimaryEmail));

        return history;
    }

    private OrcidDeprecated getOrcidDeprecated(ProfileEntity profileEntity) {
        OrcidDeprecated orcidDeprecated = null;
        if (profileEntity.getPrimaryRecord() != null) {
            orcidDeprecated = new OrcidDeprecated();
            orcidDeprecated.setDate(new DeprecatedDate(toXMLGregorianCalendar(profileEntity.getDeprecatedDate())));
            PrimaryRecord primaryRecord = new PrimaryRecord();
            OrcidIdentifier orcidIdentifier = new OrcidIdentifier(getOrcidIdBase(profileEntity.getPrimaryRecord().getId()));
            primaryRecord.setOrcidIdentifier(orcidIdentifier);
            orcidDeprecated.setPrimaryRecord(primaryRecord);
        }
        return orcidDeprecated;
    }

    @Override
    public OrcidIdBase getOrcidIdBase(String id) {
        OrcidIdBase orcidId = new OrcidIdBase();
        String correctedBaseUri = baseUri.replace("https", "http");
        try {
            URI uri = new URI(correctedBaseUri);
            orcidId.setHost(uri.getHost());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error parsing base uri", e);
        }
        if (OrcidStringUtils.isClientId(id)) {
            correctedBaseUri += "/client";
        }
        orcidId.setUri(correctedBaseUri + "/" + id);
        orcidId.setPath(id);
        return orcidId;
    }

    private OrcidActivities getOrcidActivities(ProfileEntity profileEntity) {
        Affiliations affiliations = getAffiliations(profileEntity);
        FundingList fundings = getFundingList(profileEntity);
        OrcidWorks orcidWorks = getOrcidWorks(profileEntity);
        if (NullUtils.allNull(fundings, orcidWorks, affiliations)) {
            return null;
        }
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidActivities.setFundings(fundings);
        orcidActivities.setOrcidWorks(orcidWorks);
        orcidActivities.setAffiliations(affiliations);
        return orcidActivities;
    }

    private FundingList getFundingList(ProfileEntity profileEntity) {
        LOGGER.debug("About to convert fundings from entity: " + profileEntity.getId());
        Set<ProfileFundingEntity> profileFundings = profileEntity.getProfileFunding();
        if (profileFundings != null && !profileFundings.isEmpty()) {
            FundingList fundingList = new FundingList();
            List<Funding> fundings = fundingList.getFundings();
            for (ProfileFundingEntity profileFundingEntity : profileFundings) {
                fundings.add(getFunding(profileFundingEntity));
            }

            return fundingList;
        }
        return null;
    }

    private OrcidWorks getOrcidWorks(ProfileEntity profileEntity) {
        String orcid = profileEntity.getId();
        LOGGER.debug("About to convert works from entity: " + orcid);
        Date lastModified = profileEntity.getLastModified();
        List<WorkEntity> works = workEntityCacheManager.retrieveFullWorks(orcid, lastModified != null ? lastModified.getTime() : 0);
        if (works != null && !works.isEmpty()) {
            List<OrcidWork> unsorted = new ArrayList<>();
            for (WorkEntity workEntity : works) {
                OrcidWork orcidWork = getOrcidWork(workEntity);
                orcidWork.setVisibility(workEntity.getVisibility());
                unsorted.add(orcidWork);
            }
            OrcidWorks orcidWorks = new OrcidWorks();
            orcidWorks.setOrcidWork(sortWorks(unsorted));
            return orcidWorks;
        }
        return null;
    }

    public List<OrcidWork> sortWorks(List<OrcidWork> unsorted) {
        return unsorted.stream().sorted(workDisplayIndexComparator().thenComparing(workPubDateComparator()).thenComparing(workTitleComparator()))
                .collect(Collectors.toList());
    }
    
    public Comparator<OrcidWork> workDisplayIndexComparator() {
        return (work1, work2) -> {
            Long displayIndex1 = work1.getDisplayIndex();
            Long displayIndex2 = work2.getDisplayIndex();
            if (displayIndex1 != null && displayIndex2 != null) {
                return -displayIndex1.compareTo(displayIndex2);
            } else {
                return NullUtils.compareNulls(displayIndex1, displayIndex2);
            }
        };
    }

    public Comparator<OrcidWork> workPubDateComparator() {
        return (work1, work2) -> {
            PublicationDate pubDate1 = work1.getPublicationDate();
            PublicationDate pubDate2 = work2.getPublicationDate();
            if (pubDate1 != null && pubDate2 != null) {
                @SuppressWarnings("deprecation")
                String dateString1 = PojoUtil.createDateSortString(null, pubDate1);
                @SuppressWarnings("deprecation")
                String dateString2 = PojoUtil.createDateSortString(null, pubDate2);
                return -dateString1.compareTo(dateString2);
            } else {
                return NullUtils.compareNulls(pubDate1, pubDate2);
            }
        };
    }

    public Comparator<OrcidWork> workTitleComparator() {
        return (work1, work2) -> {
            WorkTitle title1 = work1.getWorkTitle();
            WorkTitle title2 = work2.getWorkTitle();
            if (title1 != null && title2 != null) {
                return title1.getTitle().getContent().compareTo(title2.getTitle().getContent());
            } else {
                return NullUtils.compareNulls(title1, title2);
            }
        };
    }

    private OrcidBio getOrcidBio(ProfileEntity profileEntity) {
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setContactDetails(getContactDetails(profileEntity));
        orcidBio.setExternalIdentifiers(getExternalIdentifiers(profileEntity));
        orcidBio.setDelegation(getDelegation(profileEntity));
        orcidBio.setPersonalDetails(getPersonalDetails(profileEntity));
        orcidBio.setKeywords(getKeywords(profileEntity));
        orcidBio.setBiography(getBiography(profileEntity));
        orcidBio.setResearcherUrls(getResearcherUrls(profileEntity));
        return orcidBio;
    }

    private PersonalDetails getPersonalDetails(ProfileEntity profileEntity) {
        PersonalDetails personalDetails = new PersonalDetails();   
        personalDetails.setGivenNames(getGivenNames(profileEntity));
        personalDetails.setFamilyName(getFamilyName(profileEntity));
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

        affiliation.setCreatedDate(new CreatedDate(toXMLGregorianCalendar(orgAffiliationRelationEntity.getDateCreated())));
        affiliation.setLastModifiedDate(new LastModifiedDate(toXMLGregorianCalendar(orgAffiliationRelationEntity.getLastModified())));

        return affiliation;
    }

    /**
     * Transforms a profileFundingEntity into a Funding
     * 
     * @param profileFundingEntity
     * @return Funding
     * */
    public Funding getFunding(ProfileFundingEntity profileFundingEntity) {
        Funding funding = new Funding();

        if (profileFundingEntity.getNumericAmount() != null) {
            String stringAmount = profileFundingEntity.getNumericAmount().toString();
            Amount orcidAmount = new Amount();
            orcidAmount.setContent(stringAmount);
            orcidAmount.setCurrencyCode(profileFundingEntity.getCurrencyCode() != null ? profileFundingEntity.getCurrencyCode() : null);
            funding.setAmount(orcidAmount);
        }

        funding.setDescription(StringUtils.isNotEmpty(profileFundingEntity.getDescription()) ? profileFundingEntity.getDescription() : null);
        FundingTitle title = new FundingTitle();
        title.setTitle(StringUtils.isNotEmpty(profileFundingEntity.getTitle()) ? new Title(profileFundingEntity.getTitle()) : null);
        if (StringUtils.isNotEmpty(profileFundingEntity.getTranslatedTitle())) {
            String translatedTitleValue = profileFundingEntity.getTranslatedTitle();
            String code = profileFundingEntity.getTranslatedTitleLanguageCode();
            TranslatedTitle translatedTitle = new TranslatedTitle(translatedTitleValue, code);
            title.setTranslatedTitle(translatedTitle);
        }
        funding.setTitle(title);
        funding.setType(profileFundingEntity.getType() != null ? profileFundingEntity.getType() : null);
        funding.setOrganizationDefinedFundingType(profileFundingEntity.getOrganizationDefinedType() != null ? new OrganizationDefinedFundingSubType(profileFundingEntity
                .getOrganizationDefinedType()) : null);
        funding.setUrl(StringUtils.isNotEmpty(profileFundingEntity.getUrl()) ? new Url(profileFundingEntity.getUrl()) : new Url(new String()));
        funding.setVisibility(profileFundingEntity.getVisibility() != null ? profileFundingEntity.getVisibility() : Visibility.PRIVATE);
        funding.setPutCode(Long.toString(profileFundingEntity.getId()));
        funding.setFundingContributors(getFundingContributors(profileFundingEntity));
        
        if (profileFundingEntity.getExternalIdentifiersJson() != null){
            FundingExternalIdentifiers ids = FundingExternalIdentifiers.fromDBJSONString(profileFundingEntity.getExternalIdentifiersJson());
            funding.setFundingExternalIdentifiers(ids.toMessagePojo());
        }

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

        funding.setCreatedDate(new CreatedDate(toXMLGregorianCalendar(profileFundingEntity.getDateCreated())));
        funding.setLastModifiedDate(new LastModifiedDate(toXMLGregorianCalendar(profileFundingEntity.getLastModified())));

        return funding;
    }


    /**
     * Get the funding contributors from a profileFundingEntity
     * 
     * @param profileFundingEntity
     * @return the contributors in a form of FundingContributors object
     * */
    private FundingContributors getFundingContributors(ProfileFundingEntity profileFundingEntity) {
        FundingContributors fundingContributors = new FundingContributors();
        // New way of doing work contributors
        String jsonString = profileFundingEntity.getContributorsJson();
        if (jsonString != null) {
            fundingContributors = JsonUtils.readObjectFromJsonString(jsonString, FundingContributors.class);
            for (FundingContributor contributor : fundingContributors.getContributor()) {
                // Make sure contributor credit name has the same visibility as
                // the funding relation
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
                        if (orcid == null) {
                            orcid = contributorOrcid.getPath();
                        }
                        contributor.setContributorOrcid(new ContributorOrcid(getOrcidIdBase(orcid)));
                    }
                }
            }
        }
        return fundingContributors;
    }    

    public DisambiguatedOrganization getDisambiguatedOrganization(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        DisambiguatedOrganization disambiguatedOrganization = new DisambiguatedOrganization();
        disambiguatedOrganization.setDisambiguatedOrganizationIdentifier(orgDisambiguatedEntity.getSourceId());
        disambiguatedOrganization.setDisambiguationSource(orgDisambiguatedEntity.getSourceType());
        disambiguatedOrganization.setId(orgDisambiguatedEntity.getId());
        return disambiguatedOrganization;
    }

    private Affiliations getAffiliations(ProfileEntity profileEntity) {
        LOGGER.debug("About to convert affiliations from entity: " + profileEntity.getId());
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
            Visibility mostRestrictive = Visibility.PUBLIC;
            for (ProfileKeywordEntity keywordEntity : profileEntityKeywords) {
                
                //will only be null if there's an issue with the data or you're using this layer directly
                Visibility vis = (keywordEntity.getVisibility() != null)?Visibility.fromValue(keywordEntity.getVisibility().value()):Visibility.PRIVATE;                
                if (vis.isMoreRestrictiveThan(mostRestrictive))
                    mostRestrictive = vis;
                
                Keyword keyword = new Keyword(keywordEntity.getKeywordName(), vis);
                if(!PojoUtil.isEmpty(keywordEntity.getElementSourceId())) {
                    Source source = getSource(keywordEntity);
                    keyword.setSource(source);
                }
                keywords.getKeyword().add(keyword);
            }
            keywords.setVisibility(mostRestrictive);
            return keywords;
        }
        return null;
    }

    private Biography getBiography(ProfileEntity profileEntity) {
        String biography = null;
        Visibility biographyVisibility = null;
        
        if(profileEntity.getBiographyEntity() != null) {
            if(!PojoUtil.isEmpty(profileEntity.getBiographyEntity().getBiography())) {
                biography = profileEntity.getBiographyEntity().getBiography();
            }      
            if(profileEntity.getBiographyEntity().getVisibility() != null) {
                biographyVisibility = Visibility.fromValue(profileEntity.getBiographyEntity().getVisibility().value());
            }
        }
        
        return (biography == null && biographyVisibility == null) ? null : new Biography(biography, biographyVisibility);
    }

    private ResearcherUrls getResearcherUrls(ProfileEntity profileEntity) {
        Set<ResearcherUrlEntity> researcherUrlEntities = profileEntity.getResearcherUrls();
        if (researcherUrlEntities != null) {
            ResearcherUrls researcherUrls = new ResearcherUrls();
            Visibility mostRestrictive = Visibility.PUBLIC;
            for (ResearcherUrlEntity researcherUrl : researcherUrlEntities) {
                
                //will only be null if there's an issue with the data or you're using this layer directly
                Visibility vis = (researcherUrl.getVisibility() != null)?Visibility.fromValue(researcherUrl.getVisibility().value()):Visibility.PRIVATE;                
                if (vis.isMoreRestrictiveThan(mostRestrictive))
                    mostRestrictive = vis;

                ResearcherUrl url = new ResearcherUrl(new Url(researcherUrl.getUrl()),vis);
                if (!StringUtils.isBlank(researcherUrl.getUrlName()))
                    url.setUrlName(new UrlName(researcherUrl.getUrlName()));
                
                if(!PojoUtil.isEmpty(researcherUrl.getElementSourceId())) {
                    Source source = getSource(researcherUrl);
                    url.setSource(source);
                }
                researcherUrls.setVisibility(mostRestrictive);
                researcherUrls.getResearcherUrl().add(url);
            }
            return researcherUrls;
        }
        return null;
    }

    private ExternalIdentifiers getExternalIdentifiers(ProfileEntity profileEntity) {
        Set<ExternalIdentifierEntity> externalIdentifierEntities = profileEntity.getExternalIdentifiers();
        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        Visibility mostRestrictive = Visibility.PUBLIC;
        if (externalIdentifierEntities != null) {
            for (ExternalIdentifierEntity externalIdentifierEntity : externalIdentifierEntities) {
                
                //will only be null if there's an issue with the data or you're using this layer directly
                Visibility vis = (externalIdentifierEntity.getVisibility() != null)?Visibility.fromValue(externalIdentifierEntity.getVisibility().value()):Visibility.PRIVATE;
                if (vis.isMoreRestrictiveThan(mostRestrictive))
                    mostRestrictive = vis;

                ExternalIdentifier externalIdentifier = new ExternalIdentifier(vis);
                
                externalIdentifier.setSource(getSource(externalIdentifierEntity));
                
                externalIdentifier.setExternalIdReference(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdReference()) ? new ExternalIdReference(
                        externalIdentifierEntity.getExternalIdReference()) : null);
                externalIdentifier.setExternalIdCommonName(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdCommonName()) ? new ExternalIdCommonName(
                        externalIdentifierEntity.getExternalIdCommonName()) : null);
                externalIdentifier.setExternalIdUrl(StringUtils.isNotBlank(externalIdentifierEntity.getExternalIdUrl()) ? new ExternalIdUrl(externalIdentifierEntity
                        .getExternalIdUrl()) : null);
                
                externalIdentifiers.getExternalIdentifier().add(externalIdentifier);
            }
        }
        externalIdentifiers.setVisibility(mostRestrictive);
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
                delegateSummary
                        .setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(givenPermissionToEntity.getReceiver().getLastModified())));
                String receiverCreditName = givenPermissionToEntity.getReceiver().getDisplayName();
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
                delegateSummary.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(givenPermissionByEntity.getGiver().getLastModified())));
                String creditName = givenPermissionByEntity.getGiver().getDisplayName();
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
        if(profileEntity.getAddresses() != null && !profileEntity.getAddresses().isEmpty()) {
            //The primary will be the one with the biggest display index
            AddressEntity primary = null;
            for(AddressEntity address : profileEntity.getAddresses()) {
                if(primary == null || primary.getDisplayIndex() < address.getDisplayIndex()) {
                    primary = address;
                }                                                
            }
            
            Source source = getSource(primary);
            
            Address address = new Address();
            Country country = new Country(Iso3166Country.fromValue(primary.getIso2Country().value()));
            country.setSource(source);
            country.setVisibility(Visibility.fromValue(primary.getVisibility().value()));
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
                email.setSource(emailEntity.getSourceId());
                email.setSourceClientId(emailEntity.getClientSourceId());
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

    private Source getSponsor(ProfileEntity profileEntity) {
        SourceEntity sourceEntity = profileEntity.getSource();
        if (sourceEntity != null) {
            Source sponsor = new Source();
            SourceName sponsorName = new SourceName(sourceEntity.getSourceName());
            sponsor.setSourceName(sponsorName);
            ClientDetailsEntity sourceClient = sourceEntity.getSourceClient();
            if (sourceClient != null && !OrcidStringUtils.isValidOrcid(sourceClient.getClientId())) {
                SourceClientId sourceClientId = new SourceClientId(getOrcidIdBase(sourceClient.getId()));
                sponsor.setSourceClientId(sourceClientId);
            } else {
                SourceOrcid sponsorOrcid = StringUtils.isNotBlank(sourceEntity.getSourceId()) ? new SourceOrcid(getOrcidIdBase(sourceEntity.getSourceId())) : null;
                sponsor.setSourceOrcid(sponsorOrcid);
            }
            return sponsor;
        }
        return null;
    }

    public OrcidWork getOrcidWork(WorkEntity work) {
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
        orcidWork.setWorkContributors(getWorkContributors(work));
        if (work.getExternalIdentifiersJson() != null){
            org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIdentifiers extIds = org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIdentifiers.fromDBJSONString(work.getExternalIdentifiersJson());        
            orcidWork.setWorkExternalIdentifiers(extIds.toMessagePojo());            
        }
        orcidWork.setSource(getSource(work));
        orcidWork.setWorkTitle(getWorkTitle(work));
        orcidWork.setJournalTitle(StringUtils.isNotBlank(work.getJournalTitle()) ? new Title(work.getJournalTitle()) : null);
        orcidWork.setLanguageCode(normalizeLanguageCode(work.getLanguageCode()));

        if (work.getIso2Country() != null) {
            Country country = new Country(work.getIso2Country());
            country.setVisibility(OrcidVisibilityDefaults.WORKS_COUNTRY_DEFAULT.getVisibility());
            orcidWork.setCountry(country);
        }
        orcidWork.setWorkType(work.getWorkType());
        orcidWork.setVisibility(work.getVisibility());

        orcidWork.setCreatedDate(new CreatedDate(toXMLGregorianCalendar(work.getDateCreated())));
        orcidWork.setLastModifiedDate(new LastModifiedDate(toXMLGregorianCalendar(work.getLastModified())));
        orcidWork.setDisplayIndex(work.getDisplayIndex());

        return orcidWork;
    }

    /*
     * converts locale codes to only return language code, with the exception of
     * zh_CN and zh_TW
     */
    static public String normalizeLanguageCode(String code) {
        if (code == null || code.length() < 2)
            return null;
        java.util.Locale locale = new java.util.Locale(code);
        String localeString = locale.toString();
        if (localeString.startsWith("zh")) {
            if (localeString.startsWith("zh_CN") || localeString.startsWith("zh_TW")) {
                return localeString.substring(0, 5);
            } else if (localeString.startsWith("zh_cn")) {
                return "zh_CN";
            } else if (localeString.startsWith("zh_tw")) {
                return "zh_TW";
            } else {
                return "zh_CN"; // bit of a gamble here :-/
            }
        }
        return localeString.substring(0, 2);
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

    private WorkContributors getWorkContributors(WorkEntity work) {
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
                    creditName.setVisibility(work.getVisibility());
                }
                // Strip out any contributor emails
                contributor.setContributorEmail(null);
                // Make sure orcid-id in new format
                ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                if (contributorOrcid != null) {
                    String uri = contributorOrcid.getUri();
                    if (uri == null) {
                        String orcid = contributorOrcid.getValueAsString();
                        if (orcid == null) {
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
        Visibility mostRestrictive = Visibility.PUBLIC;
        Set<OtherNameEntity> otherNamesEntitiy = profile.getOtherNames();
        if (otherNamesEntitiy != null && otherNamesEntitiy.size() > 0) {
            for (OtherNameEntity otherNameEntity : otherNamesEntitiy) {
                
                //will only be null if there's an issue with the data or you're using this layer directly
                Visibility vis = (otherNameEntity.getVisibility() != null)?Visibility.fromValue(otherNameEntity.getVisibility().value()):Visibility.PRIVATE;                
                if (vis.isMoreRestrictiveThan(mostRestrictive))
                    mostRestrictive = vis;
                
                OtherName otherName = new OtherName(otherNameEntity.getDisplayName(), vis);
                if(!PojoUtil.isEmpty(otherNameEntity.getElementSourceId())) {
                    Source source = getSource(otherNameEntity);
                    otherName.setSource(source);
                }
                otherNames.getOtherName().add(otherName);
            }
        }
        otherNames.setVisibility(mostRestrictive);
        return otherNames;
    }

    private GivenNames getGivenNames(ProfileEntity profileEntity) {
        RecordNameEntity recordName = profileEntity.getRecordNameEntity();
        if(recordName != null) {
            if (StringUtils.isNotBlank(recordName.getGivenNames())) {
                GivenNames names = new GivenNames();
                names.setContent(recordName.getGivenNames());
                names.setVisibility(recordName.getVisibility() == null ? OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility() : Visibility.fromValue(recordName.getVisibility().value()));
                return names;
            }
        }        
        return null;
    }

    private FamilyName getFamilyName(ProfileEntity profileEntity) {
        RecordNameEntity recordName = profileEntity.getRecordNameEntity();
        if(recordName != null) {
            if (StringUtils.isNotBlank(recordName.getFamilyName())) {
                FamilyName name = new FamilyName();
                name.setContent(recordName.getFamilyName());
                name.setVisibility(recordName.getVisibility() == null ? OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility() : Visibility.fromValue(recordName.getVisibility().value()));
                return name;
            }
        }        
        return null;
    }

    private CreditName getCreditName(ProfileEntity profileEntity) {
        RecordNameEntity recordName = profileEntity.getRecordNameEntity();
        if(recordName != null) {
            if (StringUtils.isNotBlank(recordName.getCreditName())) {
                CreditName name = new CreditName();
                name.setContent(recordName.getCreditName());
                name.setVisibility(recordName.getVisibility() == null ? OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility() : Visibility.fromValue(recordName.getVisibility().value()));
                return name;
            }
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
        preferences.setSendEmailFrequencyDays(String.valueOf(profileEntity.getSendEmailFrequencyDays()));
        preferences.setSendChangeNotifications(new SendChangeNotifications(
                profileEntity.getSendChangeNotifications() == null ? DefaultPreferences.SEND_CHANGE_NOTIFICATIONS_DEFAULT : profileEntity.getSendChangeNotifications()));
        preferences.setSendAdministrativeChangeNotifications(new SendAdministrativeChangeNotifications(
                profileEntity.getSendAdministrativeChangeNotifications() == null ? preferences.getSendChangeNotifications().isValue() : profileEntity
                        .getSendAdministrativeChangeNotifications()));
        preferences.setSendOrcidNews(new SendOrcidNews(profileEntity.getSendOrcidNews() == null ? DefaultPreferences.SEND_ORCID_NEWS_DEFAULT : profileEntity
                .getSendOrcidNews()));
        preferences.setSendMemberUpdateRequests(profileEntity.getSendMemberUpdateRequests() == null ? DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS : profileEntity
                .getSendMemberUpdateRequests());
        preferences.setNotificationsEnabled(profileEntity.getEnableNotifications() == null ? DefaultPreferences.NOTIFICATIONS_ENABLED : profileEntity.getEnableNotifications());
        // This column is constrained as not null in the DB so don't have to
        // worry about null!
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(profileEntity.getActivitiesVisibilityDefault()));

        // Set developer tools preference
        preferences.setDeveloperToolsEnabled(new DeveloperToolsEnabled(profileEntity.getEnableDeveloperTools()));

        preferences.setNotificationsEnabled(profileEntity.getEnableNotifications());

        if (profileEntity.getReferredBy() != null) {
            orcidInternal.setReferredBy(new ReferredBy(getOrcidIdBase(profileEntity.getReferredBy())));
        }

        orcidInternal.setSalesforceId(profileEntity.getSalesforeId() == null ? null : new SalesforceId(profileEntity.getSalesforeId()));

        return orcidInternal;
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        return DateUtils.convertToXMLGregorianCalendar(date);
    }        
    
    /**
     * Get the source of a sowrceAware object
     * 
     * @param sourceAwareEntity
     *            The entity to obtain the source
     * @return the source of the object
     * */
    private Source getSource(SourceAwareEntity<?> sourceAwareEntity) {
        if (sourceAwareEntity == null || PojoUtil.isEmpty(sourceAwareEntity.getElementSourceId())) {
            return null;
        }
        Source source = new Source();        
        if (!PojoUtil.isEmpty(sourceAwareEntity.getSourceId())) {
            source.setSourceOrcid(new SourceOrcid(getOrcidIdBase(sourceAwareEntity.getSourceId())));                
        } 
        
        String clientSourceId = sourceAwareEntity.getClientSourceId();
        if (!PojoUtil.isEmpty(clientSourceId)) {
            if (OrcidStringUtils.isValidOrcid(clientSourceId)) {
                source.setSourceOrcid(new SourceOrcid(getOrcidIdBase(clientSourceId)));
            } else {
                source.setSourceClientId(new SourceClientId(getOrcidIdBase(clientSourceId)));
            }
        }
                
        Date createdDate = sourceAwareEntity.getDateCreated();
        source.setSourceDate(new SourceDate(DateUtils.convertToXMLGregorianCalendar(createdDate)));
        
        return source;
    }

}
