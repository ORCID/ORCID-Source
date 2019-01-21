package org.orcid.core.manager.v3.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidSearchException;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.RelevancyScore;
import org.orcid.jaxb.model.v3.rc2.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.rc2.search.Result;
import org.orcid.jaxb.model.v3.rc2.search.Search;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.utils.DateUtils;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    @Value("${org.orcid.core.public_caching_source:SOLR}")
    private String cachingSource;

    @Resource
    private SolrDao solrDao;
    
    @Resource
    private ProfileDao profileDaoReadOnly;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    protected Jpa2JaxbAdapter jpaJaxbAdapter;
    
    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    private static String SOLR = "SOLR";

    private static String DB = "DB";

    private OrcidProfileCacheManager orcidProfileCacheManager;
    
    @Required
    public void setOrcidProfileCacheManager(OrcidProfileCacheManager orcidProfileCacheManager) {
        this.orcidProfileCacheManager = orcidProfileCacheManager;
    }

    @Override
    public Search findOrcidSearchResultsById(String orcid) {

        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
        if (indexedOrcid != null) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfile(Arrays.asList(indexedOrcid));
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        searchResults.setNumFound(1);
        return orcidMessage;
    }

    private Search buildSearchResultsFromPublicProfile(List<OrcidSolrResult> solrResults) {
        Search searchResults = new Search();
        Iterator<OrcidSolrResult> it = solrResults.iterator();
        while (it.hasNext()) {
            OrcidSolrResult solrResult = it.next();
            OrcidIdentifier oi = new OrcidIdentifier(solrResult.getOrcid());
            Result r = new Result();
            r.setOrcidIdentifier(oi);
            searchResults.getResults().add(r);
        }
        return searchResults;
    }

    @Override
    public Search findPublicProfileById(String orcid) {
        OrcidMessage om = null;
        try {
            if (cachingSource.equals(DB)) {
                OrcidProfile orcidProfile = orcidProfileCacheManager.retrievePublic(orcid);
                orcidProfile.setOrcidInternal(null);
                om = new OrcidMessage(orcidProfile);
            } else {
                try (Reader reader = solrDao.findByOrcidAsReader(orcid)) {
                    if (reader != null) {
                        BufferedReader br = new BufferedReader(reader);
                        om = OrcidMessage.unmarshall(br);
                    }
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
    public Search findOrcidsByQuery(String query) {
        return findOrcidsByQuery(query, null, null);
    }

    @Override
    public Search findOrcidsByQuery(String query, Integer start, Integer rows) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query, start, rows);
        searchResults.setNumFound(orcidSolrResults.getNumFound());
        List<OrcidSolrResult> indexedOrcids = orcidSolrResults.getResults();
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {
            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfile(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);
        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;
    }

    @Override
    public Search findOrcidsByQuery(Map<String, List<String>> query) {
        OrcidMessage orcidMessage = new OrcidMessage();
        OrcidSearchResults searchResults = new OrcidSearchResults();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query);
        searchResults.setNumFound(orcidSolrResults.getNumFound());
        List<OrcidSolrResult> indexedOrcids = orcidSolrResults.getResults();
        if (indexedOrcids != null && !indexedOrcids.isEmpty()) {

            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfile(indexedOrcids);
            searchResults.getOrcidSearchResult().addAll(orcidSearchResults);

        }
        orcidMessage.setOrcidSearchResults(searchResults);
        return orcidMessage;

    }

    @Override
    public Search findOrcidIds(Map<String, List<String>> queryParameters) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(queryParameters);
        if (orcidSolrResults != null && orcidSolrResults.getResults() != null) {
            List<Result> orcidIdList = orcidSolrResults.getResults().stream().map(r -> {
                Result result = new Result();
                result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(r.getOrcid()));
                return result;
            }).collect(Collectors.toList());
            search.getResults().addAll(orcidIdList);
            search.setNumFound(orcidSolrResults.getNumFound());
        } else {
            search.setNumFound(0L);
        }                                
        return search;
    }

}
