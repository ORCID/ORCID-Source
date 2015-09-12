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

import java.util.Date;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.orcid.persistence.dao.InternalSSODao;
import org.orcid.persistence.jpa.entities.InternalSSOEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Angel Montenegro
 */
@PersistenceContext(unitName = "orcid")
public class InternalSSODaoImpl extends GenericDaoImpl<InternalSSOEntity, String> implements InternalSSODao {

    public InternalSSODaoImpl() {
        super(InternalSSOEntity.class);
    }

    @Override
    @Transactional
    public InternalSSOEntity insert(String orcid, String token) {
        InternalSSOEntity entity = new InternalSSOEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(new Date());
        entity.setOrcid(orcid);
        entity.setToken(token);
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public boolean delete(String orcid) {
        Query query = entityManager.createNativeQuery("DELETE FROM internal_sso WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    public InternalSSOEntity update(String orcid, String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean verify(String orcid, String token, long maxAge) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void recordModified(String orcid, String token, long maxAge) {
        // TODO Auto-generated method stub

    }

}
