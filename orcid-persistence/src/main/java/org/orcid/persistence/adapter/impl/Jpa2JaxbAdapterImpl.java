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
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.*;
import org.orcid.persistence.adapter.Jpa2JaxbAdapter;
import org.orcid.persistence.jpa.entities.*;
import org.orcid.utils.DateUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

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

    private DatatypeFactory datatypeFactory = null;

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
        if (profileEntity == null) {
            throw new IllegalArgumentException("Cannot convert a null profileEntity");
        }

        OrcidProfile profile = new OrcidProfile();
        OrcidType type = profileEntity.getOrcidType();
        profile.setOrcid(profileEntity.getId());
        profile.setOrcidId("http://orcid.org/" + profileEntity.getId());
        profile.setOrcidActivities(getOrcidActivities(profileEntity));
        profile.setOrcidBio(getOrcidBio(profileEntity));
        profile.setOrcidHistory(getOrcidHistory(profileEntity));
        profile.setOrcidInternal(getOrcidInternal(profileEntity));
        profile.setPassword(profileEntity.getEncryptedPassword());
        profile.setSecurityQuestionAnswer(profileEntity.getEncryptedSecurityAnswer());
        profile.setType(type == null ? OrcidType.USER : type);
        profile.setVerificationCode(profileEntity.getEncryptedVerificationCode());
        return profile;
    }

    @Override
    public OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity) {
        OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupOrcid(profileEntity.getId());
        group.setGroupName(profileEntity.getCreditName());
        // Old way of doing emails
        if (profileEntity.getEmail() != null) {
            group.setEmail(profileEntity.getEmail());
        }
        // New way of doing emails
        else {
            Set<EmailEntity> emailEntities = profileEntity.getEmails();
            for (EmailEntity emailEntity : emailEntities) {
                group.setEmail(emailEntity.getId());
            }
        }
        for (ProfileEntity clientProfileEntity : profileEntity.getClientProfiles()) {
            OrcidClient client = new OrcidClient();
            group.getOrcidClient().add(client);
            client.setDisplayName(clientProfileEntity.getCreditName());
            client.setClientId(clientProfileEntity.getId());
            client.setShortDescription(clientProfileEntity.getBiography());
            Set<ResearcherUrlEntity> researcherUrls = clientProfileEntity.getResearcherUrls();
            if (researcherUrls != null && !researcherUrls.isEmpty()) {
                client.setWebsite(researcherUrls.iterator().next().getUrl());
            }
            ClientDetailsEntity clientDetailsEntity = clientProfileEntity.getClientDetails();
            if (clientDetailsEntity != null) {
                client.setClientSecret(clientDetailsEntity.getClientSecretForJpa());
                Set<ClientRedirectUriEntity> redirectUriEntities = clientDetailsEntity.getClientRegisteredRedirectUris();
                RedirectUris redirectUris = new RedirectUris();
                client.setRedirectUris(redirectUris);
                for (ClientRedirectUriEntity redirectUriEntity : redirectUriEntities) {
                    RedirectUri redirectUri = new RedirectUri(redirectUriEntity.getRedirectUri());
                    String predefinedScope = redirectUriEntity.getPredefinedClientScope();
                    if (StringUtils.isNotBlank(predefinedScope)) {
                        List<ScopePathType> scopePathType = new ArrayList<ScopePathType>(ScopePathType.getScopesFromSpaceSeparatedString(predefinedScope));
                        redirectUri.setScope(scopePathType);
                    }
                    redirectUris.getRedirectUri().add(redirectUri);
                }
            }
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

        return history;
    }

    private OrcidActivities getOrcidActivities(ProfileEntity profileEntity) {
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidActivities.setOrcidGrants(getOrcidGrants(profileEntity));
        orcidActivities.setOrcidPatents(getOrcidPatents(profileEntity));
        orcidActivities.setOrcidWorks(getOrcidWorks(profileEntity));
        return orcidActivities;
    }

    private OrcidGrants getOrcidGrants(ProfileEntity profileEntity) {
        Set<ProfileGrantEntity> profileGrants = profileEntity.getProfileGrants();
        if (profileGrants == null || profileGrants.isEmpty()) {
            return null;
        }
        OrcidGrants orcidGrants = new OrcidGrants();
        for (ProfileGrantEntity profileGrant : profileGrants) {
            GrantEntity grant = profileGrant.getGrant();
            OrcidGrant orcidGrant = new OrcidGrant();
            orcidGrant.setFundingAgency(getFundingAgency(profileGrant));
            orcidGrant.setGrantContributors(getGrantContributors(profileGrant));
            orcidGrant.setGrantDate(grant.getGrantDate() != null ? new GrantDate(toXMLGregorianCalendar(grant.getGrantDate())) : null);
            orcidGrant.setGrantExternalIdentifier(getGrantExternalIdentifier(profileGrant));
            orcidGrant.setGrantNumber(StringUtils.isNotBlank(grant.getGrantNo()) ? new GrantNumber(grant.getGrantNo()) : null);
            orcidGrant.setGrantSources(getGrantSources(profileGrant));
            orcidGrant.setPutCode(grant != null ? Long.toString(grant.getId()) : null);
            orcidGrant.setShortDescription(StringUtils.isNotBlank(grant.getShortDescription()) ? grant.getShortDescription() : null);
            orcidGrant.setVisibility(profileGrant.getVisibility());
            orcidGrants.getOrcidGrant().add(orcidGrant);
        }
        return orcidGrants;
    }

    private GrantSources getGrantSources(ProfileGrantEntity profileGrant) {
        if (profileGrant == null || profileGrant.getGrant() == null || profileGrant.getSources() == null || profileGrant.getSources().isEmpty()) {
            return null;
        }
        GrantSources grantSources = new GrantSources();
        Set<GrantSourceEntity> sourceEntities = profileGrant.getSources();
        for (GrantSourceEntity sourceEntity : sourceEntities) {
            Source source = new Source();
            source.setSourceDate(sourceEntity.getDepositedDate() != null ? new SourceDate(toXMLGregorianCalendar(sourceEntity.getDepositedDate())) : null);
            ProfileEntity sponsorOrcid = sourceEntity.getSponsorOrcid();
            if (sponsorOrcid != null) {
                String creditName = sponsorOrcid.getCreditName();
                source.setSourceName(StringUtils.isNotBlank(creditName) ? new SourceName(creditName) : null);
                source.setSourceOrcid(new SourceOrcid(sponsorOrcid.getId()));
                grantSources.getSource().add(source);
            }
        }
        return grantSources;
    }

    private GrantExternalIdentifier getGrantExternalIdentifier(ProfileGrantEntity profileGrant) {
        if (profileGrant == null || profileGrant.getGrant() == null) {
            return null;
        }
        GrantExternalIdentifier grantExternalIdentifier = new GrantExternalIdentifier();
        GrantEntity grant = profileGrant.getGrant();
        grantExternalIdentifier.setGrantExternalProgram(StringUtils.isNotBlank(grant.getGrantExternalProgram()) ? new GrantExternalProgram(grant
                .getGrantExternalProgram()) : null);
        grantExternalIdentifier.setGrantExternalId(StringUtils.isNotBlank(grant.getGrantExternalId()) ? new GrantExternalId(grant.getGrantExternalId()) : null);
        return grantExternalIdentifier;
    }

    private GrantContributors getGrantContributors(ProfileGrantEntity profileGrant) {
        GrantEntity grant = profileGrant.getGrant();
        if (grant == null || grant.getContributors() == null || grant.getContributors().isEmpty()) {
            return null;
        }
        GrantContributors grantContributors = new GrantContributors();
        Set<GrantContributorEntity> contributorEntities = grant.getContributors();
        for (GrantContributorEntity contributorEntity : contributorEntities) {
            grantContributors.getContributor().add(getContributor(contributorEntity));
        }
        return grantContributors;
    }

    private Contributor getContributor(GrantContributorEntity contributorEntity) {
        if (contributorEntity == null) {
            return null;
        }
        Contributor contributor = new Contributor();
        contributor.setContributorAttributes(getContributorAttributes(contributorEntity));
        contributor.setContributorEmail(StringUtils.isNotBlank(contributorEntity.getContributorEmail()) ? new ContributorEmail(contributorEntity.getContributorEmail())
                : null);
        contributor.setContributorOrcid(contributorEntity.getProfile() != null ? new ContributorOrcid(contributorEntity.getProfile().getId()) : null);
        contributor.setCreditName(StringUtils.isNotBlank(contributorEntity.getCreditName()) ? new CreditName(contributorEntity.getCreditName()) : null);
        return contributor;
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

    private FundingAgency getFundingAgency(ProfileGrantEntity profileGrant) {
        GrantEntity grant = profileGrant.getGrant();
        if (grant == null) {
            return null;
        }
        FundingAgency fundingAgency = new FundingAgency();
        fundingAgency.setAgencyName(StringUtils.isNotBlank(grant.getAgencyName()) ? new AgencyName(grant.getAgencyName()) : null);
        fundingAgency.setAgencyOrcid(grant.getAgencyOrcid() != null ? new AgencyOrcid(grant.getAgencyOrcid().getId()) : null);
        return fundingAgency;
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
        orcidPatent.setCountry(StringUtils.isNotBlank(patentEntity.getCountryOfIssue()) ? new Country(patentEntity.getCountryOfIssue()) : null);
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
        Set<Affiliation> allAffiliations = getAffiliations(profileEntity);
        if (allAffiliations != null && !allAffiliations.isEmpty()) {
            orcidBio.getAffiliations().addAll(allAffiliations);
        }

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

    private Affiliation getAffiliation(AffiliationEntity affiliationEntity) {
        Affiliation affiliation = new Affiliation();
        affiliation.setAffiliationType(affiliationEntity.getAffiliationType());
        affiliation.setRoleTitle(affiliationEntity.getRoleTitle());

        Date startDate = affiliationEntity.getStartDate();
        Date endDate = affiliationEntity.getEndDate();
        affiliation.setStartDate(startDate != null ? new StartDate(toXMLGregorianCalendarWithoutTime(startDate)) : null);
        affiliation.setEndDate(endDate != null ? new EndDate(toXMLGregorianCalendarWithoutTime(endDate)) : null);
        affiliation.setVisibility(affiliationEntity.getAffiliationVisibility());
        affiliation.setDepartmentName(affiliationEntity.getDepartmentName());
        affiliation.setAddress(getAddress(affiliationEntity));
        affiliation.setAffiliationName(affiliationEntity.getInstitutionEntity() != null ? affiliationEntity.getInstitutionEntity().getName() : null);

        return affiliation;
    }

    private Set<Affiliation> getAffiliations(ProfileEntity profileEntity) {
        Set<AffiliationEntity> affiliationEntities = profileEntity.getAffiliations();
        if (affiliationEntities != null && !affiliationEntities.isEmpty()) {
            Set<Affiliation> affiliations = new LinkedHashSet<Affiliation>();
            for (AffiliationEntity affiliationEntity : affiliationEntities) {
                affiliations.add(getAffiliation(affiliationEntity));
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
                String urlName = !(StringUtils.isBlank(researcherUrl.getUrlName())) ? researcherUrl.getUrlName() : "";
                url.setUrlName(new UrlName(urlName));
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
                externalIdentifier.setExternalIdOrcid(externalIdEntity != null ? new ExternalIdOrcid(externalIdEntity.getId()) : null);
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
                DelegateSummary delegateSummary = new DelegateSummary(new Orcid(givenPermissionToEntity.getReceiver().getId()));
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
                DelegateSummary delegateSummary = new DelegateSummary(new Orcid(givenPermissionByEntity.getGiver().getId()));
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
        if (profileEntity.getEmail() != null) {
            setEmailsFromOldDbSchema(profileEntity, contactDetails);
        } else {
            setEmails(profileEntity, contactDetails);
        }
        setCountry(profileEntity, contactDetails);
        return contactDetails;
    }

    private void setCountry(ProfileEntity profileEntity, ContactDetails contactDetails) {
        String iso2Country = profileEntity.getIso2Country();
        if (StringUtils.isNotBlank(iso2Country)) {
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
                emailList.add(email);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void setEmailsFromOldDbSchema(ProfileEntity profileEntity, ContactDetails contactDetails) {
        List<Email> emailList = contactDetails.getEmail();
        // The old way of doing emails.
        Email primaryEmail = new Email(profileEntity.getEmail());
        primaryEmail.setVisibility(profileEntity.getEmailVisibility());
        primaryEmail.setPrimary(true);
        primaryEmail.setVerified(profileEntity.getEmailVerified());
        primaryEmail.setCurrent(true);
        emailList.add(primaryEmail);
        Set<AlternateEmailEntity> alternateEmails = profileEntity.getAlternateEmails();
        if (alternateEmails != null) {
            for (AlternateEmailEntity alternativeEmailEntity : alternateEmails) {
                Email email = new Email(alternativeEmailEntity.getAlternateEmail());
                email.setVisibility(profileEntity.getAlternativeEmailsVisibility());
                email.setPrimary(false);
                email.setVerified(false);
                email.setCurrent(true);
                emailList.add(email);
            }
        }
    }

    private Address getAddress(AffiliationEntity affiliationEntity) {
        InstitutionEntity institutionEntity = affiliationEntity.getInstitutionEntity();
        if (institutionEntity != null && institutionEntity.getAddress() != null) {
            AddressEntity addressEntity = institutionEntity.getAddress();
            Address address = new Address();
            String country = addressEntity.getCountry();
            address.setCountry(country == null ? null : new Country(country));
            address.getCountry().setVisibility(affiliationEntity.getAffiliationAddressVisibility());
            return address;
        }
        return null;
    }

    private Source getSponsor(ProfileEntity profileEntity) {
        ProfileEntity sponsorProfileEntity = profileEntity.getSource();
        if (sponsorProfileEntity != null) {
            Source sponsor = new Source();
            SourceName sponsorName = StringUtils.isNotBlank(sponsorProfileEntity.getCreditName()) ? new SourceName(sponsorProfileEntity.getCreditName()) : null;
            SourceOrcid sponsorOrcid = StringUtils.isNotBlank(sponsorProfileEntity.getId()) ? new SourceOrcid(sponsorProfileEntity.getId()) : null;
            sponsor.setSourceName(sponsorName);
            sponsor.setSourceOrcid(sponsorOrcid);
            return sponsor;
        }
        return null;
    }

    private Applications getApplications(ProfileEntity profileEntity) {
        Set<OrcidOauth2TokenDetail> tokenDetails = profileEntity.getTokenDetails();
        if (tokenDetails != null && !tokenDetails.isEmpty()) {
            Applications applications = new Applications();
            for (OrcidOauth2TokenDetail tokenDetail : tokenDetails) {
                if (tokenDetail.getTokenDisabled() == null || !tokenDetail.getTokenDisabled()) {
                    ApplicationSummary applicationSummary = new ApplicationSummary();
                    ClientDetailsEntity acceptedClient = tokenDetail.getClientDetailsEntity();

                    ProfileEntity acceptedClientProfileEntity = acceptedClient != null ? acceptedClient.getProfileEntity() : null;
                    if (acceptedClientProfileEntity != null) {
                        applicationSummary.setApplicationOrcid(new ApplicationOrcid(acceptedClient.getClientId()));
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
                        }
                        applications.getApplicationSummary().add(applicationSummary);
                    }

                }
            }
            return applications;
        }
        return null;
    }

    private void getScope() {
        // TODO Auto-generated method stub

    }

    private OrcidWork getOrcidWork(ProfileWorkEntity profileWorkEntity) {
        WorkEntity work = profileWorkEntity.getWork();
        if (work == null) {
            return null;
        }
        OrcidWork orcidWork = new OrcidWork();
        FuzzyDate publicationDate = work.getPublicationDate();
        orcidWork.setPublicationDate(getPublicationDateFromFuzzyDate(publicationDate));
        orcidWork.setPutCode(Long.toString(work.getId()));
        orcidWork.setShortDescription(work.getDescription());
        orcidWork.setUrl(StringUtils.isNotBlank(work.getWorkUrl()) ? new Url(work.getWorkUrl()) : null);
        orcidWork.setWorkCitation(getWorkCitation(work));
        orcidWork.setWorkContributors(getWorkContributors(work));
        orcidWork.setWorkExternalIdentifiers(getWorkExternalIdentifiers(work));
        orcidWork.setWorkSources(getWorkSources(profileWorkEntity));
        orcidWork.setWorkTitle(getWorkTitle(work));
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
        return workTitle;
    }

    private WorkSources getWorkSources(ProfileWorkEntity profileWorkEntity) {
        if (profileWorkEntity == null || profileWorkEntity.getSources() == null || profileWorkEntity.getSources().isEmpty()) {
            return null;
        }
        Set<WorkSourceEntity> sources = profileWorkEntity.getSources();
        WorkSources workSources = new WorkSources();
        for (WorkSourceEntity workSourceEntity : sources) {
            workSources.getSource().add(getWorkSource(workSourceEntity));
        }

        return workSources;
    }

    private Source getWorkSource(WorkSourceEntity workSourceEntity) {
        if (workSourceEntity == null) {
            return null;
        }
        Source source = new Source();
        Date depositedDate = workSourceEntity.getDepositedDate();
        source.setSourceDate(getSourceDate(depositedDate));
        ProfileEntity sponsorOrcid = workSourceEntity.getSponsorOrcid();
        source.setSourceName(sponsorOrcid != null ? new SourceName(sponsorOrcid.getCreditName()) : null);
        source.setSourceOrcid(sponsorOrcid != null ? new SourceOrcid(sponsorOrcid.getId()) : null);
        return source;
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

    private WorkContributors getWorkContributors(WorkEntity work) {
        if (work == null || work.getContributors() == null || work.getContributors().isEmpty()) {
            return null;
        }
        Set<WorkContributorEntity> contributorEntities = work.getContributors();
        WorkContributors workContributors = new WorkContributors();
        for (WorkContributorEntity contributorEntity : contributorEntities) {
            Contributor workContributor = getWorkContributor(contributorEntity);
            if (workContributor != null) {
                workContributors.getContributor().add(workContributor);
            }
        }
        return workContributors;
    }

    private Contributor getWorkContributor(WorkContributorEntity contributorEntity) {
        if (contributorEntity == null) {
            return null;
        }
        Contributor contributor = new Contributor();
        contributor.setContributorEmail(StringUtils.isNotBlank(contributorEntity.getContributorEmail()) ? new ContributorEmail(contributorEntity.getContributorEmail())
                : null);
        contributor.setContributorAttributes(getContributorAttributes(contributorEntity));
        ProfileEntity profile = contributorEntity.getProfile();
        if (profile != null) {
            contributor.setContributorOrcid(new ContributorOrcid(profile.getId()));
            contributor.setCreditName(new CreditName(profile.getCreditName()));
        } else {
            contributor.setCreditName(new CreditName(contributorEntity.getCreditName()));
        }
        return contributor;
    }

    private PublicationDate getPublicationDateFromFuzzyDate(FuzzyDate fuzzyDate) {
        if (fuzzyDate == null) {
            return null;
        }
        Year year = fuzzyDate.getYear() != null ? new Year(fuzzyDate.getYear()) : null;
        Month month = fuzzyDate.getMonth() != null ? new Month(fuzzyDate.getMonth()) : null;
        Day day = fuzzyDate.getDay() != null ? new Day(fuzzyDate.getDay()) : null;

        return new PublicationDate(year, month, day);
    }

    private CreditName getAuthorCreditName(WorkContributorEntity contributorEntity) {
        if (contributorEntity != null && StringUtils.isNotBlank(contributorEntity.getCreditName())) {
            CreditName creditName = new CreditName();
            creditName.setContent(contributorEntity.getCreditName());
            return creditName;
        }
        return null;
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

    private XMLGregorianCalendar toXMLGregorianCalendarWithoutTime(Date date) {
        if (date != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlGregorianCalendar.setDay(c.get(Calendar.DAY_OF_MONTH));
            xmlGregorianCalendar.setMonth(c.get(Calendar.MONTH) + 1);
            xmlGregorianCalendar.setYear(c.get(Calendar.YEAR));
            return xmlGregorianCalendar;
        } else {
            return null;
        }
    }

}
