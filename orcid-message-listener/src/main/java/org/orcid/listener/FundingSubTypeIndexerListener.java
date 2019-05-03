package org.orcid.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.orcid.listener.solr.FundingSubTypeMessageProcessor;
import org.orcid.utils.solr.entities.OrgDefinedFundingTypeSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FundingSubTypeIndexerListener implements MessageListener {

    Logger LOG = LoggerFactory.getLogger(FundingSubTypeIndexerListener.class);

    private FundingSubTypeMessageProcessor processor;

    public FundingSubTypeIndexerListener(FundingSubTypeMessageProcessor processor){
        this.processor = processor;
    }
    
    protected OrgDefinedFundingTypeSolrDocument getObjectFromMessage(Message message) throws JMSException {
        ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
        Object obj = objectMessage.getObject();
        if (!OrgDefinedFundingTypeSolrDocument.class.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Unable to transofrm " + obj.getClass().getName() + " into a OrgDefinedFundingTypeSolrDocument");
        }
        return (OrgDefinedFundingTypeSolrDocument) obj;
    }

    @Override
    public void onMessage(Message message) {
        try {
            OrgDefinedFundingTypeSolrDocument obj = getObjectFromMessage(message);
            LOG.info("Recieved " + message.getJMSDestination() + " message for funding sub type" + obj.getOrgDefinedFundingType());
            processor.accept(obj);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

}
