package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientScopePk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientDetailsEntity;
    private String scopeType;

    public ClientScopePk() {
    }

    public ClientScopePk(String clientDetailsEntity, String scopeType) {
        this.clientDetailsEntity = clientDetailsEntity;
        this.scopeType = scopeType;
    }

    public String getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
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

        if (!clientDetailsEntity.equals(that.clientDetailsEntity))
            return false;
        if (!scopeType.equals(that.scopeType))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientDetailsEntity.hashCode();
        result = 31 * result + scopeType.hashCode();
        return result;
    }
}
