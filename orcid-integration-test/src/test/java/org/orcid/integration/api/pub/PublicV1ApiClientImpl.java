package org.orcid.integration.api.pub;

import static org.orcid.core.api.OrcidApiConstants.PROFILE_ROOT_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.net.URI;
import java.net.URISyntaxException;

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
    
    public ClientResponse viewRootProfile(String orcid, String token) {
        URI rootProfileUri = UriBuilder.fromPath(PROFILE_ROOT_PATH).build(orcid);
        ClientResponse result = null;
        if(PojoUtil.isEmpty(token)) {
            result = orcidClientHelper.getClientResponse(rootProfileUri, VND_ORCID_XML);
        } else {
            result = orcidClientHelper.getClientResponseWithToken(rootProfileUri, VND_ORCID_XML, token);
        }
        return result;
    }
}
