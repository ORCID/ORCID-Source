package org.orcid.persistence.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class ProfileDaoImpl extends GenericDaoImpl<ProfileEntity, String> implements ProfileDao {

    private static final String PRIVATE_VISIBILITY = "PRIVATE";

    @Value("${org.orcid.postgres.query.timeout:30000}")
    private Integer queryTimeout;

    public ProfileDaoImpl() {
        super(ProfileEntity.class);
    }

    @Override
    public void remove(String id) {
        super.remove(id);
    }

    /**
     * Get a list of the ORCID id's with the given indexing status
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
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Integer delay) {
        return findOrcidsByIndexingStatus(indexingStatus, maxResults, Collections.EMPTY_LIST, delay);
    }

    /**
     * Get a list of the ORCID id's with the given indexing status
     * 
     * @param indexingStatuses
     *            The list of desired indexing status
     * @param maxResults
     *            Max number of results
     * @param orcidsToExclude
     *            List of ORCID id's to exclude from the results
     * @param delay
     *            A delay that will allow us to obtain records after no one is
     *            modifying it anymore, so, we prevent processing the same
     *            record several times
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude, Integer delay) {
        StringBuilder builder = new StringBuilder("SELECT p.orcid FROM profile p WHERE p.indexing_status = :indexingStatus ");
        if (delay != null && delay > 0) {
            builder.append(" AND (p.last_indexed_date is null OR p.last_indexed_date < now() - INTERVAL '" + delay + " min') ");
        }
        if (!orcidsToExclude.isEmpty()) {
            builder.append(" AND p.orcid NOT IN :orcidsToExclude");
        }
        // Ordering by last modified so we get the oldest modified first
        builder.append(" ORDER BY p.last_modified");
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setParameter("indexingStatus", indexingStatus.name());
        if (!orcidsToExclude.isEmpty()) {
            query.setParameter("orcidsToExclude", orcidsToExclude);
        }
        query.setMaxResults(maxResults);
        // Sets a timeout for this query
        query.setHint("javax.persistence.query.timeout", queryTimeout);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxDaysBack, int maxResults, Collection<String> orcidsToExclude) {

        StringBuilder builder = new StringBuilder("SELECT orcid FROM profile p");
        builder.append(" WHERE p.claimed = false");
        builder.append(" AND p.indexing_status != :indexingStatus");
        // Has to be have been created before our min wait date
        builder.append(" AND p.date_created < now() - (CAST('1' AS INTERVAL DAY) * ");
        builder.append(waitPeriodDays);
        builder.append(")");
        // Max number of days limits how many days we go back and check
        builder.append(" AND p.date_created > now() - (CAST('1' AS INTERVAL DAY) * ");
        builder.append(maxDaysBack);
        builder.append(")");
        // Has not been indexed during the waitPeriodDays number of days after
        // creation.
        builder.append(" AND (p.last_indexed_date < p.date_created + (CAST('1' AS INTERVAL DAY) * ");
        builder.append(waitPeriodDays);
        builder.append(") OR p.last_indexed_date IS NULL)");
        if (!orcidsToExclude.isEmpty()) {
            builder.append(" AND p.orcid NOT IN :orcidsToExclude");
        }
        builder.append(" ORDER BY p.last_modified");
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setParameter("indexingStatus", IndexingStatus.PENDING.name());
        if (!orcidsToExclude.isEmpty()) {
            query.setParameter("orcidsToExclude", orcidsToExclude);
        }
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pair<String, Date>> findEmailsUnverfiedDays(int daysUnverified, int maxResults) {
        StringBuilder queryString = new StringBuilder("SELECT e.email, e.date_created FROM email e ");
        queryString.append("LEFT JOIN email_event ev ON e.email = ev.email ");
        queryString.append("JOIN profile p on p.orcid = e.orcid and p.claimed = true ");
        queryString.append("AND p.deprecated_date is null AND p.profile_deactivation_date is null AND p.account_expiry is null ");
        queryString.append("where ev.email IS NULL " + "and e.is_verified = false ");
        queryString.append("and e.date_created < (now() - CAST('").append(daysUnverified).append("' AS INTERVAL DAY)) ");
        queryString.append("and (e.source_id = e.orcid OR e.source_id is null)");
        queryString.append(" ORDER BY e.last_modified");

        Query query = entityManager.createNativeQuery(queryString.toString());
        query.setMaxResults(maxResults);
        List<Object[]> dbInfo = query.getResultList();
        List<Pair<String, Date>> results = new ArrayList<Pair<String, Date>>();
        dbInfo.stream().forEach(element -> {
            Pair<String, Date> pair = Pair.of((String) element[0], (Date) element[1]);
            results.add(pair);
        });
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findUnclaimedNeedingReminder(int remindAfterDays, int maxResults, Collection<String> orcidsToExclude) {
        StringBuilder builder = new StringBuilder(
                "SELECT p.orcid FROM profile p LEFT JOIN profile_event e ON e.orcid = p.orcid AND e.profile_event_type = :profileEventType");
        builder.append(" WHERE p.claimed = false");
        builder.append(" AND p.deprecated_date is null AND p.profile_deactivation_date is null AND p.account_expiry is null ");
        // Hasn't already been sent a reminder
        builder.append(" AND e.orcid IS NULL");
        // Has to be have been created at least remindAfterDays number of days
        // ago
        builder.append(" AND p.date_created < (now() - CAST('");
        // Doesn't seem to work correctly in postgresql when using placeholder
        // param, so wait period is inlined.
        builder.append(remindAfterDays);
        builder.append("' AS INTERVAL DAY))");
        if (!orcidsToExclude.isEmpty()) {
            builder.append(" AND p.orcid NOT IN :orcidsToExclude");
        }
        builder.append(" ORDER BY p.last_modified");
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setParameter("profileEventType", ProfileEventType.CLAIM_REMINDER_SENT.name());
        if (!orcidsToExclude.isEmpty()) {
            query.setParameter("orcidsToExclude", orcidsToExclude);
        }
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pets, Collection<String> orcidsToExclude, boolean not) {
        return findByMissingEventTypes(maxResults, pets, orcidsToExclude, not, false);
    }

    /**
     * Finds ORCID Ids by ProfileEventTypes
     * 
     * @param maxResults
     *            Maximum number of results returned.
     * 
     * @param pets
     *            A list of ProfileEventTypes.
     * 
     * @param not
     *            If false ORCIDs returned ARE associated with one of the
     *            ProfileEventTypes If true ORCIDs returned ARE NOT associated
     *            with one of the ProfileEventTypes
     * 
     * @param orcidsToExclude
     *            ORCID Ids to be excluded from reuturned results
     * @param checkQuarterlyTipsEnabled
     *            If true, results will include only orcid ids with
     *            send_quarterly_tips enabled
     * @return list of ORCID Ids as a list of strings
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pets, Collection<String> orcidsToExclude, boolean not,
            boolean checkQuarterlyTipsEnabled) {
        /*
         * builder produces a query that will look like the following
         * 
         * select p.orcid from profile p left join profile_event pe on pe.orcid
         * = p.orcid and
         * (pe.profile_event_type0='EMAIL_VERIFY_CROSSREF_MARKETING_CHECK' or
         * pe.profile_event_type0='EMAIL_VERIFY_CROSSREF_MARKETING_FAIL') where
         * pe.orcid is null limit 1000;
         */
        StringBuilder builder = new StringBuilder();

        builder.append("select p.orcid from profile p left join profile_event pe on pe.orcid = p.orcid and ");

        // builder.append("in (select pe.orcid from profile_event as pe where
        // ");
        builder.append("(");
        for (int i = 0; i < pets.size(); i++) {
            if (i != 0)
                builder.append("or ");
            builder.append("pe.profile_event_type=:profileEventType");
            builder.append(Integer.toString(i));
            builder.append(" ");
        }
        builder.append(")");

        if (checkQuarterlyTipsEnabled) {
            builder.append(" left join email_frequency e on e.orcid = p.orcid");
        }

        builder.append(" where pe.orcid is ");
        if (!not)
            builder.append("not ");
        builder.append("null ");

        if (checkQuarterlyTipsEnabled) {
            builder.append(" AND e.send_quarterly_tips is true");
        }

        if (orcidsToExclude != null && !orcidsToExclude.isEmpty()) {
            builder.append(" AND p.orcid NOT IN :orcidsToExclude");
        }

        Query query = entityManager.createNativeQuery(builder.toString());

        for (int i = 0; i < pets.size(); i++) {
            query.setParameter("profileEventType" + i, pets.get(i).name());
        }

        if (orcidsToExclude != null && !orcidsToExclude.isEmpty()) {
            query.setParameter("orcidsToExclude", orcidsToExclude);
        }
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public List<String> findOrcidsNeedingEmailMigration(int maxResults) {
        TypedQuery<String> query = entityManager.createQuery("select p.id from ProfileEntity p where email is not null and orcidType != 'CLIENT'", String.class);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public List<ProfileEntity> findProfilesThatMissedIndexing(int maxResults) {
        TypedQuery<ProfileEntity> query = entityManager.createQuery(
                "from ProfileEntity where (lastModified > lastIndexedDate or lastIndexedDate is null) and indexingStatus not in ('PENDING', 'IGNORE') order by lastModified",
                ProfileEntity.class);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public boolean orcidExists(String orcid) {
        TypedQuery<Long> query = entityManager.createQuery("select count(pe.id) from ProfileEntity pe where pe.id=:orcid", Long.class);
        query.setParameter("orcid", orcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        TypedQuery<Long> query = entityManager
                .createQuery("select count(gpt.id) from GivenPermissionToEntity gpt where gpt.giver = :giverOrcid and gpt.receiver = :receiverOrcid", Long.class);
        query.setParameter("giverOrcid", giverOrcid);
        query.setParameter("receiverOrcid", receiverOrcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select count(p.id) from ProfileEntity p where p.claimed = FALSE and (p.source.sourceClient.id = :clientId or p.source.sourceProfile.id = :clientId) and p.id = :messageOrcid",
                Long.class);
        query.setParameter("clientId", clientId);
        query.setParameter("messageOrcid", messageOrcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public IndexingStatus retrieveIndexingStatus(String orcid) {
        TypedQuery<IndexingStatus> query = entityManager.createQuery("select indexingStatus from ProfileEntity where orcid = :orcid", IndexingStatus.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateIndexingStatus(String orcid, IndexingStatus indexingStatus) {
        String queryString = null;
        if (IndexingStatus.DONE.equals(indexingStatus)) {
            queryString = "update ProfileEntity set indexingStatus = :indexingStatus, lastIndexedDate = now() where orcid = :orcid";
            updateWebhookProfileLastUpdate(orcid);
        } else {
            queryString = "update ProfileEntity set indexingStatus = :indexingStatus where orcid = :orcid";
        }
        Query query = entityManager.createQuery(queryString);
        query.setParameter("orcid", orcid);
        query.setParameter("indexingStatus", indexingStatus);
        // Sets a timeout for this query
        query.setHint("javax.persistence.query.timeout", queryTimeout);
        query.executeUpdate();
    }

    @Override
    public Long getConfirmedProfileCount() {
        TypedQuery<Long> query = entityManager.createQuery("select count(pe) from ProfileEntity pe where pe.completedDate is not null", Long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLastModifiedDateAndIndexingStatusWithoutResult(String orcid, Date lastModified, IndexingStatus indexingStatus) {
        Query query = entityManager.createNativeQuery("update profile set last_modified = :lastModified, indexing_status = :indexingStatus where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.setParameter("lastModified", lastModified);
        query.setParameter("indexingStatus", indexingStatus.name());
        query.executeUpdate();
    }

    private void updateWebhookProfileLastUpdate(String orcid) {
        Query query = entityManager
                .createNativeQuery("update webhook set profile_last_modified = (select last_modified from profile where orcid = :orcid ) where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    public String retrieveOrcidType(String orcid) {
        TypedQuery<String> query = entityManager.createQuery("select orcidType from ProfileEntity where orcid = :orcid", String.class);
        query.setParameter("orcid", orcid);
        List<String> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Object[]> findInfoForDecryptionAnalysis() {
        Query query = entityManager.createQuery("select id, encryptedSecurityAnswer from ProfileEntity");
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results;
    }

    @Override
    public String retrieveLocale(String orcid) {
        TypedQuery<String> query = entityManager.createQuery("select locale from ProfileEntity where orcid = :orcid", String.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLocale(String orcid, String locale) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), locale = :locale, indexingStatus = :indexing_status where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("locale", locale);
        updateQuery.setParameter("indexing_status", IndexingStatus.PENDING);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public boolean deprecateProfile(String toDeprecate, String primaryOrcid, String deprecatedMethod, String adminUser) {
        StringBuilder queryString = new StringBuilder(
                "update ProfileEntity set lastModified = now(), deprecatedDate = now(), deactivationDate = now(), indexingStatus = :indexing_status, primaryRecord = :primary_record, activitiesVisibilityDefault = :defaultVisibility, deprecatedMethod = :deprecatedMethod");
        if (ProfileEntity.ADMIN_DEPRECATION.equals(deprecatedMethod) && adminUser != null) {
            queryString.append(", deprecatingAdmin = :deprecatingAdmin");
        }
        queryString.append(" where orcid = :orcid");

        Query query = entityManager.createQuery(queryString.toString());
        query.setParameter("orcid", toDeprecate);
        query.setParameter("indexing_status", IndexingStatus.PENDING);
        query.setParameter("primary_record", new ProfileEntity(primaryOrcid));
        query.setParameter("defaultVisibility", PRIVATE_VISIBILITY);
        query.setParameter("deprecatedMethod", deprecatedMethod);
        if (ProfileEntity.ADMIN_DEPRECATION.equals(deprecatedMethod) && adminUser != null) {
            query.setParameter("deprecatingAdmin", adminUser);
        }

        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    public boolean isProfileDeprecated(String orcid) {
        return retrievePrimaryAccountOrcid(orcid) != null;
    }

    @Override
    public String retrievePrimaryAccountOrcid(String deprecatedOrcid) {
        Query query = entityManager.createNativeQuery("select primary_record from profile where orcid = :orcid");
        query.setParameter("orcid", deprecatedOrcid);
        return (String) query.getSingleResult();
    }

    @Override
    @Transactional
    public void changeEncryptedPassword(String orcid, String encryptedPassword) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set encryptedPassword = :encryptedPassword where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("encryptedPassword", encryptedPassword);
        updateQuery.executeUpdate();
    }

    /**
     * enable or disable developer tools from a user
     * 
     * @param orcid
     *            the orcid of the profile to be updated
     * @param enabled
     *            the new value of the developer tools
     * @return true if the developer tools was successfully updated
     */
    @Override
    @Transactional
    public boolean updateDeveloperTools(String orcid, boolean enabled) {
        Query query = entityManager.createQuery("update ProfileEntity set enableDeveloperTools=:enabled, lastModified=now() where orcid=:orcid");
        if (enabled)
            query = entityManager
                    .createQuery("update ProfileEntity set enableDeveloperTools=:enabled, developerToolsEnabledDate=now(), lastModified=now() where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("enabled", enabled);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean getClaimedStatus(String orcid) {
        Query query = entityManager.createNativeQuery("select claimed from profile where orcid=:orcid");
        query.setParameter("orcid", orcid);
        return (Boolean) query.getSingleResult();
    }

    /**
     * Get the client type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the client type, null if it is not a client
     */
    @Override
    public String getClientType(String orcid) {
        TypedQuery<String> query = entityManager.createQuery("select clientType from ClientDetailsEntity where id = :orcid", String.class);
        query.setParameter("orcid", orcid);
        List<String> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get the group type of a profile
     * 
     * @param orcid
     *            The profile to look for
     * @return the group type, null if it is not a group
     */
    @Override
    public String getGroupType(String orcid) {
        TypedQuery<String> query = entityManager.createQuery("select groupType from ProfileEntity where orcid = :orcid", String.class);
        query.setParameter("orcid", orcid);
        List<String> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Removes a deactivated profile
     * 
     * @param orcid
     *            The id of the profile to remove
     * @return true if the profile was deleted
     */
    @Override
    @Transactional
    public boolean removeProfile(String orcid) {
        ProfileEntity toDelete = this.find(orcid);
        if (toDelete.getDeactivationDate() != null) {
            this.remove(toDelete);
            return true;
        }
        return false;
    }

    /**
     * Set the locked status of an account to true
     * 
     * @param orcid
     *            the id of the profile that should be locked
     * @return true if the account was locked
     */
    @Override
    @Transactional
    public boolean lockProfile(String orcid, String reason, String description, String adminUser) {
        Query query = entityManager.createNativeQuery(
                "update profile set record_locked=true, last_modified=now(), record_locked_date=now(), record_locked_admin_id=:adminUser, indexing_status=:indexingStatus, reason_locked=:lockReason, reason_locked_description=:description where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("lockReason", reason);
        query.setParameter("description", description.trim());
        query.setParameter("adminUser", adminUser);
        query.setParameter("indexingStatus", IndexingStatus.REINDEX.name());
        return query.executeUpdate() > 0;
    }

    /**
     * Set the locked status of an account to false
     * 
     * @param orcid
     *            the id of the profile that should be unlocked
     * @return true if the account was locked
     */
    @Override
    @Transactional
    public boolean unlockProfile(String orcid) {
        Query query = entityManager.createNativeQuery(
                "update profile set record_locked=false, last_modified=now(), record_locked_date=null, record_locked_admin_id=null, indexing_status=:indexingStatus, reason_locked=null, reason_locked_description=null where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("indexingStatus", IndexingStatus.REINDEX.name());
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean isLocked(String orcid) {
        TypedQuery<Boolean> query = entityManager.createQuery("select recordLocked from ProfileEntity where orcid = :orcid", Boolean.class);
        query.setParameter("orcid", orcid);
        Boolean result;
        try {
            result = query.getSingleResult();
        } catch (NoResultException nre) {
            throw new NoResultException("ORCID iD " + orcid + " not found");
        }
        return result;
    }

    @Override
    public boolean isDeactivated(String orcid) {
        TypedQuery<Date> query = entityManager.createQuery("select deactivationDate from ProfileEntity where orcid = :orcid", Date.class);
        query.setParameter("orcid", orcid);
        Date result = query.getSingleResult();
        return (result == null) ? false : true;
    }

    @Override
    @Transactional
    public void updateLastLoginDetails(String orcid, String ipAddress) {
        Query query = entityManager.createNativeQuery("update profile set last_login=now(), user_last_ip=:ipAddr where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("ipAddr", ipAddress);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public boolean reviewProfile(String orcid) {
        return changeReviewedStatus(orcid, true);
    }

    @Override
    @Transactional
    public boolean unreviewProfile(String orcid) {
        return changeReviewedStatus(orcid, false);
    }

    @Transactional
    private boolean changeReviewedStatus(String orcid, boolean reviewFlag) {
        Query query = entityManager.createNativeQuery("update profile set last_modified=now(), indexing_status='REINDEX', reviewed=:reviewed where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("reviewed", reviewFlag);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean getClaimedStatusByEmailHash(String emailHash) {
        Query query = entityManager.createNativeQuery("SELECT claimed FROM profile WHERE orcid=(SELECT orcid FROM email WHERE email_hash = :emailHash)");
        query.setParameter("emailHash", emailHash);
        return (Boolean) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateDefaultVisibility(String orcid, String visibility) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), activitiesVisibilityDefault = :activitiesVisibilityDefault where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("activitiesVisibilityDefault", visibility);
        return updateQuery.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getProfilesWithNoHashedOrcid(int limit) {
        Query query = entityManager.createNativeQuery("select orcid from profile where hashed_orcid is null");
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void hashOrcidIds(String orcid, String hashedOrcid) {
        Query query = entityManager.createNativeQuery("update profile set hashed_orcid = :hashedOrcid where orcid = :orcid");
        query.setParameter("hashedOrcid", hashedOrcid);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    public Date getLastLogin(String orcid) {
        TypedQuery<Date> query = entityManager.createQuery("select lastLogin from ProfileEntity where orcid = :orcid", Date.class);
        query.setParameter("orcid", orcid);
        Date result = query.getSingleResult();
        return result;
    }

    @Override
    @Transactional
    public void disable2FA(String orcid) {
        Query query = entityManager.createQuery("update ProfileEntity set lastModified = now(), using2FA = false, secretFor2FA = null where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void enable2FA(String orcid) {
        Query query = entityManager.createQuery("update ProfileEntity set lastModified = now(), using2FA = true where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void update2FASecret(String orcid, String secret) {
        Query query = entityManager.createQuery("update ProfileEntity set lastModified = now(), secretFor2FA = :secret where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("secret", secret);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public boolean deactivate(String orcid) {
        Query query = entityManager.createQuery("update ProfileEntity set lastModified = now(), profile_deactivation_date = now() where orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrcidGrantedAuthority> getGrantedAuthoritiesForProfile(String orcid) {
        Query query = entityManager.createQuery("from OrcidGrantedAuthority where orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProfileEventEntity> getProfileEvents(String orcid, List<ProfileEventType> eventTypes) {
        Query query = entityManager.createQuery("from ProfileEventEntity where orcid = :orcid and type IN :types");
        query.setParameter("orcid", orcid);
        query.setParameter("types", eventTypes);
        return query.getResultList();
    }

    @Override
    public ProfileEntity getLockedReason(String orcid) {
        TypedQuery<ProfileEntity> query = entityManager.createQuery("FROM ProfileEntity where orcid = :orcid", ProfileEntity.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    public List<ProfileEntity> findByOrcidType(String orcidType) {
        TypedQuery<ProfileEntity> query = entityManager.createQuery("FROM ProfileEntity where orcidType = :orcidType", ProfileEntity.class);
        query.setParameter("orcidType", orcidType);
        return query.getResultList();
    }

    @Override
    @Transactional
    public int deleteProfilesOfType(String orcidType) {
        Query query = entityManager.createNativeQuery("DELETE FROM profile WHERE orcid_type = :orcidType");
        query.setParameter("orcidType", orcidType);
        return query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAllOrcidIdsForInvalidRecords() {
        Query query = entityManager
                .createNativeQuery("SELECT orcid FROM profile WHERE profile_deactivation_date IS NOT NULL OR deprecated_date IS NOT NULL OR record_locked IS TRUE");
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateIndexingStatus(List<String> ids, IndexingStatus indexingStatus) {
        String queryString = null;
        if (IndexingStatus.DONE.equals(indexingStatus)) {
            queryString = "UPDATE profile SET indexing_status = :indexingStatus, last_indexed_date = now() WHERE orcid IN :ids";
            ids.forEach(orcid -> updateWebhookProfileLastUpdate(orcid));
        } else {
            queryString = "UPDATE profile SET indexing_status = :indexingStatus WHERE orcid IN :ids";
        }
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("ids", ids);
        query.setParameter("indexingStatus", indexingStatus.name());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<String> registeredBetween(Date startDate, Date endDate) {
        Query query = entityManager.createNativeQuery("SELECT orcid FROM profile WHERE date_created between :startDate and :endDate");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public ProfileEntity merge(ProfileEntity entity) {
        return super.merge(entity);
    }

    @Override
    public boolean isOrcidValidAsDelegate(String orcid) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT count(orcid) FROM ProfileEntity WHERE orcid=:orcid AND profile_deactivation_date is NULL AND deprecated_date is NULL AND primary_record is NULL AND (record_locked is NULL OR record_locked is FALSE)",
                Long.class);
        query.setParameter("orcid", orcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    // ********* Signin Lock Methods *********

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<Object[]> getSigninLock(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT signin_lock_start, signin_lock_last_attempt, signin_lock_count from profile WHERE orcid= :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void startSigninLock(String orcid) {
        String queryString = "UPDATE profile SET signin_lock_start = now(), signin_lock_last_attempt = now(), signin_lock_count = 1  WHERE orcid= :orcid";

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
        return;
    }
    
    @Override
    @Transactional
    public void resetSigninLock(String orcid) {
        String queryString = "UPDATE profile SET signin_lock_start = NULL, signin_lock_count = 0  WHERE orcid= :orcid";

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
        return;
    }

    @Override
    @Transactional
    public void updateSigninLock(String orcid, Integer count) {

        String queryString = "UPDATE profile SET signin_lock_last_attempt = now(), signin_lock_count= :count  WHERE orcid= :orcid";

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("orcid", orcid);
        query.setParameter("count", count);
        query.executeUpdate();
        return;
    }

}
