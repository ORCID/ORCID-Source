package org.orcid.core.manager.v3.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.search.Result;
import org.orcid.jaxb.model.v3.rc2.search.Search;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    @Resource
    private SolrDao solrDao;
    
    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Override
    public Search findOrcidSearchResultsById(String orcid) {
        Search search = new Search();
        OrcidSolrResult indexedOrcid = solrDao.findByOrcid(orcid);
        if (indexedOrcid != null) {
            search.setNumFound(1L);
            Result result = new Result();
            result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(indexedOrcid.getOrcid()));
            search.getResults().add(result);
        } else {
            search.setNumFound(0L);
        }
        return search;
    }    

    @Override
    public Search findOrcidsByQuery(String query) {
        return findOrcidsByQuery(query, null, null);
    }

    @Override
    public Search findOrcidsByQuery(String query, Integer start, Integer rows) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query, start, rows);
        setSearchResults(orcidSolrResults, search);
        return search;
    }

    @Override
    public Search findOrcidsByQuery(Map<String, List<String>> query) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query);
        setSearchResults(orcidSolrResults, search);
        return search;    
    }

    @Override
    public Search findOrcidIds(Map<String, List<String>> queryParameters) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(queryParameters);
        setSearchResults(orcidSolrResults, search);
        return search;
    }
    
    private void setSearchResults(OrcidSolrResults solrResults, Search searchResults) {
        if(solrResults != null && solrResults.getResults() != null) {
            searchResults.setNumFound(Long.valueOf(solrResults.getResults().size()));
            solrResults.getResults().stream().forEach(r -> {
                Result result = new Result();
                result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(r.getOrcid()));
                searchResults.getResults().add(result);
            });
        } else {
            searchResults.setNumFound(0L);
        }
    }

}
