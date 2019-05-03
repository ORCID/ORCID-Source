package org.orcid.core.messaging;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.solr.entities.OrgDefinedFundingTypeSolrDocument;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
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
    
    @Resource
    private JmsTemplate jmsTemplate;
    
    @Resource(name = "orgDisambiguatedSolrServer")
    private SolrServer solrServer;
    
    protected boolean sendObject(final Object obj, String destination) throws JmsException{
        if (isEnabled() && !pauseForAWhile){
            jmsTemplate.convertAndSend(destination, obj);
            return true;
        }
        LOG.info("Not sending message: isEnabled="+isEnabled()+" pauseForAWhile"+pauseForAWhile);
        return false;            
    }
    
    protected boolean sendText(final String text, String destination) throws JmsException{
        if (isEnabled() && !pauseForAWhile){
            jmsTemplate.convertAndSend(destination, text);
            return true;
        }
        LOG.info("Not sending message: isEnabled="+isEnabled()+" pauseForAWhile"+pauseForAWhile);
        return false;            
    }
    
    protected boolean sendMap(final Map<String,String> map, String destination) throws JmsException{
        if (isEnabled() && !pauseForAWhile){
            jmsTemplate.convertAndSend(destination, map);
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
    public boolean send(LastModifiedMessage mess, String destination){
        try{
            return this.sendMap(mess.getMap(), destination);                             
        } catch(JmsException e) {
            //TODO: How we unflag the problem?
            //flagConnectionProblem(e);
            LOG.error("Couldnt send " + mess.getOrcid() + " to the message queue", e);
        }
        return false;
    }
    
    /**Sends a OrgDisambiguatedSolrDocument to the selected queue
     * 
     * @param mess the message
     * @param d the destination queue
     * @return true if message sent successfully 
     */
    public boolean send(OrgDisambiguatedSolrDocument mess, String destination){
        try{
            return this.sendObject(mess, destination);                             
        } catch(JmsException e) {
            //TODO: How we unflag the problem?
            //flagConnectionProblem(e);
            LOG.error("Couldnt send message for disambiguated id " + mess.getOrgDisambiguatedId() + " to the message queue", e);
        }
        return false;
    }
    
    /**Sends a OrgDefinedFundingTypeSolrDocument to the selected queue
     * 
     * @param mess the message
     * @param d the destination queue
     * @return true if message sent successfully 
     */
    public boolean send(OrgDefinedFundingTypeSolrDocument mess, String destination){
        try{
            return this.sendObject(mess, destination);                             
        } catch(JmsException e) {
            //TODO: How we unflag the problem?
            //flagConnectionProblem(e);
            LOG.error("Couldnt send message for fundingSubType " + mess.getOrgDefinedFundingType() + " to the message queue", e);
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
