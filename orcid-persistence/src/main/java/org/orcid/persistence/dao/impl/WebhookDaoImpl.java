/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
public class WebhookDaoImpl extends GenericDaoImpl<WebhookEntity, WebhookEntityPk> implements WebhookDao {

    public WebhookDaoImpl() {
        super(WebhookEntity.class);
    }

    @Override
    public List<WebhookEntity> findWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes, int maxResults) {
        TypedQuery<WebhookEntity> query = entityManager.createNamedQuery(WebhookEntity.FIND_WEBHOOKS_READY_TO_PROCESS, WebhookEntity.class);
        query.setParameter("retryDelayMinutes", retryDelayMinutes);
        return query.getResultList();
    }

    @Override
    public long countWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes) {
        TypedQuery<BigInteger> query = entityManager.createNamedQuery(WebhookEntity.COUNT_WEBHOOKS_READY_TO_PROCESS, BigInteger.class);
        query.setParameter("retryDelayMinutes", retryDelayMinutes);
        return query.getSingleResult().longValue();
    }

}
