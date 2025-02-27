package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientResourceIdPk;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_resource_id")
public class ClientResourceIdEntity extends BaseEntity<ClientResourceIdPk> {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private ClientResourceIdPk id;

    @Override
    public ClientResourceIdPk getId() {
        return id;
    }

    public void setId(ClientResourceIdPk id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientResourceIdEntity that = (ClientResourceIdEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
