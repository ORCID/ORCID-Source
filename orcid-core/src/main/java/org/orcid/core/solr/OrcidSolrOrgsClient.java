package org.orcid.core.solr;

import static org.orcid.core.solr.SolrConstants.ORG_DISAMBIGUATED_ID;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrOrgsClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidSolrOrgsClient.class);
    
    @Resource(name = "solrReadOnlyOrgsClient")
    private SolrClient solrReadOnlyOrgsClient;

    @Value("${org.orcid.core.orgs.query:(org-disambiguated-name:\"%s\")^100.0  (org-disambiguated-name:%s*)^10.0}" )
    private String SOLR_ORGS_QUERY;
    
    private static final String SOLR_SELF_SERVICE_ORGS_QUERY = "(org-disambiguated-id-from-source:%s)^50.0 (org-disambiguated-name%s)^50.0 (org-disambiguated-name-string:%s)^25.0";

    private static final String SOLR_ORG_BY_ROR_ID_QUERY = "org-disambiguated-id-from-source:%s";
    
    private static final String ORG_NAMES_HIGHLIGHT_DELIMITATOR ="::";

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

   public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean fundersOnly, boolean withNamesHighlight) {
        StringBuilder queryString = new StringBuilder(SOLR_ORGS_QUERY.replace("%s", searchTerm));
        if (fundersOnly) {
            queryString.append(" AND is-funding-org:true");
        } 

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString.toString());
        query.addOrUpdateSort("score", ORDER.desc);
        query.addOrUpdateSort("org-disambiguated-popularity", ORDER.desc);
        
        // Set the preserveMulti parameter
        query.setParam("preserveMulti", "true");
        query.setParam("lowercaseOperators", "true");
        
        if(fundersOnly) {
            query.addFilterQuery(String.format("(%s:(%s OR %s))", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "ROR", "FUNDREF"));
        } else {
            query.addFilterQuery(String.format("(%s:(%s))", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "ROR"));
        }
        
        if(withNamesHighlight) {
            query.setHighlight(withNamesHighlight);
            query.addHighlightField(SolrConstants.ORG_NAMES);
            query.setHighlightSnippets(maxResult);
            query.setHighlightSimplePost(ORG_NAMES_HIGHLIGHT_DELIMITATOR);
            query.setHighlightSimplePre(ORG_NAMES_HIGHLIGHT_DELIMITATOR);
        }
        
        LOGGER.debug("SOLR Query: " + query.toQueryString());

        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            List<OrgDisambiguatedSolrDocument> orgs = queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
            // Get the highlight results
            if(withNamesHighlight) {
                List<OrgDisambiguatedSolrDocument> orgsNamesHighlighted = new ArrayList<OrgDisambiguatedSolrDocument>();
                Map<String, Map<String, List<String>>> highlightMap = queryResponse.getHighlighting();
                for(OrgDisambiguatedSolrDocument org : orgs) {
                 // Print highlighted snippets
                    if (highlightMap.containsKey(org.getOrgDisambiguatedId())) {
                        Map<String, List<String>> fieldHighlightMap = highlightMap.get(org.getOrgDisambiguatedId());
                        if (fieldHighlightMap.containsKey(SolrConstants.ORG_NAMES)) {
                            List<String> highlights = fieldHighlightMap.get(SolrConstants.ORG_NAMES);
                            OrgDisambiguatedSolrDocument highlightOrg;
                            for (String highlight : highlights) {
                                //strip the highlight delimitator ORG_NAMES_HIGHLIGHT_DELIMITATOR
                                highlightOrg = new OrgDisambiguatedSolrDocument(org);
                                highlightOrg.setOrgDisambiguatedName(highlight.replaceAll(ORG_NAMES_HIGHLIGHT_DELIMITATOR, ""));
                                orgsNamesHighlighted.add(highlightOrg);
                            }
                        }
                    }
                }   
                return orgsNamesHighlighted;
            }
            return orgs;
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
    
    public OrgDisambiguatedSolrDocument getOrgByRorId(String rorId) {
        SolrQuery query = new SolrQuery();
        // Escape the : on the email domain to be able to search in solr
        query.setQuery(SOLR_ORG_BY_ROR_ID_QUERY.replace("%s", rorId.replace(":", "\\:")));
        query.addOrUpdateSort("score", ORDER.desc);        
        try {
            QueryResponse queryResponse = solrReadOnlyOrgsClient.query(query);
            List<OrgDisambiguatedSolrDocument> result = queryResponse.getBeans(OrgDisambiguatedSolrDocument.class); 
            return (result == null || result.isEmpty()) ? null : result.get(0); 
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs by ror id, with ror id {0}", new Object[] { rorId });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }        
    }
}
