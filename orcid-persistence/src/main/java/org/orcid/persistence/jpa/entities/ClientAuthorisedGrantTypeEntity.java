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
@IdClass(ClientAuthorisedGrantTypePk.class)
public class ClientAuthorisedGrantTypeEntity extends BaseEntity<ClientAuthorisedGrantTypePk> {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String grantType;

    @Id
    @Column(name = "client_details_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Id
    @Column(name = "grant_type")
    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientAuthorisedGrantTypeEntity that = (ClientAuthorisedGrantTypeEntity) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(grantType, that.grantType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, grantType);
    }

    /**
     * As this uses a composite key this is ignored. Always returns null
     *
     * @return always null
     */
    @Override
    @Transient
    public ClientAuthorisedGrantTypePk getId() {
        return null;
    }
}
