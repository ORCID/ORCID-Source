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

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

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
    public OrgEntity findByNameCityRegionAndCountry(String name, String city, String region, Iso3166Country country) {
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

}