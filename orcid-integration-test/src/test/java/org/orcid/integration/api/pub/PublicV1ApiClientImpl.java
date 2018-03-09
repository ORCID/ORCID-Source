package org.orcid.integration.api.pub;

import static org.orcid.core.api.OrcidApiConstants.PROFILE_ROOT_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.BIO_SEARCH_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.pojo.ajaxForm.PojoUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PublicV1ApiClientImpl {
    
    private OrcidClientHelper orcidClientHelper;

    public PublicV1ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }  
    
    public ClientResponse viewRootProfile(String orcid) {
        return viewRootProfile(orcid, null);
    }

    public ClientResponse viewRootProfileJson(String orcid) {
        return viewRootProfileJson(orcid, null);
    }

    public ClientResponse viewRootProfile(String orcid, String token) {
        URI rootProfileUri = UriBuilder.fromPath(PROFILE_ROOT_PATH).build(orcid);
        return getClientReponse(rootProfileUri, token);
    }

    public ClientResponse viewRootProfileJson(String orcid, String token) {
        URI rootProfileUri = UriBuilder.fromPath(PROFILE_ROOT_PATH).build(orcid);
        return getClientReponse(rootProfileUri, token, MediaType.APPLICATION_JSON);
    }

    public ClientResponse viewPublicProfile(String orcid) {
        return viewPublicProfile(orcid, null);
    }
    
    public ClientResponse viewPublicProfile(String orcid, String token) {
        URI profileUri = UriBuilder.fromPath(PROFILE_GET_PATH).build(orcid);
        return getClientReponse(profileUri, token);
    }
    
    public ClientResponse doPublicSearch(String orcid) {        
        URI searchUri = UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery("q=orcid:" + orcid).build();
        return getClientReponse(searchUri, null);
    }
    
    public ClientResponse doPublicSearch(String orcid, String token) {
        URI searchUri = UriBuilder.fromPath(BIO_SEARCH_PATH).replaceQuery("q=orcid:" + orcid).build();
        return getClientReponse(searchUri, token);
    }

    public ClientResponse viewInvalidEndpoint(String orcid) {
        URI searchUri = UriBuilder.fromPath(PROFILE_ROOT_PATH + "/invalid").build(orcid);
        return getClientReponse(searchUri, null);
    }
    
    private ClientResponse getClientReponse(URI uri, String token) {
        return getClientReponse(uri, token, VND_ORCID_XML);
    }

    private ClientResponse getClientReponse(URI uri, String token, String mediaType) {
        ClientResponse result = null;
        if (PojoUtil.isEmpty(token)) {
            result = orcidClientHelper.getClientResponse(uri, mediaType);
        } else {
            result = orcidClientHelper.getClientResponseWithToken(uri, mediaType, token);
        }
        return result;
    }
}
