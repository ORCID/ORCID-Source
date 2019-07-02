package org.orcid.pojo;

import java.util.HashMap;
import java.util.Map;

import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.notification.Notification;

/**
 * 
 * @author Will Simpson
 *
 */
public class DigestEmail {

    private Map<String, DigestSourceNotifications> notificationsBySourceId = new HashMap<>();

    public void addNotification(Notification notification) {
        Source source = notification.getSource();
        String sourceId = null;
        if (source == null) {
            sourceId = "ORCID";
        } else {
            sourceId = source.retrieveSourcePath();
        }
        DigestSourceNotifications digestSourceNotifications = notificationsBySourceId.get(sourceId);
        if (digestSourceNotifications == null) {
            digestSourceNotifications = new DigestSourceNotifications(source);
            notificationsBySourceId.put(sourceId, digestSourceNotifications);
        }
        digestSourceNotifications.addNotification(notification);
    }

    public Map<String, DigestSourceNotifications> getNotificationsBySourceId() {
        return notificationsBySourceId;
    }
    
}
