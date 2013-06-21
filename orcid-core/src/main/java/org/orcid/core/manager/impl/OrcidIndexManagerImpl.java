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
package org.orcid.core.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.orcid.utils.NullUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrcidIndexManagerImpl implements OrcidIndexManager {

    @Resource
    private SolrDao solrDao;

    @Resource(name = "visibilityFilter")
    private VisibilityFilter visibilityFilter;

    public void setSolrDao(SolrDao solrDao) {
        this.solrDao = solrDao;
    }

    @Override
    public void persistProfileInformationForIndexing(OrcidProfile orcidProfile) {
        OrcidMessage messageToFilter = new OrcidMessage();
        messageToFilter.setOrcidProfile(orcidProfile);
        OrcidMessage filteredMessage = visibilityFilter.filter(messageToFilter, Visibility.PUBLIC);
        OrcidProfile filteredProfile = filteredMessage.getOrcidProfile();

        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(filteredProfile.getOrcid().getValue());
        OrcidBio orcidBio = filteredProfile.getOrcidBio();
        if (orcidBio != null) {
            PersonalDetails personalDetails = orcidBio.getPersonalDetails();
            boolean persistPersonalDetails = personalDetails != null;
            if (persistPersonalDetails) {
                profileIndexDocument.setFamilyName(personalDetails.getFamilyName() != null ? personalDetails.getFamilyName().getContent() : null);
                profileIndexDocument.setGivenNames(personalDetails.getGivenNames() != null ? personalDetails.getGivenNames().getContent() : null);
                profileIndexDocument.setCreditName(personalDetails.getCreditName() != null ? personalDetails.getCreditName().getContent() : null);
                List<OtherName> otherNames = personalDetails.getOtherNames() != null ? personalDetails.getOtherNames().getOtherName() : null;
                if (otherNames != null && !otherNames.isEmpty()) {
                    List<String> names = new ArrayList<String>();
                    for (OtherName otherName : otherNames) {
                        names.add(otherName.getContent());
                    }
                    profileIndexDocument.setOtherNames(names);
                }
            }

            ContactDetails contactDetails = orcidBio.getContactDetails();
            if (contactDetails != null) {
                for (Email email : contactDetails.getEmail()) {
                    profileIndexDocument.addEmailAddress(email.getValue());
                }
            }

            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
            if (externalIdentifiers != null) {
                List<String> extIdOrcids = new ArrayList<String>();
                List<String> extIdRefs = new ArrayList<String>();
                List<String> extIdOrcidsAndRefs = new ArrayList<String>();
                for (ExternalIdentifier externalIdentifier : externalIdentifiers.getExternalIdentifier()) {
                    ExternalIdOrcid externalIdOrcid = externalIdentifier.getExternalIdOrcid();
                    if (externalIdOrcid != null) {
                        extIdOrcids.add(externalIdOrcid.getValue());
                    }
                    ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
                    if (externalIdReference != null) {
                        extIdRefs.add(externalIdReference.getContent());
                    }
                    if (NullUtils.noneNull(externalIdOrcid, externalIdReference)) {
                        extIdOrcidsAndRefs.add(externalIdOrcid.getValue() + "=" + externalIdReference.getContent());
                    }
                }
                if (!extIdOrcids.isEmpty()) {
                    profileIndexDocument.setExternalIdOrcids(extIdOrcids);
                }
                if (!extIdRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdReferences(extIdRefs);
                }
                if (!extIdOrcidsAndRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdOrcidsAndReferences(extIdOrcidsAndRefs);
                }
            }

            List<Affiliation> pastInsts = orcidBio.getAffiliationsByType(AffiliationType.PAST_INSTITUTION);
            if (pastInsts != null && !pastInsts.isEmpty()) {
                List<String> pastInstNames = new ArrayList<String>();
                for (Affiliation pastAffiliation : pastInsts) {
                    pastInstNames.add(pastAffiliation.getAffiliationName());
                }

                profileIndexDocument.setAffiliatePastInstitutionNames(pastInstNames);
            }

            List<Affiliation> primaryInsts = orcidBio.getAffiliationsByType(AffiliationType.CURRENT_PRIMARY_INSTITUTION);
            if (primaryInsts != null && !primaryInsts.isEmpty()) {
                List<String> primaryInstNames = new ArrayList<String>();
                for (Affiliation primaryAffiliation : primaryInsts) {
                    primaryInstNames.add(primaryAffiliation.getAffiliationName());
                }

                profileIndexDocument.setAffiliatePrimaryInstitutionNames(primaryInstNames);
            }

            List<Affiliation> currentNonPrimaryInsts = orcidBio.getAffiliationsByType(AffiliationType.CURRENT_INSTITUTION);
            if (currentNonPrimaryInsts != null && !currentNonPrimaryInsts.isEmpty()) {
                List<String> affiliateInstNames = new ArrayList<String>();
                for (Affiliation currentAffiliation : currentNonPrimaryInsts) {
                    affiliateInstNames.add(currentAffiliation.getAffiliationName());
                }

                profileIndexDocument.setAffiliateInstitutionNames(affiliateInstNames);
            }

            List<String> keywords = extractKeywordsAsStringFromBio(orcidBio);
            if (keywords != null) {
                profileIndexDocument.setKeywords(keywords);
            }
        }
        List<OrcidWork> orcidWorks = filteredProfile.retrieveOrcidWorks() != null ? filteredProfile.retrieveOrcidWorks().getOrcidWork() : null;
        if (orcidWorks != null) {
            List<String> workTitles = new ArrayList<String>();
            List<String> workIdentifiers = new ArrayList<String>();
            for (OrcidWork orcidWork : orcidWorks) {

                if (orcidWork.getWorkExternalIdentifiers() != null) {

                    for (WorkExternalIdentifier workExternalIdentifier : orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {

                        if (nullSafeCheckWorkForDoi(workExternalIdentifier)) {
                            workIdentifiers.add(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
                        }

                    }
                }

                if (orcidWork.getWorkTitle() != null) {
                    Title workMainTitle = orcidWork.getWorkTitle().getTitle();
                    Subtitle worksubTitle = orcidWork.getWorkTitle().getSubtitle();
                    if (workMainTitle != null && !StringUtils.isBlank(workMainTitle.getContent())) {
                        workTitles.add(workMainTitle.getContent());
                    }

                    if (worksubTitle != null && !StringUtils.isBlank(worksubTitle.getContent())) {
                        workTitles.add(worksubTitle.getContent());
                    }
                }
            }

            profileIndexDocument.setWorkTitles(workTitles);
            profileIndexDocument.setDigitalObjectIds(workIdentifiers);
        }

        List<OrcidGrant> orcidGrants = filteredProfile.retrieveOrcidGrants() != null ? filteredProfile.retrieveOrcidGrants().getOrcidGrant() : null;
        if (orcidGrants != null) {
            List<String> grantNumbers = new ArrayList<String>();
            for (OrcidGrant orcidGrant : orcidGrants) {
                if (orcidGrant.getGrantNumber() != null && !StringUtils.isBlank(orcidGrant.getGrantNumber().getContent())) {
                    grantNumbers.add(orcidGrant.getGrantNumber().getContent());
                }
            }

            profileIndexDocument.setGrantNumbers(grantNumbers);
        }

        List<OrcidPatent> orcidPatents = filteredProfile.retrieveOrcidPatents() != null ? filteredProfile.retrieveOrcidPatents().getOrcidPatent() : null;
        if (orcidPatents != null) {
            List<String> patentNumbers = new ArrayList<String>();
            for (OrcidPatent orcidPatent : orcidPatents) {
                if (orcidPatent.getPatentNumber() != null && !StringUtils.isBlank(orcidPatent.getPatentNumber().getContent())) {
                    patentNumbers.add(orcidPatent.getPatentNumber().getContent());
                }
            }

            profileIndexDocument.setPatentNumbers(patentNumbers);
        }

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        orcidMessage.setOrcidProfile(filteredProfile);
        profileIndexDocument.setPublicProfileMessage(orcidMessage.toString());
        solrDao.persist(profileIndexDocument);
    }

    private List<String> extractKeywordsAsStringFromBio(OrcidBio orcidBio) {

        if (orcidBio != null && orcidBio.getKeywords() != null) {
            List<Keyword> keyWords = orcidBio.getKeywords().getKeyword();
            if (keyWords != null && keyWords.size() > 0) {
                List<String> keywordValues = new ArrayList<String>();
                for (Keyword keyword : keyWords) {
                    keywordValues.add(keyword.getContent());
                }

                return keywordValues;
            }
        }
        return null;
    }

    @Override
    public void deleteOrcidProfile(OrcidProfile orcidProfile) {
        deleteOrcidProfile(orcidProfile.getOrcid().getValue());

    }

    @Override
    public void deleteOrcidProfile(String orcid) {
        solrDao.removeOrcids(Arrays.asList(orcid));
    }

    private boolean nullSafeCheckWorkForDoi(WorkExternalIdentifier workExternalIdentifier) {
        // need to check that the identifier isn't null, of type doi and has a
        // value in its id field..
        if (workExternalIdentifier != null) {
            boolean doiType = WorkExternalIdentifierType.DOI.equals(workExternalIdentifier.getWorkExternalIdentifierType());
            return doiType && workExternalIdentifier.getWorkExternalIdentifierId() != null
                    && !StringUtils.isBlank(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        }

        return false;

    }

}
