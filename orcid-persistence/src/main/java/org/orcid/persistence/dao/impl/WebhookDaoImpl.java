package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Will Simpson
 */
public class WebhookDaoImpl extends GenericDaoImpl<WebhookEntity, WebhookEntityPk> implements WebhookDao {

    @Value("${org.orcid.persistence.webhook.maxAttemptCount:25}")
    private int maxAttemptCount;
    
    public WebhookDaoImpl() {
        super(WebhookEntity.class);
    }

    @Override
    public List<WebhookEntity> findWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes, int maxResults, Set<String> clientsToExclude) {
        TypedQuery<WebhookEntity> query = entityManager.createNamedQuery(WebhookEntity.FIND_WEBHOOKS_READY_TO_PROCESS, WebhookEntity.class);
        query.setParameter("retryDelayMinutes", retryDelayMinutes);
        query.setParameter("maxAttemptCount", maxAttemptCount);
        // This query will not work fine if the clientsToExclude is null or empty because if the in clase, so, replace it with a list with an empty string will fix that
        query.setParameter("clientsToExclude", (clientsToExclude == null || clientsToExclude.isEmpty()) ? Set.of(" ") : clientsToExclude);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public long countWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes) {
        TypedQuery<BigInteger> query = entityManager.createNamedQuery(WebhookEntity.COUNT_WEBHOOKS_READY_TO_PROCESS, BigInteger.class);
        query.setParameter("retryDelayMinutes", retryDelayMinutes);
        query.setParameter("maxAttemptCount", maxAttemptCount);
        return query.getSingleResult().longValue();
    }

    @Override
    public boolean markAsSent(String orcid, String uri) {
        Query query = entityManager.createNativeQuery("UPDATE webhook SET last_sent=now(), failed_attempt_count=0 where orcid = :orcid and uri = :uri");
        query.setParameter("orcid", orcid);
        query.setParameter("uri", uri);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean markAsFailed(String orcid, String uri) {
        Query query = entityManager.createNativeQuery("UPDATE webhook SET last_failed=now(), failed_attempt_count=(failed_attempt_count + 1) where orcid = :orcid and uri = :uri");
        query.setParameter("orcid", orcid);
        query.setParameter("uri", uri);
        return query.executeUpdate() > 0;
    }

}
