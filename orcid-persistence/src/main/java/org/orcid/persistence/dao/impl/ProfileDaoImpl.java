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
package org.orcid.persistence.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.transaction.annotation.Transactional;

public class ProfileDaoImpl extends GenericDaoImpl<ProfileEntity, String> implements ProfileDao {

    public ProfileDaoImpl() {
        super(ProfileEntity.class);
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
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
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults) {
        return findOrcidsByIndexingStatus(indexingStatus, maxResults, Collections.EMPTY_LIST);
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
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @Override
    public List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude) {
        List<IndexingStatus> indexingStatuses = new ArrayList<>(1);
        indexingStatuses.add(indexingStatus);
        return findOrcidsByIndexingStatus(indexingStatuses, maxResults, orcidsToExclude);
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
     * @return a list of object arrays where the object[0] contains the orcid id
     *         and object[1] contains the indexing status
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Pair<String, IndexingStatus>> findOrcidsByIndexingStatus(Collection<IndexingStatus> indexingStatuses, int maxResults,
            Collection<String> orcidsToExclude) {
        StringBuilder builder = new StringBuilder("SELECT p.orcid, p.indexing_status FROM profile p WHERE p.indexing_status IN :indexingStatus");
        if (!orcidsToExclude.isEmpty()) {
            builder.append(" AND p.orcid NOT IN :orcidsToExclude");
        }
        // Ordering by indexing status will force re-indexing to be lower
        // priority than normal indexing
        builder.append(" ORDER BY (p.last_modified > (NOW() - CAST('1' as INTERVAL HOUR))) DESC, indexing_status, p.last_modified");
        Query query = entityManager.createNativeQuery(builder.toString());
        query.setParameter("indexingStatus", IndexingStatus.getNames(indexingStatuses));
        if (!orcidsToExclude.isEmpty()) {
            query.setParameter("orcidsToExclude", orcidsToExclude);
        }
        query.setMaxResults(maxResults);
        List<Object[]> dbInfo = query.getResultList();
        List<Pair<String, IndexingStatus>> results = new ArrayList<Pair<String, IndexingStatus>>();
        dbInfo.stream().forEach(element -> {
            IndexingStatus i = element[1] == null ? null : IndexingStatus.valueOf((String) element[1]);
            Pair<String, IndexingStatus> pair = Pair.of((String) element[0], i);
            results.add(pair);
        });
        return results;
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
    public List<String> findEmailsUnverfiedDays(int daysUnverified, int ignoreOlderThanDays, int maxResults, EmailEventType ev) {
        // @formatter:off
		String queryStr = "SELECT e.email FROM email e "
				+ "LEFT JOIN email_event ev ON e.email = ev.email "
				+ "AND (ev.email_event_type = :evt or ev.email_event_type='VERIFY_EMAIL_7_DAYS_SENT_SKIPPED') "
				+ "JOIN profile p on p.orcid = e.orcid and p.claimed = true "
				+ "AND p.deprecated_date is null AND p.profile_deactivation_date is null AND p.account_expiry is null "
				+ "where ev.email IS NULL " + "and e.is_verified = false "
				+ "and e.date_created < (now() - CAST('" + daysUnverified
				+ "' AS INTERVAL DAY)) "
				+ "and e.date_created > (now() - CAST('" + ignoreOlderThanDays
                                + "' AS INTERVAL DAY)) "
				+ "and (e.source_id = e.orcid OR e.source_id is null)"
				+ " ORDER BY e.last_modified";
	// @formatter:on
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("evt", ev.name());
        query.setMaxResults(maxResults);
        return query.getResultList();
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
     * 
     * @return list of ORCID Ids as a list of strings
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findByMissingEventTypes(int maxResults, List<ProfileEventType> pets, Collection<String> orcidsToExclude, boolean not) {
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
        builder.append("where pe.orcid is ");
        if (!not)
            builder.append("not ");
        builder.append("null ");

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
    public void remove(String giverOrcid, String receiverOrcid) {
        ProfileEntity profileEntity = find(giverOrcid);
        if (profileEntity != null) {
            if (profileEntity.getGivenPermissionTo() != null) {
                Set<GivenPermissionToEntity> filtered = new HashSet<GivenPermissionToEntity>();
                for (GivenPermissionToEntity givenPermissionToEntity : profileEntity.getGivenPermissionTo()) {
                    if (!receiverOrcid.equals(givenPermissionToEntity.getReceiver().getId())) {
                        filtered.add(givenPermissionToEntity);
                    }
                }
                profileEntity.setGivenPermissionTo(filtered);
            }
        }
    }

    @Override
    @Transactional
    public void removeChildrenWithGeneratedIds(ProfileEntity profileEntity) {
        String orcid = profileEntity.getId();
        removeChildren(orcid, profileEntity.getResearcherUrls(), "user.id");
        removeChildren(orcid, profileEntity.getOtherNames());
        removeChildren(orcid, profileEntity.getGivenPermissionTo(), "giver");
    }

    private void removeChildren(String orcid, Collection<? extends BaseEntity<?>> entities) {
        removeChildren(orcid, entities, "profile.id");
    }

    private void removeChildren(String orcid, Collection<? extends BaseEntity<?>> entities, String orcidPath) {
        if (entities != null && !entities.isEmpty()) {
            entityManager.createQuery("delete from " + entities.iterator().next().getClass().getName() + " where " + orcidPath + " = :orcid").setParameter("orcid", orcid)
                    .executeUpdate();
            entities.clear();
        }
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        TypedQuery<Long> query = entityManager
                .createQuery("select count(gpt.id) from GivenPermissionToEntity gpt where gpt.giver = :giverOrcid and gpt.receiver.id = :receiverOrcid", Long.class);
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
    public boolean exists(String orcid) {
        TypedQuery<Long> query = entityManager.createQuery("select count(p.id) from ProfileEntity p where p.id = :orcid", Long.class);
        query.setParameter("orcid", orcid);
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
    @ExcludeFromProfileLastModifiedUpdate
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
        query.executeUpdate();
    }

    @Override
    public Long getConfirmedProfileCount() {
        TypedQuery<Long> query = entityManager.createQuery("select count(pe) from ProfileEntity pe where pe.completedDate is not null", Long.class);
        return query.getSingleResult();
    }

    /**
     * Fetches the last modified from the database Do not call unless it also
     * manages the request level cache
     * 
     * @See ProfileLastModifiedAspect
     * 
     */
    @SuppressWarnings("unchecked")
    public Date retrieveLastModifiedDate(String orcid) {
        Query nativeQuery = entityManager.createNativeQuery("Select p.last_modified FROM profile p WHERE p.orcid =:orcid");
        nativeQuery.setParameter("orcid", orcid);
        List<Timestamp> tsList = nativeQuery.getResultList();
        if (tsList != null && !tsList.isEmpty()) {
            return new Date(tsList.get(0).getTime());
        }
        return null;
    }

    @Override
    @Transactional
    public void updateLastModifiedDateWithoutResult(String orcid) {
        Query query = entityManager.createNativeQuery("update profile set last_modified = now() where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
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

    /**
     * This method is used to update the last modified and indexing status
     * without triggering last update events
     * 
     * @param orcid
     * @param indexingStatus
     */
    @Override
    @Transactional
    @ExcludeFromProfileLastModifiedUpdate
    public void updateLastModifiedDateAndIndexingStatus(String orcid, IndexingStatus indexingStatus) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set lastModified = now(), indexingStatus = :indexingStatus where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("indexingStatus", indexingStatus);
        updateQuery.executeUpdate();
    }

    private void updateWebhookProfileLastUpdate(String orcid) {
        Query query = entityManager
                .createNativeQuery("update webhook set profile_last_modified = (select last_modified from profile where orcid = :orcid ) " + "where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    public OrcidType retrieveOrcidType(String orcid) {
        TypedQuery<OrcidType> query = entityManager.createQuery("select orcidType from ProfileEntity where orcid = :orcid", OrcidType.class);
        query.setParameter("orcid", orcid);
        List<OrcidType> results = query.getResultList();
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
    public Locale retrieveLocale(String orcid) {
        TypedQuery<Locale> query = entityManager.createQuery("select locale from ProfileEntity where orcid = :orcid", Locale.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLocale(String orcid, Locale locale) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set lastModified = now(), locale = :locale where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("locale", locale);
        updateQuery.executeUpdate();

    }

    @Override
    @Transactional
    public boolean deprecateProfile(String toDeprecate, String primaryOrcid) {
        Query query = entityManager.createQuery(
                "update ProfileEntity set lastModified = now(), deprecatedDate = now(), deactivationDate = now(), indexingStatus = :indexing_status, primaryRecord = :primary_record, activitiesVisibilityDefault = :defaultVisibility where orcid = :orcid");
        query.setParameter("orcid", toDeprecate);
        query.setParameter("indexing_status", IndexingStatus.PENDING);
        query.setParameter("primary_record", new ProfileEntity(primaryOrcid));
        query.setParameter("defaultVisibility", Visibility.PRIVATE);

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
    public void updateEncryptedPassword(String orcid, String encryptedPassword) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set lastModified = now(), encryptedPassword = :encryptedPassword where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("encryptedPassword", encryptedPassword);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSecurityQuestion(String orcid, Integer securityQuestionId, String encryptedSecurityAnswer) {
        Query updateQuery = entityManager.createQuery(
                "update ProfileEntity set lastModified = now(), securityQuestion.id = :securityQuestionId, encryptedSecurityAnswer = :encryptedSecurityAnswer where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("securityQuestionId", securityQuestionId);
        updateQuery.setParameter("encryptedSecurityAnswer", encryptedSecurityAnswer);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updatePreferences(String orcid, boolean sendChangeNotifications, boolean sendAdministrativeChangeNotifications, boolean sendOrcidNews,
            boolean sendMemberUpdateRequests, Visibility activitiesVisibilityDefault, boolean enableDeveloperTools, float sendEmailFrequencyDays) {
        Query updateQuery = entityManager.createQuery(
                "update ProfileEntity set lastModified = now(), sendChangeNotifications = :sendChangeNotifications, sendAdministrativeChangeNotifications = :sendAdministrativeChangeNotifications, sendOrcidNews = :sendOrcidNews, sendMemberUpdateRequests = :sendMemberUpdateRequests, activitiesVisibilityDefault = :activitiesVisibilityDefault, enableDeveloperTools = :enableDeveloperTools, sendEmailFrequencyDays = :sendEmailFrequencyDays where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("sendChangeNotifications", sendChangeNotifications);
        updateQuery.setParameter("sendAdministrativeChangeNotifications", sendAdministrativeChangeNotifications);
        updateQuery.setParameter("sendOrcidNews", sendOrcidNews);
        updateQuery.setParameter("sendMemberUpdateRequests", sendMemberUpdateRequests);
        updateQuery.setParameter("activitiesVisibilityDefault", activitiesVisibilityDefault);
        updateQuery.setParameter("enableDeveloperTools", enableDeveloperTools);
        updateQuery.setParameter("sendEmailFrequencyDays", sendEmailFrequencyDays);
        updateQuery.executeUpdate();
    }

    /**
     * Return the list of profiles that belongs to the provided OrcidType
     * 
     * @param type
     *            OrcidType that indicates the profile type we want to fetch
     * @return the list of profiles that belongs to the specified type
     */
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<ProfileEntity> findProfilesByOrcidType(OrcidType type) {
        Query query = entityManager.createQuery("from ProfileEntity where profile_deactivation_date=NULL and orcidType=:type");
        query.setParameter("type", type);
        return (List<ProfileEntity>) query.getResultList();
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
    public ClientType getClientType(String orcid) {
        TypedQuery<ClientType> query = entityManager.createQuery("select clientType from ClientDetailsEntity where id = :orcid", ClientType.class);
        query.setParameter("orcid", orcid);
        List<ClientType> results = query.getResultList();
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
    public MemberType getGroupType(String orcid) {
        TypedQuery<MemberType> query = entityManager.createQuery("select groupType from ProfileEntity where orcid = :orcid", MemberType.class);
        query.setParameter("orcid", orcid);
        List<MemberType> results = query.getResultList();
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
    public boolean lockProfile(String orcid, String reason, String description) {
        return changeLockedStatus(orcid, true, reason, description);
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
        return changeLockedStatus(orcid, false, null, null);
    }

    @Transactional
    private boolean changeLockedStatus(String orcid, boolean locked, String reason, String description) {
        Query query = entityManager.createNativeQuery(
                "update profile set last_modified=now(), indexing_status='REINDEX', record_locked=:locked, reason_locked=:lockReason, reason_locked_description=:description where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("locked", locked);
        query.setParameter("lockReason", reason);
        query.setParameter("description", description);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean isLocked(String orcid) {
        TypedQuery<Boolean> query = entityManager.createQuery("select recordLocked from ProfileEntity where orcid = :orcid", Boolean.class);
        query.setParameter("orcid", orcid);
        Boolean result = query.getSingleResult();
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
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Object[]> findProfilesWhereNamesAreNotMigrated(int batchSize) {
        Query query = entityManager.createNativeQuery(
                "SELECT orcid, given_names, family_name, credit_name, names_visibility, biography, biography_visibility, activities_visibility_default FROM profile p WHERE NOT EXISTS (SELECT n.orcid FROM record_name n WHERE n.orcid = p.orcid) order by orcid limit :batchSize");
        query.setParameter("batchSize", batchSize);
        return (List<Object[]>) query.getResultList();
    }

    @Override
    public boolean getClaimedStatusByEmail(String email) {
        Query query = entityManager.createNativeQuery("SELECT claimed FROM profile WHERE orcid=(SELECT orcid FROM email WHERE trim(lower(email)) = trim(lower(:email)))");
        query.setParameter("email", email);
        return (Boolean) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateNotificationsPreferences(String orcid, boolean sendChangeNotifications, boolean sendAdministrativeChangeNotifications, boolean sendOrcidNews,
            boolean sendMemberUpdateRequests) {
        Query updateQuery = entityManager.createQuery(
                "update ProfileEntity set lastModified = now(), sendChangeNotifications = :sendChangeNotifications, sendAdministrativeChangeNotifications = :sendAdministrativeChangeNotifications, sendOrcidNews = :sendOrcidNews, sendMemberUpdateRequests = :sendMemberUpdateRequests where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("sendChangeNotifications", sendChangeNotifications);
        updateQuery.setParameter("sendAdministrativeChangeNotifications", sendAdministrativeChangeNotifications);
        updateQuery.setParameter("sendOrcidNews", sendOrcidNews);
        updateQuery.setParameter("sendMemberUpdateRequests", sendMemberUpdateRequests);
        return updateQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateDefaultVisibility(String orcid, Visibility visibility) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), activitiesVisibilityDefault = :activitiesVisibilityDefault where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("activitiesVisibilityDefault", visibility);
        return updateQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendEmailFrequencyDays(String orcid, Float sendEmailFrequencyDays) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), sendEmailFrequencyDays = :sendEmailFrequencyDays where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("sendEmailFrequencyDays", sendEmailFrequencyDays);
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
        Query query = entityManager.createQuery("update ProfileEntity set lastModified = now(), profile_deactivation_date = now(), indexing_status = 'REINDEX' where orcid = :orcid");
        query.setParameter("orcid", orcid);        
        return query.executeUpdate() > 0;
    }
}
