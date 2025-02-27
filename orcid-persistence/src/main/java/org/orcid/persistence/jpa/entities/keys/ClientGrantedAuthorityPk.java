package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
public class ClientGrantedAuthorityPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "client_details_id")
    private String clientId;
    @Column(name = "granted_authority")
    private String authority;

    public ClientGrantedAuthorityPk() {
    }

    public ClientGrantedAuthorityPk(String clientDetailsEntity, String authority) {
        this.clientId = clientDetailsEntity;
        this.authority = authority;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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
        if (!clientId.equals(that.clientId))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + authority.hashCode();
        return result;
    }
}
