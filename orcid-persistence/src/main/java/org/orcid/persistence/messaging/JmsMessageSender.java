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

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
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
 * It has a failsafe - if the connection to the broker goes down, it stops trying to send messages for one minute.
 * send(LastModifiedMessage mess, JmsDestination d) will instead write REINDEX flags to records so that they are picked up by the scheduler 
 * Different logic can be implemented for future message types.
 * 
 * @author tom
 *
 */
@Component
public class JmsMessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(JmsMessageSender.class);
    
    private boolean enabled = false;    
    private boolean pauseForAWhile = false;
    
    public enum JmsDestination{
        TEST(MessageConstants.Queues.TEST),
        TEST_REPLY(MessageConstants.Queues.TEST_REPLY), 
        UPDATED_ORCIDS(MessageConstants.Queues.UPDATED_ORCIDS), 
        REINDEX(MessageConstants.Queues.REINDEX);        
        public final String value;
        JmsDestination(String value){
            this.value = value;
        }
    }
        
    private JmsTemplate jmsTemplate;
    
    public void setJmsTemplate(JmsTemplate otherJmsTemplate) {
    	this.jmsTemplate = otherJmsTemplate;
    }
    
    protected boolean sendText(final String text, JmsDestination dest ) throws JmsException{
        if (isEnabled() && !pauseForAWhile){
            jmsTemplate.convertAndSend(dest.value, text);
            return true;
        }
        LOG.info("Not sending message: isEnabled="+isEnabled()+" pauseForAWhile"+pauseForAWhile);
        return false;
            
    }
    
    protected boolean sendMap(final Map<String,String> map, JmsDestination dest) throws JmsException{
        if (isEnabled() && !pauseForAWhile){
            jmsTemplate.convertAndSend(dest.value, map);
            return true;
        }
        LOG.info("Not sending message: isEnabled="+isEnabled()+" pauseForAWhile="+pauseForAWhile);
        return false;                
    }
    
    /**Sends a LastModifiedMessage to the selected queue
     * 
     * @param mess the message
     * @param d the destination queue
     * @return true if message sent successfully 
     */
    public boolean send(LastModifiedMessage mess, JmsDestination d){
        try{
            return this.sendMap(mess.getMap(), d);                             
        } catch(JmsException e) {
            //TODO: How we unflag the problem?
            //flagConnectionProblem(e);
            LOG.error("Couldnt send " + mess.getOrcid() + " to the message queue", e);
        }
        return false;
    }
    
    /** Silenty discard messages for a while
     * 
     */
    public void flagConnectionProblem(Exception e){
        LOG.error("JMS connection problem found, pausing messaging. "+e.getMessage());
        pauseForAWhile = true;
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
    @Scheduled(fixedDelay=60000)
    public void timer(){
        synchronized(this){
            if (pauseForAWhile){
                pauseForAWhile = false;
            }            
        }
    }
    
}
