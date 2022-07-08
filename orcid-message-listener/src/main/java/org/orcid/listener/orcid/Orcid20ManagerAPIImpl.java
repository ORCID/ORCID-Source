package org.orcid.listener.orcid;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

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
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.orcid.utils.listener.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.ws.rs.core.MediaType;

@Component
public class Orcid20ManagerAPIImpl implements Orcid20Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid20ManagerAPIImpl.class);
    
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    protected final String baseUri;
    protected final String accessToken;

    //loads on read.
    private final LoadingCache<BaseMessage, RecordContainer> v2ThreadSharedCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100).build(
            new CacheLoader<BaseMessage, RecordContainer>() {
                public RecordContainer load(BaseMessage message){
                    RecordContainer container = new RecordContainer();
                    String url = baseUri + message.getOrcid() + "/record";
                    JerseyClientResponse<Record, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, Record.class, OrcidError.class);
                    if (response.getStatus() != 200) {
                        container.status = response.getStatus();
                        container.error = response.getError();
                        return container;
                    }
                    container.status = 200;
                    container.record = response.getEntity();
                    return container;
                }
              });
    
    @Autowired
    public Orcid20ManagerAPIImpl(@Value("${org.orcid.message-listener.api20BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri);
        this.baseUri = baseUri.endsWith("/") ? baseUri : baseUri + '/';
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
        String url = baseUri + message.getOrcid() + "/activities";
        JerseyClientResponse<ActivitiesSummary, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, ActivitiesSummary.class, OrcidError.class);
        
        if (response.getStatus() != 200) {
            OrcidError orcidError = null;
            switch (response.getStatus()) {
            case 301:
                orcidError = response.getError();
                throw new DeprecatedRecordException(orcidError);
            case 409:
                orcidError = response.getError();
                throw new LockedRecordException(orcidError);
            default:
                LOG.error("Unable to fetch public activities for " + message.getOrcid() + " on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();        
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchAffiliation(java.lang.String, java.lang.Long, org.orcid.jaxb.model.record_v2.AffiliationType)
     */
    @Override
    public Affiliation fetchAffiliation(String orcid, Long putCode, AffiliationType type){
        String url = baseUri + orcid + "/" + type.value() + "/" + putCode;
        if(AffiliationType.EDUCATION.equals(type)) {
            JerseyClientResponse<Education, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, Education.class, OrcidError.class);
            if (response.getStatus() != 200) {
                switch (response.getStatus()) {
                    default:
                    LOG.error("Unable to fetch affiliation from record " + orcid + "/" + type.value() + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
            }
            
            return response.getEntity();   
        } else {
            JerseyClientResponse<Employment, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, Employment.class, OrcidError.class);
            if (response.getStatus() != 200) {
                switch (response.getStatus()) {
                    default:
                    LOG.error("Unable to fetch affiliation from record " + orcid + "/" + type.value() + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
            }
            
            return response.getEntity();
        }        
    }
    
    //TODO: add caching for solr once activities listener is also here.
    @Override
    public Funding fetchFunding(String orcid, Long putCode){
        String url = baseUri + orcid + "/funding/" + putCode;
        JerseyClientResponse<Funding, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, Funding.class, OrcidError.class);        
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch funding record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchWork(java.lang.String, java.lang.Long)
     */
    @Override
    public Work fetchWork(String orcid, Long putCode){
        String url = baseUri + orcid + "/work/" + putCode;
        JerseyClientResponse<Work, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, Work.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch work from record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchPeerReview(java.lang.String, java.lang.Long)
     */
    @Override
    public PeerReview fetchPeerReview(String orcid, Long putCode){
        String url = baseUri + orcid + "/peer-review/" + putCode;
        JerseyClientResponse<PeerReview, OrcidError> response = jerseyClientHelper.executeGetRequest(url, MediaType.APPLICATION_XML_TYPE, accessToken, PeerReview.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch peer review from record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }
    
    private final class RecordContainer {
        public Record record;
        public int status;
        public OrcidError error;
    }      
}
