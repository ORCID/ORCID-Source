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
package org.orcid.api.notifications.server.delegator.impl;

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
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.api.notifications.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.OrcidNotificationException;
import org.orcid.core.exception.OrcidNotificationNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.NotificationValidationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermissions;
import org.orcid.jaxb.model.notification_rc3.Notification;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
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

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private NotificationValidationManager notificationValidationManager;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Resource
    private ProfileDao profileDao;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotifications(String orcid) {
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
        checkIfProfileDeprecated(orcid);
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
        checkIfProfileDeprecated(orcid);
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
        checkIfProfileDeprecated(orcid);
        notificationValidationManager.validateNotificationPermission(notification);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile == null) {
            throw OrcidNotFoundException.newInstance(orcid);
        }
        if (profile.getSendMemberUpdateRequests() != null && !profile.getSendMemberUpdateRequests()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            throw new OrcidNotificationException(params);
        }
        Notification createdNotification = notificationManager.createNotification(orcid, notification);
        try {
            return Response.created(new URI(uriInfo.getAbsolutePath() + "/" + createdNotification.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.notification_uri.exception"), e);
        }
    }

    private void checkIfProfileDeprecated(String orcid) {
        ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        if (entity != null && profileDao.isProfileDeprecated(orcid)) {
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(entity.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, primary.toString());
            if (entity.getDeprecatedDate() != null) {
                XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(entity.getDeprecatedDate());
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
            }
            throw new OrcidDeprecatedException(params);
        }
    }
}
