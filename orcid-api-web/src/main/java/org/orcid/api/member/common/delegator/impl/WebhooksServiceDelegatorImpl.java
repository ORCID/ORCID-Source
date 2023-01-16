package org.orcid.api.member.common.delegator.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import jakarta.ws.rs.core.Response;

import org.orcid.api.member.common.delegator.WebhooksServiceDelegator;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidWebhookNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.WebhookManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

public class WebhooksServiceDelegatorImpl implements WebhooksServiceDelegator {

    @Resource
    private WebhookManager webhookManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "sourceManagerV3")
    protected SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Override
    public Response registerWebhook(String orcid, String webhookUri, URI absolutePathUri) {
        orcidSecurityManager.checkScopes(ScopePathType.WEBHOOK);
        try {
            new URI(webhookUri);
        } catch (URISyntaxException e) {
            Object params[] = { webhookUri };
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_incorrect_webhook.exception", params));
        }

        if (!profileEntityManager.orcidExists(orcid)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            throw new OrcidNotFoundException(params);
        }

        String clientId = sourceManager.retrieveActiveSourceId();

        WebhookEntityPk webhookPk = new WebhookEntityPk(orcid, webhookUri);
        WebhookEntity webhook = webhookManager.find(webhookPk);
        boolean isNew = webhook == null;
        if (isNew) {
            Date lastModifiedDate = profileEntityManager.getLastModifiedDate(orcid);
            webhook = new WebhookEntity();

            webhook.setProfileLastModified(lastModifiedDate);
            webhook.setProfile(orcid);
            webhook.setEnabled(true);
            webhook.setUri(webhookUri);
            webhook.setClientDetailsId(clientId);
        }
        webhookManager.update(webhook);
        return isNew ? Response.created(absolutePathUri).build() : Response.noContent().build();
    }

    @Override
    public Response unregisterWebhook(String orcid, String webhookUri) {
        orcidSecurityManager.checkScopes(ScopePathType.WEBHOOK);
        if (!profileEntityManager.orcidExists(orcid)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            throw new OrcidNotFoundException(params);
        }

        WebhookEntityPk webhookPk = new WebhookEntityPk(orcid, webhookUri);
        WebhookEntity webhook = webhookManager.find(webhookPk);
        if (webhook == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("orcid", orcid);
            params.put("uri", webhookUri);
            throw new OrcidWebhookNotFoundException(params);
        } else {
            String clientId = sourceManager.retrieveActiveSourceId();

            // Check if client can unregister this webhook
            if (webhook.getClientDetailsId().equals(clientId)) {
                webhookManager.delete(webhookPk);
                return Response.noContent().build();
            } else {
                // Throw 403 exception: user is not allowed to unregister
                // that webhook
                throw new OrcidForbiddenException(localeManager.resolveMessage("apiError.forbidden_unregister_webhook.exception"));
            }
        }
    }

}
