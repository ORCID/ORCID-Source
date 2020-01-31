package org.orcid.core.manager.v3.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.solr.CSVSolrClient;
import org.orcid.core.solr.OrcidSolrProfileClient;
import org.orcid.jaxb.model.v3.release.search.ExpandedResult;
import org.orcid.jaxb.model.v3.release.search.ExpandedSearch;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidSearchManagerImpl.class);

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private OrcidSolrProfileClient orcidSolrProfileClient;

    @Resource
    private CSVSolrClient csvSolrClient;

    @Override
    public Search findOrcidIds(Map<String, List<String>> queryParameters) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = orcidSolrProfileClient.findByDocumentCriteria(queryParameters);
        setSearchResults(orcidSolrResults, search);
        return search;
    }

    @Override
    public Search findOrcidsByQuery(String query, Integer start, Integer rows) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = orcidSolrProfileClient.findByDocumentCriteria(query, start, rows);
        setSearchResults(orcidSolrResults, search);
        return search;
    }

    private void setSearchResults(OrcidSolrResults solrResults, Search searchResults) {
        if (solrResults != null && solrResults.getResults() != null) {
            searchResults.setNumFound(solrResults.getNumFound());
            solrResults.getResults().stream().forEach(r -> {
                try {
                    orcidSecurityManager.checkProfile(r.getOrcid());
                    Result result = new Result();
                    result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(r.getOrcid()));
                    searchResults.getResults().add(result);
                } catch (OrcidNoResultException onre) {
                    LOGGER.error("ORCID id found in SOLR but not in the DB: " + r.getOrcid());
                } catch (Exception e) {
                    LOGGER.error("Exception for ORCID " + r.getOrcid(), e);
                }
            });
        } else {
            searchResults.setNumFound(0L);
        }
    }

    @Override
    public String findOrcidIdsAsCSV(Map<String, List<String>> solrParams) {
        try {
            return csvSolrClient.findCSVByDocumentCriteria(solrParams);
        } catch (ClientProtocolException e) {
            throw new ApplicationException(e);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public ExpandedSearch expandedSearch(Map<String, List<String>> solrParams) {
        ExpandedSearch search = new ExpandedSearch();
        OrcidSolrResults orcidSolrResults = orcidSolrProfileClient.findExpandedByDocumentCriteria(solrParams);
        setExpandedSearchResults(orcidSolrResults, search);
        return search;
    }
    
    private void setExpandedSearchResults(OrcidSolrResults solrResults, ExpandedSearch searchResults) {
        if (solrResults != null && solrResults.getResults() != null) {
            searchResults.setNumFound(solrResults.getNumFound());
            solrResults.getResults().stream().forEach(r -> {
                try {
                    orcidSecurityManager.checkProfile(r.getOrcid());
                    searchResults.getResults().add(getExpandedResult(r));
                } catch (OrcidNoResultException onre) {
                    LOGGER.error("ORCID id found in SOLR but not in the DB: " + r.getOrcid());
                } catch (Exception e) {
                    LOGGER.error("Exception for ORCID " + r.getOrcid(), e);
                }
            });
        } else {
            searchResults.setNumFound(0L);
        }
    }

    private ExpandedResult getExpandedResult(OrcidSolrResult solrResult) {
        ExpandedResult result = new ExpandedResult();
        result.setOrcidId(solrResult.getOrcid());
        result.setGivenNames(solrResult.getGivenNames());
        result.setFamilyNames(solrResult.getFamilyName());
        result.setCreditName(solrResult.getCreditName());
        result.setEmail(solrResult.getEmail());
        
        if (solrResult.getOtherNames() != null) {
            result.setOtherNames(solrResult.getOtherNames().toArray(new String[0]));
        }
        
        if (solrResult.getInstitutionAffiliationNames() != null) { 
            result.setInstitutionNames(solrResult.getInstitutionAffiliationNames().toArray(new String[0]));
        }
        
        return result;
    }
    
}