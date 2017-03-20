package org.orcid.cron;

import java.util.List;

import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class HandleFailedMessages {

    private static final int BATCH_SIZE = 1000;
    
    @Value("${org.orcid.message-listener.retry:3}")
    private Integer maxFailuresBeforeNotify;
    
    @Autowired
    private RecordStatusManager manager;
    
    @Scheduled(fixedDelay=5000)
    public void resendFailedElements() {
        
        List<RecordStatusEntity> failedElements = null;
        do {
            failedElements = manager.getFailedElements(1000);
            
            for(RecordStatusEntity element : failedElements) {
                if(element.getDumpStatus12Api() > maxFailuresBeforeNotify || element.getDumpStatus20Api() > maxFailuresBeforeNotify || element.getSolrStatus20Api() > maxFailuresBeforeNotify) {
                    //Slack notify
                }
                //Resend message
            }
            
        } while (failedElements != null && !failedElements.isEmpty());                
    }
}
