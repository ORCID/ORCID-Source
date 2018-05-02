package org.orcid.scheduler.web.messaging;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.scheduler.messaging.JmsMessageSender;
import org.springframework.test.context.ContextConfiguration;

/** Note, this test only works if you have ActiveMQ working in the background on the default port
 * It must have "test" and "test_reply" queues available.
 * 
 * @author tom
 *
 */
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class JmsMessagingSendReceiveReplyTest {

    @Resource
    JmsMessageSender messageSender;
    
    @Test
    public void testConfig() throws InterruptedException{
        Long time = System.currentTimeMillis();
        messageSender.sendText("test "+time, "test");        
        Thread.sleep(1000);
        assertEquals(EchoTestMessageListener2.lastMessage, "test "+time);
        Thread.sleep(1000);
        assertEquals(EchoTestMessageListener3.lastMessage, "Echo: test "+time);        
    }     
    
}
