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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.BaseEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    /**
     * <p>
     * Search for an ProfileEntity with a matching email address.
     * </p>
     * 
     * @param email
     *            to find the profile by
     * @return the OrcidProfile with an email matching the email in their
     *         profile
     */
    @Override
    public ProfileEntity findByEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            TypedQuery<ProfileEntity> query = entityManager.createQuery("from ProfileEntity pe where lower(pe.email) = :email", ProfileEntity.class);
            query.setParameter("email", email.toLowerCase());
            try {
                return query.getSingleResult();
            } catch (NonUniqueResultException e) {
                LOGGER.error("Attempted to retrieve ProfileEntity by email and was a non-unique result, for email {} ", email);
            } catch (NoResultException e) {
                LOGGER.debug("No ProfileEntity result found for email {} ", email);
            }
        }
        return null;
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
        StringBuilder builder = new StringBuilder("select p.id from ProfileEntity p where p.indexingStatus = :indexingStatus");
        if (!orcidsToExclude.isEmpty()) {
            builder.append(" and p.id not in :orcidsToExclude");
        }
        builder.append(" order by p.lastModified");
        TypedQuery<String> query = entityManager.createQuery(builder.toString(), String.class);
        query.setParameter("indexingStatus", indexingStatus);
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

    @Override
    public List<String> findOrcidsNeedingEmailMigration(int maxResults) {
        TypedQuery<String> query = entityManager.createQuery("select p.id from ProfileEntity p where email is not null and orcidType != 'CLIENT'", String.class);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    /**
     * Similar to that of {@link #findByEmail}, but in this case it will simply
     * return true or false dependent on the count returned by the query
     * <p/>
     * The email address to check CANNOT be null
     * 
     * @param email
     *            the ORCID to limit the query to
     * @return true if the count of the query is greater than 0 (should only
     *         ever be 0 or 1)
     */
    @Override
    public boolean emailExists(String email) {
        Assert.hasText(email, "Cannot check for an empty email address");
        TypedQuery<Long> query = entityManager.createQuery("select count(pe.email) from ProfileEntity pe where lower(pe.email) = :email", Long.class);
        query.setParameter("email", email.toLowerCase());
        Long result = query.getSingleResult();
        return (result != null && result > 0);
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
        removeChildren(orcid, profileEntity.getProfileGrants());
        removeChildren(orcid, profileEntity.getProfilePatents());
        removeChildren(orcid, profileEntity.getProfileWorks());
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

}
