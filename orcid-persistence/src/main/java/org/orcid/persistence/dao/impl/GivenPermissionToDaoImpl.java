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

import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
@PersistenceContext(unitName="orcid")
public class GivenPermissionToDaoImpl extends GenericDaoImpl<GivenPermissionToEntity, Long> implements GivenPermissionToDao {

    public GivenPermissionToDaoImpl() {
        super(GivenPermissionToEntity.class);
    }

    @Override
    @Transactional
    public void remove(String giverOrcid, String receiverOrcid) {
        Query query = entityManager.createQuery("delete from GivenPermissionToEntity g where g.giver = :giverOrcid and g.receiver.id = :receiverOrcid");
        query.setParameter("giverOrcid", giverOrcid);
        query.setParameter("receiverOrcid", receiverOrcid);
        query.executeUpdate();
    }

}
