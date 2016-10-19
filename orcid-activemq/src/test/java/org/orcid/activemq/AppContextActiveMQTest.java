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
package org.orcid.activemq;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.apache.activemq.xbean.BrokerFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-activemq-web-context.xml" })
public class AppContextActiveMQTest {

    @Resource
    private BrokerFactoryBean activeMQ;
    
    @Test
    public void testServerRunning() throws Exception {
        assertNotNull(activeMQ);
        assertTrue(activeMQ.getBroker().isStarted());
        assertNull(activeMQ.getBroker().getStartException());
    }
}
