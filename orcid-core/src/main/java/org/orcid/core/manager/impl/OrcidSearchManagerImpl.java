package org.orcid.core.manager.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.utils.solr.entities.OrcidSolrResults;
import org.springframework.beans.factory.annotation.Value;

public class OrcidSearchManagerImpl implements OrcidSearchManager {

    @Value("${org.orcid.core.public_caching_source:SOLR}")
    private String cachingSource;

    @Resource
    private SolrDao solrDao;
    
    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Override
    public Search findOrcidIds(Map<String, List<String>> queryParameters) {
        Search search = new Search();
        OrcidSolrResults orcidSolrResults = solrDao.findByDocumentCriteria(queryParameters);
        if (orcidSolrResults != null && orcidSolrResults.getResults() != null) {
            List<Result> orcidIdList = orcidSolrResults.getResults().stream().map(r -> {
                try {
                    orcidSecurityManager.checkProfile(r.getOrcid());
                    Result result = new Result();
                    result.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(r.getOrcid()));
                    return result;
                } catch(Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            search.getResults().addAll(orcidIdList);
            search.setNumFound(orcidSolrResults.getNumFound());
        } else {
            search.setNumFound(0L);
        }                                
        return search;
    }

}
