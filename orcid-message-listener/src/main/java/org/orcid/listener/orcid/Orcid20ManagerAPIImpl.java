package org.orcid.listener.orcid;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

import jakarta.ws.rs.core.MediaType;

@Component
public class Orcid20ManagerAPIImpl implements Orcid20Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid20ManagerAPIImpl.class);
    
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    protected final String baseUri;
    protected final String accessToken;

    @Autowired
    public Orcid20ManagerAPIImpl(@Value("${org.orcid.message-listener.api20BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri);
        this.baseUri = baseUri.endsWith("/") ? baseUri : baseUri + '/';
        this.accessToken = accessToken;
    }

    /**
     * Fetch public record data
     */
    @Override
    public Record fetchPublicRecord(BaseMessage message) throws LockedRecordException, DeprecatedRecordException, ExecutionException {
        String url = baseUri + message.getOrcid() + "/record";
        JerseyClientResponse<Record, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Record.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            case 301:
                throw new DeprecatedRecordException(response.getError());
            case 409:
                throw new LockedRecordException(response.getError());
            default:
                LOG.error("Unable to fetch public record " + message.getOrcid() + " on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }
    
    /* (non-Javadoc)
     * @see org.orcid.listener.orcid.Orcid20Manager#fetchPublicActivitiesSummary(org.orcid.utils.listener.BaseMessage)
     */
    @Override
    public ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws LockedRecordException, DeprecatedRecordException {
        String url = baseUri + message.getOrcid() + "/activities";
        JerseyClientResponse<ActivitiesSummary, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), ActivitiesSummary.class, OrcidError.class);
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
            JerseyClientResponse<Education, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Education.class, OrcidError.class);
            if (response.getStatus() != 200) {
                switch (response.getStatus()) {
                    default:
                    LOG.error("Unable to fetch affiliation from record " + orcid + "/" + type.value() + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                }
            }
            
            return response.getEntity();   
        } else {
            JerseyClientResponse<Employment, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Employment.class, OrcidError.class);
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
        JerseyClientResponse<Funding, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Funding.class, OrcidError.class);        
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
        JerseyClientResponse<Work, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Work.class, OrcidError.class);
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
        JerseyClientResponse<PeerReview, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), PeerReview.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
                default:
                LOG.error("Unable to fetch peer review from record " + orcid + "/" + putCode+" on API 2.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }
    
}
