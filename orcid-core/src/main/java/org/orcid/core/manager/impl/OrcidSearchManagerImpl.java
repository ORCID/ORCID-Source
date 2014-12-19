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
package org.orcid.core.manager.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidSearchException;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.RelevancyScore;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.orcid.persistence.solr.entities.OrcidSolrResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    @Value("${org.orcid.core.public_caching_source:SOLR}")
    private String cachingSource;

    @Resource
    private SolrDao solrDao;

    @SuppressWarnings("unused")
    private static String SOLR = "SOLR";

    private static String DB = "DB";

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

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

        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
        if (indexedOrcid != null) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfileInSolr(Arrays.asList(indexedOrcid));
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        searchResults.setNumFound(1);
        return orcidMessage;
    }

    private List<OrcidSearchResult> buildSearchResultsFromPublicProfileInSolr(List<OrcidSolrResult> solrResults) {

        List<OrcidSearchResult> orcidSearchResults = new ArrayList<OrcidSearchResult>();
        for (OrcidSolrResult solrResult : solrResults) {
            OrcidProfile orcidProfile = null;
            String orcidMessageString = solrResult.getPublicProfileMessage();
            if (orcidMessageString == null) {
                // Fall back to DB
                orcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(solrResult.getOrcid());
            } else {
                OrcidMessage orcidMessage = OrcidMessage.unmarshall(orcidMessageString);
                orcidProfile = orcidMessage.getOrcidProfile();
            }
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

                FundingList reducedFundings = new FundingList();
                FundingList fullOrcidFundings = orcidProfile.retrieveFundings();
                if (fullOrcidFundings != null && !fullOrcidFundings.getFundings().isEmpty()) {

                    for (Funding fullOrcidFunding : fullOrcidFundings.getFundings()) {
                        Funding reducedFunding = new Funding();
                        reducedFunding.setVisibility(fullOrcidFunding.getVisibility());
                        reducedFunding.setDescription(fullOrcidFunding.getDescription());
                        reducedFunding.setTitle(fullOrcidFunding.getTitle());
                        reducedFundings.getFundings().add(reducedFunding);
                    }
                }
                orcidProfile.setOrcidWorks(orcidWorksTitlesOnly);
                orcidProfile.setFundings(reducedFundings);

                orcidSearchResult.setOrcidProfile(orcidProfile);

                orcidSearchResults.add(orcidSearchResult);
            }
        }
        return orcidSearchResults;
    }

    @Override
    public OrcidMessage findPublicProfileById(String orcid) {
        OrcidMessage om = null;
        try {
            if (cachingSource.equals(DB)) {
                OrcidProfile orcidProfile = orcidProfileCacheManager.retrievePublic(orcid);
                orcidProfile.setOrcidInternal(null);
                om = new OrcidMessage();
                om.setOrcidProfile(orcidProfile);
            } else {
                try (Reader reader = new BufferedReader(solrDao.findByOrcidAsReader(orcid))) {
                    om = OrcidMessage.unmarshall(reader);
                }
            }
        } catch (NonTransientDataAccessResourceException e) {
            throw new OrcidSearchException("Error searching by id: " + orcid, e);
        } catch (IOException e) {
            throw new OrcidSearchException("Error closing stream for id: " + orcid, e);
        }
        if (om == null)
            throw new OrcidSearchException("Result is null");
        return om;
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query) {
        return findOrcidsByQuery(query, null, null);
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query, Integer start, Integer rows) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query, start, rows);
        searchResults.setNumFound(orcidSolrResults.getNumFound());
        List<OrcidSolrResult> indexedOrcids = orcidSolrResults.getResults();
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {
            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfileInSolr(indexedOrcids);
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

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfileInSolr(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

}
