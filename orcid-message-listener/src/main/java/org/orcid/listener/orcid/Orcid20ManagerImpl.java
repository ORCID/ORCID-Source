package org.orcid.listener.orcid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record_v2.Record;
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

import javax.ws.rs.core.MediaType;

@Component
public class Orcid20ManagerImpl implements Orcid20Manager {

    Logger LOG = LoggerFactory.getLogger(Orcid20ManagerImpl.class);
    
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    @Resource
    private OrcidAPIClient orcidAPIClient;
    
    protected final String baseUri;
    protected final String accessToken;

    @Autowired
    public Orcid20ManagerImpl(@Value("${org.orcid.message-listener.api20BaseURI}") String baseUri,
            @Value("${org.orcid.message-listener.api.read_public_access_token}") String accessToken) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri);
        this.baseUri = baseUri.endsWith("/") ? baseUri : baseUri + '/';
        this.accessToken = accessToken;
    }

    /**
     * Fetch public record data
     */
    @Override
    public Record fetchPublicRecord(String orcid) throws LockedRecordException, DeprecatedRecordException, ExecutionException {
        String url = baseUri + orcid + "/record";
        JerseyClientResponse<Record, OrcidError> response = jerseyClientHelper.executeGetRequestWithCustomHeaders(url, MediaType.APPLICATION_XML_TYPE, accessToken, Map.of("User-Agent","orcid/message-listener"), Record.class, OrcidError.class);
        if (response.getStatus() != 200) {
            switch (response.getStatus()) {
            case 301:
                throw new DeprecatedRecordException(response.getError());
            case 409:
                throw new LockedRecordException(response.getError());
            default:
                LOG.error("Unable to fetch public record " + orcid + " on API 2.0 HTTP error code: " + response.getStatus());
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
                LOG.error("Unable to fetch " + endpoint + " from record " + orcid + "/" + putCode + " on API 2.0 HTTP error code: " + activity.statusCode());
                throw new RuntimeException("Failed : HTTP error code : " + activity.statusCode());
            }
            return activity.body();
        } catch (IOException | InterruptedException e) {
            LOG.error("Unable to fetch " + url, e);
            throw e;
        }
    }    
}
