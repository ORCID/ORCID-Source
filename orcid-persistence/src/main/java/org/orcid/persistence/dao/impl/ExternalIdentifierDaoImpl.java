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

import java.util.List;

import javax.persistence.Query;

import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ExternalIdentifierDaoImpl extends GenericDaoImpl<ExternalIdentifierEntity, Long> implements ExternalIdentifierDao {

    public ExternalIdentifierDaoImpl() {
        super(ExternalIdentifierEntity.class);
    }

    /**
     * Removes an external identifier from database based on his ID. The ID for
     * external identifiers consists of the "orcid" of the owner and the
     * "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     */
    @Override
    @Transactional
    public boolean removeExternalIdentifier(String orcid, String externalIdReference) {
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where owner.id=:orcid and externalIdReference=:externalIdReference");
        query.setParameter("orcid", orcid);
        query.setParameter("externalIdReference", externalIdReference);
        return query.executeUpdate() > 0 ? true : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable(value = "dao-external-identifiers", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE owner.id = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, Visibility visibility) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE owner.id = :orcid and visibility = :visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }

    @Override
    public ExternalIdentifierEntity getExternalIdentifierEntity(String orcid, Long id) {
        Query query = entityManager.createQuery("FROM ExternalIdentifierEntity WHERE owner.id = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return (ExternalIdentifierEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean removeExternalIdentifier(String orcid, Long id) {
        Query query = entityManager.createQuery("delete from ExternalIdentifierEntity where owner.id=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

}
