package org.orcid.api.notificationsV3.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.MAX_NOTIFICATIONS_AVAILABLE;
import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.notificationsV3.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.OrcidNotificationNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.NotificationValidationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.notification.Notification;
import org.orcid.jaxb.model.v3.rc2.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.rc2.notification.permission.NotificationPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Component
public class NotificationsApiServiceDelegatorImpl implements NotificationsApiServiceDelegator<NotificationPermission> {

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource(name = "notificationValidationManagerV3")
    private NotificationValidationManager notificationValidationManager;

    @Resource
    private SourceManager sourceManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotifications(String orcid) {
        checkProfileStatus(orcid, true);
        
        // Get the client profile information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = null;
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            clientId = authorizationRequest.getClientId();
        }

        NotificationPermissions notifications = notificationManager.findPermissionsByOrcidAndClient(orcid, clientId, 0, MAX_NOTIFICATIONS_AVAILABLE);
        return Response.ok(notifications).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotification(String orcid, Long id) {
        checkProfileStatus(orcid, true);
        Notification notification = notificationManager.findByOrcidAndId(orcid, id);
        if (notification != null) {
            checkSource(notification);
            return Response.ok(notification).build();
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            params.put("id", String.valueOf(id));
            throw new OrcidNotificationNotFoundException(params);
        }
    }

    private void checkSource(Notification notification) {
        String notificationSourceId = notification.getSource().retrieveSourcePath();
        String currentSourceId = sourceManager.retrieveSourceOrcid();
        if (!notificationSourceId.equals(currentSourceId)) {
            Object params[] = { currentSourceId };
            throw new AccessControlException(localeManager.resolveMessage("apiError.notification_accesscontrol.exception", params));
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        checkProfileStatus(orcid, false);
        Notification notification = notificationManager.flagAsArchived(orcid, id);
        if (notification == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            params.put("id", String.valueOf(id));
            throw new OrcidNotificationNotFoundException(params);
        }
        return Response.ok(notification).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response addPermissionNotification(UriInfo uriInfo, String orcid, NotificationPermission notification) {
        checkProfileStatus(orcid, false);
        notificationValidationManager.validateNotificationPermission(notification);
        Notification createdNotification = notificationManager.createPermissionNotification(orcid, notification);
        try {
            if(createdNotification == null) {
                return Response.notModified().build();
            }
            return Response.created(new URI(uriInfo.getAbsolutePath() + "/" + createdNotification.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.notification_uri.exception"), e);
        }
    }

    private void checkProfileStatus(String orcid, boolean readOperation) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch (DeactivatedException e) {
            // If it is a read operation, ignore the deactivated status since we
            // are going to return the empty element with the deactivation date
            if (!readOperation) {
                throw e;
            }
        }
    }
}
