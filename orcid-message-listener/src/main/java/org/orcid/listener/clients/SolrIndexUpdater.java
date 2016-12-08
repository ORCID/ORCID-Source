/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.listener.clients;

import static org.orcid.utils.solr.entities.SolrConstants.ORCID;
import static org.orcid.utils.solr.entities.SolrConstants.PROFILE_LAST_MODIFIED_DATE;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.listener.converters.OrcidProfileToSolrDocument;
import org.orcid.listener.converters.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexUpdater {

    Logger LOG = LoggerFactory.getLogger(SolrIndexUpdater.class);

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnalbed;
    @Value("${org.orcid.core.indexPublicProfile:false}")
    private boolean indexPublicProfile;
        
    @Resource(name = "solrServer")
    private SolrServer solrServer;

    private OrcidProfileToSolrDocument conv = new OrcidProfileToSolrDocument();
    private OrcidRecordToSolrDocument recordConv;
    
    public SolrIndexUpdater(){
        recordConv = new OrcidRecordToSolrDocument(indexPublicProfile);
    }
    
    public void updateSolrIndex(Record record, String v12profileXML) {        
        LOG.info("Updating using Record " + record.getOrcidIdentifier().getPath() + " in SOLR index");
        if(!isSolrIndexingEnalbed) {
            LOG.info("Solr indexing is disabled");
            return;
        }        
        this.persist(recordConv.convert(record, v12profileXML));
    }

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

    public Date retrieveLastModified(String orcid) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORCID + ":\"" + orcid + "\"");
        query.setFields(PROFILE_LAST_MODIFIED_DATE);
        try {
            QueryResponse response = solrServer.query(query);
            List<SolrDocument> results = response.getResults();
            if (results.isEmpty()) {
                return null;
            } else {
                return (Date) results.get(0).getFieldValue(PROFILE_LAST_MODIFIED_DATE);
            }

        } catch (SolrServerException e) {
            throw new NonTransientDataAccessResourceException("Error retrieving last modified date from SOLR Server", e);
        }
    }

    /** Updates solr with just the ORCID and lastUpdated, blanking the record.
     * 
     * @param orcid
     * @param lastUpdated
     */
    public void updateSolrIndexForLockedRecord(String orcid, Date lastUpdated) {
        if(!isSolrIndexingEnalbed) {
            LOG.info("Solr indexing is disabled");
            return;
        }
        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(orcid);        
        profileIndexDocument.setProfileLastModifiedDate(lastUpdated); 
        this.persist(profileIndexDocument);
    }

}
