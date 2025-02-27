package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientScopePk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientId;
    private String scopeType;

    public ClientScopePk() {
    }

    public ClientScopePk(String clientDetailsId, String scopeType) {
        this.clientId = clientDetailsId;
        this.scopeType = scopeType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientDetailsEntity) {
        this.clientId = clientDetailsEntity;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientScopePk that = (ClientScopePk) o;

        if (!clientId.equals(that.clientId))
            return false;
        if (!scopeType.equals(that.scopeType))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + scopeType.hashCode();
        return result;
    }
}
