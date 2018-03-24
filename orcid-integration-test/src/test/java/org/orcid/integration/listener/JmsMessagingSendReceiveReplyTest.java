package org.orcid.integration.listener;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;

/** Note, this test only works if you have ActiveMQ working in the background on the default port
 * It must have "test" and "test_reply" queues available.
 * 
 * @author tom
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-listener-context.xml" })
public class JmsMessagingSendReceiveReplyTest {

    @Resource
    private JmsTemplate jmsTemplate;
    
    private final String orcid = "";    
    
    @Test
    public void testMessagBrokerWorking() throws InterruptedException{
        Date now = new Date();
        LastModifiedMessage mess = new LastModifiedMessage(orcid, now);
        jmsTemplate.convertAndSend(MessageConstants.Queues.TEST, mess.map);
        Thread.sleep(1000);
        assertEquals(EchoTestMessageListener.message.getOrcid(), orcid);
        assertEquals(EchoTestMessageListener.message.getLastUpdated(), now);
    }
    
}
