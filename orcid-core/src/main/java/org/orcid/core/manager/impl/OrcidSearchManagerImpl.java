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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
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
import org.orcid.jaxb.model.message.RelevancyScore;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrResult;

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

        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
        if (indexedOrcid != null) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromDb(Arrays.asList(indexedOrcid));
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
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

    @Override
    public OrcidMessage findOrcidsByQuery(String query) {
        return findOrcidsByQuery(query, null, null);
    }

    @Override
    public OrcidMessage findOrcidsByQuery(String query, Integer start, Integer rows) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        List<OrcidSolrResult> indexedOrcids = solrDao.findByDocumentCriteria(query, start, rows);
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromDb(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

    @Override
    public OrcidMessage findOrcidsByQuery(Map<String, List<String>> query) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        List<OrcidSolrResult> indexedOrcids = solrDao.findByDocumentCriteria(query);
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromDb(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

}
