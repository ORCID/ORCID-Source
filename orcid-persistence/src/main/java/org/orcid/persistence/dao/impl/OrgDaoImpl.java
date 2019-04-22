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
    public List<BigInteger> getIdsForClientSourceCorrection(int limit) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org WHERE client_source_id = source_id AND client_source_id IN (SELECT client_details_id FROM client_details WHERE client_type != 'PUBLIC_CLIENT')");
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
    public List<BigInteger> getIdsForUserSourceCorrection(int limit) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org WHERE client_source_id = source_id AND client_source_id IN (SELECT client_details_id FROM client_details WHERE client_type = 'PUBLIC_CLIENT')");
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

}