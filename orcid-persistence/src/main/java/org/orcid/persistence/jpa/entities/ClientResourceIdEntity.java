package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientResourceIdPk;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_resource_id")
@IdClass(ClientResourceIdPk.class)
public class ClientResourceIdEntity extends BaseEntity<ClientResourceIdPk> {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String resourceId;

    @Id
    @Column(name = "client_details_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Id
    @Column(name = "resource_id")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientResourceIdEntity that = (ClientResourceIdEntity) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, resourceId);
    }

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
}
