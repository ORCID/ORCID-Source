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

import javax.annotation.Resource;

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
