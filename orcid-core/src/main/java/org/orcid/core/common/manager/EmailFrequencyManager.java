package org.orcid.core.common.manager;

import java.util.Map;

import org.orcid.persistence.constants.SendEmailFrequency;

public interface EmailFrequencyManager {

    public static final String ADMINISTRATIVE_CHANGE_NOTIFICATIONS = "send_administrative_change_notifications";
    public static final String CHANGE_NOTIFICATIONS = "send_change_notifications";
    public static final String MEMBER_UPDATE_REQUESTS = "send_member_update_requests";
    public static final String QUARTERLY_TIPS = "send_quarterly_tips";

    Map<String, String> getEmailFrequency(String orcid);
    
    Map<String, String> getEmailFrequencyById(String id);
    
    boolean createOnRegister(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips);

    boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency);

    boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency);

    boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency);

    boolean updateSendQuarterlyTips(String orcid, Boolean enabled);
    
    boolean emailFrequencyExists(String orcid);
    
    boolean update(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips);

    boolean updateById(String id, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips);
}
