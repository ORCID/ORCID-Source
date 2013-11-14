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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.springframework.cache.annotation.Cacheable;
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
    public OrgDisambiguatedEntity findByNameCityRegionCountryAndSourceType(String name, String city, String region, Iso3166Country country, String sourceType) {
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
    public List<OrgDisambiguatedEntity> findOrgsByIndexingStatus(IndexingStatus indexingStatus, int firstResult, int maxResult) {
        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createQuery("from OrgDisambiguatedEntity where indexingStatus = :indexingStatus",
                OrgDisambiguatedEntity.class);
        query.setParameter("indexingStatus", indexingStatus);
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

}