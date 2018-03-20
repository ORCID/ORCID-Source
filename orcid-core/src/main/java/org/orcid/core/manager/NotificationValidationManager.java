package org.orcid.core.manager;

import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface NotificationValidationManager {

    void validateNotificationPermission(NotificationPermission notification);

}
