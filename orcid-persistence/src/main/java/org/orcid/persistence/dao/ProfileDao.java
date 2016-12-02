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

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    public boolean exists(String orcid);

    List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * @param indexingStatuses
     *          The list of desired indexing status
     * @param maxResults
     *          Max number of results
     * @return a list of object arrays where the object[0] contains the orcid id and object[1] contains the indexing status                           
     * */
    List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * @param indexingStatuses
     *          The list of desired indexing status
     * @param maxResults
     *          Max number of results
     * @param orcidsToExclude
     *          List of ORCID ids to exclude from the results
     * @return a list of object arrays where the object[0] contains the orcid id and object[1] contains the indexing status                           
     * */
    List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * @param indexingStatuses
     *          The list of desired indexing status
     * @param maxResults
     *          Max number of results
     * @param orcidsToExclude
     *          List of ORCID ids to exclude from the results
     * @return a list of object arrays where the object[0] contains the orcid id and object[1] contains the indexing status                           
     * */
    List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(Collection<IndexingStatus> indexingStatuses, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxDaysBack, int maxResults, Collection<String> orcidsToExclude);

    List<String> findUnclaimedNeedingReminder(int reminderAfterDays, int maxResults, Collection<String> orcidsToExclude);

    List<String> findOrcidsNeedingEmailMigration(int maxResults);

    List<ProfileEntity> findProfilesThatMissedIndexing(int maxResults);

    boolean orcidExists(String orcid);

    void remove(String giverOrcid, String receiverOrcid);

    void removeChildrenWithGeneratedIds(ProfileEntity profileEntity);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    void updateIndexingStatus(String orcid, IndexingStatus indexingStatus);

    IndexingStatus retrieveIndexingStatus(String orcid);

    Long getConfirmedProfileCount();

    Date retrieveLastModifiedDate(String orcid);

    @Deprecated
    void updateLastModifiedDateWithoutResult(String orcid);

    void updateLastModifiedDateAndIndexingStatusWithoutResult(String orcid, Date lastModified, IndexingStatus indexingStatus);

    void updateLastModifiedDateAndIndexingStatus(String orcid, IndexingStatus indexingStatus);

    public List<String> findEmailsUnverfiedDays(int daysUnverified, int maxResults, EmailEventType ev);

    OrcidType retrieveOrcidType(String orcid);

    List<Object[]> findInfoForDecryptionAnalysis();

    Locale retrieveLocale(String orcid);

    void updateLocale(String orcid, Locale locale);

    boolean deprecateProfile(String toDeprecate, String primaryOrcid);

    String retrievePrimaryAccountOrcid(String deprecatedOrcid);

    boolean isProfileDeprecated(String orcid);

    void updateEncryptedPassword(String orcid, String encryptedPassword);

    void updateSecurityQuestion(String orcid, Integer securityQuestionId, String encryptedSecurityAnswer);

    void updatePreferences(String orcid, boolean sendChangeNotifications, boolean sendAdministrativeChangeNotifications, boolean sendOrcidNews,
            boolean sendMemberUpdateRequests, Visibility activitiesVisibilityDefault, boolean enableDeveloperTools, float sendEmailFrequencyDays);

    List<ProfileEntity> findProfilesByOrcidType(OrcidType type);

    boolean updateDeveloperTools(String orcid, boolean enabled);

    public boolean getClaimedStatus(String orcid);
    
    public boolean getClaimedStatusByEmail(String email);

    ClientType getClientType(String orcid);

    MemberType getGroupType(String orcid);

    public boolean removeProfile(String orcid);

    public boolean lockProfile(String orcid);

    public boolean unlockProfile(String orcid);

    public boolean isLocked(String orcid);

    public boolean isDeactivated(String orcid);

    public void updateIpAddress(String orcid, String ipAddress);

    boolean reviewProfile(String orcid);

    boolean unreviewProfile(String orcid);
    
    List<Object[]> findProfilesWhereNamesAreNotMigrated(int batchSize);    
}
