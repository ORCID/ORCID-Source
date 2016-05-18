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

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** This class allows you to send text messages via JMS/ActiveMQ.  
 * It is available as a Spring managed Bean.
 * 
 * Spring config will scan this package for Component annotated classes 
 * and register all methods annotated with JmsListener.
 * 
 * To create a listener, look at EchoTestMessageListener2 & 3 for examples of queue listeners.
 * 
 * It has VERY Naive failsafe - if the connection to the broker goes down, it stops trying to send messages for two minutes
 * it simply discards them.  Otherwise it would take the server down with it.
 * 
 * @author tom
 *
 */
@Component
public class JmsMessageSender {

    public static final String TEST = "test";
    public static final String TEST_REPLY = "test_reply";
    public static final String UPDATED_ORCIDS = "updated_orcids";
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsMessageSender.class);
    
    private boolean enabled = false;    
    private boolean discardForAWhile = false;
    
    public enum JmsDestination{
        TEST(JmsMessageSender.TEST),
        TEST_REPLY(JmsMessageSender.TEST_REPLY), 
        UPDATED_ORCIDS(JmsMessageSender.UPDATED_ORCIDS);
        public final String value;
        JmsDestination(String value){
            this.value = value;
        }
    }
    
    @Resource
    private JmsTemplate jmsTemplate;
    
    public void sendText(final String text, JmsDestination dest ) {
        try{
            if (isEnabled() && !discardForAWhile)
                jmsTemplate.convertAndSend(dest.value, text);
        }catch (JmsException e){
            flagConnectionProblem(e);
        }
    }
    
    public void sendMap(final Map<String,String> map, JmsDestination dest) {
        try{
            if (isEnabled() && !discardForAWhile)
                jmsTemplate.convertAndSend(dest.value, map);
        }catch(JmsException e){
            flagConnectionProblem(e);
        }
    }
    
    /** Silenty discard messages for a while
     * 
     */
    public void flagConnectionProblem(Exception e){
        LOG.error("JMS connection problem found, pausing messaging. "+e.getMessage());
        discardForAWhile = true;
    }
    
    public boolean isEnabled(){
        return enabled;
    }
    
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    /** retry connecting if bad after every couple of minutes
     * 
     */
    @Scheduled(fixedDelay=120000)
    public void timer(){
        synchronized(this){
            if (discardForAWhile){
                discardForAWhile = false;
            }            
        }
    }
    
}
