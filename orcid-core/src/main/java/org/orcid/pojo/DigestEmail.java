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

import java.util.HashMap;
import java.util.Map;

import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.notification_v2.Notification;

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
