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
package org.orcid.core.manager;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.amended_rc3.AmendedSection;
import org.orcid.jaxb.model.notification.permission_rc3.Item;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermissions;
import org.orcid.jaxb.model.notification_rc3.Notification;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface NotificationManager {

    // void sendRegistrationEmail(RegistrationEntity registration, URI baseUri);

    void sendWelcomeEmail(OrcidProfile orcidProfile, String email);

    void sendVerificationEmail(OrcidProfile orcidProfile, String email);

    public void sendVerificationReminderEmail(OrcidProfile orcidProfile, String email);

    void sendPasswordResetEmail(String toEmail, OrcidProfile orcidProfile);
    
    void sendReactivationEmail(String submittedEmail, OrcidProfile orcidProfile);

    public String createVerificationUrl(String email, String baseUri);

    public String deriveEmailFriendlyName(OrcidProfile orcidProfile);

    public String deriveEmailFriendlyName(ProfileEntity profileEntity);

    void sendNotificationToAddedDelegate(OrcidProfile grantingUser, List<DelegationDetails> delegatesGrantedByUser);

    void sendAmendEmail(String orcid, AmendedSection amendedSection, Item item);

    void sendAmendEmail(OrcidProfile amendedProfile, AmendedSection amendedSection);

    void sendAmendEmail(OrcidProfile amendedProfile, AmendedSection amendedSection, Collection<Item> activities);

    void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate);

    void sendOrcidLockedEmail(OrcidProfile orcidToLock);

    void sendApiRecordCreationEmail(String toEmail, OrcidProfile createdProfile);

    void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail);

    void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation);

    public boolean sendPrivPolicyEmail2014_03(OrcidProfile orcidProfile);

    void sendDelegationRequestEmail(OrcidProfile managed, OrcidProfile trusted, String link);

    public List<Notification> findUnsentByOrcid(String orcid);

    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults);

    public List<Notification> findNotificationAlertsByOrcid(String orcid);

    public Notification findById(Long id);

    public Notification findByOrcidAndId(String orcid, Long id);

    public Notification createNotification(String orcid, Notification notification);

    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException;

    public Notification setActionedAndReadDate(String orcid, Long id);

    public void addMessageParams(Map<String, Object> templateParams, OrcidProfile orcidProfile);

    public String getSubject(String code, OrcidProfile orcidProfile);

    public boolean sendServiceAnnouncement_1_For_2015(OrcidProfile orcidProfile);

    public String createClaimVerificationUrl(String email, String baseUri);

    void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException;

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException;
    
    public void sendAutoDeprecateNotification(OrcidProfile orcidProfile, String deprecatedOrcid);

    NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults);

}
