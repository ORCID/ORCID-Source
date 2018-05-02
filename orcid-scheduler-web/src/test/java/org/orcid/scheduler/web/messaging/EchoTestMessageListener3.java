package org.orcid.scheduler.web.messaging;

import javax.annotation.Resource;

import org.orcid.scheduler.messaging.JmsMessageSender;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.jms.annotation.JmsListener;

// use @Component or add as a bean in the XML config.
public class EchoTestMessageListener3 {

    public static String lastMessage = "";
    
    @Resource
    JmsMessageSender sender;
    
    @JmsListener(destination=MessageConstants.Queues.TEST_REPLY)
    public void processMessage(String text) {
      lastMessage = text;
    }
}
