/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.notification_v2.Notification;

/**
 * 
 * @author Will Simpson
 *
 */
public class DigestSourceNotifications {

    private Source source;

    private Map<String, List<Notification>> notificationsByType = new HashMap<>();

    public DigestSourceNotifications(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public Map<String, List<Notification>> getNotificationsByType() {
        return notificationsByType;
    }

    public void addNotification(Notification notification) {
        String key = notification.getNotificationType().name();
        List<Notification> notificationsForType = notificationsByType.get(key);
        if (notificationsForType == null) {
            notificationsForType = new ArrayList<>();
            notificationsByType.put(key, notificationsForType);
        }
        notificationsForType.add(notification);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> allNotifications = new ArrayList<>();
        for (List<Notification> notificationsForAction : notificationsByType.values()) {
            allNotifications.addAll(notificationsForAction);
        }
        return allNotifications;
    }

}
