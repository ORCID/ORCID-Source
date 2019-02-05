package org.orcid.core.manager.v3.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.search.Result;
import org.orcid.jaxb.model.v3.rc2.search.Search;
import org.orcid.persistence.dao.SolrDao;
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
    private SolrDao solrDao;
    
    @Override
    public Search findOrcidIds(Map<String, List<String>> queryParameters) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(queryParameters);
        setSearchResults(orcidSolrResults, search);
        return search;
    }
    
    @Override
    public Search findOrcidsByQuery(String query, Integer start, Integer rows) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(query, start, rows);
        setSearchResults(orcidSolrResults, search);
        return search;
    }

    private void setSearchResults(OrcidSolrResults solrResults, Search searchResults) {
        if(solrResults != null && solrResults.getResults() != null) {
            searchResults.setNumFound(Long.valueOf(solrResults.getResults().size()));
            solrResults.getResults().stream().forEach(r -> {
                try {
                    orcidSecurityManager.checkProfile(r.getOrcid());
                    Result result = new Result();
                    result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(r.getOrcid()));
                    searchResults.getResults().add(result);
                } catch(OrcidNoResultException onre) {
                    LOGGER.error("ORCID id found in SOLR but not in the DB: " + r.getOrcid());
                } catch(Exception e) {
                    LOGGER.error("Exception for ORCID " + r.getOrcid(), e);
                }
            });
        } else {
            searchResults.setNumFound(0L);
        }
    }

}