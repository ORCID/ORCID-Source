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
package org.orcid.record_2_0.server;

import static org.orcid.api.common.OrcidApiConstants.*;

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
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.record_2_0.server.delegator.MemberApiServiceDelegator;

/**
 * @author rcpeters
 */
abstract public class MemberApiServiceImplBase {

    @Context
    private UriInfo uriInfo;

    private MemberApiServiceDelegator serviceDelegator;

    public void setServiceDelegator(MemberApiServiceDelegator serviceDelegator) {
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
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Path(ADD_ACTIVITIES_PATH)
    public Response viewAddActivitiesNotificationsXml(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewActivities(orcid);
    }
}
