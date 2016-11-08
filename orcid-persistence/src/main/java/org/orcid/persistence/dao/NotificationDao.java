/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.NotificationEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface NotificationDao extends GenericDao<NotificationEntity, Long> {

	List<NotificationEntity> findByOrcid(String orcid, boolean includeArchived,
			int firstResult, int maxResults);

	NotificationEntity findLatestByOrcid(String orcid);

	List<NotificationEntity> findUnsentByOrcid(String orcid);

	List<NotificationEntity> findNotificationAlertsByOrcid(String orcid);

	int getUnreadCount(String orcid);

	List<String> findOrcidsWithNotificationsToSend();

	/**
	 * @param effectiveNow
	 *            Normally this would be the current date and time, but it is
	 *            useful to be able to pass in a different value for testing.
	 */
	List<String> findOrcidsWithNotificationsToSend(Date effectiveNow);

	NotificationEntity findByOricdAndId(String orcid, Long id);

	void flagAsSent(Collection<Long> ids);

	void flagAsRead(String orcid, Long id);

	void flagAsArchived(String orcid, Long id);

	void deleteNotificationById(Long notificationId);

	void deleteNotificationItemByNotificationId(Long notificationId);

	void deleteNotificationWorkByNotificationId(Long notificationId);

	List<NotificationEntity> findPermissionsByOrcidAndClient(String orcid, String client,
			int firstResult, int maxResults);

}
