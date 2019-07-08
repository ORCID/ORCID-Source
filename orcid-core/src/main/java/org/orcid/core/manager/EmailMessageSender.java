package org.orcid.core.manager;

import java.util.Collection;

import org.orcid.jaxb.model.v3.release.notification.Notification;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailMessageSender {

    EmailMessage createDigest(String orcid, Collection<Notification> notifications);        

    EmailMessage createDigestLegacy(String orcid, Collection<Notification> notifications);
    
    void sendServiceAnnouncements(Integer customBatchSize);
    
    void sendTips(Integer customBatchSize, String fromAddress);
    
    void sendEmailMessages();

}
