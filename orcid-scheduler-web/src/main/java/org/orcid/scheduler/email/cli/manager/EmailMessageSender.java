package org.orcid.scheduler.email.cli.manager;

import java.util.Collection;

import org.orcid.core.manager.EmailMessage;
import org.orcid.jaxb.model.v3.release.notification.Notification;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailMessageSender {

    EmailMessage createDigest(String orcid, Collection<Notification> notifications);        
    
    void sendServiceAnnouncements(Integer customBatchSize);
    
    void sendTips(Integer customBatchSize, String fromAddress);
    
    void sendEmailMessages();
    
    void processUnverifiedEmails2Days();

    EmailMessage createAddWorksToRecordEmail(String email, String orcid);
}
