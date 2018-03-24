package org.orcid.api.notificationsV3.server.delegator;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.core.exception.OrcidNotificationAlreadyReadException;

/**
 * 
 * @author Will Simpson
 *
 */
public interface NotificationsApiServiceDelegator<NOTIFICATIONPERMISSION> {

    Response viewStatusText();

    Response findPermissionNotifications(String orcid);
    
    Response findPermissionNotification(String orcid, Long id);
    
    Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException;

    Response addPermissionNotification(UriInfo uriInfo, String orcid, NOTIFICATIONPERMISSION notification);

}
