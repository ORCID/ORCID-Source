package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

public interface ClientScopeDao extends GenericDao<ClientScopeEntity, ClientScopePk> {

    boolean deleteScope(String clientId, String scopeType);

    void insertClientScope(String clientDetailsId, String scope);
    
    List<String> getActiveScopes(String clientDetailsId);
}
