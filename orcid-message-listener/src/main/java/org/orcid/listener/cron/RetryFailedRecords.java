package org.orcid.listener.cron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.listener.persistence.entities.ActivitiesStatusEntity;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.managers.ActivitiesStatusManager;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.solr.SolrMessageProcessor;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Configuration
@EnableScheduling
public class RetryFailedRecords {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryFailedRecords.class);
    private static final int BATCH_SIZE = 1000;

    private Client client;

    static ObjectMapper mapper;

    @Value("${org.orcid.message-listener.retry:5}")
    private Integer maxFailuresBeforeNotify;

    @Value("${org.orcid.core.slack.webhookUrl:}")
    private String webhookUrl;

    @Autowired
    private RecordStatusManager manager;

    @Autowired
    private ActivitiesStatusManager activitiesManager;
    
    @Resource
    private S3MessageProcessor s3Processor;

    @Resource
    private SolrMessageProcessor solrProcessor;
    
    public RetryFailedRecords() {
        client = Client.create();
        mapper = new ObjectMapper();
    }

    @Scheduled(cron = "${org.orcid.cron.reindex-failed:0 0 */12 * * *}")
    public void resendFailedSummaries() {
        // Process summaries first
        List<RecordStatusEntity> failedElements = manager.getFailedElements(BATCH_SIZE);
        List<RecordStatusEntity> elementsToNotify = new ArrayList<RecordStatusEntity>();

        for (RecordStatusEntity element : failedElements) {
            try {
                // Send RetryMessage for solr indexing
                if (element.getSolrStatus20Api() > 0) {
                    RetryMessage message = new RetryMessage(element.getId(), AvailableBroker.SOLR.value());
                    solrProcessor.accept(message);
                }
                
                // Send RetryMessage for summaries
                if(element.getDumpStatus20Api() > 0) {
                    RetryMessage message = new RetryMessage(element.getId(), AvailableBroker.DUMP_STATUS_2_0_API.value());
                    s3Processor.update20Summary(message);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                // Should we notify about this element?
                if ((element.getDumpStatus20Api() > maxFailuresBeforeNotify)
                        || (element.getSolrStatus20Api() > maxFailuresBeforeNotify)) {
                    elementsToNotify.add(element);
                }
            }
        }

        // Send summary
        if (!elementsToNotify.isEmpty()) {
            String message = buildNotificationMessage(elementsToNotify);
            sendSystemAlert(message);
        }
    }

    @Scheduled(cron = "${org.orcid.cron.reindex-failed:0 0 */12 * * *}")
    public void resendFailedActivities() {
        // Process activities second
        List<ActivitiesStatusEntity> failedElements = activitiesManager.getFailedElements(BATCH_SIZE);
        List<ActivitiesStatusEntity> elementsToNotify = new ArrayList<ActivitiesStatusEntity>();

        for (ActivitiesStatusEntity element : failedElements) {
            try {
                List<ActivityType> retryTypes = new ArrayList<ActivityType>();

                // Send RetryMessage for Educations
                if (element.getEducationsStatus() > 0) {
                    retryTypes.add(ActivityType.EDUCATIONS);
                }
                // Send RetryMessage for Employments
                if (element.getEmploymentsStatus() > 0) {
                    retryTypes.add(ActivityType.EMPLOYMENTS);
                }
                // Send RetryMessage for Fundings
                if (element.getFundingsStatus() > 0) {
                    retryTypes.add(ActivityType.FUNDINGS);
                }
                // Send RetryMessage for Peer Reviews
                if (element.getPeerReviewsStatus() > 0) {
                    retryTypes.add(ActivityType.PEER_REVIEWS);
                }
                // Send RetryMessage for Works
                if (element.getWorksStatus() > 0) {
                    retryTypes.add(ActivityType.WORKS);
                }

                s3Processor.retry(element.getId(), retryTypes);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                // Should we notify about this element?
                if ((element.getEducationsStatus() > maxFailuresBeforeNotify) || (element.getEmploymentsStatus() > maxFailuresBeforeNotify)
                        || (element.getFundingsStatus() > maxFailuresBeforeNotify) || element.getWorksStatus() > maxFailuresBeforeNotify
                        || element.getPeerReviewsStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
        }
    }
    
    private String buildNotificationMessage(List<RecordStatusEntity> elements) {
        StringBuilder sb = new StringBuilder("The following records failed to be processed in the message listener: ");
        sb.append(System.lineSeparator() + System.lineSeparator());

        for (RecordStatusEntity element : elements) {
            sb.append("*ORCID: '").append(element.getId()).append("':* ");
            if (element.getDumpStatus12Api() > maxFailuresBeforeNotify) {
                sb.append(" (1.2 API Dump: ");
                sb.append(element.getDumpStatus12Api());
                sb.append(" failures)");
            }

            if (element.getDumpStatus20Api() > maxFailuresBeforeNotify) {
                sb.append(" (2.0 API Dump: ");
                sb.append(element.getDumpStatus20Api());
                sb.append(" failures)");
            }

            if (element.getSolrStatus20Api() > maxFailuresBeforeNotify) {
                sb.append(" (2.0 Solr indexing: ");
                sb.append(element.getSolrStatus20Api());
                sb.append(" failures)");
            }

            sb.append(System.lineSeparator());
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

            WebResource resource = client.resource(webhookUrl);
            ClientResponse response = resource.entity(bodyJson).post(ClientResponse.class);
            int status = response.getStatus();
            if (status != 200) {
                LOGGER.warn("Unable to send message to Slack, status={}, error={}, message={}", new Object[] { status, response.getEntity(String.class), message });
            }
        }
    }
}