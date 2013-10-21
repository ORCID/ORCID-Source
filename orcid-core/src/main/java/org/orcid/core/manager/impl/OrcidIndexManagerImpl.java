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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.orcid.utils.NullUtils;
import org.springframework.stereotype.Service;

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
		OrcidMessage filteredMessage = visibilityFilter.filter(messageToFilter,
				Visibility.PUBLIC);
		OrcidProfile filteredProfile = filteredMessage.getOrcidProfile();

		OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
		profileIndexDocument.setOrcid(filteredProfile.getOrcid().getValue());

		OrcidDeprecated orcidDeprecated = filteredProfile.getOrcidDeprecated();
		if (orcidDeprecated != null) {
			profileIndexDocument.setPrimaryRecord(orcidDeprecated
					.getPrimaryRecord() != null ? orcidDeprecated
					.getPrimaryRecord().getOrcid().getValue() : null);
		}

		OrcidBio orcidBio = filteredProfile.getOrcidBio();
		if (orcidBio != null) {
			PersonalDetails personalDetails = orcidBio.getPersonalDetails();
			boolean persistPersonalDetails = personalDetails != null;
			if (persistPersonalDetails) {
				profileIndexDocument.setFamilyName(personalDetails
						.getFamilyName() != null ? personalDetails
						.getFamilyName().getContent() : null);
				profileIndexDocument.setGivenNames(personalDetails
						.getGivenNames() != null ? personalDetails
						.getGivenNames().getContent() : null);
				profileIndexDocument.setCreditName(personalDetails
						.getCreditName() != null ? personalDetails
						.getCreditName().getContent() : null);
				List<OtherName> otherNames = personalDetails.getOtherNames() != null ? personalDetails
						.getOtherNames().getOtherName() : null;
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

			ExternalIdentifiers externalIdentifiers = orcidBio
					.getExternalIdentifiers();
			if (externalIdentifiers != null) {
				List<String> extIdOrcids = new ArrayList<String>();
				List<String> extIdRefs = new ArrayList<String>();
				List<String> extIdOrcidsAndRefs = new ArrayList<String>();
				for (ExternalIdentifier externalIdentifier : externalIdentifiers
						.getExternalIdentifier()) {
					ExternalIdOrcid externalIdOrcid = externalIdentifier
							.getExternalIdOrcid();
					if (externalIdOrcid != null) {
						extIdOrcids.add(externalIdOrcid.getValue());
					}
					ExternalIdReference externalIdReference = externalIdentifier
							.getExternalIdReference();
					if (externalIdReference != null) {
						extIdRefs.add(externalIdReference.getContent());
					}
					if (NullUtils
							.noneNull(externalIdOrcid, externalIdReference)) {
						extIdOrcidsAndRefs.add(externalIdOrcid.getValue() + "="
								+ externalIdReference.getContent());
					}
				}
				if (!extIdOrcids.isEmpty()) {
					profileIndexDocument.setExternalIdOrcids(extIdOrcids);
				}
				if (!extIdRefs.isEmpty()) {
					profileIndexDocument.setExternalIdReferences(extIdRefs);
				}
				if (!extIdOrcidsAndRefs.isEmpty()) {
					profileIndexDocument
							.setExternalIdOrcidsAndReferences(extIdOrcidsAndRefs);
				}
			}

			OrcidActivities orcidActivities = filteredProfile
					.getOrcidActivities();
			if (orcidActivities != null) {
//				Affiliations affiliations = orcidActivities.getAffiliations();
//				if (affiliations != null) {
//					List<Affiliation> pastInsts = affiliations
//							.getAffiliationsByType(AffiliationType.PAST_INSTITUTION);
//					if (pastInsts != null && !pastInsts.isEmpty()) {
//						List<String> pastInstNames = new ArrayList<String>();
//						for (Affiliation pastAffiliation : pastInsts) {
//							pastInstNames.add(pastAffiliation
//									.getAffiliationName());
//						}
//
//						profileIndexDocument
//								.setAffiliatePastInstitutionNames(pastInstNames);
//					}
//
//					List<Affiliation> primaryInsts = affiliations
//							.getAffiliationsByType(AffiliationType.CURRENT_PRIMARY_INSTITUTION);
//					if (primaryInsts != null && !primaryInsts.isEmpty()) {
//						List<String> primaryInstNames = new ArrayList<String>();
//						for (Affiliation primaryAffiliation : primaryInsts) {
//							primaryInstNames.add(primaryAffiliation
//									.getAffiliationName());
//						}
//
//						profileIndexDocument
//								.setAffiliatePrimaryInstitutionNames(primaryInstNames);
//					}
//
//					List<Affiliation> currentNonPrimaryInsts = affiliations
//							.getAffiliationsByType(AffiliationType.CURRENT_INSTITUTION);
//					if (currentNonPrimaryInsts != null
//							&& !currentNonPrimaryInsts.isEmpty()) {
//						List<String> affiliateInstNames = new ArrayList<String>();
//						for (Affiliation currentAffiliation : currentNonPrimaryInsts) {
//							affiliateInstNames.add(currentAffiliation
//									.getAffiliationName());
//						}
//
//						profileIndexDocument
//								.setAffiliateInstitutionNames(affiliateInstNames);
//					}
//				}

				List<String> keywords = extractKeywordsAsStringFromBio(orcidBio);
				if (keywords != null) {
					profileIndexDocument.setKeywords(keywords);
				}
			}
			List<OrcidWork> orcidWorks = filteredProfile.retrieveOrcidWorks() != null ? filteredProfile
					.retrieveOrcidWorks().getOrcidWork() : null;
			if (orcidWorks != null) {
				List<String> workTitles = new ArrayList<String>();
				Map<WorkExternalIdentifierType, List<String>> allExternalIdentifiers = new HashMap<WorkExternalIdentifierType, List<String>>();
				for (OrcidWork orcidWork : orcidWorks) {

					if (orcidWork.getWorkExternalIdentifiers() != null) {

						for (WorkExternalIdentifier workExternalIdentifier : orcidWork
								.getWorkExternalIdentifiers()
								.getWorkExternalIdentifier()) {

							/**
							 * Creates a map that contains all different
							 * external identifiers for the current work
							 * */
							if (nullSafeCheckForWorkExternalIdentifier(workExternalIdentifier)) {
								WorkExternalIdentifierType type = workExternalIdentifier
										.getWorkExternalIdentifierType();
								if (!allExternalIdentifiers.containsKey(type)) {
									List<String> content = new ArrayList<String>();
									content.add(workExternalIdentifier
											.getWorkExternalIdentifierId()
											.getContent());
									allExternalIdentifiers.put(type, content);
								} else {
									allExternalIdentifiers
											.get(type)
											.add(workExternalIdentifier
													.getWorkExternalIdentifierId()
													.getContent());
								}
							}

						}
					}

					if (orcidWork.getWorkTitle() != null) {
						Title workMainTitle = orcidWork.getWorkTitle()
								.getTitle();
						Subtitle worksubTitle = orcidWork.getWorkTitle()
								.getSubtitle();
						TranslatedTitle translatedTitle = orcidWork
								.getWorkTitle().getTranslatedTitle();
						if (workMainTitle != null
								&& !StringUtils.isBlank(workMainTitle
										.getContent())) {
							workTitles.add(workMainTitle.getContent());
						}

						if (worksubTitle != null
								&& !StringUtils.isBlank(worksubTitle
										.getContent())) {
							workTitles.add(worksubTitle.getContent());
						}

						if (translatedTitle != null
								&& !StringUtils.isBlank(translatedTitle
										.getContent())) {
							workTitles.add(translatedTitle.getContent());
						}
					}
				}

				profileIndexDocument.setWorkTitles(workTitles);

				// Set the list of external identifiers to the document list
				addExternalIdentifiersToIndexDocument(profileIndexDocument,
						allExternalIdentifiers);
			}

			List<OrcidGrant> orcidGrants = filteredProfile
					.retrieveOrcidGrants() != null ? filteredProfile
					.retrieveOrcidGrants().getOrcidGrant() : null;
			if (orcidGrants != null) {
				List<String> grantNumbers = new ArrayList<String>();
				for (OrcidGrant orcidGrant : orcidGrants) {
					if (orcidGrant.getGrantNumber() != null
							&& !StringUtils.isBlank(orcidGrant.getGrantNumber()
									.getContent())) {
						grantNumbers.add(orcidGrant.getGrantNumber()
								.getContent());
					}
				}

				profileIndexDocument.setGrantNumbers(grantNumbers);
			}

			List<OrcidPatent> orcidPatents = filteredProfile
					.retrieveOrcidPatents() != null ? filteredProfile
					.retrieveOrcidPatents().getOrcidPatent() : null;
			if (orcidPatents != null) {
				List<String> patentNumbers = new ArrayList<String>();
				for (OrcidPatent orcidPatent : orcidPatents) {
					if (orcidPatent.getPatentNumber() != null
							&& !StringUtils.isBlank(orcidPatent
									.getPatentNumber().getContent())) {
						patentNumbers.add(orcidPatent.getPatentNumber()
								.getContent());
					}
				}

				profileIndexDocument.setPatentNumbers(patentNumbers);
			}
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

	private boolean nullSafeCheckForWorkExternalIdentifier(
			WorkExternalIdentifier workExternalIdentifier) {
		return workExternalIdentifier.getWorkExternalIdentifierId() != null
				&& !StringUtils.isBlank(workExternalIdentifier
						.getWorkExternalIdentifierId().getContent());
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
	private void addExternalIdentifiersToIndexDocument(
			OrcidSolrDocument profileIndexDocument,
			Map<WorkExternalIdentifierType, List<String>> externalIdentifiers) {
		initializeExternalIdentifiersOnIdexDocument(profileIndexDocument);
		Iterator<Entry<WorkExternalIdentifierType, List<String>>> it = externalIdentifiers
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<WorkExternalIdentifierType, List<String>> entry = (Map.Entry<WorkExternalIdentifierType, List<String>>) it
					.next();
			if (entry.getKey() != null && entry.getValue() != null
					&& !entry.getValue().isEmpty()) {
				switch (entry.getKey()) {
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
				case DOI:
					profileIndexDocument.setDigitalObjectIds(entry.getValue());
					break;
				case EID:
					profileIndexDocument.setEid(entry.getValue());
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
					profileIndexDocument.setOtherIdentifierType(entry
							.getValue());
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
				case SSRN:
					profileIndexDocument.setSsrn(entry.getValue());
					break;
				case XBL:
					profileIndexDocument.setZbl(entry.getValue());
					break;
				}
			}
		}
	}

	private void initializeExternalIdentifiersOnIdexDocument(
			OrcidSolrDocument profileIndexDocument) {
		if (profileIndexDocument.getArxiv() == null)
			profileIndexDocument.setArxiv(new ArrayList<String>());
		if (profileIndexDocument.getAsin() == null)
			profileIndexDocument.setAsin(new ArrayList<String>());
		if (profileIndexDocument.getAsintld() == null)
			profileIndexDocument.setAsintld(new ArrayList<String>());
		if (profileIndexDocument.getBibcode() == null)
			profileIndexDocument.setBibcode(new ArrayList<String>());
		if (profileIndexDocument.getDigitalObjectIds() == null)
			profileIndexDocument.setDigitalObjectIds(new ArrayList<String>());
		if (profileIndexDocument.getEid() == null)
			profileIndexDocument.setEid(new ArrayList<String>());
		if (profileIndexDocument.getIsbn() == null)
			profileIndexDocument.setIsbn(new ArrayList<String>());
		if (profileIndexDocument.getIssn() == null)
			profileIndexDocument.setIssn(new ArrayList<String>());
		if (profileIndexDocument.getJfm() == null)
			profileIndexDocument.setJfm(new ArrayList<String>());
		if (profileIndexDocument.getJstor() == null)
			profileIndexDocument.setJstor(new ArrayList<String>());
		if (profileIndexDocument.getLccn() == null)
			profileIndexDocument.setLccn(new ArrayList<String>());
		if (profileIndexDocument.getMr() == null)
			profileIndexDocument.setMr(new ArrayList<String>());
		if (profileIndexDocument.getOclc() == null)
			profileIndexDocument.setOclc(new ArrayList<String>());
		if (profileIndexDocument.getOl() == null)
			profileIndexDocument.setOl(new ArrayList<String>());
		if (profileIndexDocument.getOsti() == null)
			profileIndexDocument.setOsti(new ArrayList<String>());
		if (profileIndexDocument.getOtherIdentifierType() == null)
			profileIndexDocument
					.setOtherIdentifierType(new ArrayList<String>());
		if (profileIndexDocument.getPmc() == null)
			profileIndexDocument.setPmc(new ArrayList<String>());
		if (profileIndexDocument.getPmid() == null)
			profileIndexDocument.setPmid(new ArrayList<String>());
		if (profileIndexDocument.getRfc() == null)
			profileIndexDocument.setRfc(new ArrayList<String>());
		if (profileIndexDocument.getSsrn() == null)
			profileIndexDocument.setSsrn(new ArrayList<String>());
		if (profileIndexDocument.getZbl() == null)
			profileIndexDocument.setZbl(new ArrayList<String>());

	}
}
