package org.orcid.core.manager;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermissions;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface NotificationManager {
    
    Notification sendAmendEmail(String userOrcid, AmendedSection amendedSection, Collection<Item> activities);

    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults);

    public List<Notification> findNotificationAlertsByOrcid(String orcid);

    /**
     * Filters the list of notification alerts by archiving any that have
     * already been actioned
     * 
     * @param notifications
     *            The list of notification alerts, as returned by
     *            {@link #findNotificationAlertsByOrcid(String)}
     * @return The list of notification alerts, minus any that have already been
     *         actioned
     */
    public List<Notification> filterActionedNotificationAlerts(Collection<Notification> notifications, String userOrcid);

    public Notification findByOrcidAndId(String orcid, Long id);

    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException;

    Notification flagAsArchived(String orcid, Long id, boolean validateForApi) throws OrcidNotificationAlreadyReadException;

    void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException;

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException;
    
    NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults);

    int getUnreadCount(String orcid);

    ActionableNotificationEntity findActionableNotificationEntity(Long id); //pass trough to (ActionableNotificationEntity) find(id) and cast.

    Notification createPermissionNotification(String orcid, NotificationPermission notification);
    
}
