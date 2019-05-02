package org.orcid.listener.solr;

import static org.orcid.utils.solr.entities.SolrConstants.ORCID;
import static org.orcid.utils.solr.entities.SolrConstants.PROFILE_LAST_MODIFIED_DATE;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexUpdater {

    Logger LOG = LoggerFactory.getLogger(SolrIndexUpdater.class);

    @Resource(name = "solrClient")
    private SolrClient solrClient;
    
    @Resource(name = "solrOrgsClient")
    private SolrClient solrOrgsClient;
    
    @Value("${org.orcid.persistence.messaging.solr_indexing.auto_commit:false}")
    boolean autoCommit;
    
    public void delete(String orgDisambiguatedId) {
        try {            
            solrOrgsClient.deleteById(orgDisambiguatedId);            
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting org " + orgDisambiguatedId + " to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting org " + orgDisambiguatedId + " to SOLR", ioe);
        }
    }
    
    public void persist(OrgDisambiguatedSolrDocument orgDisambiguatedSolrDocument) {
        try {            
            solrOrgsClient.addBean(orgDisambiguatedSolrDocument);            
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting org " + orgDisambiguatedSolrDocument.getOrgDisambiguatedId() + " to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting org " + orgDisambiguatedSolrDocument.getOrgDisambiguatedId() + " to SOLR", ioe);
        }
    } 
    
    public void persist(OrcidSolrDocument orcidSolrDocument) {
        try {
            solrClient.addBean(orcidSolrDocument);
            if(autoCommit) {
                solrClient.commit();
            }
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
            QueryResponse response = solrClient.query(query);
            List<SolrDocument> results = response.getResults();
            if (results.isEmpty()) {
                return null;
            } else {
                return (Date) results.get(0).getFieldValue(PROFILE_LAST_MODIFIED_DATE);
            }
        } catch (SolrServerException | IOException e) {
            throw new NonTransientDataAccessResourceException("Error retrieving last modified date from SOLR Server", e);
        } 
    }

    public void processInvalidRecord(String orcid) {
        OrcidSolrDocument doc = new OrcidSolrDocument();
        doc.setOrcid(orcid);
        doc.setProfileLastModifiedDate(retrieveLastModified(orcid));
        this.persist(doc);
    }
}
