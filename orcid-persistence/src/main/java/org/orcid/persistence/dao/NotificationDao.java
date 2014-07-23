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

import org.orcid.persistence.jpa.entities.NotificationEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface NotificationDao extends GenericDao<NotificationEntity, Long> {

    List<NotificationEntity> findByOrcid(String orcid, int firstResult, int maxResults);

    NotificationEntity findLatestByOrcid(String orcid);

    List<String> findOrcidsWithNotificationsToSend();

    /**
     * @param effectiveNow
     *            Normally this would be the current date and time, but it is
     *            useful to be able to pass in a different value for testing.
     */
    List<String> findOrcidsWithNotificationsToSend(Date effectiveNow);

}
