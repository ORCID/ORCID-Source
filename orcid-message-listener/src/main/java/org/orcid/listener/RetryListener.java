package org.orcid.listener;

import java.util.Map;
import java.util.MissingResourceException;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.MessageConstants;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class RetryListener {

    Logger LOG = LoggerFactory.getLogger(RetryListener.class);

    @Resource
    private S3MessageProcessor s3Processor;

    @Resource
    private SolrMessageProcessor solrProcessor;

    /**
     * Processes messages on receipt.
     * 
     * @param map
     * @throws JsonProcessingException
     * @throws JAXBException
     * @throws AmazonClientException
     */
    @JmsListener(destination = MessageConstants.Queues.RETRY)
    public void processMessage(final Map<String, String> map) throws JsonProcessingException, AmazonClientException, JAXBException {
        RetryMessage message = new RetryMessage(map);
        String orcid = message.getOrcid();
        if (message.getMap() == null || message.getMap().get(RetryMessage.BROKER_NAME) == null) {
            throw new MissingResourceException("Unable to find destination broker", String.class.getName(), RetryMessage.BROKER_NAME);
        }
        AvailableBroker destinationBroker = AvailableBroker.fromValue(message.getMap().get(RetryMessage.BROKER_NAME));
        LOG.info("Recieved " + MessageConstants.Queues.RETRY + " message for orcid " + orcid + " to broker " + destinationBroker);
        if (AvailableBroker.DUMP_STATUS_1_2_API.equals(destinationBroker) || AvailableBroker.DUMP_STATUS_2_0_API.equals(destinationBroker)) {
            s3Processor.accept(message);
        } else if (AvailableBroker.SOLR.equals(destinationBroker)) {
            solrProcessor.accept(message);
        }
    }
}
