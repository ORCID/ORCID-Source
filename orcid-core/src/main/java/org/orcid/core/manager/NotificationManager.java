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
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface NotificationManager {

    void sendWelcomeEmail(String userOrcid, String email);

    void sendVerificationEmail(String userOrcid, String email);

    void sendPasswordResetEmail(String toEmail, String userOrcid);
    
    void sendReactivationEmail(String submittedEmail, String userOrcid);

    public String createVerificationUrl(String email, String baseUri);

    public String deriveEmailFriendlyName(ProfileEntity profileEntity);    

    Notification sendAmendEmail(String userOrcid, AmendedSection amendedSection, Collection<Item> activities);

    void sendOrcidDeactivateEmail(String userOrcid);

    void sendOrcidLockedEmail(String orcidToLock);

    void sendApiRecordCreationEmail(String toEmail, String orcid);
    
    void sendEmailAddressChangedNotification(String currentUserOrcid, String newEmail, String oldEmail);

    void sendClaimReminderEmail(String userOrcid, int daysUntilActivation);

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

    public String createClaimVerificationUrl(String email, String baseUri);

    void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException;

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException;
    
    public void sendAutoDeprecateNotification(String primaryOrcid, String deprecatedOrcid);

    NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults);

    int getUnreadCount(String orcid);
    
    void flagAsRead(String orcid, Long id);

    ActionableNotificationEntity findActionableNotificationEntity(Long id); //pass trough to (ActionableNotificationEntity) find(id) and cast.
    
    void processOldNotificationsToAutoArchive();

    void processOldNotificationsToAutoDelete();

    void removeNotification(Long notificationId);

    String createUpdateEmailFrequencyUrl(String email);
    
    Notification createPermissionNotification(String orcid, NotificationPermission notification);
}
