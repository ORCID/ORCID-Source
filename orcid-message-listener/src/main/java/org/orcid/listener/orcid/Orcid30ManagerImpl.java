package org.orcid.listener.orcid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.http.OrcidAPIClient;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
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
public class Orcid30ManagerImpl implements Orcid30Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid30ManagerImpl.class);
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    protected final String baseUri;
    protected final String accessToken;

    @Resource
    private OrcidAPIClient orcidAPIClient;

    // loads on read.
    private final LoadingCache<String, RecordContainer> v3ThreadSharedCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(100)
            .build(new CacheLoader<String, RecordContainer>() {
                public RecordContainer load(String orcid) {
                    RecordContainer container = new RecordContainer();
                    String url = baseUri + orcid + "/record";
                    JerseyClientResponse<Record, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE,
                            accessToken, Map.of("User-Agent", "orcid/message-listener"), Record.class, OrcidError.class);
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
    public Orcid30ManagerImpl(@Value("${org.orcid.message-listener.api30BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid30APIClient with baseUri = " + baseUri);
        this.baseUri = baseUri.endsWith("/") ? baseUri : baseUri + '/';
        this.accessToken = accessToken;
    }

    /**
     * uses the loading cache to fetch records. blocks concurrent requests for
     * same message until first has returned.
     * 
     */
    @Override
    public Record fetchPublicRecord(String orcid) throws DeprecatedRecordException, LockedRecordException, ExecutionException {
        RecordContainer container = v3ThreadSharedCache.get(orcid);
        if (container.status != 200) {
            switch (container.status) {
            case 301:
                throw new DeprecatedRecordException(container.error);
            case 409:
                throw new LockedRecordException(container.error);
            default:
                LOG.error("Unable to fetch public record " + orcid + " on API 3.0 HTTP error code: " + container.status);
                throw new RuntimeException("Failed : HTTP error code : " + container.status);
            }
        }
        return container.record;
    }

    // Helper to allow 2.0 indexing endpoint to fetch the research resources
    @Override
    public ResearchResources fetchResearchResources(String orcid) {
        String url = baseUri + orcid + "/research-resources";
        JerseyClientResponse<ResearchResources, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE,
                accessToken, Map.of("User-Agent", "orcid/message-listener"), ResearchResources.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch research resources from record " + orcid + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }

    @Override
    public ResearchResource fetchResearchResource(String orcid, Long putCode) {
        String url = baseUri + orcid + "/research-resource/" + putCode;
        JerseyClientResponse<ResearchResource, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE,
                accessToken, Map.of("User-Agent", "orcid/message-listener"), ResearchResource.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            default:
                LOG.error("Unable to fetch research-resource from record " + orcid + "/" + putCode + " on API 3.0 HTTP error code: " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
        }
        return response.getEntity();
    }

    @Override
    public byte[] fetchActivity(String orcid, Long putCode, String endpoint) throws IOException, InterruptedException {
        String url = baseUri + orcid + "/" + endpoint + "/" + putCode;
        try {
            HttpResponse<byte[]> activity = orcidAPIClient.getActivity(url);
            if (activity.statusCode() != 200) {
                LOG.error("Unable to fetch " + endpoint + " from record " + orcid + "/" + putCode + " on API 3.0 HTTP error code: " + activity.statusCode());
                throw new RuntimeException("Failed : HTTP error code : " + activity.statusCode());
            }
            return activity.body();
        } catch (IOException | InterruptedException e) {
            LOG.error("Unable to fetch " + url, e);
            throw e;
        }
    }
    
    private final class RecordContainer {
        public Record record;
        public int status;
        public OrcidError error;
    }
}
