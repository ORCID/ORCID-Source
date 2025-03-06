package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.aop.UpdateProfileLastModified;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AddressDaoImpl extends GenericDaoImpl<AddressEntity, Long> implements AddressDao {
    
    private static final String PUBLIC_VISIBILITY = "PUBLIC";

    public AddressDaoImpl() {
        super(AddressEntity.class);
    }

    @Override
    public AddressEntity getAddress(String orcid, Long putCode) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE orcid = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", putCode);
        return (AddressEntity) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAddressesToMigrate() {
        String queryString = "SELECT p.orcid, p.iso2_country, p.profile_address_visibility FROM profile p WHERE p.iso2_country IS NOT NULL AND NOT EXISTS (SELECT a.orcid FROM address a WHERE a.orcid = p.orcid) LIMIT 10000;";                
        Query query = entityManager.createNativeQuery(queryString);                         
        return (List<Object[]>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Cacheable(value = "dao-address", key = "#orcid.concat('-').concat(#lastModified)")
    public List<AddressEntity> getAddresses(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE orcid = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-address", key = "#orcid.concat('-').concat(#lastModified)")
    public List<AddressEntity> getPublicAddresses(String orcid, long lastModified) {
        return getAddresses(orcid, PUBLIC_VISIBILITY);
    }
   
    @SuppressWarnings("unchecked")
    @Override
    public List<AddressEntity> getAddresses(String orcid, String visibility) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE orcid = :orcid and visibility = :visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean deleteAddress(String orcid, Long putCode) {
        Query query = entityManager.createQuery("DELETE FROM AddressEntity WHERE id=:id and orcid = :orcid");
        query.setParameter("id", putCode);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public void removeAllAddress(String orcid) {
        Query query = entityManager.createQuery("delete from AddressEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE address SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE address SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE address SET assertion_origin_source_id = orcid where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NOT NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void revertUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE address SET assertion_origin_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE assertion_origin_source_id IS NOT NULL");
        query.setMaxResults(max);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsOfAddressesReferencingClientProfiles(int max, List<String> ids) {
        Query query = entityManager.createNativeQuery("SELECT id FROM address WHERE source_id IN :ids");
        query.setParameter("ids", ids);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean updateVisibility(String orcid, Visibility visibility) {
        Query query = entityManager.createNativeQuery("UPDATE address SET visibility = :visibility WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility.name());
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void persist(AddressEntity address) {
        super.persist(address);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public void remove(AddressEntity address) {
        super.remove(address);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public AddressEntity merge(AddressEntity address) {
        return super.merge(address);
    }
}
