package org.orcid.listener.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20APIClient;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrMessageProcessor implements Consumer<LastModifiedMessage>{

    Logger LOG = LoggerFactory.getLogger(SolrMessageProcessor.class);

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnabled;
    
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    
    @Resource
    private SolrIndexUpdater solrUpdater;
    
    @Resource
    private RecordStatusManager recordStatusManager;

    private OrcidRecordToSolrDocument recordConv;

    @Autowired
    public SolrMessageProcessor(
            @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}") boolean isSolrIndexingEnabled, 
            @Value("${org.orcid.core.indexPublicProfile}") boolean indexPublicProfile) throws JAXBException{
        this.isSolrIndexingEnabled = isSolrIndexingEnabled;
        recordConv = new OrcidRecordToSolrDocument(indexPublicProfile);
    }
    
    @Override
    public void accept(LastModifiedMessage t) {
        updateSolrIndex(t);
    }
    
    public void accept(RetryMessage m) {
        updateSolrIndex(m);
    }

    private void updateSolrIndex(BaseMessage message) {
        String orcid = message.getOrcid();
        LOG.info("Updating using Record " + orcid + " in SOLR index");
        if(!isSolrIndexingEnabled) {
            LOG.info("Solr indexing is disabled");
            return;
        }
        try{
            org.orcid.jaxb.model.record_v2.Record record = orcid20ApiClient.fetchPublicRecord(message); 
            
            // Remove deactivated records from SOLR index
            if (record.getHistory() != null && record.getHistory().getDeactivationDate() != null && record.getHistory().getDeactivationDate().getValue() != null) {
                solrUpdater.processInvalidRecord(orcid);
                recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
                return;
            }
            
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
            recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
        } catch(LockedRecordException lre) {
            LOG.error("Record " + orcid + " is locked");
            solrUpdater.processInvalidRecord(orcid);
            recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
        } catch(DeprecatedRecordException dre) {
            LOG.error("Record " + orcid + " is deprecated");
            solrUpdater.processInvalidRecord(orcid);
            recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
        } catch (Exception e){
            LOG.error("Unable to fetch record " + orcid + " for SOLR");
            LOG.error(e.getMessage(), e);
            recordStatusManager.markAsFailed(orcid, AvailableBroker.SOLR);
        }
    }
}
