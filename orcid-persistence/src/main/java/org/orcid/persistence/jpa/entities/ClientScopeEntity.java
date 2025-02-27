package org.orcid.persistence.jpa.entities;

import javax.persistence.*;

import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_scope")
public class ClientScopeEntity extends BaseEntity<ClientScopePk> {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ClientScopePk id;

    @Override
    public ClientScopePk getId() {
        return id;
    }

    public void setId(ClientScopePk id) {
        this.id = id;
    }
}
