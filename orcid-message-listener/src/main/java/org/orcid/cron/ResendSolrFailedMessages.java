package org.orcid.cron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.listener.persistence.entities.SearchEngineRecordStatusEntity;
import org.orcid.listener.persistence.managers.SearchEngineRecordStatusManager;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.MediaType;

@Configuration
@EnableScheduling
public class ResendSolrFailedMessages {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResendV3FailedMessages.class);
    private static final int BATCH_SIZE = 1000;
    
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    static ObjectMapper mapper = new ObjectMapper();
    
    @Value("${org.orcid.message-listener.retry:5}")
    private Integer maxFailuresBeforeNotify;
    
    @Value("${org.orcid.core.slack.webhookUrl:}")
    private String webhookUrl;
    
    @Resource
    private SearchEngineRecordStatusManager searchEngineRecordStatusManager;
    
    @Resource
    private SolrMessageProcessor proc;
    
    @Scheduled(cron = "${org.orcid.cron.solr.reindex-failed:0 0 8,15 * * *}")
    public void resendFailedElements() {
        LOGGER.info("Processing failed elements for V2.0");
        // Get elements that failed
        List<SearchEngineRecordStatusEntity> failedElements = searchEngineRecordStatusManager.getFailedElements(BATCH_SIZE);
        List<SearchEngineRecordStatusEntity> elementsToNotify = new ArrayList<SearchEngineRecordStatusEntity>();
        
        for (SearchEngineRecordStatusEntity element : failedElements) {
            if(element.getSolrStatus() > 0) {
                if(element.getSolrStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
                                    
            RetryMessage message = new RetryMessage(element.getId(), null);
            proc.accept(message);
        }

        // Send summary
        if (!elementsToNotify.isEmpty()) {
            String message = buildAlertMessage(elementsToNotify);
            sendSystemAlert(message);
        }
    }
    
    private String buildAlertMessage(List<SearchEngineRecordStatusEntity> elements) {
        StringBuilder sb = new StringBuilder("The following records failed to be sent to SOLR by the message listener: ");
        sb.append(System.lineSeparator() + System.lineSeparator());

        int counter = 0;
        for (SearchEngineRecordStatusEntity element : elements) {            
            sb.append("*ORCID: '").append(element.getId()).append("':* ");

            if(element.getSolrStatus() > 0) {
                sb.append(" Failed attempts: ");
                sb.append(element.getSolrStatus());
                sb.append(" Last time it was indexed: ");
                sb.append(element.getSolrLastIndexed() == null ? "never" : element.getSolrLastIndexed());
            }
            
            sb.append(System.lineSeparator());
            
            // Notify only the first 10 failures
            counter += 1;
            if(counter == 10) {
                break;
            }
        }

        return sb.toString();
    }
    
    private void sendSystemAlert(String message) {
        if (StringUtils.isNotBlank(webhookUrl)) {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("text", message);
            String bodyJson = null;
            try {
                bodyJson = mapper.writeValueAsString(bodyMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            JerseyClientResponse<String, String> response = jerseyClientHelper.executePostRequest(webhookUrl, MediaType.APPLICATION_JSON_TYPE, bodyJson, String.class, String.class);
            int status = response.getStatus();
            if (status != 200) {
                LOGGER.warn("Unable to send message to Slack, status={}, error={}, message={}", new Object[] { status, response.getError(), message });
            }
        }
    }
}
