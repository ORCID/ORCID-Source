/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.springframework.mail.MailSender;

public interface NotificationManager {

    void setMailSender(MailSender mailSender);

    void setSecurityQuestionDao(GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao);

    // void sendRegistrationEmail(RegistrationEntity registration, URI baseUri);

    void sendVerificationEmail(OrcidProfile orcidProfile, URI baseUri, String email);
    
    public void sendVerificationReminderEmail(OrcidProfile orcidProfile, URI baseUri, String email);

    void sendPasswordResetEmail(String toEmail, OrcidProfile orcidProfile, URI baseUri);
    
    public String createVerificationUrl(String email, URI baseUri); 

    public String deriveEmailFriendlyName(OrcidProfile orcidProfile);

    void sendNotificationToAddedDelegate(OrcidProfile grantingUser, List<DelegationDetails> delegatesGrantedByUser);

    void sendAmendEmail(OrcidProfile amendedProfile, String amenderOrcid);

    void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate, URI baseUri);

    void sendApiRecordCreationEmail(String toEmail, OrcidProfile createdProfile);

    void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail, URI baseUri);

    void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation);

}
