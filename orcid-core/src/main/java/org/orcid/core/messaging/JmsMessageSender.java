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
package org.orcid.core.messaging;

import javax.annotation.Resource;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/** This class allows you to send text messages via JMS/ActiveMQ.  
 * It is available as a Spring managed Bean.
 * 
 * Spring config will scan this package for Component annotated classes 
 * and register all methods annotated with JmsListener.
 * 
 * To create a listener, look at EchoTestMessageListener2 & 3 for examples of queue listeners.
 * 
 * TODO: expand to work with other types via JSON/XML serialisation etc.
 * 
 * @author tom
 *
 */
@Component
public class JmsMessageSender {

    public static final String TEST = "test";
    public static final String TEST_REPLY = "test_reply";
    
    public enum JmsDestination{
        TEST(JmsMessageSender.TEST),
        TEST_REPLY(JmsMessageSender.TEST_REPLY);
        public final String value;
        JmsDestination(String value){
            this.value = value;
        }
    }
    
    @Resource
    private JmsTemplate jmsTemplate;
    
    public void sendText(final String text, JmsDestination dest ) {
        jmsTemplate.convertAndSend(dest.value, text);
    }
    
}
