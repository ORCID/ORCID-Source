package org.orcid.persistence.dao.impl;

import static org.orcid.utils.solr.entities.SolrConstants.ORG_DISAMBIGUATED_ID;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class OrgDisambiguatedSolrDaoImpl implements OrgDisambiguatedSolrDao {

    @Resource(name = "orgDisambiguatedSolrServer")
    private SolrServer solrServer;

    @Resource(name = "orgDisambiguatedSolrServerReadOnly")
    private SolrServer solrServerReadOnly;

    @Override
    public void persist(OrgDisambiguatedSolrDocument orgDisambiguatedSolrDocument) {
        try {
            solrServer.addBean(orgDisambiguatedSolrDocument);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting org to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting org to SOLR", ioe);
        }
    }

    @Override
    public void remove(Long id) {
        try {
            solrServer.deleteById(String.valueOf(id));
        } catch (SolrServerException | IOException e) {
            throw new NonTransientDataAccessResourceException("Error removing org from SOLR Server", e);
        }
    }

    @Override
    public OrgDisambiguatedSolrDocument findById(Long id) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORG_DISAMBIGUATED_ID + ":" + id).setFields("*");
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                OrgDisambiguatedSolrDocument document = queryResponse.getBeans(OrgDisambiguatedSolrDocument.class).get(0);
                return document;
            }
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve org {0}", new Object[] { id });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
        return null;
    }

    @Override
    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult) {
        return getOrgs(searchTerm, firstResult, maxResult, false);
    }

    @Override
    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean fundersOnly) {
        SolrQuery query = new SolrQuery();
        if (fundersOnly) {
            query.setQuery(
                    "{!edismax qf='org-disambiguated-name^50.0 text^1.0' pf='org-disambiguated-name^50.0' mm=1 sort='score desc, org-disambiguated-popularity desc'}"
                            + searchTerm + "* AND is-funding-org:true").setFields("*");
        } else {
            query.setQuery(
                    "{!edismax qf='org-disambiguated-name^50.0 text^1.0' pf='org-disambiguated-name^50.0' mm=1 sort='score desc, org-disambiguated-popularity desc'}"
                            + searchTerm + "*").setFields("*");
        }
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            return queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }
    
    @Override
    public List<OrgDisambiguatedSolrDocument> getOrgsForSelfService(String searchTerm, int firstResult, int maxResult) {
        SolrQuery query = new SolrQuery();
        query.addFilterQuery(String.format("(%s:(%s OR %s OR %s)) OR (%s:%s AND %s:%s)", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "GRID", "RINGGOLD", "FUNDREF", SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE, "LEI", SolrConstants.ORG_CHOSEN_BY_MEMBER, true));
        query.setQuery("{!edismax qf='org-disambiguated-id-from-source^50.0 org-disambiguated-name^50.0 org-names^1.0' pf='org-disambiguated-name^50.0' mm=1 sort='score desc, org-disambiguated-popularity desc'}"
                + searchTerm + "*").setFields("*");
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            return queryResponse.getBeans(OrgDisambiguatedSolrDocument.class);
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs for self-service, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }
    
}
