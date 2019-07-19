package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "biography", key = "#orcid.concat('-').concat(#lastModified)")
    public BiographyEntity getBiography(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM BiographyEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return (BiographyEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateBiography(String orcid, String biography, String visibility) {
        Query query = entityManager.createNativeQuery(
                "update biography set biography = :biography, visibility = :visibility, last_modified = now() where orcid = :orcid");
        query.setParameter("biography", biography);
        query.setParameter("visibility", StringUtils.upperCase(visibility));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public void persistBiography(String orcid, String biography, String visibility) {
        BiographyEntity bio = new BiographyEntity();
        bio.setVisibility(visibility);
        bio.setBiography(biography);
        bio.setOrcid(orcid);
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

    @Override
    @Transactional
    public boolean removeForId(String orcid) {
        Query query = entityManager.createQuery("DELETE FROM BiographyEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }   
}
