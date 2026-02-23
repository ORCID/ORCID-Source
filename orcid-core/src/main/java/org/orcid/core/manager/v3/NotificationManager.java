package org.orcid.core.manager.v3;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermissions;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;

public interface NotificationManager {
   
    void sendNotificationToAddedDelegate(String userGrantingPermission, String userReceivingPermission);

    void sendNotificationToUserGrantingPermission(String userGrantingPermission, String userReceivingPermission);
    
    void sendRevokeNotificationToUserGrantingPermission(String userGrantingPermission, String userReceivingPermission);

    Notification sendAmendEmail(String userOrcid, AmendedSection amendedSection, Collection<Item> activities);

    void sendDelegationRequestEmail(String managedOrcid, String trustedOrcid, String link);

    public List<Notification> findUnsentByOrcid(String orcid);

    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults);

    public List<Notification> findNotificationAlertsByOrcid(String orcid);
    
    public List<Notification> findNotificationsToSend(String orcid, Float emailFrequencyDays, Date recordActiveDate);
    
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

    public Notification findById(Long id);

    public Notification findByOrcidAndId(String orcid, Long id);

    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException;

    Notification flagAsArchived(String orcid, Long id, boolean validateForApi) throws OrcidNotificationAlreadyReadException;

    public Notification setActionedAndReadDate(String orcid, Long id);

    void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException;

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException;
    
    public void sendAutoDeprecateNotification(String primaryOrcid, String deprecatedOrcid);

    NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults);

    int getUnreadCount(String orcid);

    int getTotalCount(String var1, boolean archived);
    
    void flagAsRead(String orcid, Long id);

    ActionableNotificationEntity findActionableNotificationEntity(Long id); //pass trough to (ActionableNotificationEntity) find(id) and cast.
    
    Notification createPermissionNotification(String orcid, NotificationPermission notification);

    Integer archiveOffsetNotifications();
    
    Integer deleteOffsetNotifications();
    
    void deleteNotificationsForRecord(String orcid);

    NotificationFindMyStuffEntity createFindMyStuffNotification(String userOrcid, String clientId, String authorizationUrl);  
    
    void sendOrcidIntegrationNotificationToUser(String orcid, ClientDetailsEntity clientDetails, String memberName) throws UnsupportedEncodingException;
    
    List<NotificationEntity> findByOrcidAndClientAndNotificationFamilyNoClientToken(String orcid, String clientId,  String notificationFamily);

    void autoArchiveNotifications();

    void autoDeleteNotifications();
}