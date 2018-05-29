package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ResearcherUrlDaoImpl extends GenericDaoImpl<ResearcherUrlEntity, Long> implements ResearcherUrlDao {

    private static final String PUBLIC_VISIBILITY = "PUBLIC";

    public ResearcherUrlDaoImpl() {
        super(ResearcherUrlEntity.class);
    }

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "researcher-urls", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE orcid = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-researcher-urls", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ResearcherUrlEntity> getPublicResearcherUrls(String orcid, long lastModified) {
        return getResearcherUrls(orcid, PUBLIC_VISIBILITY);
    }

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @param visibility
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    @Override
    @SuppressWarnings("unchecked")
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, String visibility) {
        Query query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE orcid = :orcid AND visibility = :visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }
    
    /**
     * Deleted a researcher url from database
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    @Override
    @Transactional
    public boolean deleteResearcherUrl(String orcid, long id) {
        Query query = entityManager.createNativeQuery("DELETE FROM researcher_url WHERE orcid = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);        
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    @Override
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id) {
        TypedQuery<ResearcherUrlEntity> query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE id = :id AND user.id = :orcid", ResearcherUrlEntity.class);
        query.setParameter("id", id);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }
    
    /**
     * Updates an existing researcher url
     * @param orcid
     * @param oldUrl
     * @param newUrl
     * @return true if the researcher url was updated
     * */
    @Override
    @Transactional
    public boolean updateResearcherUrl(long id, String newUrl) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET url=:newUrl WHERE id=:id");
        query.setParameter("newUrl", newUrl);
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public void removeAllResearcherUrls(String orcid) {
        Query query = entityManager.createQuery("delete from ResearcherUrlEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

}
