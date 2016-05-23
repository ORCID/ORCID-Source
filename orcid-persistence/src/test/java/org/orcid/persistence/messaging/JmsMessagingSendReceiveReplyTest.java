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
package org.orcid.persistence.messaging;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/** Note, this test only works if you have ActiveMQ working in the background on the default port
 * It must have "test" and "test_reply" queues available.
 * 
 * @author tom
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class JmsMessagingSendReceiveReplyTest {

    @Resource
    JmsMessageSender messageSender;
    
    @Test
    public void testConfig() throws InterruptedException{
        Long time = System.currentTimeMillis();
        messageSender.sendText("test "+time, JmsMessageSender.JmsDestination.TEST);        
        Thread.sleep(1000);
        assertEquals(EchoTestMessageListener2.lastMessage, "test "+time);
        Thread.sleep(1000);
        assertEquals(EchoTestMessageListener3.lastMessage, "Echo: test "+time);        
    }     
    
}
