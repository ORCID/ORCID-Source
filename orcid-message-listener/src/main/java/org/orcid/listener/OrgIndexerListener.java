package org.orcid.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.orcid.listener.solr.SolrOrgsMessageProcessor;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgIndexerListener implements MessageListener {

    Logger LOG = LoggerFactory.getLogger(OrgIndexerListener.class);

    private SolrOrgsMessageProcessor processor;

    public OrgIndexerListener(SolrOrgsMessageProcessor processor){
        this.processor = processor;
    }
    
    protected OrgDisambiguatedSolrDocument getObjectFromMessage(Message message) throws JMSException {
        ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
        Object obj = objectMessage.getObject();
        if (!OrgDisambiguatedSolrDocument.class.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Unable to transofrm " + obj.getClass().getName() + " into a OrgDisambiguatedSolrDocument");
        }
        return (OrgDisambiguatedSolrDocument) obj;
    }

    @Override
    public void onMessage(Message message) {
        try {
            OrgDisambiguatedSolrDocument obj = getObjectFromMessage(message);
            LOG.info("Recieved " + message.getJMSDestination() + " message for org disambiguated " + obj.getOrgDisambiguatedId());
            processor.accept(obj);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

}
