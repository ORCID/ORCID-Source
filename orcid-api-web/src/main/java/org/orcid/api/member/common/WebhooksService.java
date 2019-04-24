package org.orcid.api.member.common;

import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WEBHOOKS_PATH;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.member.common.delegator.WebhooksServiceDelegator;
import org.springframework.stereotype.Component;

@Path(WEBHOOKS_PATH)
@Component
public class WebhooksService {

    @Context
    private UriInfo uriInfo;
    
    @Resource(name = "webhooksServiceDelegator")
    private WebhooksServiceDelegator serviceDelegator;
    
    /**
     * Register a webhook for a specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be added 
     */
    @PUT    
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    public Response registerWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.registerWebhook(orcid, webhookUri, uriInfo.getAbsolutePath());
    } 
    
    /**
     * Register a webhook for a specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be added 
     */
    @PUT    
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    public Response registerWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.registerWebhook(orcid, webhookUri, uriInfo.getAbsolutePath());
    }   
    
    /**
     * Unregister a webhook from specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be deleted from the user
     * @return
     * */
    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML })   
    public Response unregisterWebhookXML(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.unregisterWebhook(orcid, webhookUri);
    }

    /**
     * Unregister a webhook from specific client.
     * 
     * @param orcid
     *            the ORCID that corresponds to the user's record
     * @param webhook_uri
     *            the webhook that will be deleted from the user
     * @return
     * */
    @DELETE
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })    
    public Response unregisterWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.unregisterWebhook(orcid, webhookUri);
    }
}
