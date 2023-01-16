package org.orcid.api.member.common.delegator;

import java.net.URI;

import jakarta.ws.rs.core.Response;

public interface WebhooksServiceDelegator {
    Response registerWebhook(String orcid, String webhookUri, URI absolutePathUri);

    Response unregisterWebhook(String orcid, String webhookUri);
}
