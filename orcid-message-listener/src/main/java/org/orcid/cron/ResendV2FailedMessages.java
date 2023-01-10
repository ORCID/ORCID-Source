package org.orcid.cron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.listener.persistence.entities.Api20RecordStatusEntity;
import org.orcid.listener.persistence.managers.Api20RecordStatusManager;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.s3.S3MessageProcessorAPIV3;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
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
public class ResendV2FailedMessages {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResendV2FailedMessages.class);
    private static final int BATCH_SIZE = 1000;
    
    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    static ObjectMapper mapper = new ObjectMapper();
    
    @Value("${org.orcid.message-listener.retry:5}")
    private Integer maxFailuresBeforeNotify;
    
    @Value("${org.orcid.core.slack.webhookUrl:}")
    private String webhookUrl;
    
    @Resource
    private Api20RecordStatusManager api20RecordStatusManager;
    
    @Resource
    private S3MessageProcessorAPIV3 proc;
    
    @Scheduled(cron = "${org.orcid.cron.v2.reindex-failed:0 0 4,11 * * *}")
    public void resendFailedElements() {
        LOGGER.info("Processing failed elements for V2.0");
        List<ActivityType> retryList = new ArrayList<ActivityType>();
        // Get elements that failed
        List<Api20RecordStatusEntity> failedElements = api20RecordStatusManager.getFailedElements(BATCH_SIZE);
        List<Api20RecordStatusEntity> elementsToNotify = new ArrayList<Api20RecordStatusEntity>();
        
        for (Api20RecordStatusEntity element : failedElements) {
            boolean summaryFailed = false;
            
            if(element.getSummaryStatus() > 0) {
                summaryFailed = true;
                if(element.getSummaryStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            if(element.getEducationsStatus() > 0) {
                retryList.add(ActivityType.EDUCATIONS);
                if(element.getEducationsStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            if(element.getEmploymentsStatus() > 0) {
                retryList.add(ActivityType.EMPLOYMENTS);
                if(element.getEmploymentsStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            if(element.getFundingsStatus() > 0) {
                retryList.add(ActivityType.FUNDINGS);
                if(element.getFundingsStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            if(element.getPeerReviewsStatus() > 0) {
                retryList.add(ActivityType.PEER_REVIEWS);
                if(element.getPeerReviewsStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            if(element.getWorksStatus() > 0) {
                retryList.add(ActivityType.WORKS);
                if(element.getWorksStatus() > maxFailuresBeforeNotify) {
                    elementsToNotify.add(element);
                }
            }
            
            proc.retry(element.getId(), summaryFailed, retryList);
        }

        // Send summary
        if (!elementsToNotify.isEmpty()) {
            String message = buildAlertMessage(elementsToNotify);
            sendSystemAlert(message);
        }
    }
    
    private String buildAlertMessage(List<Api20RecordStatusEntity> elements) {
        StringBuilder sb = new StringBuilder("The following records failed to be processed in the message listener: ");
        sb.append(System.lineSeparator() + System.lineSeparator());

        int counter = 0;
        for (Api20RecordStatusEntity element : elements) {            
            sb.append("*ORCID: '").append(element.getId()).append("':* ");

            if(element.getSummaryStatus() > 0) {
                sb.append(" (3.0 Summary: ");
                sb.append(element.getSummaryStatus());
                sb.append(" failures)");
            }
            
            if(element.getEducationsStatus() > 0) {
                sb.append(" (3.0 Educations: ");
                sb.append(element.getEducationsStatus());
                sb.append(" failures)");
            }
            
            if(element.getEmploymentsStatus() > 0) {
                sb.append(" (3.0 Employments: ");
                sb.append(element.getEmploymentsStatus());
                sb.append(" failures)");
            }
            
            if(element.getFundingsStatus() > 0) {
                sb.append(" (3.0 Fundings: ");
                sb.append(element.getFundingsStatus());
                sb.append(" failures)");
            }
            
            if(element.getPeerReviewsStatus() > 0) {
                sb.append(" (3.0 Peer Reviews: ");
                sb.append(element.getPeerReviewsStatus());
                sb.append(" failures)");
            }
            
            if(element.getWorksStatus() > 0) {
                sb.append(" (3.0 Works: ");
                sb.append(element.getWorksStatus());
                sb.append(" failures)");
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
