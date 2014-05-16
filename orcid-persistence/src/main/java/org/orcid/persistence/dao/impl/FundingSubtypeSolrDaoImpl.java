/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.persistence.dao.FundingSubTypeSolrDao;
import org.orcid.persistence.solr.entities.OrgDefinedFundingTypeSolrDocument;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class FundingSubtypeSolrDaoImpl implements FundingSubTypeSolrDao {

    @Resource(name = "orgFundingSubTypeSolrServer")
    private SolrServer solrServer;

    @Resource(name = "orgFundingSubTypeSolrServerReadOnly")
    private SolrServer solrServerReadOnly;

    @Override
    public void persist(OrgDefinedFundingTypeSolrDocument fundingType) {
        try {
            solrServer.addBean(fundingType);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting funding type to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting funding type to SOLR", ioe);
        }
    }

    @Override
    public List<OrgDefinedFundingTypeSolrDocument> getFundingTypes(String searchTerm, int firstResult, int maxResult) {
        SolrQuery query = new SolrQuery();
        query.setQuery(
                "{!edismax qf='org-defined-funding-type^50.0 text^1.0' pf='org-defined-funding-type^50.0' mm=1 sort='score desc'}"
                        + searchTerm + "*").setFields("*");
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            return queryResponse.getBeans(OrgDefinedFundingTypeSolrDocument.class);
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to search for orgs, with search term {0}", new Object[] { searchTerm });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }
    }

}
