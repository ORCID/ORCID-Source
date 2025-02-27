package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 */
public class ClientSecretPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "client_details_id")
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;

    public ClientSecretPk() {
    }

    public ClientSecretPk(String clientDetailsId, String clientSecret) {
        this.clientId = clientDetailsId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String scopeType) {
        this.clientSecret = scopeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientSecretPk that = (ClientSecretPk) o;

        if (!clientId.equals(that.clientId))
            return false;
        if (!clientSecret.equals(that.clientSecret))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + clientSecret.hashCode();
        return result;
    }
}
