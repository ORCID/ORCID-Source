package org.orcid.listener.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.SolrIndexUpdater;
import org.orcid.listener.converters.OrcidRecordToSolrDocument;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SolrLastModifiedMessageProcessor implements Consumer<LastModifiedMessage>{

    Logger LOG = LoggerFactory.getLogger(SolrLastModifiedMessageProcessor.class);

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnabled;
    
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    
    @Resource
    private SolrIndexUpdater solrUpdater;
    
    private OrcidRecordToSolrDocument recordConv;

    @Autowired
    public SolrLastModifiedMessageProcessor(
            @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}") boolean isSolrIndexingEnabled, 
            @Value("${org.orcid.core.indexPublicProfile}") boolean indexPublicProfile) throws JAXBException{
        this.isSolrIndexingEnabled = isSolrIndexingEnabled;
        recordConv = new OrcidRecordToSolrDocument(indexPublicProfile);
    }
    
    @Override
    public void accept(LastModifiedMessage t) {
        updateSolrIndex(t.getOrcid());
    }

    private void updateSolrIndex(String orcid) {        
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
            solrUpdater.persist(recordConv.convert(record,fundings));
        } catch(LockedRecordException lre) {    
            LOG.error("Record " + orcid + " is locked");
            solrUpdater.updateSolrIndexForLockedRecord(orcid, solrUpdater.retrieveLastModified(orcid));
        } catch(DeprecatedRecordException dre) {
            LOG.error("Record " + orcid + " is deprecated");
            //TODO: ???
        }
    }
}
