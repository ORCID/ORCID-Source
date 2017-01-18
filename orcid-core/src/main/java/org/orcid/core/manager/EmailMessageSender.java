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

import java.util.Collection;
import java.util.Locale;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification_v2.Notification;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailMessageSender {

    EmailMessage createDigest(String orcid, Collection<Notification> notifications);
    
    EmailMessage createDigest(OrcidProfile orcidProfile, Collection<Notification> notifications, Locale locale);

    void sendEmailMessages();

}
