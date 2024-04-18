package org.orcid.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private List<String> sources = new ArrayList<>();

    public void addNotification(Notification notification) {
        Source source = notification.getSource();
        String sourceId = null;
        if (source == null) {
            sourceId = "ORCID";
        } else {
            sourceId = source.retrieveSourcePath();
        }

        if (source != null && source.getSourceName() != null && source.getSourceName().getContent() != null &&
                !sources.contains(source.getSourceName().getContent())) {
            sources.add(source.getSourceName().getContent());
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

    public List<String> getSources() {
        return sources;
    }

}
