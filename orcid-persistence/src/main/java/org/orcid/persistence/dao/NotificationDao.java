package org.orcid.persistence.dao;

import java.math.BigInteger;
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

    List<NotificationEntity> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults);

    NotificationEntity findLatestByOrcid(String orcid);

    List<NotificationEntity> findUnsentByOrcid(String orcid);

    List<NotificationEntity> findNotificationAlertsByOrcid(String orcid);

    int getUnreadCount(String orcid);

    int getTotalCount(String orcid, boolean archived);

    List<Object[]> findRecordsWithUnsentNotifications();

    List<NotificationEntity> findNotificationsToSend(Date effectiveDate, String orcid, Date recordActiveDate);
    
    List<NotificationEntity> findNotificationsToSendLegacy(Date effectiveDate, String orcid, Float emailFrequency, Date recordActiveDate);

    NotificationEntity findByOricdAndId(String orcid, Long id);
    
    void flagAsSent(Long id);

    void flagAsSent(Collection<Long> ids);

    void flagAsRead(String orcid, Long id);

    void flagAsArchived(String orcid, Long id);
    
    void flagAsSendable(String orcid, Long id);
    
    void flagAsNonSendable(String orcid, Long id);
    
    void deleteNotificationById(Long notificationId);

    void deleteNotificationItemByNotificationId(Long notificationId);

    void deleteNotificationWorkByNotificationId(Long notificationId);

    List<NotificationEntity> findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults);

    int archiveNotificationsCreatedBefore(Date createdBefore, int batchSize);
    
    Integer archiveOffsetNotifications(Integer offset);
    
    List<Object[]> findNotificationsToDeleteByOffset(Integer offset, Integer recordsPerBatch);
    
    List<NotificationEntity> findNotificationsCreatedBefore(Date createdBefore, int batchSize);
    
    List<NotificationEntity> findUnsentServiceAnnouncements(int batchSize);
    
    List<NotificationEntity> findUnsentTips(int batchSize);
    
    void updateRetryCount(String orcid, Long id, Long retryCount);

    boolean deleteNotificationsForRecord(String orcid, int batchSize);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);

    List<BigInteger> getIdsOfNotificationsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);
    
    List<NotificationEntity> findNotificationsByOrcidAndClientAndFamilyNoClientToken(String orcid, String clientId, String notificationFamily);


}
