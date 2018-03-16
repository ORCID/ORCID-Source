package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;

public interface ClientRedirectDao extends GenericDao<ClientRedirectUriEntity, ClientRedirectUriPk> {

    public List<ClientRedirectUriEntity> findClientDetailsWithRedirectScope();

    void addClientRedirectUri(String clientId, String redirectUri);

    void removeClientRedirectUri(String clientId, String redirectUri);
}
