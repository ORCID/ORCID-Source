package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDisambiguatedDaoImpl extends GenericDaoImpl<OrgDisambiguatedEntity, Long> implements OrgDisambiguatedDao {

    public OrgDisambiguatedDaoImpl() {
        super(OrgDisambiguatedEntity.class);
    }

    @Override
    @Transactional
    //todo: do we need to cache?
    public OrgDisambiguatedEntity findBySourceIdAndSourceType(String sourceId, String sourceType) {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createQuery("from OrgDisambiguatedEntity where sourceId = :sourceId and sourceType = :sourceType",
                OrgDisambiguatedEntity.class);
        query.setParameter("sourceId", sourceId);
        query.setParameter("sourceType", sourceType);
        List<OrgDisambiguatedEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<OrgDisambiguatedEntity> getChunk(int firstResult, int maxResults) {
        // Order by id so that we can page through in a predictable way
        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createQuery("from OrgDisambiguatedEntity order by id", OrgDisambiguatedEntity.class);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public OrgDisambiguatedEntity findByNameCityRegionCountryAndSourceType(String name, String city, String region, String country, String sourceType) {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager
                .createQuery(
                        "from OrgDisambiguatedEntity where name = :name and city = :city and (region = :region or (region is null and :region is null)) and country = :country and sourceType = :sourceType",
                        OrgDisambiguatedEntity.class);
        query.setParameter("name", name);
        query.setParameter("city", city);
        query.setParameter("region", region);
        query.setParameter("country", country);
        query.setParameter("sourceType", sourceType);
        List<OrgDisambiguatedEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<OrgDisambiguatedEntity> findByName(String name) {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager
                .createQuery("from OrgDisambiguatedEntity where lower(name) = lower(:name)", OrgDisambiguatedEntity.class);
        query.setParameter("name", name);
        List<OrgDisambiguatedEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable("orgs")
    public List<OrgDisambiguatedEntity> getOrgs(String searchTerm, int firstResult, int maxResults) {
        String qStr = "select od.*, COUNT(*) as countAll from org_disambiguated od left join org_affiliation_relation oa on od.id = oa.org_id"
                + " where lower(name) like '%' || lower(:searchTerm) || '%' group by od.id "
                + " order by position(lower(:searchTerm) in lower(name)), char_length(name), countAll DESC, od.name";

        Query query = entityManager.createNativeQuery(qStr, OrgDisambiguatedEntity.class);
        query.setParameter("searchTerm", searchTerm);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    @Override
    public List<OrgDisambiguatedEntity> findOrgsPendingIndexing(int firstResult, int maxResult) {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createQuery("from OrgDisambiguatedEntity where indexingStatus != :indexingStatus",
                OrgDisambiguatedEntity.class);
        query.setParameter("indexingStatus", IndexingStatus.DONE);
        query.setFirstResult(0);
        query.setMaxResults(maxResult);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateIndexingStatus(Long orgDisambiguatedId, IndexingStatus indexingStatus) {
        String queryString = null;
        if (IndexingStatus.DONE.equals(indexingStatus)) {
            queryString = "update OrgDisambiguatedEntity set indexingStatus = :indexingStatus, lastIndexedDate = now() where id = :orgDisambiguatedId";
        } else {
            queryString = "update OrgDisambiguatedEntity set indexingStatus = :indexingStatus where id = :orgDisambiguatedId";
        }
        Query query = entityManager.createQuery(queryString);
        query.setParameter("orgDisambiguatedId", orgDisambiguatedId);
        query.setParameter("indexingStatus", indexingStatus);
        query.executeUpdate();
    }

    @Override
    public List<Pair<Long, Integer>> findDisambuguatedOrgsWithIncorrectPopularity(int maxResults) {
        Query query = entityManager
                .createNativeQuery("SELECT od1.id, actual.popularity FROM org_disambiguated od1 JOIN"
                        + " (SELECT od2.id id, COUNT(*) popularity FROM org_disambiguated od2 JOIN org o ON o.org_disambiguated_id = od2.id JOIN org_affiliation_relation oar ON oar.org_id = o.id GROUP BY od2.id)"
                        + " actual ON actual.id = od1.id WHERE od1.popularity <> actual.popularity");
        query.setMaxResults(maxResults);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<Pair<Long, Integer>> pairs = new ArrayList<>();
        for (Object[] row : results) {
            Long id = ((BigInteger) row[0]).longValue();
            Integer popularity = ((BigInteger) row[1]).intValue();
            Pair<Long, Integer> pair = new ImmutablePair<Long, Integer>(id, popularity);
            pairs.add(pair);
        }
        return pairs;
    }

    @Override
    @Transactional
    public void updatePopularity(Long orgDisambiguatedId, Integer popularity) {
        Query query = entityManager.createQuery("update OrgDisambiguatedEntity set indexingStatus = 'PENDING', popularity = :popularity where id = :orgDisambiguatedId");
        query.setParameter("orgDisambiguatedId", orgDisambiguatedId);
        query.setParameter("popularity", popularity);
        query.executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void replace(long deletedOrgDisambiguatedId, long replacementOrgDisambiguatedId) {
        Query query = entityManager
                .createQuery("update OrgEntity set orgDisambiguated.id = :replacementOrgDisambiguatedId where orgDisambiguated.id = :deletedOrgDisambiguatedId");
        query.setParameter("deletedOrgDisambiguatedId", deletedOrgDisambiguatedId);
        query.setParameter("replacementOrgDisambiguatedId", replacementOrgDisambiguatedId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void dropUniqueConstraint() {
        Query query = entityManager.createNativeQuery("ALTER TABLE org_disambiguated DROP CONSTRAINT IF EXISTS org_disambiguated_unique_constraints");
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void createUniqueConstraint() {
        Query query = entityManager
                .createNativeQuery("ALTER TABLE org_disambiguated ADD CONSTRAINT org_disambiguated_unique_constraints UNIQUE (name, city, region, country, source_type)");
        query.executeUpdate();
    }

    @Override
    public List<OrgDisambiguatedEntity> findDuplicates() {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createNamedQuery(OrgDisambiguatedEntity.FIND_DUPLICATES, OrgDisambiguatedEntity.class);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public void clearMemberChosenOrgs() {
        entityManager.createQuery("DELETE from MemberChosenOrgDisambiguatedEntity").executeUpdate();
    }

    @Override
    @Transactional
    public void persistChosenOrg(MemberChosenOrgDisambiguatedEntity chosenOrg) {
        entityManager.persist(chosenOrg);
    }

}