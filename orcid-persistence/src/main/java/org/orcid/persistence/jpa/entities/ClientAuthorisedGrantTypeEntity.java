package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_authorised_grant_type")
public class ClientAuthorisedGrantTypeEntity extends BaseEntity<ClientAuthorisedGrantTypePk> {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private ClientAuthorisedGrantTypePk id;

    @Override
    public ClientAuthorisedGrantTypePk getId() {
        return id;
    }

    public void setId(ClientAuthorisedGrantTypePk id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientAuthorisedGrantTypeEntity that = (ClientAuthorisedGrantTypeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
