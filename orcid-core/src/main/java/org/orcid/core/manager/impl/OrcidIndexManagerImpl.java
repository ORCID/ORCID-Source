package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class OrcidIndexManagerImpl implements OrcidIndexManager {

    @Value("${org.orcid.core.indexPublicProfile:true}")
    private boolean indexPublicProfile;

    @Resource
    private SolrDao solrDao;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "visibilityFilter")
    private VisibilityFilter visibilityFilter;

    private static final Logger LOG = LoggerFactory.getLogger(OrcidIndexManagerImpl.class);

    public void setSolrDao(SolrDao solrDao) {
        this.solrDao = solrDao;
    }

    @Override
    @Deprecated
    //now going via orcid-message-listener
    public void persistProfileInformationForIndexingIfNecessary(OrcidProfile orcidProfile) {
        String orcid = orcidProfile.getOrcidIdentifier().getPath();
        Date lastModifiedFromSolr = solrDao.retrieveLastModified(orcid);
        Date lastModifiedFromDb = orcidProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        if (lastModifiedFromDb.equals(lastModifiedFromSolr)) {
            // Check if re-indexing
            IndexingStatus indexingStatus = profileDao.retrieveIndexingStatus(orcid);
            if (!IndexingStatus.REINDEX.equals(indexingStatus)) {
                // If not re-indexing then skip
                LOG.info("Index is already up to date for orcid: {}", orcid);
                return;
            }
        }
        persistProfileInformationForIndexing(orcidProfile);
    }

    @Override
    @Deprecated
    public void persistProfileInformationForIndexing(OrcidProfile orcidProfile) {

        // Check if the profile is locked
        if (orcidProfile.isLocked()) {
            orcidProfile.downgradeToOrcidIdentifierOnly();
        }

        OrcidMessage messageToFilter = new OrcidMessage();
        messageToFilter.setOrcidProfile(orcidProfile);
        OrcidMessage filteredMessage = visibilityFilter.filter(messageToFilter, Visibility.PUBLIC);
        OrcidProfile filteredProfile = filteredMessage.getOrcidProfile();

        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(filteredProfile.getOrcidIdentifier().getPath());

        OrcidDeprecated orcidDeprecated = filteredProfile.getOrcidDeprecated();
        if (orcidDeprecated != null) {
            profileIndexDocument.setPrimaryRecord(orcidDeprecated.getPrimaryRecord() != null ? orcidDeprecated.getPrimaryRecord().getOrcidIdentifier().getPath() : null);
        }

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
                    Source source = externalIdentifier.getSource();
                    String sourcePath = null;
                    if (source != null) {
                        sourcePath = source.retrieveSourcePath();
                        if (sourcePath != null) {
                            extIdOrcids.add(sourcePath);
                        }
                    }
                    ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
                    if (externalIdReference != null) {
                        extIdRefs.add(externalIdReference.getContent());
                    }
                    if (NullUtils.noneNull(sourcePath, externalIdReference)) {
                        extIdOrcidsAndRefs.add(sourcePath + "=" + externalIdReference.getContent());
                    }
                }
                if (!extIdOrcids.isEmpty()) {
                    profileIndexDocument.setExternalIdSources(extIdOrcids);
                }
                if (!extIdRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdReferences(extIdRefs);
                }
            }

            OrcidActivities orcidActivities = filteredProfile.getOrcidActivities();
            if (orcidActivities != null) {
                // Affiliations affiliations =
                // orcidActivities.getAffiliations();
                // if (affiliations != null) {
                // List<Affiliation> pastInsts = affiliations
                // .getAffiliationsByType(AffiliationType.PAST_INSTITUTION);
                // if (pastInsts != null && !pastInsts.isEmpty()) {
                // List<String> pastInstNames = new ArrayList<String>();
                // for (Affiliation pastAffiliation : pastInsts) {
                // pastInstNames.add(pastAffiliation
                // .getAffiliationName());
                // }
                //
                // profileIndexDocument
                // .setAffiliatePastInstitutionNames(pastInstNames);
                // }
                //
                // List<Affiliation> primaryInsts = affiliations
                // .getAffiliationsByType(AffiliationType.CURRENT_PRIMARY_INSTITUTION);
                // if (primaryInsts != null && !primaryInsts.isEmpty()) {
                // List<String> primaryInstNames = new ArrayList<String>();
                // for (Affiliation primaryAffiliation : primaryInsts) {
                // primaryInstNames.add(primaryAffiliation
                // .getAffiliationName());
                // }
                //
                // profileIndexDocument
                // .setAffiliatePrimaryInstitutionNames(primaryInstNames);
                // }
                //
                // List<Affiliation> currentNonPrimaryInsts = affiliations
                // .getAffiliationsByType(AffiliationType.CURRENT_INSTITUTION);
                // if (currentNonPrimaryInsts != null
                // && !currentNonPrimaryInsts.isEmpty()) {
                // List<String> affiliateInstNames = new ArrayList<String>();
                // for (Affiliation currentAffiliation : currentNonPrimaryInsts)
                // {
                // affiliateInstNames.add(currentAffiliation
                // .getAffiliationName());
                // }
                //
                // profileIndexDocument
                // .setAffiliateInstitutionNames(affiliateInstNames);
                // }
                // }

                List<String> keywords = extractKeywordsAsStringFromBio(orcidBio);
                if (keywords != null) {
                    profileIndexDocument.setKeywords(keywords);
                }
            }
            List<OrcidWork> orcidWorks = filteredProfile.retrieveOrcidWorks() != null ? filteredProfile.retrieveOrcidWorks().getOrcidWork() : null;
            if (orcidWorks != null) {
                List<String> workTitles = new ArrayList<String>();
                Map<WorkExternalIdentifierType, List<String>> allExternalIdentifiers = new HashMap<WorkExternalIdentifierType, List<String>>();
                for (OrcidWork orcidWork : orcidWorks) {

                    if (orcidWork.getWorkExternalIdentifiers() != null) {

                        for (WorkExternalIdentifier workExternalIdentifier : orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {

                            /**
                             * Creates a map that contains all different
                             * external identifiers for the current work
                             * */
                            if (nullSafeCheckForWorkExternalIdentifier(workExternalIdentifier)) {
                                WorkExternalIdentifierType type = workExternalIdentifier.getWorkExternalIdentifierType();
                                if (!allExternalIdentifiers.containsKey(type)) {
                                    List<String> content = new ArrayList<String>();
                                    content.add(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
                                    allExternalIdentifiers.put(type, content);
                                } else {
                                    allExternalIdentifiers.get(type).add(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
                                }
                            }

                        }
                    }

                    if (orcidWork.getWorkTitle() != null) {
                        Title workMainTitle = orcidWork.getWorkTitle().getTitle();
                        Subtitle worksubTitle = orcidWork.getWorkTitle().getSubtitle();
                        TranslatedTitle translatedTitle = orcidWork.getWorkTitle().getTranslatedTitle();
                        if (workMainTitle != null && !StringUtils.isBlank(workMainTitle.getContent())) {
                            workTitles.add(workMainTitle.getContent());
                        }

                        if (worksubTitle != null && !StringUtils.isBlank(worksubTitle.getContent())) {
                            workTitles.add(worksubTitle.getContent());
                        }

                        if (translatedTitle != null && !StringUtils.isBlank(translatedTitle.getContent())) {
                            workTitles.add(translatedTitle.getContent());
                        }
                    }
                }

                profileIndexDocument.setWorkTitles(workTitles);

                // Set the list of external identifiers to the document list
                addExternalIdentifiersToIndexDocument(profileIndexDocument, allExternalIdentifiers);
            }

            List<Funding> orcidFundings = filteredProfile.retrieveFundings() != null ? filteredProfile.retrieveFundings().getFundings() : null;
            if (orcidFundings != null) {
                List<String> fundingTitle = new ArrayList<String>();
                for (Funding orcidFunding : orcidFundings) {
                    FundingTitle title = orcidFunding.getTitle();
                    if (title != null) {
                        if (title.getTitle() != null && !StringUtils.isBlank(title.getTitle().getContent())) {
                            fundingTitle.add(title.getTitle().getContent());
                        }

                        if (title.getTranslatedTitle() != null && StringUtils.isBlank(title.getTranslatedTitle().getContent())) {
                            fundingTitle.add(title.getTranslatedTitle().getContent());
                        }
                    }

                }

                profileIndexDocument.setFundingTitles(fundingTitle);
            }

        }

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        orcidMessage.setOrcidProfile(filteredProfile);
        OrcidHistory orcidHistory = filteredProfile.getOrcidHistory();
        if (orcidHistory != null) {
            LastModifiedDate lastModifiedDate = orcidHistory.getLastModifiedDate();
            if (lastModifiedDate != null) {
                profileIndexDocument.setProfileLastModifiedDate(lastModifiedDate.getValue().toGregorianCalendar().getTime());
            }
            SubmissionDate submissionDate = orcidHistory.getSubmissionDate();
            if (submissionDate != null) {
                profileIndexDocument.setProfileSubmissionDate(submissionDate.getValue().toGregorianCalendar().getTime());
            }
        }
        if (indexPublicProfile) {
            profileIndexDocument.setPublicProfileMessage(orcidMessage.toXmlString());
        }
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
        deleteOrcidProfile(orcidProfile.getOrcidIdentifier().getPath());

    }

    @Override
    public void deleteOrcidProfile(String orcid) {
        solrDao.removeOrcids(Arrays.asList(orcid));
    }

    private boolean nullSafeCheckForWorkExternalIdentifier(WorkExternalIdentifier workExternalIdentifier) {
        return workExternalIdentifier.getWorkExternalIdentifierId() != null && !StringUtils.isBlank(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
    }

    /**
     * Fill all the different external identifiers in the profile index
     * document.
     * 
     * @param profileIndexDocument
     *            The document that will be indexed by solr
     * @param externalIdentifiers
     *            The list of external identifiers
     * */
    private void addExternalIdentifiersToIndexDocument(OrcidSolrDocument profileIndexDocument, Map<WorkExternalIdentifierType, List<String>> externalIdentifiers) {
        Iterator<Entry<WorkExternalIdentifierType, List<String>>> it = externalIdentifiers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<WorkExternalIdentifierType, List<String>> entry = (Map.Entry<WorkExternalIdentifierType, List<String>>) it.next();
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                switch (entry.getKey()) {
                case AGR:
                    profileIndexDocument.setAgr(entry.getValue());
                    break;
                case ARXIV:
                    profileIndexDocument.setArxiv(entry.getValue());
                    break;
                case ASIN:
                    profileIndexDocument.setAsin(entry.getValue());
                    break;
                case ASIN_TLD:
                    profileIndexDocument.setAsintld(entry.getValue());
                    break;
                case BIBCODE:
                    profileIndexDocument.setBibcode(entry.getValue());
                    break;
                case CBA:
                    profileIndexDocument.setCba(entry.getValue());
                    break;
                case CIT:
                    profileIndexDocument.setCit(entry.getValue());
                    break;
                case CTX:
                    profileIndexDocument.setCtx(entry.getValue());
                    break;
                case DOI:
                    profileIndexDocument.setDigitalObjectIds(entry.getValue());
                    break;
                case EID:
                    profileIndexDocument.setEid(entry.getValue());
                    break;
                case ETHOS:
                    profileIndexDocument.setEthos(entry.getValue());
                    break;
                case HANDLE:
                    profileIndexDocument.setHandle(entry.getValue());
                    break;
                case HIR:
                    profileIndexDocument.setHir(entry.getValue());
                    break;
                case ISBN:
                    profileIndexDocument.setIsbn(entry.getValue());
                    break;
                case ISSN:
                    profileIndexDocument.setIssn(entry.getValue());
                    break;
                case JFM:
                    profileIndexDocument.setJfm(entry.getValue());
                    break;
                case JSTOR:
                    profileIndexDocument.setJstor(entry.getValue());
                    break;
                case LCCN:
                    profileIndexDocument.setLccn(entry.getValue());
                    break;
                case MR:
                    profileIndexDocument.setMr(entry.getValue());
                    break;
                case OCLC:
                    profileIndexDocument.setOclc(entry.getValue());
                    break;
                case OL:
                    profileIndexDocument.setOl(entry.getValue());
                    break;
                case OSTI:
                    profileIndexDocument.setOsti(entry.getValue());
                    break;
                case OTHER_ID:
                    profileIndexDocument.setOtherIdentifierType(entry.getValue());
                    break;
                case PAT:
                    profileIndexDocument.setPat(entry.getValue());
                    break;
                case PMC:
                    profileIndexDocument.setPmc(entry.getValue());
                    break;
                case PMID:
                    profileIndexDocument.setPmid(entry.getValue());
                    break;
                case RFC:
                    profileIndexDocument.setRfc(entry.getValue());
                    break;
                case SOURCE_WORK_ID:
                    profileIndexDocument.setSourceWorkId(entry.getValue());
                    break;
                case SSRN:
                    profileIndexDocument.setSsrn(entry.getValue());
                    break;
                case URI:
                    profileIndexDocument.setUri(entry.getValue());
                    break;
                case URN:
                    profileIndexDocument.setUrn(entry.getValue());
                    break;
                case WOSUID:
                    profileIndexDocument.setWosuid(entry.getValue());
                case ZBL:
                    profileIndexDocument.setZbl(entry.getValue());
                    break;
                }
            }
        }
    }

}
