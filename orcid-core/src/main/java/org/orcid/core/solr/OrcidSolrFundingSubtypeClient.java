package org.orcid.core.solr;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.utils.solr.entities.OrgDefinedFundingTypeSolrDocument;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrFundingSubtypeClient {
    @Resource(name = "solrReadOnlyFundingSubTypeClient")
    private SolrClient solrReadOnlyFundingSubTypeClient;

    public List<OrgDefinedFundingTypeSolrDocument> getFundingTypes(String searchTerm, int firstResult, int maxResult) {
        SolrQuery query = new SolrQuery();
        query.setQuery("{!edismax qf='org-defined-funding-type^50.0 text^1.0' pf='org-defined-funding-type^50.0' mm=1 sort='score desc'}" + searchTerm + "*")
                .setFields("*");
        try {
            QueryResponse queryResponse = solrReadOnlyFundingSubTypeClient.query(query);
            return queryResponse.getBeans(OrgDefinedFundingTypeSolrDocument.class);
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }
}
