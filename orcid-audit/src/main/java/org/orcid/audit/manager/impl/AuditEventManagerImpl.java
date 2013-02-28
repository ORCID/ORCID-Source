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
package org.orcid.audit.manager.impl;

import org.orcid.audit.dao.AuditEventDao;
import org.orcid.audit.entities.AuditEvent;
import org.orcid.audit.manager.AuditEventManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
public class AuditEventManagerImpl implements AuditEventManager {

    @Resource
    private AuditEventDao auditEventDao;

    @Override
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public Long count() {
        return auditEventDao.count();
    }

    @Override
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void persist(AuditEvent event) {
        auditEventDao.persist(event);
    }
}
