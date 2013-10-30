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

import static schema.constants.SolrConstants.ORG_DISAMBIGUATED_ID;

import java.io.IOException;
import java.text.MessageFormat;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
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
    public OrgDisambiguatedSolrDocument findById(Long id) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORG_DISAMBIGUATED_ID + ":" + id).setFields("*");
        ;
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

}
