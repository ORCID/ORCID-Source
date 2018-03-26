package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_scope")
@IdClass(ClientScopePk.class)
public class ClientScopeEntity extends BaseEntity<ClientScopePk> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ClientScopeEntity() {
    }

    public ClientScopeEntity(String scopeType) {
        this.scopeType = scopeType;
    }

    private String scopeType;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is ignored always returns null
     * 
     * @return always returns null
     */
    @Override
    @Transient
    public ClientScopePk getId() {
        return null;
    }

    @Id
    @Column(name = "scope_type", length = 70)
    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((scopeType == null) ? 0 : scopeType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientScopeEntity other = (ClientScopeEntity) obj;
        if (scopeType == null) {
            if (other.scopeType != null)
                return false;
        } else if (!scopeType.equals(other.scopeType))
            return false;
        return true;
    }        
}
