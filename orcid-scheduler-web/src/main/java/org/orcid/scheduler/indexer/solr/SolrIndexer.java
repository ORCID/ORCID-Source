package org.orcid.scheduler.indexer.solr;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexer {

    Logger LOG = LoggerFactory.getLogger(SolrIndexer.class);

    @Resource(name = "solrServer")
    private SolrServer solrServer;
    
    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;
    @Resource
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;
    
    @Resource
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;
    
    private OrcidRecordToSolrDocument converter = new OrcidRecordToSolrDocument(true);
    
    public void persist(String orcid) {
        Record publicRecord = recordManagerReadOnly.getPublicRecord(orcid);
        List<Funding> allFundings = profileFundingManagerReadOnly.getFundingList(orcid);
        // We MUST keep only public fundings
        Iterator<Funding> fundingIt = allFundings.iterator();
        while(fundingIt.hasNext()) {
            Funding f = fundingIt.next();
            if(!org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.equals(f.getVisibility())) {
                fundingIt.remove();
            }
        }
        
        List<ResearchResource> allResearchResources = researchResourceManagerReadOnly.findResearchResources(orcid);
        //We MUST keep only public research resources
        Iterator<ResearchResource> rrIt = allResearchResources.iterator();
        while(rrIt.hasNext()) {
            ResearchResource r = rrIt.next();
            if(!org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(r.getVisibility())){
                rrIt.remove();
            }
        }
        
        persist(converter.convert(publicRecord, allFundings, allResearchResources));
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

    public void processInvalidRecord(String orcid, Date lastModified) {
        OrcidSolrDocument doc = new OrcidSolrDocument();
        doc.setOrcid(orcid);
        doc.setProfileLastModifiedDate(lastModified);
        this.persist(doc);
    }
}
