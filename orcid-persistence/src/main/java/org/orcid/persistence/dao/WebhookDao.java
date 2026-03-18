package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
public interface WebhookDao extends GenericDao<WebhookEntity, WebhookEntityPk> {

    List<WebhookEntity> findWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes, int maxResults, Set<String> clientsToExclude);

    long countWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes);

    boolean markAsSent(String orcid, String uri);
    
    boolean markAsFailed(String orcid, String uri);
}
