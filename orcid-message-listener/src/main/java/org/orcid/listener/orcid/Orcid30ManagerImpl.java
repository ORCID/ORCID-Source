package org.orcid.listener.orcid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.jaxb.model.v3.rc2.error.OrcidError;
import org.orcid.jaxb.model.v3.rc2.record.Activity;
import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.Record;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.listener.exception.V3DeprecatedRecordException;
import org.orcid.listener.exception.V3LockedRecordException;
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
public class Orcid30ManagerImpl implements Orcid30Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid30ManagerImpl.class);
    @Resource
    protected Client jerseyClient;
    protected final URI baseUri;
    protected final String accessToken;

    // loads on read.
    private final LoadingCache<BaseMessage, RecordContainer> v3ThreadSharedCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100)
            .build(new CacheLoader<BaseMessage, RecordContainer>() {
                public RecordContainer load(BaseMessage message) {
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
    public Record fetchPublicRecord(BaseMessage message) throws V3DeprecatedRecordException, V3LockedRecordException, ExecutionException {
        RecordContainer container = v3ThreadSharedCache.get(message);
        if (container.status != 200) {
            switch (container.status) {
            case 301:
                throw new V3DeprecatedRecordException(container.error);
            case 409:
                throw new V3LockedRecordException(container.error);
            default:
                LOG.error("Unable to fetch public record " + message.getOrcid() + " on API 3.0 HTTP error code: " + container.status);
                throw new RuntimeException("Failed : HTTP error code : " + container.status);
            }
        }
        return container.record;
    }

    @Override
    public ActivitiesSummary fetchPublicActivitiesSummary(BaseMessage message) throws V3LockedRecordException, V3DeprecatedRecordException {
        WebResource webResource = jerseyClient.resource(baseUri).path(message.getOrcid() + "/activities");
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            OrcidError orcidError = null;
            switch (response.getStatus()) {
            case 301:
                orcidError = response.getEntity(OrcidError.class);
                throw new V3DeprecatedRecordException(orcidError);
            case 409:
                orcidError = response.getEntity(OrcidError.class);
                throw new V3LockedRecordException(orcidError);
            default:
                LOG.error("Unable to fetch public activities for " + message.getOrcid() + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(ActivitiesSummary.class);
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
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/" + endpoint + "/" + putCode);
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch " + endpoint + " from record " + orcid + "/" + putCode + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(c);
    }

    private final class RecordContainer {
        public Record record;
        public int status;
        public OrcidError error;
    }

    // Helper to allow 2.0 indexing endpoint to fetch the research resources
    @Override
    public ResearchResources fetchResearchResources(String orcid) {
        WebResource webResource = jerseyClient.resource(baseUri).path(orcid + "/research-resources");
        webResource.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
        Builder builder = webResource.accept(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch research resources from record " + orcid + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity(ResearchResources.class);
    }
}
