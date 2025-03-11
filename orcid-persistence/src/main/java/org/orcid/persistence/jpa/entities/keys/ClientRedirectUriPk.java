package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientRedirectUriPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3948069038449324678L;
    private String clientId;
    private String redirectUri;
    private String redirectUriType;

    public ClientRedirectUriPk() {
    }

    public ClientRedirectUriPk(String clientId, String redirectUri, String redirectUriType) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.redirectUriType = redirectUriType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRedirectUriType() {
        return redirectUriType;
    }

    public void setRedirectUriType(String redirectUriType) {
        this.redirectUriType = redirectUriType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientRedirectUriPk that = (ClientRedirectUriPk) o;

        if (!clientId.equals(that.clientId))
            return false;
        if (!redirectUri.equals(that.redirectUri))
            return false;
        if(!redirectUriType.equals(that.redirectUriType))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + redirectUri.hashCode();
        result = 31 * result + redirectUriType.hashCode();
        return result;
    }
}
