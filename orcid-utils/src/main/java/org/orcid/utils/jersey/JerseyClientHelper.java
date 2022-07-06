package org.orcid.utils.jersey;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

@Component
public class JerseyClientHelper implements DisposableBean {
    
    protected Client jerseyClient;
    
    public JerseyClientHelper() {
        this(false);
    }
    
    public JerseyClientHelper(Boolean isInDevelopmentMode) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        Map<String, Object> jerseyProperties = new HashMap<String, Object>();
        jerseyProperties.put("com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER", connectionManager);
        jerseyClient = OrcidJerseyClientHandler.create(isInDevelopmentMode, jerseyProperties);
    }
    
    @Override
    public void destroy() throws Exception {
        if(jerseyClient != null) {
            jerseyClient.close();
        }
    }

    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, null, null, responseType, errorResponseType);
    }
    
    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, String mediaType, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, mediaType, null, responseType, errorResponseType);
    }
    
    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, String mediaType, String accessToken, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, mediaType,  accessToken, false, responseType, errorResponseType);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, String mediaType, String accessToken, Boolean followRedirects, Class<T> responseType, Class<E> errorResponseType) {
        WebTarget webTarget = jerseyClient.target(url);
        if(followRedirects == null || !followRedirects) {
            // Follow redirects is set to false by default
            webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        } else {
            webTarget.property(ClientProperties.FOLLOW_REDIRECTS, followRedirects);
        }
        
        Builder builder;
        
        if(StringUtils.isEmpty(mediaType)) {
            builder = webTarget.request(); 
        } else {
            builder = webTarget.request(mediaType);
        }
        
        if(!StringUtils.isEmpty(accessToken)) {            
            builder = builder.header("Authorization", "Bearer " + accessToken);
        }
        
        Response response = builder.get(Response.class);        
        JerseyClientResponse<T, E> jcr;
        if(response.getStatus() == 200) {
            jcr = new JerseyClientResponse(response.getStatus(), response.readEntity(responseType), null);
        } else {            
            // Try to obtain the error object if available
            E errorEntity = null;
            try {
                errorEntity = response.readEntity(errorResponseType);
            } catch(Exception e) {
                // Do nothing
            }
            jcr = new JerseyClientResponse(response.getStatus(), null, errorEntity);
        } 
        
        return jcr;
    }       
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, E> JerseyClientResponse<T, E> executePostRequest(String url, String mediaType, String message, String accessToken, Class<T> responseType, Class<E> errorResponseType) {
        WebTarget webTarget = jerseyClient.target(url);
        
        Builder builder;
        
        if(StringUtils.isEmpty(mediaType)) {
            builder = webTarget.request(); 
        } else {
            builder = webTarget.request(mediaType);
        }
        
        if(!StringUtils.isEmpty(accessToken)) {            
            builder = builder.header("Authorization", "Bearer " + accessToken);
        }
        
        Response response = builder.post(Entity.entity(message, mediaType));
                        
        JerseyClientResponse<T, E> jcr;
        if(response.getStatus() == 200) {
            jcr = new JerseyClientResponse(response.getStatus(), response.readEntity(responseType), null);
        } else {
            jcr = new JerseyClientResponse(response.getStatus(), null, response.readEntity(errorResponseType));
        }
        
        return jcr;
    }
    
    public Response executeGetRequest(String url, Boolean followRedirects, String mediaType, Map<String, String> queryParams) {
        WebTarget webTarget = jerseyClient.target(url);
        for(Entry<String, String> entry : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
        } 
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, followRedirects);
        Builder builder = webTarget.request(mediaType);
        return builder.get(Response.class);
    }
    
    public Response executeGetRequest(URI baseUri, String path, String accessToken) {
        WebTarget webTarget = jerseyClient.target(baseUri).path(path);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        Builder builder = webTarget.request(MediaType.APPLICATION_XML).header("Authorization", "Bearer " + accessToken);
        return builder.get(Response.class);
    }
    
    public Response postMessage(String url, String message) {
        WebTarget webTarget = jerseyClient.target(url);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        return builder.post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    public Response postWithAuthentication(String url, String username, String password, Form form) {
        HttpAuthenticationFeature auth = HttpAuthenticationFeature.basic(username, password);
        WebTarget webTarget = jerseyClient.target(url).register(auth);
        Builder builder = webTarget.request(MediaType.APPLICATION_FORM_URLENCODED);
        return builder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
    }
    
}
