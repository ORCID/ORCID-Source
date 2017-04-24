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

import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.SendEmailFrequency;

public interface PreferenceManager {
    boolean updateEmailFrequencyDays(String orcid, SendEmailFrequency newValue);

    boolean updateNotifications(String orcid, Boolean sendChangeNotifications, Boolean sendAdministrativeChangeNotifications, Boolean sendOrcidNews,
            Boolean sendMemberUpdateRequests);

    boolean updateDefaultVisibility(String orcid, Visibility newValue);
}
