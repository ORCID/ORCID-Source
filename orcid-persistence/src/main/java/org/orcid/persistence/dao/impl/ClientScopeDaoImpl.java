package org.orcid.persistence.dao.impl;

import javax.persistence.Query;

import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.keys.ClientScopePk;
import org.springframework.transaction.annotation.Transactional;

public class ClientScopeDaoImpl extends GenericDaoImpl<ClientScopeEntity, ClientScopePk> implements ClientScopeDao {    

    public ClientScopeDaoImpl() {
        super(ClientScopeEntity.class);
    }

    /**
     * Removes a client secret key
     * 
     * @param clientId
     * @param clientSecret
     * @return true if a entity is removed
     * */
    @Override
    @Transactional
    public boolean deleteScope(String clientId, String scopeType) {
        Query deleteQuery = entityManager.createNativeQuery("delete from client_scope where client_details_id=:clientId and scope_type=:scopeType");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("scopeType", scopeType);
        return deleteQuery.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public void insertClientScope(String clientDetailsId, String scope) {
        Query insertQuery = entityManager.createNativeQuery("INSERT INTO client_scope (date_created, last_modified, client_details_id, scope_type) VALUES (now(), now(), :clientDetailsId, :scope)");
        insertQuery.setParameter("clientDetailsId", clientDetailsId);
        insertQuery.setParameter("scope", scope);
        insertQuery.executeUpdate();
    }

}
