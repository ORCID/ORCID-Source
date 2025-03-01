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
@IdClass(ClientGrantedAuthorityPk.class)
public class ClientGrantedAuthorityEntity extends BaseEntity<ClientGrantedAuthorityPk> implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String authority;

    @Id
    @Column(name = "client_details_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Id
    @Column(name = "granted_authority")
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientGrantedAuthorityEntity that = (ClientGrantedAuthorityEntity) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, authority);
    }

    /**
     * As this uses a composite key this is ignored. Always returns null
     *
     * @return always null
     */
    @Override
    @Transient
    public ClientGrantedAuthorityPk getId() {
        return null;
    }
}
