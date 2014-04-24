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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@PersistenceContext(unitName = "orcid")
public class ProfileDaoImpl extends GenericDaoImpl<ProfileEntity, String> implements ProfileDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileDaoImpl.class);

    public ProfileDaoImpl() {
        super(ProfileEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProfileEntity> retrieveSelectableSponsors() {
        return (List<ProfileEntity>) entityManager.createQuery("from ProfileEntity where isSelectableSponsor=true order by vocativeName").getResultList();
    }

    @Override
    public List<String> findOrcidsByName(String name) {
        TypedQuery<String> query = entityManager.createQuery("select id from ProfileEntity where lower(givenNames) like lower(:name || '%') or lower"
                + "(familyName) like lower(:name || '%') or lower(vocativeName) like lower(:name || " + "'%') or lower(creditName) like lower(:name || '%')",
                String.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults) {
        return findOrcidsByIndexingStatus(indexingStatus, maxResults, Collections.EMPTY_LIST);
    }

    @Override
    public List<String> findOrcidsByIndexingStatus(IndexingStatus indexingStatus, int maxResults, Collection<String> orcidsToExclude) {
        List<IndexingStatus> indexingStatuses = new ArrayList<>(1);
        indexingStatuses.add(indexingStatus);
        return findOrcidsByIndexingStatus(indexingStatuses, maxResults, orcidsToExclude);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findOrcidsByIndexingStatus(Collection<IndexingStatus> indexingStatuses, int maxResults, Collection<String> orcidsToExclude) {
        StringBuilder builder = new StringBuilder("SELECT p.orcid FROM profile p WHERE p.indexing_status IN :indexingStatus");
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
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findUnclaimedNotIndexedAfterWaitPeriod(int waitPeriodDays, int maxResults, Collection<String> orcidsToExclude) {
        StringBuilder builder = new StringBuilder("SELECT orcid FROM profile p");
        builder.append(" WHERE p.claimed = false");
        builder.append(" AND p.indexing_status != :indexingStatus");
        // Has to be have been created at least waitPeriodDay number of days ago
        builder.append(" AND p.date_created < (now() - CAST('");
        // Doesn't seem to work correctly in postgresql when using placeholder
        // param, so wait period is inlined.
        builder.append(waitPeriodDays);
        builder.append("' AS INTERVAL DAY))");
        // Has not been indexed during the waitPeriodDays number of days after
        // creation
        builder.append(" AND (p.last_indexed_date < (p.date_created + CAST('");
        builder.append(waitPeriodDays);
        builder.append("' AS INTERVAL DAY)) OR p.last_indexed_date IS NULL)");
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
    public List<String> findEmailsUnverfiedDays(int daysUnverified, int maxResults, EmailEventType ev) {
        //@formatter:off
        String queryStr = 
                  "SELECT e.email FROM email e "
                + "LEFT JOIN email_event ev ON e.email = ev.email "
                + "AND (ev.email_event_type = :evt or ev.email_event_type='VERIFY_EMAIL_7_DAYS_SENT_SKIPPED') "
                + "JOIN profile p on p.orcid = e.orcid and p.claimed = true " 
                + "AND p.deprecated_date is null AND p.profile_deactivation_date is null AND p.account_expiry is null "
                + "where ev.email IS NULL "
                +    "and e.is_verified = false "
                +    "and e.date_created < (now() - CAST('" + daysUnverified + "' AS INTERVAL DAY)) "
                +    " ORDER BY e.last_modified";
        //@formatter:on
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
    public List<String> findByEventTypes(int maxResults, List<ProfileEventType> pets, Collection<String> orcidsToExclude, boolean not) {
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

        // builder.append("in (select pe.orcid from profile_event as pe where ");
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
            entityManager.createQuery("delete from " + entities.iterator().next().getClass().getName() + " where " + orcidPath + " = :orcid")
                    .setParameter("orcid", orcid).executeUpdate();
            entities.clear();
        }
    }

    @Override
    public boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select count(gpt.id) from GivenPermissionToEntity gpt where gpt.giver = :giverOrcid and gpt.receiver.id = :receiverOrcid", Long.class);
        query.setParameter("giverOrcid", giverOrcid);
        query.setParameter("receiverOrcid", receiverOrcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select count(p.id) from ProfileEntity p where p.claimed = FALSE and p.source.id = :clientId and p.id = :messageOrcid", Long.class);
        query.setParameter("clientId", clientId);
        query.setParameter("messageOrcid", messageOrcid);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
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
        query.executeUpdate();
    }

    @Override
    public Long getConfirmedProfileCount() {
        TypedQuery<Long> query = entityManager.createQuery("select count(pe) from ProfileEntity pe where pe.completedDate is not null", Long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateProfile(ProfileEntity profile) {
        Query query = entityManager
                .createNativeQuery("update profile set last_modified=now(), credit_name=:credit_name, family_name=:family_name, given_names=:given_names, biography=:biography, iso2_country=:iso2_country, biography_visibility=:biography_visibility, keywords_visibility=:keywords_visibility, researcher_urls_visibility=:researcher_urls_visibility, other_names_visibility=:other_names_visibility, credit_name_visibility=:credit_name_visibility, profile_address_visibility=:profile_address_visibility, indexing_status='PENDING' where orcid=:orcid");
        query.setParameter("credit_name", profile.getCreditName());
        query.setParameter("family_name", profile.getFamilyName());
        query.setParameter("given_names", profile.getGivenNames());
        query.setParameter("biography", profile.getBiography());
        Iso3166Country iso2Country = profile.getIso2Country();
        query.setParameter("iso2_country", iso2Country != null ? iso2Country.value() : null);
        query.setParameter("biography_visibility", StringUtils.upperCase(profile.getBiographyVisibility().value()));
        query.setParameter("keywords_visibility", StringUtils.upperCase(profile.getKeywordsVisibility().value()));
        query.setParameter("researcher_urls_visibility", StringUtils.upperCase(profile.getResearcherUrlsVisibility().value()));
        query.setParameter("other_names_visibility", StringUtils.upperCase(profile.getOtherNamesVisibility().value()));
        query.setParameter("credit_name_visibility", StringUtils.upperCase(profile.getCreditNameVisibility().value()));
        query.setParameter("profile_address_visibility", StringUtils.upperCase(profile.getProfileAddressVisibility().value()));
        query.setParameter("orcid", profile.getId());

        boolean result = query.executeUpdate() > 0 ? true : false;

        return result;
    }

    public Date retrieveLastModifiedDate(String orcid) {
        TypedQuery<Date> query = entityManager.createQuery("select lastModified from ProfileEntity where orcid = :orcid", Date.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public Date updateLastModifiedDate(String orcid) {
        updateLastModifiedDateWithoutResult(orcid);
        return retrieveLastModifiedDate(orcid);
    }

    @Override
    @Transactional
    public void updateLastModifiedDateWithoutResult(String orcid) {
        Query query = entityManager.createNativeQuery("update profile set last_modified = now() where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    private void updateWebhookProfileLastUpdate(String orcid) {
        Query query = entityManager.createNativeQuery("update webhook set profile_last_modified = (select last_modified from profile where orcid = :orcid ) "
                + "where orcid = :orcid ");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void updateLastModifiedDateAndIndexingStatus(String orcid) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set lastModified = now(), indexingStatus = 'PENDING' where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.executeUpdate();
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
    public boolean deprecateProfile(String deprecatedOrcid, String primaryOrcid) {
        Query query = entityManager
                .createNativeQuery("update profile set last_modified=now(), indexing_status='PENDING', primary_record=:primary_record, deprecated_date=now() where orcid=:orcid");
        query.setParameter("orcid", deprecatedOrcid);
        query.setParameter("primary_record", primaryOrcid);

        boolean result = query.executeUpdate() > 0 ? true : false;

        return result;
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

    public void updateEncryptedPassword(String orcid, String encryptedPassword) {
        Query updateQuery = entityManager.createQuery("update ProfileEntity set lastModified = now(), encryptedPassword = :encryptedPassword where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("encryptedPassword", encryptedPassword);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updateSecurityQuestion(String orcid, Integer securityQuestionId, String encryptedSecurityAnswer) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), securityQuestion.id = :securityQuestionId, encryptedSecurityAnswer = :encryptedSecurityAnswer where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("securityQuestionId", securityQuestionId);
        updateQuery.setParameter("encryptedSecurityAnswer", encryptedSecurityAnswer);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updatePreferences(String orcid, boolean sendChangeNotifications, boolean sendOrcidNews, Visibility activitiesVisibilityDefault,
            boolean enableDeveloperTools) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), sendChangeNotifications = :sendChangeNotifications, sendOrcidNews = :sendOrcidNews, activitiesVisibilityDefault = :activitiesVisibilityDefault, enableDeveloperTools = :enableDeveloperTools where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("sendChangeNotifications", sendChangeNotifications);
        updateQuery.setParameter("sendOrcidNews", sendOrcidNews);
        updateQuery.setParameter("activitiesVisibilityDefault", activitiesVisibilityDefault);
        updateQuery.setParameter("enableDeveloperTools", enableDeveloperTools);
        updateQuery.executeUpdate();
    }
    

    @Override
    @Transactional
    public void updateCountry(String orcid, Iso3166Country iso2Country, Visibility profileAddressVisibility) {
        Query updateQuery = entityManager
                .createQuery("update ProfileEntity set lastModified = now(), iso2_country = :iso2Country,  profile_address_visibility = :profileAddressVisibility where orcid = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.setParameter("iso2Country", iso2Country != null ? iso2Country.value() : null);
        updateQuery.setParameter("profileAddressVisibility", StringUtils.upperCase(profileAddressVisibility.value()));
        updateQuery.executeUpdate();
    }


    /**
     * Return the list of profiles that belongs to the provided OrcidType
     * 
     * @param type
     *            OrcidType that indicates the profile type we want to fetch
     * @return the list of profiles that belongs to the specified type
     * */
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
     * */
    @Override
    @Transactional
    public boolean updateDeveloperTools(String orcid, boolean enabled) {
        Query query = entityManager.createQuery("update ProfileEntity set enableDeveloperTools=:enabled, lastModified=now() where orcid=:orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("enabled", enabled);
        return query.executeUpdate() > 0;
    }
    
}
