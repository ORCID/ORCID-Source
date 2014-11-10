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

import java.net.URI;
import java.util.List;

import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.generic.Notification;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface NotificationManager {

    // void sendRegistrationEmail(RegistrationEntity registration, URI baseUri);

    void sendVerificationEmail(OrcidProfile orcidProfile, URI baseUri, String email);

    public void sendVerificationReminderEmail(OrcidProfile orcidProfile, String email);

    void sendPasswordResetEmail(String toEmail, OrcidProfile orcidProfile, URI baseUri);

    public String createVerificationUrl(String email, URI baseUri);

    public String deriveEmailFriendlyName(OrcidProfile orcidProfile);

    void sendNotificationToAddedDelegate(OrcidProfile grantingUser, List<DelegationDetails> delegatesGrantedByUser);

    void sendAmendEmail(OrcidProfile amendedProfile, String amenderOrcid);

    void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate, URI baseUri);

    void sendApiRecordCreationEmail(String toEmail, OrcidProfile createdProfile);

    void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail, URI baseUri);

    void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation);

    public boolean sendPrivPolicyEmail2014_03(OrcidProfile orcidProfile, URI baseUri);

    void sendDelegationRequestEmail(OrcidProfile managed, OrcidProfile trusted, String link);

    public List<Notification> findUnsentByOrcid(String orcid);

    public List<Notification> findByOrcid(String orcid, int firstResult, int maxResults);

    public Notification findById(Long id);

    public Notification findByOrcidAndId(String orcid, Long id);

}
