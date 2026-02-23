package org.orcid.pojo.ajaxForm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Summary of search and link wizard client: id, name, redirectUri, scopes, redirectUriMetadata, isConnected.
 */
public class SearchAndLinkWizardFormSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String redirectUri;
    private String scopes;
    private transient JsonNode redirectUriMetadata;
    private boolean isConnected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public JsonNode getRedirectUriMetadata() {
        return redirectUriMetadata;
    }

    public void setRedirectUriMetadata(JsonNode redirectUriMetadata) {
        this.redirectUriMetadata = redirectUriMetadata;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
