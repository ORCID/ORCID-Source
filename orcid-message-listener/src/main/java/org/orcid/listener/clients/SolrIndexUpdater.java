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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.converters.OrcidRecordToSolrDocument;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexUpdater {

    Logger LOG = LoggerFactory.getLogger(SolrIndexUpdater.class);

    private final boolean isSolrIndexingEnabled;
        
    @Resource(name = "solrServer")
    private SolrServer solrServer;
    
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    
    private OrcidRecordToSolrDocument recordConv;
    
    @Autowired
    public SolrIndexUpdater(
            @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}") boolean isSolrIndexingEnabled, 
            @Value("${org.orcid.core.indexPublicProfile}") boolean indexPublicProfile) throws JAXBException{
        this.isSolrIndexingEnabled = isSolrIndexingEnabled;
        recordConv = new OrcidRecordToSolrDocument(indexPublicProfile);
    }
    
    public void updateSolrIndex(String orcid) {        
        LOG.info("Updating using Record " + orcid + " in SOLR index");
        if(!isSolrIndexingEnabled) {
            LOG.info("Solr indexing is disabled");
            return;
        }
        try{
            Record record = orcid20ApiClient.fetchPublicProfile(orcid); 
            //get detailed funding so we can discover org name and id
            List<Funding> fundings = new ArrayList<Funding>();
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getFundings() != null && record.getActivitiesSummary().getFundings().getFundingGroup() != null){
                for (FundingGroup group : record.getActivitiesSummary().getFundings().getFundingGroup()){
                    if (group.getFundingSummary() !=null){
                        for (FundingSummary f : group.getFundingSummary()){
                            fundings.add(orcid20ApiClient.fetchFunding(record.getOrcidIdentifier().getPath(), f.getPutCode()));
                        }
                    }
                }
            }            
            this.persist(recordConv.convert(record,fundings));
        } catch(LockedRecordException lre) {    
            LOG.error("Record " + orcid + " is locked");
            updateSolrIndexForLockedRecord(orcid, retrieveLastModified(orcid));
        } catch(DeprecatedRecordException dre) {
            LOG.error("Record " + orcid + " is deprecated");
            //TODO: ???
        }
    }

    private void persist(OrcidSolrDocument orcidSolrDocument) {
        try {
            solrServer.addBean(orcidSolrDocument);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting to SOLR", ioe);
        }

    }

    private Date retrieveLastModified(String orcid) {
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
    private void updateSolrIndexForLockedRecord(String orcid, Date lastUpdated) {
        if(!isSolrIndexingEnabled) {
            LOG.info("Solr indexing is disabled");
            return;
        }
        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(orcid);        
        profileIndexDocument.setProfileLastModifiedDate(lastUpdated); 
        this.persist(profileIndexDocument);
    }

}
