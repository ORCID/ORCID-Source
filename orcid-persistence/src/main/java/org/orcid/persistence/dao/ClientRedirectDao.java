package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ClientRedirectDao extends GenericDao<ClientRedirectUriEntity, ClientRedirectUriPk> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<ClientRedirectUriEntity> findClientDetailsWithRedirectScope(String redirectUriType);

    @Transactional(propagation = Propagation.REQUIRED)
    void addClientRedirectUri(String clientId, String redirectUri);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeClientRedirectUri(String clientId, String redirectUri);

    @Transactional(propagation = Propagation.REQUIRED)
    public void addClientRedirectUri(String clientId, String uri, String value, String scope);
}
