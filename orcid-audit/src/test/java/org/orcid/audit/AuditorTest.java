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
package org.orcid.audit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.audit.dao.AuditEventDao;
import org.orcid.audit.entities.AuditEventType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-audit-context.xml" })
public class AuditorTest {

    @Resource(name = "auditEventDao")
    private AuditEventDao auditEventDao;

    @Resource(name = "auditor")
    private Auditor auditor;

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
    public void testAuditor() throws InterruptedException {
        auditor.audit("0000-1234-1234-1234", "0000-1234-1234-1235", "USER", "192.168.1.223", "GB", AuditEventType.UPDATE, "API", "Updated a record");
        Thread.sleep(500L);
        assertEquals(Long.valueOf(1), auditEventDao.count());
    }

}
