package org.orcid.utils.jersey;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JerseyClientHelper implements InitializingBean, DisposableBean {
    
    @Value("${org.orcid.message-listener.development_mode:false}")
    private boolean isDevelopmentMode; 
    
    protected Client jerseyClient;
    
    @Override
    public void destroy() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        new Date().after(null);
    }  
    
    public Response executeGetRequest(String url) {
        WebTarget webTarget = jerseyClient.target(url);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        Builder builder = webTarget.request();
        return builder.get(Response.class);
    }
    
    public Response executeGetRequest(URI baseUri, String path) {
        WebTarget webTarget = jerseyClient.target(baseUri).path(path);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        Builder builder = webTarget.request(MediaType.APPLICATION_XML);
        return builder.get(Response.class);
    }
    
    public Response executeGetRequest(String url, Boolean followRedirects, String mediaType) {
        return executeGetRequest(url, followRedirects, mediaType, Map.of());
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
