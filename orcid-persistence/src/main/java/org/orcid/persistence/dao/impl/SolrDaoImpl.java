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

import static schema.constants.SolrConstants.SCORE;
import static schema.constants.SolrConstants.ORCID;
import static schema.constants.SolrConstants.PUBLIC_PROFILE;

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
import org.orcid.persistence.solr.entities.OrcidSolrResults;
import org.springframework.dao.NonTransientDataAccessResourceException;

public class SolrDaoImpl implements SolrDao {

    @Resource(name = "solrServer")
    private SolrServer solrServer;

    @Resource(name = "solrServerReadOnly")
    private SolrServer solrServerReadOnly;

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
        query.setQuery(ORCID + ":" + orcid).setFields(SCORE, ORCID, PUBLIC_PROFILE);
        ;
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                SolrDocument solrDocument = queryResponse.getResults().get(0);
                orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get(SCORE));
                orcidSolrResult.setOrcid((String) solrDocument.get(ORCID));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue(PUBLIC_PROFILE));
            }
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve orcid {0}", new Object[] { orcid });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }

        return orcidSolrResult;
    }

    @Override
    public OrcidSolrResults findByDocumentCriteria(String queryString, Integer start, Integer rows) {
        SolrQuery query = new SolrQuery(queryString).setFields(SCORE, ORCID, PUBLIC_PROFILE);
        if (start != null)
            query.setStart(start);
        if (rows != null)
            query.setRows(rows);
        return querySolr(query);
    }

    @Override
    public OrcidSolrResults findByDocumentCriteria(Map<String, List<String>> queryMap) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        SolrQuery solrQuery = new SolrQuery();
        for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
            String queryKey = entry.getKey();
            List<String> queryVals = entry.getValue();
            solrQuery.add(queryKey, queryVals.get(0));
        }
        solrQuery.setFields(SCORE, ORCID, PUBLIC_PROFILE);
        return querySolr(solrQuery);
    }

    private OrcidSolrResults querySolr(SolrQuery query) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.getFieldValue(SCORE));
                orcidSolrResult.setOrcid((String) solrDocument.getFieldValue(ORCID));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue(PUBLIC_PROFILE));
                orcidSolrResultsList.add(orcidSolrResult);
            }
            orcidSolrResults.setNumFound(queryResponse.getResults().getNumFound());

        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }
        return orcidSolrResults;
    }

}
