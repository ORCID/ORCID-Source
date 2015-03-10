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
package org.orcid.api.notifications.server;

import static org.orcid.core.api.OrcidApiConstants.ADD_ACTIVITIES_FLAG_AS_ARCHIVED_PATH;
import static org.orcid.core.api.OrcidApiConstants.ADD_ACTIVITIES_PATH;
import static org.orcid.core.api.OrcidApiConstants.ADD_ACTIVITIES_VIEW_PATH;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.notifications.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;

/**
 * @author Will Simpson
 */
abstract public class NotificationsApiServiceImplBase {

    @Context
    private UriInfo uriInfo;

    private NotificationsApiServiceDelegator serviceDelegator;

    public void setServiceDelegator(NotificationsApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }

    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path(ADD_ACTIVITIES_PATH)
    public Response viewAddActivitiesNotificationsHtml(@PathParam("orcid") String orcid) {
        Response response = serviceDelegator.findAddActivitiesNotifications(orcid);
        return Response.fromResponse(response).header("Content-Disposition", "attachment; filename=\"" + orcid + "-add-activities-notifications.xml\"").build();
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(ADD_ACTIVITIES_PATH)
    public Response viewAddActivitiesNotificationsXml(@PathParam("orcid") String orcid) {
        return serviceDelegator.findAddActivitiesNotifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADD_ACTIVITIES_PATH)
    public Response viewAddActivitiesNotificationsJson(@PathParam("orcid") String orcid) {
        return serviceDelegator.findAddActivitiesNotifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(ADD_ACTIVITIES_VIEW_PATH)
    public Response viewAddActivitiesNotificationXml(@PathParam("orcid") String orcid, @PathParam("id") Long id) {
        return serviceDelegator.findAddActivitiesNotification(orcid, id);
    }

    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADD_ACTIVITIES_VIEW_PATH)
    public Response viewAddActivitiesNotificationJson(@PathParam("orcid") String orcid, @PathParam("id") Long id) {
        return serviceDelegator.findAddActivitiesNotification(orcid, id);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(ADD_ACTIVITIES_FLAG_AS_ARCHIVED_PATH)
    public Response flagAsArchivedAddActivitiesNotificationXml(@PathParam("orcid") String orcid, @PathParam("id") Long id) throws OrcidNotificationAlreadyReadException {
        return serviceDelegator.flagNotificationAsArchived(orcid, id);
    }

    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADD_ACTIVITIES_FLAG_AS_ARCHIVED_PATH)
    public Response flagAsArchivedAddActivitiesNotificationJson(@PathParam("orcid") String orcid, @PathParam("id") Long id) throws OrcidNotificationAlreadyReadException {
        return serviceDelegator.flagNotificationAsArchived(orcid, id);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
    @Path(ADD_ACTIVITIES_PATH)
    public Response addAddActivitiesNotificationXml(@PathParam("orcid") String orcid, NotificationAddActivities notification) {
        return serviceDelegator.addAddActivitiesNotification(uriInfo, orcid, notification);
    }

    @POST
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADD_ACTIVITIES_PATH)
    public Response addAddActivitiesNotificationJson(@PathParam("orcid") String orcid, NotificationAddActivities notification) {
        return serviceDelegator.addAddActivitiesNotification(uriInfo, orcid, notification);
    }

}
