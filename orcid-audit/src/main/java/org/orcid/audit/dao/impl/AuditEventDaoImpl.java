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
package org.orcid.audit.dao.impl;

import org.orcid.audit.dao.AuditEventDao;
import org.orcid.audit.entities.AuditEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
@PersistenceUnit(name = "auditEntityManagerFactory")
public class AuditEventDaoImpl implements AuditEventDao {

    @PersistenceContext(unitName = "orcid_audit")
    protected EntityManager entityManager;

    @Override
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRED)
    public Long count() {
        return (Long) entityManager.createQuery("select count(e) from AuditEvent e").getSingleResult();
    }

    @Override
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRED)
    public void persist(AuditEvent event) {
        entityManager.persist(event);
    }

    // Only use for testing as this will not notify the entityManager of the update
    @Override
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRED)
    public void removeAll() {
        Query query = entityManager.createQuery("delete from AuditEvent");
        query.executeUpdate();
    }

}
