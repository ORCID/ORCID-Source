package org.orcid.scheduler.indexer.solr;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.utils.solr.entities.OrcidSolrDocumentLegacy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexer {

    Logger LOG = LoggerFactory.getLogger(SolrIndexer.class);

    @Value("${org.orcid.persistence.solr.legacy.url}")
    private String legacySolrMasterUrl;
    
    @Resource
    private RecordManagerReadOnly recordManagerReadOnly;
    @Resource
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;
    
    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;
    
    private OrcidRecordToSolrDocument converter = new OrcidRecordToSolrDocument(true);
    
    public void persist(String orcid) throws IOException {
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
    
    private void persist(OrcidSolrDocumentLegacy orcidSolrDocument) throws IOException {
            String url = legacySolrMasterUrl + "/profile/update?commit=true";
            DocumentObjectBinder b = new DocumentObjectBinder();
            String xmlDocument = ClientUtils.toXML(b.toSolrInputDocument(orcidSolrDocument));
            
            // Surround document with <add></add> tags
            xmlDocument = "<add>" + xmlDocument + "</add>";
            
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent")+ " (orcid.org)");
            con.addRequestProperty("Content-Type", "text/xml");
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            con.setDoOutput(true);
            con.getOutputStream().write(xmlDocument.getBytes());
            int responseCode = con.getResponseCode(); 
            if(responseCode != HttpURLConnection.HTTP_OK) { 
                String responseMessage = con.getResponseMessage();
                LOG.error("Error persisting " + orcidSolrDocument.getOrcid());
                LOG.error(responseMessage);
                throw new NonTransientDataAccessResourceException("Error persisting to SOLR Server " + responseMessage);
            }                                
    }     
}
