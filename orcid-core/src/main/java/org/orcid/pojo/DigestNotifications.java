package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.release.notification.Notification;

public class DigestNotifications {
    private final List<Notification> notifications = new ArrayList<>();
    private final List<Map<String, String>> delegateNotifications = new ArrayList<>();
    private final List<Long> notificationIds = new ArrayList<>();

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Map<String, String>> getDelegateNotifications() {
        return delegateNotifications;
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }
}
