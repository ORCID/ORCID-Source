package org.orcid.api.member.common;

import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WEBHOOKS_PATH;

import jakarta.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.orcid.api.member.common.delegator.WebhooksServiceDelegator;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.springframework.stereotype.Component;

@Component
@Path(WEBHOOKS_PATH)
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
    @Operation(operationId = "registerWebhook", summary = "Register a webhook", tags = "Webhooks",
            description = "Asks the registry to notify the URI when the record changes. The request has no body. Errors come back as XML or JSON, following the Accept header.",
            security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.WEBHOOK }))
    @ApiResponse(responseCode = "201", description = "Webhook registered; the Location header repeats the request URI", headers = @Header(name = "Location", description = "URI of the webhook", schema = @Schema(type = "string", format = "uri")))
    @ApiResponse(responseCode = "204", description = "The webhook was already registered")
    @ApiResponse(responseCode = "400", description = "The webhook URI is not a valid URI", content = {
            @Content(mediaType = VND_ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = VND_ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = OrcidError.class)) })
    @ApiResponse(responseCode = "404", description = "No record has this ORCID iD", content = {
            @Content(mediaType = VND_ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = VND_ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = OrcidError.class)) })
    public Response registerWebhookXML(@PathParam("orcid") String orcid, @Parameter(description = "The URI to notify, URL-encoded, for example `https%3A%2F%2Fexample.org%2Forcid-updates`") @PathParam("webhook_uri") String webhookUri) {
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
    // OpenAPI allows one operation per path and method; registerWebhookXML describes both.
    @Hidden
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
    @Operation(operationId = "unregisterWebhook", summary = "Unregister a webhook", tags = "Webhooks",
            description = "Only the client that registered the webhook can unregister it. Errors come back as XML or JSON, following the Accept header.",
            security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.WEBHOOK }))
    @ApiResponse(responseCode = "204", description = "Webhook unregistered")
    @ApiResponse(responseCode = "403", description = "The webhook belongs to another client", content = {
            @Content(mediaType = VND_ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = VND_ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = OrcidError.class)) })
    @ApiResponse(responseCode = "404", description = "No record has this ORCID iD, or the record has no such webhook", content = {
            @Content(mediaType = VND_ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = VND_ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = ORCID_JSON, schema = @Schema(implementation = OrcidError.class)),
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = OrcidError.class)) })
    public Response unregisterWebhookXML(@PathParam("orcid") String orcid, @Parameter(description = "The URI to notify, URL-encoded, for example `https%3A%2F%2Fexample.org%2Forcid-updates`") @PathParam("webhook_uri") String webhookUri) {
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
    // OpenAPI allows one operation per path and method; unregisterWebhookXML describes both.
    @Hidden
    public Response unregisterWebhookJson(@PathParam("orcid") String orcid, @PathParam("webhook_uri") String webhookUri) {
        return serviceDelegator.unregisterWebhook(orcid, webhookUri);
    }
}
