package org.orcid.persistence.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileDao extends GenericDao<ProfileEntity, String> {

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    ProfileEntity merge(ProfileEntity entity);

    @Transactional(readOnly = true)
    List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not);

    @Transactional(readOnly = true)
    List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pet, Collection<String> orcidsToExclude, boolean not, boolean checkQuarterlyTipsEnabled);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * 
     * @param indexingStatuses
     *            The list of desired indexing status
     * @param maxResults
     *            Max number of results
     * @param delay
     *            A delay that will allow us to obtain records after no one is
     *            modifying it anymore, so, we prevent processing the same
     *            record several times
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @Transactional(readOnly = true)
    Map<String, Date> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Integer delay);

    /**
     * Get a list of the ORCID ids with the given indexing status
     * 
     * @param indexingStatuses
     *            The list of desired indexing status
     * @param maxResults
     *            Max number of results
     * @param delay
     *            A delay that will allow us to obtain records after no one is
     *            modifying it anymore, so, we prevent processing the same
     *            record several times
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @Transactional(readOnly = true)
    Map<String, Date> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude, Integer delay);

    @Transactional(readOnly = true)
    List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxDaysBack, int maxResults, Collection<String> orcidsToExclude);

    @Transactional(readOnly = true)
    List<String> findUnclaimedNeedingReminder(int reminderAfterDays, int maxResults, Collection<String> orcidsToExclude);

    @Transactional(readOnly = true)
    List<String> findOrcidsNeedingEmailMigration(int maxResults);

    @Transactional(readOnly = true)
    List<ProfileEntity> findProfilesThatMissedIndexing(int maxResults);

    @Transactional(readOnly = true)
    boolean orcidExists(String orcid);

    @Transactional(readOnly = true)
    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    @Transactional(readOnly = true)
    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateIndexingStatus(String orcid, IndexingStatus indexingStatus);

    @Transactional(readOnly = true)
    IndexingStatus retrieveIndexingStatus(String orcid);

    @Transactional(readOnly = true)
    Long getConfirmedProfileCount();

    @Transactional(readOnly = true)
    List<ProfileEntity> findByOrcidType(String orcidType);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateLastModifiedDateAndIndexingStatusWithoutResult(String orcid, Date lastModified, IndexingStatus indexingStatus);

    @Transactional(readOnly = true)
    public List<Triple<String, String, Boolean>> findEmailsUnverfiedDays(int daysUnverified, EmailEventType eventSent);

    @Transactional(readOnly = true)
    String retrieveOrcidType(String orcid);

    @Transactional(readOnly = true)
    List<Object[]> findInfoForDecryptionAnalysis();

    @Transactional(readOnly = true)
    String retrieveLocale(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateLocale(String orcid, String locale);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean deprecateProfile(String toDeprecate, String primaryOrcid, String deprecatedMethod, String adminUser);

    @Transactional(readOnly = true)
    String retrievePrimaryAccountOrcid(String deprecatedOrcid);

    @Transactional(readOnly = true)
    boolean isProfileDeprecated(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    void changeEncryptedPassword(String orcid, String encryptedPassword);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateDeveloperTools(String orcid, boolean enabled);

    @Transactional(readOnly = true)
    public boolean getClaimedStatus(String orcid);

    @Transactional(readOnly = true)
    public boolean getClaimedStatusByEmailHash(String email);

    @Transactional(readOnly = true)
    String getClientType(String orcid);

    @Transactional(readOnly = true)
    String getGroupType(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeProfile(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean lockProfile(String orcid, String lockReason, String description, String adminUser);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean unlockProfile(String orcid);

    @Transactional(readOnly = true)
    public boolean isLocked(String orcid);

    @Transactional(readOnly = true)
    public boolean isDeactivated(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastLoginDetails(String orcid, String ipAddress);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean reviewProfile(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean unreviewProfile(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateDefaultVisibility(String orcid, String visibility);

    @Transactional(readOnly = true)
    List<String> getProfilesWithNoHashedOrcid(int limit);

    @Transactional(propagation = Propagation.REQUIRED)
    void hashOrcidIds(String orcid, String hashedOrcid);

    @Transactional(readOnly = true)
    public Date getLastLogin(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    void disable2FA(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    void enable2FA(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    void update2FASecret(String orcid, String secret);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean deactivate(String orcid);

    @Transactional(readOnly = true)
    List<OrcidGrantedAuthority> getGrantedAuthoritiesForProfile(String orcid);

    @Transactional(readOnly = true)
    List<ProfileEventEntity> getProfileEvents(String orcid, List<ProfileEventType> eventTypeNames);

    @Transactional(readOnly = true)
    ProfileEntity getLockedReason(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteProfilesOfType(String orcidType);

    @Transactional(readOnly = true)
    List<String> getAllOrcidIdsForInvalidRecords();

    @Transactional(propagation = Propagation.REQUIRED)
    void updateIndexingStatus(List<String> ids, IndexingStatus reindex);

    @Transactional(readOnly = true)
    public List<String> registeredBetween(Date startDate, Date endDate);

    @Transactional(readOnly = true)
    boolean isOrcidValidAsDelegate(String orcid);

    // ********* Signin Lock Methods *********
    @Transactional(readOnly = true)
    public List<Object[]> getSigninLock(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public void startSigninLock(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public void resetSigninLock(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSigninLock(String orcid, Integer count);

    @Transactional(readOnly = true)
    boolean haveMemberPushedWorksOrAffiliationsToRecord(String orcid, String clientId);

    @Transactional(readOnly = true)
    public List<Pair<String, String>> findEmailsToSendAddWorksEmail(int profileCreatedNumberOfDaysAgo);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateDeprecation(String deprecated, String primaryOrcid);

    @Transactional(readOnly = true)
    public boolean isReviewed(String orcid);

}
