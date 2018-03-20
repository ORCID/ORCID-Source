package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientResourceIdPk;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_resource_id")
@IdClass(ClientResourceIdPk.class)
public class ClientResourceIdEntity extends BaseEntity<ClientResourceIdPk> {

    private static final long serialVersionUID = 1L;
    private String resourceId;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is ignored. Always returns null
     * 
     * @return always null
     */
    @Override
    @Transient
    public ClientResourceIdPk getId() {
        return null;
    }

    @Id
    @Column(name = "resource_id", length = 175)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
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
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
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
        ClientResourceIdEntity other = (ClientResourceIdEntity) obj;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        return true;
    }        
}
