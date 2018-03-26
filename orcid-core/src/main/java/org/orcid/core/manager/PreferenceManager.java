package org.orcid.core.manager;

import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.SendEmailFrequency;

public interface PreferenceManager {
    boolean updateEmailFrequencyDays(String orcid, SendEmailFrequency newValue);

    boolean updateNotifications(String orcid, Boolean sendChangeNotifications, Boolean sendAdministrativeChangeNotifications, Boolean sendOrcidNews,
            Boolean sendMemberUpdateRequests);

    boolean updateDefaultVisibility(String orcid, Visibility newValue);
}
