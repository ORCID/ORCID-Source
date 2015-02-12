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
package org.orcid.integration.api.notifications;

import static org.orcid.core.api.OrcidApiConstants.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
public class NotificationsApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public NotificationsApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }

    public ClientResponse viewStatusText() {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewAddActivitiesNotificationsHtml(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewAddActivitiesNotificationsXml(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewAddActivitiesNotificationsJson(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewAddActivitiesNotificationXml(String orcid, Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewAddActivitiesNotificationJson(String orcid, Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse flagAsArchivedAddActivitiesNotificationXml(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse flagAsArchivedAddActivitiesNotificationJson(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse addAddActivitiesNotificationXml(String orcid, NotificationAddActivities notification, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(NOTIFICATIONS_PATH + ADD_ACTIVITIES_PATH).build(orcid), VND_ORCID_XML, notification, accessToken);
    }

    public ClientResponse addAddActivitiesNotificationJson(String orcid, NotificationAddActivities notification) {
        return orcidClientHelper.postClientResponse(UriBuilder.fromPath(NOTIFICATIONS_PATH + ADD_ACTIVITIES_PATH).build(orcid), VND_ORCID_JSON, notification);
    }

}
