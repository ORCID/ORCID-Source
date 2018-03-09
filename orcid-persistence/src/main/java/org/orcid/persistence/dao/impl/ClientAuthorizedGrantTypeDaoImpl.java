package org.orcid.persistence.dao.impl;

import javax.persistence.Query;

import org.orcid.persistence.dao.ClientAuthorizedGrantTypeDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;
import org.springframework.transaction.annotation.Transactional;

public class ClientAuthorizedGrantTypeDaoImpl extends GenericDaoImpl<ClientAuthorisedGrantTypeEntity, ClientAuthorisedGrantTypePk>
        implements ClientAuthorizedGrantTypeDao {

    public ClientAuthorizedGrantTypeDaoImpl() {
        super(ClientAuthorisedGrantTypeEntity.class);
    }

    @Override
    @Transactional
    public void insertClientAuthorizedGrantType(String clientDetailsId, String type) {
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO client_authorised_grant_type (date_created, last_modified, client_details_id, grant_type) VALUES (now(), now(), :clientDetailsId, :type)");
        insertQuery.setParameter("clientDetailsId", clientDetailsId);
        insertQuery.setParameter("type", type);
        insertQuery.executeUpdate();
    }
}
