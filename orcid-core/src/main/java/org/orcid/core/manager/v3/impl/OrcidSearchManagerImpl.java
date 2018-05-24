package org.orcid.core.manager.v3.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.RelevancyScore;
import org.orcid.jaxb.model.v3.rc1.search.Result;
import org.orcid.jaxb.model.v3.rc1.search.Search;
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
    public OrcidMessage findOrcidSearchResultsById(String orcid) {

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

    private List<OrcidSearchResult> buildSearchResultsFromPublicProfile(List<OrcidSolrResult> solrResults) {

        List<OrcidSearchResult> orcidSearchResults = new ArrayList<OrcidSearchResult>();
        for (OrcidSolrResult solrResult : solrResults) {
            OrcidMessage orcidMessage = null;
            String orcid = solrResult.getOrcid();
            
            try {
                orcidSecurityManager.checkProfile(orcid);
            } catch(DeactivatedException | LockedException | OrcidDeprecatedException x) {
                OrcidSearchResult orcidSearchResult = new OrcidSearchResult();
                RelevancyScore relevancyScore = new RelevancyScore();
                relevancyScore.setValue(solrResult.getRelevancyScore());
                orcidSearchResult.setRelevancyScore(relevancyScore);
                OrcidProfile orcidProfile = new OrcidProfile();                
                orcidProfile.setOrcidIdentifier(new OrcidIdentifier(jpaJaxbAdapter.getOrcidIdBase(orcid)));
                OrcidHistory history = new OrcidHistory();
                Date recordLastModified = profileDaoReadOnly.retrieveLastModifiedDate(orcid);
                history.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(recordLastModified)));
                orcidProfile.setOrcidHistory(history);
                orcidSearchResult.setOrcidProfile(orcidProfile);
                orcidSearchResults.add(orcidSearchResult);
                continue;
            }
            
            if (cachingSource.equals(SOLR)) {
                try (Reader reader = solrDao.findByOrcidAsReader(orcid)) {
                    if (reader != null) {
                        BufferedReader br = new BufferedReader(reader);
                        orcidMessage = OrcidMessage.unmarshall(br);
                    }
                } catch (IOException e) {
                    throw new OrcidSearchException("Error closing record stream from solr search results for orcid: " + orcid, e);
                }
            }
            OrcidProfile orcidProfile = null;
            if (orcidMessage == null) {
                // Fall back to DB
                orcidProfile = orcidProfileCacheManager.retrievePublicBio(orcid);
            } else {
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
            List<OrcidSearchResult> orcidSearchResults = buildSearchResultsFromPublicProfile(indexedOrcids);
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
