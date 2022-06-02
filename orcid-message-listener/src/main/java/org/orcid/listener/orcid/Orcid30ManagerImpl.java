package org.orcid.listener.orcid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.util.HttpHelper;
import org.orcid.utils.listener.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.ws.rs.core.Response;

@Component
public class Orcid30ManagerImpl implements Orcid30Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid30ManagerImpl.class);
    
    protected final URI baseUri;
    protected final String accessToken;

    @Resource
    private HttpHelper httpHelper;
    
    // loads on read.
    private final LoadingCache<BaseMessage, RecordContainer> v3ThreadSharedCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100)
            .build(new CacheLoader<BaseMessage, RecordContainer>() {
                public RecordContainer load(BaseMessage message) {
                    RecordContainer container = new RecordContainer();
                    Response response = httpHelper.executeGetRequest(baseUri, message.getOrcid() + "/record", accessToken);
                    if (response.getStatus() != 200) {
                        container.status = response.getStatus();
                        container.error = response.readEntity(OrcidError.class);
                        return container;
                    }
                    container.status = 200;
                    //String responseString = response.readEntity(String.class);
                    //System.out.println(responseString);
                    container.record = response.readEntity(Record.class);
                    return container;
                }
            });

    @Autowired
    public Orcid30ManagerImpl(@Value("${org.orcid.message-listener.api30BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid30APIClient with baseUri = " + baseUri);
        this.baseUri = new URI(baseUri);
        this.accessToken = accessToken;
    }

    /**
     * uses the loading cache to fetch records. blocks concurrent requests for
     * same message until first has returned.
     * 
     */
    @Override
    public Record fetchPublicRecord(BaseMessage message) throws DeprecatedRecordException, LockedRecordException, ExecutionException {
        RecordContainer container = v3ThreadSharedCache.get(message);
        if (container.status != 200) {
            switch (container.status) {
            case 301:
                throw new DeprecatedRecordException(container.error);
            case 409:
                throw new LockedRecordException(container.error);
            default:
                LOG.error("Unable to fetch public record " + message.getOrcid() + " on API 3.0 HTTP error code: " + container.status);
                throw new RuntimeException("Failed : HTTP error code : " + container.status);
            }
        }
        return container.record;
    }

    @Override
    public ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws LockedRecordException, DeprecatedRecordException {
        Response response = httpHelper.executeGetRequest(baseUri, message.getOrcid() + "/activities", accessToken);
        if (response.getStatus() != 200) {
            OrcidError orcidError = null;
            switch (response.getStatus()) {
            case 301:
                orcidError = response.readEntity(OrcidError.class);
                throw new DeprecatedRecordException(orcidError);
            case 409:
                orcidError = response.readEntity(OrcidError.class);
                throw new LockedRecordException(orcidError);
            default:
                LOG.error("Unable to fetch public activities for " + message.getOrcid() + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.readEntity(ActivitiesSummary.class);
    }

    @Override
    public Affiliation fetchAffiliation(String orcid, Long putCode, AffiliationType type) {
        switch (type) {
        case DISTINCTION:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Distinction.class);
        case EDUCATION:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Education.class);
        case EMPLOYMENT:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Employment.class);
        case INVITED_POSITION:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), InvitedPosition.class);
        case MEMBERSHIP:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Membership.class);
        case QUALIFICATION:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Qualification.class);
        case SERVICE:
            return (Affiliation) fetchEntity(orcid, putCode, type.value(), Service.class);
        }
        // What else should we do in case the affiliation type is null or not
        // found in the switch statement?
        String error = "Invalid value " + type + " found for orcid " + orcid + " and put code " + putCode;
        LOG.error(error);
        throw new IllegalArgumentException(error);
    }

    @Override
    public Funding fetchFunding(String orcid, Long putCode) {
        return (Funding) fetchEntity(orcid, putCode, "funding", Funding.class);
    }

    @Override
    public Work fetchWork(String orcid, Long putCode) {
        return (Work) fetchEntity(orcid, putCode, "work", Work.class);
    }

    @Override
    public PeerReview fetchPeerReview(String orcid, Long putCode) {
        return (PeerReview) fetchEntity(orcid, putCode, "peer-review", PeerReview.class);
    }

    @Override
    public ResearchResource fetchResearchResource(String orcid, Long putCode) {
        return (ResearchResource) fetchEntity(orcid, putCode, "research-resource", ResearchResource.class);
    }

    private Activity fetchEntity(String orcid, Long putCode, String endpoint, Class<? extends Activity> c) {
        Response response = httpHelper.executeGetRequest(baseUri, orcid + "/" + endpoint + "/" + putCode, accessToken);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch " + endpoint + " from record " + orcid + "/" + putCode + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.readEntity(c);
    }

    // Helper to allow 2.0 indexing endpoint to fetch the research resources
    @Override
    public ResearchResources fetchResearchResources(String orcid) {
        Response response = httpHelper.executeGetRequest(baseUri, orcid + "/research-resources", accessToken);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch research resources from record " + orcid + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.readEntity(ResearchResources.class);
    }
    
    private final class RecordContainer {
        public Record record;
        public int status;
        public OrcidError error;
    }
}
