package org.orcid.listener.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.listener.exception.V3DeprecatedRecordException;
import org.orcid.listener.exception.V3LockedRecordException;
import org.orcid.listener.orcid.Orcid30Manager;
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
public class SolrMessageProcessor implements Consumer<LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(SolrMessageProcessor.class);

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnabled;
    
    @Resource
    private Orcid30Manager orcid30ApiClient;
    
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
            Record record = orcid30ApiClient.fetchPublicRecord(message); 
            
            // Remove deactivated records from SOLR index
            if (record.getHistory() != null && record.getHistory().getDeactivationDate() != null && record.getHistory().getDeactivationDate().getValue() != null) {
                solrUpdater.processInvalidRecord(orcid);
                recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
                return;
            }        
            
            //get detailed research resources to discover proposal and resource properties
            List<ResearchResource> researchResourcesList = new ArrayList<ResearchResource>();
            ResearchResources rr = orcid30ApiClient.fetchResearchResources(orcid);
            if(rr != null && rr.getResearchResourceGroup() != null) {
                for(ResearchResourceGroup group : rr.getResearchResourceGroup()) {
                    if(group.getResearchResourceSummary() != null){
                        for(ResearchResourceSummary s : group.getResearchResourceSummary()) {
                            researchResourcesList.add(orcid30ApiClient.fetchResearchResource(orcid, s.getPutCode()));
                        }
                    }
                }
            }            
            
            solrUpdater.persist(recordConv.convert(record, researchResourcesList));
            recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
        } catch(V3LockedRecordException lre) {
            LOG.error("Record " + orcid + " is locked");
            solrUpdater.processInvalidRecord(orcid);
            recordStatusManager.markAsSent(orcid, AvailableBroker.SOLR);
        } catch(V3DeprecatedRecordException dre) {
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
