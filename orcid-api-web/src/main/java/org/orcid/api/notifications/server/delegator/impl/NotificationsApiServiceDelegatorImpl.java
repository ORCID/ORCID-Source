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

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.notifications.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Component
public class NotificationsApiServiceDelegatorImpl implements NotificationsApiServiceDelegator {

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private SourceManager sourceManager;

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findAddActivitiesNotifications(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findAddActivitiesNotification(String orcid, Long id) {
        Notification notification = notificationManager.findByOrcidAndId(orcid, id);
        if (notification != null) {
            checkSource(notification);
            return Response.ok(notification).build();
        } else {
            throw new OrcidNotFoundException("Unable to find notification");
        }
    }

    private void checkSource(Notification notification) {
        String notificationSourceId = notification.getSource().retrieveSourcePath();
        String currentSourceId = sourceManager.retrieveSourceOrcid();
        if (!notificationSourceId.equals(currentSourceId)) {
            throw new AccessControlException("This notification does not belong to " + currentSourceId);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        Notification notification = notificationManager.flagAsArchived(orcid, id);
        if (notification == null) {
            throw new OrcidNotFoundException("Could not find notification with id: " + id + " for ORCID: " + orcid);
        }
        return Response.ok(notification).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response addAddActivitiesNotification(UriInfo uriInfo, String orcid, NotificationAddActivities notification) {
        Notification createdNotification = notificationManager.createNotification(orcid, notification);
        try {
            return Response.created(new URI(uriInfo.getAbsolutePath() + "/" + createdNotification.getPutCode())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error constructing URI for add activities notification", e);
        }
    }

}
