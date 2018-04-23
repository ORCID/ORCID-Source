package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

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
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid and id = :id");
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
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid order by displayIndex desc, dateCreated asc");
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
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid and visibility = :visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public boolean deleteAddress(String orcid, Long putCode) {
        Query query = entityManager.createQuery("DELETE FROM AddressEntity WHERE id=:id and user.id = :orcid");
        query.setParameter("id", putCode);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public void removeAllAddress(String orcid) {
        Query query = entityManager.createQuery("delete from AddressEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }    
}
