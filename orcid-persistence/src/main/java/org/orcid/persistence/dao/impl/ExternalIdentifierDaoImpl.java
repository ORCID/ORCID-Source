/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.keys.ExternalIdentifierEntityPk;
import org.springframework.transaction.annotation.Transactional;

public class ExternalIdentifierDaoImpl extends GenericDaoImpl<ExternalIdentifierEntity, ExternalIdentifierEntityPk> implements ExternalIdentifierDao {

    public ExternalIdentifierDaoImpl() {
        super(ExternalIdentifierEntity.class);
    }

    /**
     * Removes an external identifier from database based on his ID.
     * The ID for external identifiers consists of the "orcid" of the owner and
     * the "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     * */
    @Override
    @Transactional
    public boolean removeExternalIdentifier(String orcid, String externalIdReference) {        
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where owner.id=:orcid and externalIdReference=:externalIdReference");
        query.setParameter("orcid", orcid);
        query.setParameter("externalIdReference", externalIdReference);
        return query.executeUpdate() > 0 ? true : false;
    }

}
