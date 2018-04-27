package org.orcid.core.manager;

import java.util.Collection;

import org.orcid.jaxb.model.notification_v2.Notification;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailMessageSender {

    EmailMessage createDigest(String orcid, Collection<Notification> notifications);        

    void sendServiceAnnouncementsAndTipsMessages() throws InterruptedException;
    
    void sendEmailMessages();

}
