package org.orcid.utils.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

public class RESTHelper {
    
    @Resource
    protected Client jerseyClient;
    
    public Response executeGetRequest(URI baseUri, String path) {
        WebTarget webTarget = jerseyClient.target(baseUri).path(path);
        webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        Builder builder = webTarget.request(MediaType.APPLICATION_XML);
        return builder.get(Response.class);
    }
    
    public Response executeGetRequest(String url, Boolean followRedirects, String mediaType) {
        WebTarget webTarget = jerseyClient.target(url);
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
