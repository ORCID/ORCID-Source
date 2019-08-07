package org.orcid.core.solr;

import static org.orcid.utils.solr.entities.SolrConstants.ORG_DISAMBIGUATED_ID;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrOrgsClient {
    
    @Resource(name = "solrReadOnlyOrgsClient")
    private SolrClient solrReadOnlyOrgsClient;
    
    public OrgDisambiguatedSolrDocument findById(Long id) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORG_DISAMBIGUATED_ID + ":" + id).setFields("*");
        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                OrgDisambiguatedSolrDocument document = queryResponse.getBeans(OrgDisambiguatedSolrDocument.class).get(0);
                return document;
            }
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve org {0}", new Object[] { id });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
        return null;
    }

    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean promoteChosenOrgs) {
        return getOrgs(searchTerm, firstResult, maxResult, false, promoteChosenOrgs);
    }

    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean fundersOnly, boolean promoteChosenOrgs) {
        StringBuilder queryString = new StringBuilder("{!edismax qf='org-disambiguated-name^50.0 text^1.0' pf='org-disambiguated-name^50.0' mm=1 ");
        if (promoteChosenOrgs) {
            queryString.append("bq=").append(SolrConstants.ORG_CHOSEN_BY_MEMBER).append(":true ");
        }
        queryString.append("sort='score desc, ");
        queryString.append("org-disambiguated-popularity desc'}");
        queryString.append(searchTerm).append("*");
        
        if (fundersOnly) {
            queryString.append(" AND is-funding-org:true");
        }

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString.toString());
        query.addFilterQuery(String.format("(%s:(%s OR %s OR %s)) OR (%s:%s AND %s:%s)", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "GRID", "RINGGOLD", "FUNDREF", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "LEI", SolrConstants.ORG_CHOSEN_BY_MEMBER, true));
        query.setFields("*");
        
        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            return queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }
    
    public List<OrgDisambiguatedSolrDocument> getOrgsForSelfService(String searchTerm, int firstResult, int maxResult) {
        SolrQuery query = new SolrQuery();
        query.setQuery("{!edismax qf='org-disambiguated-id-from-source^50.0 org-disambiguated-name^50.0 org-names^1.0' pf='org-disambiguated-name^50.0' mm=1 sort='score desc, org-disambiguated-popularity desc'}"
                + searchTerm + "*").setFields("*");
        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            return queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs for self-service, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }    
}
