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
package org.orcid.api.notifications.server.delegator;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;

/**
 * 
 * @author Will Simpson
 *
 */
public interface NotificationsApiServiceDelegator {

    Response viewStatusText();

    Response findAddActivitiesNotifications(String orcid);

    Response addAddActivitiesNotification(UriInfo uriInfo, String orcid, NotificationAddActivities notification);

}
