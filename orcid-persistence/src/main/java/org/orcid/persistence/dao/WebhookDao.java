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
package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
public interface WebhookDao extends GenericDao<WebhookEntity, WebhookEntityPk> {

    List<WebhookEntity> findWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes, int maxResults);

    long countWebhooksReadyToProcess(Date profileModifiedBefore, int retryDelayMinutes);

}
