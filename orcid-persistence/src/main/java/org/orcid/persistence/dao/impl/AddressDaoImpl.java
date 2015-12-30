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

import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;

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
    public AddressEntity find(String orcid, Long id) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
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
    public List<AddressEntity> findByOrcid(String orcid) {
        Query query = entityManager.createQuery("FROM AddressEntity WHERE user.id = :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
}
