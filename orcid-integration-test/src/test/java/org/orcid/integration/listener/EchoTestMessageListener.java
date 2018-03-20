package org.orcid.integration.listener;

import java.util.Map;

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.jms.annotation.JmsListener;

// use @Component or add as a bean in the XML config.
public class EchoTestMessageListener {

    public static LastModifiedMessage message = null;
    
    @JmsListener(destination=MessageConstants.Queues.TEST)
    public void processMessage(final Map<String, String> map) {
      message = new LastModifiedMessage(map);
    }
}
