package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;

public interface ClientAuthorizedGrantTypeDao extends GenericDao<ClientAuthorisedGrantTypeEntity, ClientAuthorisedGrantTypePk> {

    void insertClientAuthorizedGrantType(String clientDetailsId, String type);
}
