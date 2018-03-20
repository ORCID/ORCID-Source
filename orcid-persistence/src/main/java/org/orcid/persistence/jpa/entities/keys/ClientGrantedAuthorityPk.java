package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientGrantedAuthorityPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientDetailsEntity;
    private String authority;

    public ClientGrantedAuthorityPk() {
    }

    public ClientGrantedAuthorityPk(String clientDetailsEntity, String authority) {
        this.clientDetailsEntity = clientDetailsEntity;
        this.authority = authority;
    }

    public String getClientDetailsEntity() {
        return this.clientDetailsEntity;
    }

    public void setClientDetailsEntity(String clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClientGrantedAuthorityPk that = (ClientGrantedAuthorityPk) o;

        if (!authority.equals(that.authority))
            return false;
        if (!clientDetailsEntity.equals(that.clientDetailsEntity))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientDetailsEntity.hashCode();
        result = 31 * result + authority.hashCode();
        return result;
    }
}
