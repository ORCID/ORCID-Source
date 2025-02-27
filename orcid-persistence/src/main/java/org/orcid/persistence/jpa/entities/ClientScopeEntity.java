package org.orcid.persistence.jpa.entities;

import javax.persistence.*;

import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

import java.util.Objects;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_scope")
@IdClass(ClientScopePk.class)
public class ClientScopeEntity extends BaseEntity<ClientScopePk> {
    private static final long serialVersionUID = 1L;

    private String clientId;
    private String scopeType;

    @Id
    @Column(name = "client_details_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Id
    @Column(name = "scope_type")
    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientScopeEntity that = (ClientScopeEntity) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(scopeType, that.scopeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, scopeType);
    }

    /**
     * As this uses a composite key this is ignored. Always returns null
     *
     * @return always null
     */
    @Override
    @Transient
    public ClientScopePk getId() {
        return null;
    }
}
