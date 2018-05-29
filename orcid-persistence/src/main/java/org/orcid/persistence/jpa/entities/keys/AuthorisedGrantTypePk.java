package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
@Embeddable
public class AuthorisedGrantTypePk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String clientId;
    private String scope;

    public AuthorisedGrantTypePk() {
    }

    public AuthorisedGrantTypePk(String clientId, String scope) {
        this.clientId = clientId;
        this.scope = scope;
    }

    @Column(name = "client_id", nullable = false)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "scope", nullable = false)
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AuthorisedGrantTypePk that = (AuthorisedGrantTypePk) o;

        if (!clientId.equals(that.clientId))
            return false;
        if (!scope.equals(that.scope))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }
}
