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

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common_rc2.Visibility;
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
    public List<AddressEntity> findByOrcid(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid");
        query.setParameter("orcid", orcid);
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
    public boolean updateAddressVisibility(String orcid, Visibility visibility) {
        Query query = entityManager
                .createNativeQuery("update profile set last_modified=now(), profile_address_visibility=:address_visibility, indexing_status='PENDING' where orcid=:orcid");
        query.setParameter("address_visibility", StringUtils.upperCase(visibility.value()));
        query.setParameter("orcid", orcid);
        boolean result = query.executeUpdate() > 0 ? true : false;
        return result;
    }
}
