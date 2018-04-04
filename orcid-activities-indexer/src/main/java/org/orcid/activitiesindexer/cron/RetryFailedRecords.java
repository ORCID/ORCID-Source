package org.orcid.activitiesindexer.cron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.activitiesindexer.persistence.entities.ActivitiesStatusEntity;
import org.orcid.activitiesindexer.persistence.managers.ActivitiesStatusManager;
import org.orcid.activitiesindexer.persistence.util.ActivityType;
import org.orcid.activitiesindexer.s3.S3MessageProcessor;
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
    private ActivitiesStatusManager manager;

    @Resource
    private S3MessageProcessor s3Processor;

    public RetryFailedRecords() {
        client = Client.create();
        mapper = new ObjectMapper();
    }

    @Scheduled(cron = "${org.orcid.cron.reindex-failed:0 0 */12 * * *}")
    public void resendFailedElements() {
        List<ActivitiesStatusEntity> failedElements = manager.getFailedElements(BATCH_SIZE);
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

        // Send summary
        if (!elementsToNotify.isEmpty()) {
            String message = buildNotificationMessage(elementsToNotify);
            sendSystemAlert(message);
        }
    }

    private String buildNotificationMessage(List<ActivitiesStatusEntity> elements) {
        StringBuilder sb = new StringBuilder("The following records failed to be processed in the activities indexer: ");
        sb.append(System.lineSeparator() + System.lineSeparator());

        for (ActivitiesStatusEntity element : elements) {
            sb.append("*ORCID: '").append(element.getId()).append("':* ");
            if (element.getEducationsStatus() > maxFailuresBeforeNotify) {
                sb.append(" (Educations: ");
                sb.append(element.getEducationsStatus());
                sb.append(" failures)");
            }

            if (element.getEmploymentsStatus() > maxFailuresBeforeNotify) {
                sb.append(" (Employments: ");
                sb.append(element.getEmploymentsStatus());
                sb.append(" failures)");
            }

            if (element.getFundingsStatus() > maxFailuresBeforeNotify) {
                sb.append(" (Fundings: ");
                sb.append(element.getFundingsStatus());
                sb.append(" failures)");
            }

            if (element.getWorksStatus() > maxFailuresBeforeNotify) {
                sb.append(" (Works: ");
                sb.append(element.getWorksStatus());
                sb.append(" failures)");
            }

            if (element.getPeerReviewsStatus() > maxFailuresBeforeNotify) {
                sb.append(" (PeerReviews: ");
                sb.append(element.getPeerReviewsStatus());
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