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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.springframework.dao.NonTransientDataAccessResourceException;

import schema.constants.SolrConstants;

public class SolrDaoImpl implements SolrDao {

    @Resource
    private SolrServer solrServer;

    @Override
    public void persist(OrcidSolrDocument orcidSolrDocument) {
        try {
            solrServer.addBean(orcidSolrDocument);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting to SOLR", ioe);
        }

    }

    @Override
    public void removeOrcids(List<String> orcids) {
        try {
            solrServer.deleteById(orcids);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error deleting orcids from SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("Error deleting orcids from SOLR Server", ioe);
        }
    }

    @Override
    public OrcidSolrResult findByOrcid(String orcid) {
        OrcidSolrResult orcidSolrResult = null;
        SolrQuery query = new SolrQuery();
        query.setQuery(SolrConstants.ORCID + ":" + orcid).setFields("score");
        ;
        try {
            QueryResponse queryResponse = solrServer.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                SolrDocument solrDocument = queryResponse.getResults().get(0);
                orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get("score"));
                orcidSolrResult.setOrcid((String) solrDocument.get("orcid"));
            }
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve orcid {0}", new Object[] { orcid });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }

        return orcidSolrResult;
    }

    @Override
    public List<OrcidSolrResult> findByDocumentCriteria(String queryString) {
        List<OrcidSolrResult> orcidSolrResults = new ArrayList<OrcidSolrResult>();
        SolrQuery query = new SolrQuery(queryString).setFields("score");
        try {
            QueryResponse queryResponse = solrServer.query(query);
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get("score"));
                orcidSolrResult.setOrcid((String) solrDocument.get("orcid"));
                orcidSolrResults.add(orcidSolrResult);
            }

        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }

        return orcidSolrResults;
    }

    @Override
    public List<OrcidSolrResult> findByDocumentCriteria(Map<String, List<String>> queryMap) {
        List<OrcidSolrResult> orcidSolrResults = new ArrayList<OrcidSolrResult>();
        SolrQuery solrQuery = new SolrQuery();
        for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
            String queryKey = entry.getKey();
            List<String> queryVals = entry.getValue();
            solrQuery.add(queryKey, queryVals.get(0));
        }

        try {
            QueryResponse queryResponse = solrServer.query(solrQuery.setFields("score"));
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get("score"));
                orcidSolrResult.setOrcid((String) solrDocument.get("orcid"));
                orcidSolrResults.add(orcidSolrResult);
            }

        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }

        return orcidSolrResults;
    }

}
