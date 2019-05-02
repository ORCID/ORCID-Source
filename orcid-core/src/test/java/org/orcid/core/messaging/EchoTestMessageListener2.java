package org.orcid.core.messaging;

import javax.annotation.Resource;

import org.orcid.core.messaging.JmsMessageSender;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;

// use @Component or add as a bean in the XML config.
public class EchoTestMessageListener2 {

    public static String lastMessage = "";
    
    @Resource
    JmsMessageSender sender;
    
    @JmsListener(destination=MessageConstants.Queues.TEST)
    @SendTo(MessageConstants.Queues.TEST_REPLY)
    public String processMessage(String text) {
      lastMessage = text;
      return "Echo: "+ text;
    }
}
