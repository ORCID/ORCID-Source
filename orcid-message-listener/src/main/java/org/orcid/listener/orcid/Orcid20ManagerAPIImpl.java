package org.orcid.listener.orcid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.AffiliationType;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.utils.listener.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;

@Component
public class Orcid20ManagerAPIImpl implements Orcid20Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid20ManagerAPIImpl.class);
    @Resource
    protected Client jerseyClient;
    protected final URI baseUri;
    protected final String accessToken;

    //loads on read.
    private final LoadingCache<BaseMessage, RecordContainer> v2ThreadSharedCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100).build(
            new CacheLoader<BaseMessage, RecordContainer>() {
                public RecordContainer load(BaseMessage message){
                    RecordContainer container = new RecordContainer();
                    WebResource webResource = jerseyClient.resource(baseUri).path(message.getOrcid() + "/record");
                    webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
                    Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
                    ClientResponse response = builder.get(ClientResponse.class);
                    if (response.getStatus() != 200) {
                        container.status = response.getStatus();
                        container.error = response.getEntity(OrcidError.class);
                        return container;
                    }
                    container.status = 200;
                    container.record = response.getEntity(Record.class);
                    return container;
                }
              });
    
    @Autowired
    public Orcid20ManagerAPIImpl(@Value("${org.orcid.message-listener.api20BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri);
        this.baseUri = new URI(baseUri);
        this.accessToken = accessToken;
    }

    /**uses the loading cache to fetch records.  blocks concurrent requests for same message until first has returned.
     * 
     */
    @Override
    public Record fetchPublicRecord(BaseMessage message) throws LockedRecordException, DeprecatedRecordException, ExecutionException {
        RecordContainer container = v2ThreadSharedCache.get(message);
        if (container.status != 200) {
            switch (container.status) {
            case 301:
                throw new DeprecatedRecordException(container.error);
            case 409:
                throw new LockedRecordException(container.error);
            default:
                LOG.error("Unable to fetch public record " + message.getOrcid() + " on API 2.0 HTTP error code: " + container.status);
                throw new RuntimeException("Failed : HTTP error code : " + container.status);
            }
        }
        return container.record;
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchPublicActivitiesSummary(org.orcid.utils.listener.BaseMessage)
     */
    @Override
    public ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws LockedRecordException, DeprecatedRecordException {
        WebResource webResource = jerseyClient.resource(baseUri).path(message.getOrcid() + "/activities");
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            OrcidError orcidError = null;
            switch (response.getStatus()) {
            case 301:
                orcidError = response.getEntity(OrcidError.class);
                throw new DeprecatedRecordException(orcidError);
            case 409:
                orcidError = response.getEntity(OrcidError.class);
                throw new LockedRecordException(orcidError);
            default:
                LOG.error("Unable to fetch public activities for " + message.getOrcid() + " on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(ActivitiesSummary.class);        
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchAffiliation(java.lang.String, java.lang.Long, org.orcid.jaxb.model.record_v2.AffiliationType)
     */
    @Override
    public Affiliation fetchAffiliation(String orcid, Long putCode, AffiliationType type){
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/" + type.value() + "/" + putCode);
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch affiliation from record " + orcid + "/" + type.value() + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        
        Affiliation aff;
        
        if(AffiliationType.EDUCATION.equals(type)) {
        	aff = response.getEntity(Education.class);
        } else {
        	aff = response.getEntity(Employment.class);
        }
        
        return aff;
    }
    
    //TODO: add caching for solr once activities listener is also here.
    @Override
    public Funding fetchFunding(String orcid, Long putCode){
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/funding/"+ putCode);
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch funding record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(Funding.class);
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchWork(java.lang.String, java.lang.Long)
     */
    @Override
    public Work fetchWork(String orcid, Long putCode){
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/work/"+ putCode);
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch work from record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(Work.class);
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchPeerReview(java.lang.String, java.lang.Long)
     */
    @Override
    public PeerReview fetchPeerReview(String orcid, Long putCode){
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/peer-review/"+ putCode);
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch peer review from record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(PeerReview.class);
    }
    
    private final class RecordContainer{
        public Record record;
        public int status;
        public OrcidError error;
    }
    
    private final class FundingContainer{
        public Funding record;
        public int status;
        public OrcidError error;
    }
}
