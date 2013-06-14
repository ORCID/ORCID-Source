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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidSearchException;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidGrants;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidPatents;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.RelevancyScore;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.orcid.persistence.solr.entities.OrcidSolrResults;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    @Resource
    private SolrDao solrDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    public SolrDao getSolrDao() {
        return solrDao;
    }

    public void setSolrDao(SolrDao solrDao) {
        this.solrDao = solrDao;
    }

    public OrcidProfileManager getOrcidProfileManager() {
        return orcidProfileManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @Override
    public OrcidMessage findOrcidSearchResultsById(String orcid) {
        return findOrcidSearchResultsById(orcid, true);
    }

    @Override
    public OrcidMessage findOrcidSearchResultsById(String orcid, boolean useDb) {

        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
        if (indexedOrcid != null) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromDb(Arrays.asList(indexedOrcid));
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        searchResults.setNumFound(1);
        return orcidMessage;
    }

    private List<OrcidSearchResult> buildSearchResultsFromDb(List<OrcidSolrResult> solrResults) {

        List<OrcidSearchResult> orcidSearchResults = new ArrayList<OrcidSearchResult>();
        for (OrcidSolrResult solrResult : solrResults) {
            OrcidProfile orcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(solrResult.getOrcid());
            if (orcidProfile != null) {

                OrcidSearchResult orcidSearchResult = new OrcidSearchResult();
                RelevancyScore relevancyScore = new RelevancyScore();
                relevancyScore.setValue(solrResult.getRelevancyScore());
                orcidSearchResult.setRelevancyScore(relevancyScore);

                OrcidWorks orcidWorksTitlesOnly = new OrcidWorks();
                OrcidWorks fullOrcidWorks = orcidProfile.retrieveOrcidWorks();
                if (fullOrcidWorks != null && !fullOrcidWorks.getOrcidWork().isEmpty()) {

                    for (OrcidWork fullOrcidWork : fullOrcidWorks.getOrcidWork()) {
                        OrcidWork orcidWorkSubset = new OrcidWork();
                        orcidWorkSubset.setVisibility(fullOrcidWork.getVisibility());
                        orcidWorkSubset.setWorkTitle(fullOrcidWork.getWorkTitle());
                        orcidWorkSubset.setWorkExternalIdentifiers(fullOrcidWork.getWorkExternalIdentifiers());
                        orcidWorksTitlesOnly.getOrcidWork().add(orcidWorkSubset);
                    }
                }

                OrcidPatents reducedPatents = new OrcidPatents();
                OrcidPatents fullOrcidPatents = orcidProfile.retrieveOrcidPatents();
                if (fullOrcidPatents != null && !fullOrcidPatents.getOrcidPatent().isEmpty()) {

                    for (OrcidPatent fullOrcidPatent : fullOrcidPatents.getOrcidPatent()) {
                        OrcidPatent reducedPatent = new OrcidPatent();
                        reducedPatent.setVisibility(fullOrcidPatent.getVisibility());
                        reducedPatent.setPatentNumber(fullOrcidPatent.getPatentNumber());
                        reducedPatent.setShortDescription(fullOrcidPatent.getShortDescription());
                        reducedPatents.getOrcidPatent().add(reducedPatent);
                    }
                }

                OrcidGrants reducedGrants = new OrcidGrants();
                OrcidGrants fullOrcidGrants = orcidProfile.retrieveOrcidGrants();
                if (fullOrcidGrants != null && !fullOrcidGrants.getOrcidGrant().isEmpty()) {

                    for (OrcidGrant fullOrcidGrant : fullOrcidGrants.getOrcidGrant()) {
                        OrcidGrant reducedGrant = new OrcidGrant();
                        reducedGrant.setVisibility(fullOrcidGrant.getVisibility());
                        reducedGrant.setGrantNumber(fullOrcidGrant.getGrantNumber());
                        reducedGrant.setShortDescription(fullOrcidGrant.getShortDescription());
                        reducedGrants.getOrcidGrant().add(reducedGrant);
                    }
                }
                orcidProfile.setOrcidWorks(orcidWorksTitlesOnly);
                orcidProfile.setOrcidPatents(reducedPatents);
                orcidProfile.setOrcidGrants(reducedGrants);

                orcidSearchResult.setOrcidProfile(orcidProfile);

                orcidSearchResults.add(orcidSearchResult);
            }

        }
        return orcidSearchResults;
    }

    private List<OrcidSearchResult> buildSearchResultsFromSolr(List<OrcidSolrResult> solrResults) {
        List<OrcidSearchResult> orcidSearchResults = new ArrayList<OrcidSearchResult>();
        for (OrcidSolrResult solrResult : solrResults) {
            OrcidSearchResult orcidSearchResult = new OrcidSearchResult();
            RelevancyScore relevancyScore = new RelevancyScore();
            relevancyScore.setValue(solrResult.getRelevancyScore());
            orcidSearchResult.setRelevancyScore(relevancyScore);

            OrcidProfile orcidProfile = new OrcidProfile();
            orcidProfile.setOrcid(solrResult.getOrcid());
            OrcidBio orcidBio = new OrcidBio();
            orcidProfile.setOrcidBio(orcidBio);
            String email = solrResult.getEmail();
            if (email != null) {
                ContactDetails contactDetails = new ContactDetails();
                orcidBio.setContactDetails(contactDetails);
                contactDetails.addOrReplacePrimaryEmail(new Email(email));
            }
            PersonalDetails personalDetails = new PersonalDetails();
            orcidBio.setPersonalDetails(personalDetails);
            personalDetails.setGivenNames(new GivenNames(solrResult.getGivenNames()));
            personalDetails.setFamilyName(new FamilyName(solrResult.getFamilyName()));
            personalDetails.setCreditName(new CreditName(solrResult.getCreditName()));
            OtherNames otherNames = new OtherNames();
            personalDetails.setOtherNames(otherNames);
            Collection<String> otherNameStrings = solrResult.getOtherNames();
            if (otherNameStrings != null) {
                for (String otherNameString : otherNameStrings) {
                    otherNames.addOtherName(otherNameString);
                }
            }
            List<Affiliation> affiliations = orcidBio.getAffiliations();
            Collection<String> currentPrimaryInstitutionAffiliationNames = solrResult.getCurrentPrimaryInstitutionAffiliationNames();
            if (currentPrimaryInstitutionAffiliationNames != null) {
                for (String currentPrimaryInstitutionsAffiliationName : currentPrimaryInstitutionAffiliationNames) {
                    Affiliation affiliation = new Affiliation();
                    affiliation.setAffiliationName(currentPrimaryInstitutionsAffiliationName);
                    affiliation.setAffiliationType(AffiliationType.CURRENT_PRIMARY_INSTITUTION);
                    affiliations.add(affiliation);
                }
            }
            Collection<String> currentInstitutionAffiliationNames = solrResult.getCurrentInstitutionAffiliationNames();
            if (currentInstitutionAffiliationNames != null) {
                for (String currentInstitutionsAffiliationName : currentInstitutionAffiliationNames) {
                    Affiliation affiliation = new Affiliation();
                    affiliation.setAffiliationName(currentInstitutionsAffiliationName);
                    affiliation.setAffiliationType(AffiliationType.CURRENT_INSTITUTION);
                    affiliations.add(affiliation);
                }
            }
            Collection<String> pastInstitutionAffiliationNames = solrResult.getPastInstitutionAffiliationNames();
            if (pastInstitutionAffiliationNames != null) {
                for (String pastInstitutionsAffiliationName : pastInstitutionAffiliationNames) {
                    Affiliation affiliation = new Affiliation();
                    affiliation.setAffiliationName(pastInstitutionsAffiliationName);
                    affiliation.setAffiliationType(AffiliationType.PAST_INSTITUTION);
                    affiliations.add(affiliation);
                }
            }

            orcidSearchResult.setOrcidProfile(orcidProfile);
            orcidSearchResults.add(orcidSearchResult);
        }
        return orcidSearchResults;
    }

    @Override
    public OrcidMessage findPublicProfileById(String orcid) {
        try {
            OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
            if (indexedOrcid == null) {
                return null;
            }
            String publicProfileMessage = indexedOrcid.getPublicProfileMessage();
            if (publicProfileMessage == null) {
                throw new OrcidSearchException("Found document in index, but no public profile in document for orcid=" + orcid);
            }
            return OrcidMessage.unmarshall(publicProfileMessage);
        } catch (NonTransientDataAccessResourceException e) {
            throw new OrcidSearchException("Error searching by id", e);
        }
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query) {
        return findOrcidsByQuery(query, null, null);
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query, boolean useDb) {
        return findOrcidsByQuery(query, null, null, useDb);
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query, Integer start, Integer rows) {
        return findOrcidsByQuery(query, start, rows, true);
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query, Integer start, Integer rows, boolean useDb) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query, start, rows);
        searchResults.setNumFound(orcidSolrResults.getNumFound());
        List<OrcidSolrResult> indexedOrcids = orcidSolrResults.getResults();
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {

            List<OrcidSearchResult> orcidSearchResults = useDb ? buildSearchResultsFromDb(indexedOrcids) : buildSearchResultsFromSolr(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;
    }

    @Override
    public OrcidMessage findOrcidsByQuery(Map<String, List<String>> query) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query);
        searchResults.setNumFound(orcidSolrResults.getNumFound());
        List<OrcidSolrResult> indexedOrcids = orcidSolrResults.getResults();
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromDb(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

}
