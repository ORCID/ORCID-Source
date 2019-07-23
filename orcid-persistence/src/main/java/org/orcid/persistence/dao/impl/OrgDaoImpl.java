package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDaoImpl extends GenericDaoImpl<OrgEntity, Long> implements OrgDao {

    public OrgDaoImpl() {
        super(OrgEntity.class);
    }

    @Override
    public List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults) {
        // Order by ID so we can page through in a predictable way
        TypedQuery<AmbiguousOrgEntity> query = entityManager.createQuery("from AmbiguousOrgEntity order by id", AmbiguousOrgEntity.class);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults) {
        TypedQuery<OrgEntity> query = entityManager.createQuery("from OrgEntity where lower(name) like lower(:searchTerm) || '%' order by name", OrgEntity.class);
        query.setParameter("searchTerm", searchTerm);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    @Override
    public List<OrgEntity> getOrgsByName(String searchTerm) {
    	TypedQuery<OrgEntity> query = entityManager.createQuery("from OrgEntity where lower(name) like lower(:searchTerm) order by name", OrgEntity.class);
    	query.setParameter("searchTerm", searchTerm);
    	return query.getResultList();
    }

    @Override
    public OrgEntity findByNameCityRegionAndCountry(String name, String city, String region, String country) {
        TypedQuery<OrgEntity> query = entityManager.createQuery(
                "from OrgEntity where name = :name and city = :city and (region = :region or (region is null and :region is null)) and country = :country",
                OrgEntity.class);
        query.setParameter("name", name);
        query.setParameter("city", city);
        query.setParameter("region", region);
        query.setParameter("country", country);
        List<OrgEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public OrgEntity findByNameCityRegionCountryAndType(String name, String city, String region, String country, String orgType) {
        TypedQuery<OrgEntity> query = entityManager.createQuery(
                "from OrgEntity where name = :name and city = :city and (region = :region or (region is null and :region is null)) and country = :country and orgDisambiguated.orgType = :orgType",
                OrgEntity.class);
        query.setParameter("name", name);
        query.setParameter("city", city);
        query.setParameter("region", region);
        query.setParameter("country", country);
        query.setParameter("orgType", orgType);
        List<OrgEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Deletes all orgs where the source matches the give app id
     * @param clientSourceId the app id
     * */
    @Override
    @Transactional
    public void removeOrgsByClientSourceId(String clientSourceId) {
        Query query = entityManager.createNativeQuery("delete from org where client_source_id=:clientSourceId");
        query.setParameter("clientSourceId", clientSourceId);
        query.executeUpdate();
    }

    @Override
    public OrgEntity findByAddressAndDisambiguatedOrg(String name, String city, String region, String country, OrgDisambiguatedEntity orgDisambiguated) {
        TypedQuery<OrgEntity> query = entityManager.createQuery(
                "from OrgEntity where name = :name and city = :city and (region = :region or (region is null and :region is null)) and country = :country and (orgDisambiguated.id = :orgDisambiguatedId or (orgDisambiguated is null and :orgDisambiguatedId is null))",
                OrgEntity.class);
        query.setParameter("name", name);
        query.setParameter("city", city);
        query.setParameter("region", region);
        query.setParameter("country", country);
        query.setParameter("orgDisambiguatedId", orgDisambiguated != null ? orgDisambiguated.getId() : null);
        List<OrgEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE org SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE org SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findConstraintViolatingDuplicateOrgDetails() {
        Query query = entityManager.createNativeQuery("SELECT name, CASE WHEN city IS NULL OR city = '' THEN 'nocity' ELSE city END AS citygroup, CASE WHEN region IS NULL OR region = '' THEN 'noregion' ELSE region END AS regiongroup, CASE WHEN country IS NULL OR country = '' THEN 'nocountry' ELSE country END AS countrygroup, org_disambiguated_id FROM org WHERE org_disambiguated_id IS NOT NULL GROUP BY name, citygroup, regiongroup, countrygroup, org_disambiguated_id HAVING COUNT(*) > 1");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getOrgIdsForDuplicateOrgDetails(String name, String city, String region, String country, Long orgDisambiguatedId) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org WHERE COALESCE(name, '') = :name AND COALESCE(city, '') = :city AND COALESCE(region, '') = :region AND COALESCE(country, '') = :country AND COALESCE(org_disambiguated_id, 0) = :orgDisambiguatedId");
        query.setParameter("name", name != null ? name : "");
        query.setParameter("city", city.equals("nocity") ? "" : city);
        query.setParameter("region", region.equals("noregion") ? "" : region);
        query.setParameter("country", country.equals("nocountry") ? "" : country);
        query.setParameter("orgDisambiguatedId", orgDisambiguatedId != null ? orgDisambiguatedId : 0L);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public int convertNullCountriesToEmptyStrings(int batchSize) {
        Query query = entityManager.createNativeQuery("select id from org where country IS NULL");
        query.setMaxResults(batchSize);
        List<BigInteger> nullCountryIds = query.getResultList();
        
        query = entityManager.createNativeQuery("UPDATE org SET country = '' where id IN (:ids)");
        query.setParameter("ids", nullCountryIds);
        return query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public int convertNullCitiesToEmptyStrings(int batchSize) {
        Query query = entityManager.createNativeQuery("select id from org where city IS NULL");
        query.setMaxResults(batchSize);
        List<BigInteger> nullCityIds = query.getResultList();
        
        query = entityManager.createNativeQuery("UPDATE org SET city = '' where id IN (:ids)");
        query.setParameter("ids", nullCityIds);
        return query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public int convertNullRegionsToEmptyStrings(int batchSize) {
        Query query = entityManager.createNativeQuery("select id from org where region IS NULL");
        query.setMaxResults(batchSize);
        List<BigInteger> nullRegionIds = query.getResultList();
        query = entityManager.createNativeQuery("UPDATE org SET region = '' where id IN (:ids)");
        query.setParameter("ids", nullRegionIds);
        return query.executeUpdate();
    }

}