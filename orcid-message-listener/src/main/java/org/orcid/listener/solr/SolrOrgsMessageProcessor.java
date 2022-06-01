package org.orcid.listener.solr;

import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.listener.util.SystemAlertsUtil;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrOrgsMessageProcessor implements Consumer<OrgDisambiguatedSolrDocument> {

    Logger LOG = LoggerFactory.getLogger(SolrOrgsMessageProcessor.class);

    private static final Integer MAX_RETRY_COUNT = 3;

    @Value("${org.orcid.persistence.messaging.solr_org_indexing.enabled:true}")
    private boolean isSolrOrgsIndexingEnabled;

    @Resource
    private SolrIndexUpdater solrUpdater;

    @Resource 
    private SystemAlertsUtil systemAlertsUtil;
    
    @Autowired
    public SolrOrgsMessageProcessor() throws JAXBException {

    }

    @Override
    public void accept(OrgDisambiguatedSolrDocument t) {
        if(isSolrOrgsIndexingEnabled) {
            process(t, 0);
        }
    }

    private void process(OrgDisambiguatedSolrDocument t, Integer retryCount) {
        try {
            if("DEPRECATED".equals(t.getOrgDisambiguatedStatus()) || "OBSOLETE".equals(t.getOrgDisambiguatedStatus()) || "PART_OF_GROUP".equals(t.getOrgDisambiguatedStatus()) ) {
                solrUpdater.delete(String.valueOf(t.getOrgDisambiguatedId()));
            } else {
                solrUpdater.persist(t);                
            }                        
        } catch (Exception e) {
            LOG.error("Unable to persists org " + t.getOrgDisambiguatedId() + " in SOLR");
            LOG.error(e.getMessage(), e);
            if (retryCount > MAX_RETRY_COUNT) {
                systemAlertsUtil.sendSystemAlert("SOLR Index error: Unable to persist org with disambiguated org id = " + t.getOrgDisambiguatedId() + " and name " + t.getOrgDisambiguatedName() + " in SOLR");
            } else {
                process(t, retryCount + 1);
            }
        }
    }    
}
