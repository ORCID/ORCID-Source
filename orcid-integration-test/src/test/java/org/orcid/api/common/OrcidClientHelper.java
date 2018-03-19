package org.orcid.api.common;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class OrcidClientHelper {

    protected Client jerseyClient;

    protected URI baseUri;
    
    private HTTPBasicAuthFilter basicAuthFilter;

    public OrcidClientHelper(URI baseUri, Client client) throws URISyntaxException {
        this.jerseyClient = client;
        if (baseUri.toString().endsWith("/")) {
            String s = baseUri.toString();
            this.baseUri = new URI(s.substring(0, s.length() - 1));
        } else {
            this.baseUri = baseUri;
        }
    }

    public ClientResponse getClientResponse(URI uri, String accept) {
        return createRootResource(uri).accept(accept).get(ClientResponse.class);
    }
    
    public ClientResponse getClientResponseWithToken(URI restPath, String accept, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).get(ClientResponse.class);
    }
    
    public ClientResponse postClientResponse(URI uri, String accept, Object jaxbRootElement) {
        return createRootResource(uri).accept(accept).type(accept).post(ClientResponse.class, jaxbRootElement);
    }
    
    public ClientResponse postClientResponseWithToken(URI restPath, String accept, Object jaxbRootElement, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).post(ClientResponse.class, jaxbRootElement);
    }

    public ClientResponse putClientResponse(URI uri, String accept, Object jaxbRootElement) {
        ClientResponse response = createRootResource(uri).accept(accept).type(accept).put(ClientResponse.class, jaxbRootElement);
        return response;
    }
    
    public ClientResponse putClientResponseWithToken(URI restPath, String accept, Object jaxbRootElement, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).put(ClientResponse.class, jaxbRootElement);
    }
    
    public ClientResponse deleteClientResponse(URI uri, String accept) {
        return createRootResource(uri).accept(accept).type(accept).delete(ClientResponse.class);
    }
    
    public ClientResponse deleteClientResponseWithToken(URI restPath, String accept, String oauthToken) {
        return setupRequestCommonParams(restPath, accept, oauthToken).delete(ClientResponse.class);
    }

    public WebResource.Builder setupRequestCommonParams(URI restpath, String accept, String oauthToken) {
        WebResource rootResource = createRootResource(restpath);
        WebResource.Builder built = addOauthHeader(rootResource, oauthToken).accept(accept).type(accept);
        return built;
    }
    
    private WebResource.Builder addOauthHeader(WebResource webResource, String oAuthToken) {
        return webResource.header("Authorization", "Bearer " + oAuthToken);
    }

    public URI deriveUriFromRestPath(String restPath) {
        URI uri = UriBuilder.fromPath(restPath).build();
        return uri;
    }

    public URI deriveUriFromRestPath(String restPath, String orcid) {
        URI uri = UriBuilder.fromPath(restPath).build(orcid);
        return uri;
    }

    public WebResource createRootResource(URI uri) {
        return (jerseyClient.resource(resolveUri(uri)));
    }

    public WebResource createRootResource(String uri) {
        return createRootResource(deriveUriFromRestPath(uri));
    }
    
    public void addBasicAuth(String username, String password) {
        basicAuthFilter = new HTTPBasicAuthFilter(username, password);
        jerseyClient.addFilter(basicAuthFilter);
    }

    public void removeBasicAuth() {
        jerseyClient.removeFilter(basicAuthFilter);
    }

    private URI resolveUri(URI uri) {
        try {
            if(uri.getHost() != null){
                return uri;
            }
            return new URI(baseUri.toString().concat(uri.toString()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Calculated URI is invalid. Please check the settings.", e);
        }
    }
}
