package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
public interface WebhookDao extends GenericDao<WebhookEntity, WebhookEntityPk> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WebhookEntity> findWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes, int maxResults, Set<String> clientsToExclude);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    long countWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean markAsSent(String orcid, String uri);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean markAsFailed(String orcid, String uri);
}
