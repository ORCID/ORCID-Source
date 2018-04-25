package org.orcid.core.common.manager;

import java.util.Map;

import org.orcid.persistence.constants.SendEmailFrequency;

public interface EmailFrequencyManager {

    Map<String, String> getEmailFrequency(String orcid);
    
    boolean createEmailFrequency(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips);

    boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency);

    boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency);

    boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency);

    boolean updateSendQuarterlyTips(String orcid, Boolean enabled);

}
