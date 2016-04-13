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

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BiographyDaoImpl extends GenericDaoImpl<BiographyEntity, Long> implements BiographyDao {
    public BiographyDaoImpl() {
        super(BiographyEntity.class);
    }

    @Override
    public BiographyEntity getBiography(String orcid) {
        Query query = entityManager.createQuery("FROM BiographyEntity WHERE profile.id = :orcid");
        query.setParameter("orcid", orcid);
        return (BiographyEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateBiography(String orcid, String biography, Visibility visibility) {
        Query query = entityManager.createNativeQuery(
                "update biography set biography = :biography, visibility = :visibility, last_modified = now() where orcid = :orcid");
        query.setParameter("biography", biography);
        query.setParameter("visibility", StringUtils.upperCase(visibility.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public void createBiography(String orcid, String biography, Visibility visibility) {
        BiographyEntity bio = new BiographyEntity();
        bio.setVisibility(visibility);
        bio.setBiography(biography);
        bio.setProfile(new ProfileEntity(orcid));
        bio.setDateCreated(new Date());
        bio.setLastModified(new Date());
        entityManager.persist(bio);
    }

    @Override
    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("select count(*) from biography where orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }   
}
