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
package org.orcid.audit.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.audit.entities.AuditEvent;
import org.orcid.audit.entities.AuditEventType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 19/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-audit-context.xml" })
public class AuditEventDaoTest  {

    @Resource(name = "auditEventDao")
    private AuditEventDao auditEventDao;

    @Before
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void init() {
        auditEventDao.removeAll();
    }

    @After
    @Transactional(value = "auditTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void cleanup() {
        auditEventDao.removeAll();
    }

    @Test
    @Rollback
    public void testFindById() throws Exception {
        AuditEvent event = new AuditEvent();
        event.setEventDescription("Event description");
        event.setEventMethod("API");
        event.setRecordModifierType("USER");
        event.setEventType(AuditEventType.UPDATE);
        event.setRecordModifiedOrcid("0000-1111-2222-3333");
        event.setRecordModifierIp("192.168.1.23");
        event.setRecordModifierIso2Country("GB");
        event.setRecordModifierOrcid("0000-1111-2222-3334");
        auditEventDao.persist(event);
        assertEquals(Long.valueOf(1), auditEventDao.count());
        auditEventDao.removeAll();
    }



}
