package org.orcid.core.oauth;

import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthError {
    
    public static final String INVALID_REQUEST = "invalid_request";
    
    public static final String INVALID_CLIENT = "invalid_client";
    
    public static final String INVALID_GRANT = "invalid_grant";
    
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    
    public static final String INVALID_SCOPE = "invalid_scope";

    public static final String SERVER_ERROR = "server_error";
    
    private String error;
    
    @JsonProperty(value = "error_description")
    private String errorDescription;
    
    @JsonIgnore
    private Status responseStatus;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Status getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Status responseStatus) {
        this.responseStatus = responseStatus;
    }
    
}
