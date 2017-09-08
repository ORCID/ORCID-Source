/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
