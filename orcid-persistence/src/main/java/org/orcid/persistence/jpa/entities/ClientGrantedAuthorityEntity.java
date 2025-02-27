package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientGrantedAuthorityPk;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_granted_authority")
public class ClientGrantedAuthorityEntity extends BaseEntity<ClientGrantedAuthorityPk> implements GrantedAuthority {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private ClientGrantedAuthorityPk id;

    @Override
    public ClientGrantedAuthorityPk getId() {
        return id;
    }

    public void setId(ClientGrantedAuthorityPk id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientGrantedAuthorityEntity that = (ClientGrantedAuthorityEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String getAuthority() {
        return (this.getId() == null) ? null : this.getId().getAuthority();
    }
}
