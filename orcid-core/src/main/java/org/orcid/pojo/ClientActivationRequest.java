package org.orcid.pojo;

public class ClientActivationRequest {
    
    private String error;
    
    private String clientId;
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
