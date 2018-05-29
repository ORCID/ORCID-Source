package org.orcid.frontend.web.controllers.helper;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Will Simpson
 *
 */
public class UserSession {

    private Set<Long> suppressedNotificationAlertIds;
    private boolean obsoleteNotificationAlertsCheckDone;

    public Set<Long> getSuppressedNotificationAlertIds() {
        if (suppressedNotificationAlertIds == null) {
            suppressedNotificationAlertIds = new HashSet<>();
        }
        return suppressedNotificationAlertIds;
    }

    public boolean isObsoleteNotificationAlertsCheckDone() {
        return obsoleteNotificationAlertsCheckDone;
    }

    public void setObsoleteNotificationAlertsCheckDone(boolean obsoleteNotificationAlertsCheckDone) {
        this.obsoleteNotificationAlertsCheckDone = obsoleteNotificationAlertsCheckDone;
    }

}
