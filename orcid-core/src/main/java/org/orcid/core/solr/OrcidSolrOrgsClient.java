package org.orcid.core.solr;

import static org.orcid.utils.solr.entities.SolrConstants.ORG_DISAMBIGUATED_ID;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrOrgsClient {

    private static final String SOLR_ORGS_QUERY = "(org-disambiguated-name:%s)^100.0 (org-disambiguated-name-string:%s)^50.0 (org-disambiguated-country:%s)^5.0 (org-disambiguated-city:%s)^5.0 (org-names:%s)^1.0 ";
    private static final String SOLR_SELF_SERVICE_ORGS_QUERY = "(org-disambiguated-id-from-source:%s)^50.0 (org-disambiguated-name%s)^50.0 (org-disambiguated-name-string:%s)^25.0 (org-names:%s)^1.0";

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
        StringBuilder queryString = new StringBuilder(SOLR_ORGS_QUERY.replace("%s", searchTerm + '*'));
        if (fundersOnly) {
            queryString.append(" AND is-funding-org:true");
        }

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString.toString());
        query.addOrUpdateSort("score", ORDER.desc);
        if (promoteChosenOrgs) {
            query.addOrUpdateSort("org-chosen-by-member", ORDER.desc);
        }
        query.addOrUpdateSort("org-disambiguated-popularity", ORDER.desc);

        query.addFilterQuery(String.format("(%s:(%s OR %s OR %s)) OR (%s:%s AND %s:%s)", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "ROR", "RINGGOLD", "FUNDREF",
                SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "LEI", SolrConstants.ORG_CHOSEN_BY_MEMBER, true));

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
        query.setQuery(SOLR_SELF_SERVICE_ORGS_QUERY.replace("%s", searchTerm));
        query.addOrUpdateSort("score", ORDER.desc);
        query.addOrUpdateSort("org-disambiguated-popularity", ORDER.desc);
        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            return queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs for self-service, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }
}
