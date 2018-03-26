package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
public interface WebhookManager {

    void processWebhooks();

    void processWebhook(WebhookEntity webhook);
    
    WebhookEntity find(WebhookEntityPk webhookPk);
    
    void update(WebhookEntity webhook);
    
    void delete(WebhookEntityPk webhook);

}
