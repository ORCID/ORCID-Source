package org.orcid.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    public boolean exists(String orcid);

    List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not);

    List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not, boolean checkQuarterlyTipsEnabled);
    
    /**
     * Get a list of the ORCID ids with the given indexing status
     * @param indexingStatuses
     *          The list of desired indexing status
     * @param maxResults
     *          Max number of results
     * @param delay
     *          A delay that will allow us to obtain records after no one is modifying it anymore, so, we prevent processing the same record several times
     * @return a list of object arrays where the object[0] contains the orcid id and object[1] contains the indexing status                           
     * */
    List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Integer delay);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * @param indexingStatuses
     *          The list of desired indexing status
     * @param maxResults
     *          Max number of results
     * @param orcidsToExclude
     *          List of ORCID ids to exclude from the results
     * @param delay
     *          A delay that will allow us to obtain records after no one is modifying it anymore, so, we prevent processing the same record several times
     * @return a list of object arrays where the object[0] contains the orcid id and object[1] contains the indexing status                           
     * */
    List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude, Integer delay);
    
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

    public List<Pair<String, Date>> findEmailsUnverfiedDays(int daysUnverified, int maxResults);

    String retrieveOrcidType(String orcid);

    List<Object[]> findInfoForDecryptionAnalysis();

    String retrieveLocale(String orcid);

    void updateLocale(String orcid, String locale);

    boolean deprecateProfile(String toDeprecate, String primaryOrcid, String deprecatedMethod, String adminUser);

    String retrievePrimaryAccountOrcid(String deprecatedOrcid);

    boolean isProfileDeprecated(String orcid);

    void updateEncryptedPassword(String orcid, String encryptedPassword);

    void updatePreferences(String orcid, boolean sendChangeNotifications, boolean sendAdministrativeChangeNotifications, boolean sendOrcidNews,
            boolean sendMemberUpdateRequests, String activitiesVisibilityDefault, boolean enableDeveloperTools, float sendEmailFrequencyDays);

    boolean updateDeveloperTools(String orcid, boolean enabled);

    public boolean getClaimedStatus(String orcid);
    
    public boolean getClaimedStatusByEmailHash(String email);

    String getClientType(String orcid);

    String getGroupType(String orcid);

    public boolean removeProfile(String orcid);

    public boolean lockProfile(String orcid, String lockReason, String description);

    public boolean unlockProfile(String orcid);

    public boolean isLocked(String orcid);

    public boolean isDeactivated(String orcid);

    public void updateLastLoginDetails(String orcid, String ipAddress);

    boolean reviewProfile(String orcid);

    boolean unreviewProfile(String orcid);
    
    List<Object[]> findProfilesWhereNamesAreNotMigrated(int batchSize);
        
    boolean updateDefaultVisibility(String orcid, String visibility);
    
    List<String> getProfilesWithNoHashedOrcid(int limit);
    
    void hashOrcidIds(String orcid, String hashedOrcid);
    
    public Date getLastLogin(String orcid);

    void disable2FA(String orcid);

    void enable2FA(String orcid);
    
    void update2FASecret(String orcid, String secret);
    
    boolean deactivate(String orcid);

    List<OrcidGrantedAuthority> getGrantedAuthoritiesForProfile(String orcid);

    List<ProfileEventEntity> getProfileEvents(String orcid, List<ProfileEventType> eventTypeNames);

    public String getLockedReason(String orcid);
}
