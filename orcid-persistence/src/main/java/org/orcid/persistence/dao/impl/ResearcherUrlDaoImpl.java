package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.aop.UpdateProfileLastModified;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
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
    @UpdateProfileLastModifiedAndIndexingStatus
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
        TypedQuery<ResearcherUrlEntity> query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE id = :id AND orcid = :orcid", ResearcherUrlEntity.class);
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
    @UpdateProfileLastModified
    public boolean updateResearcherUrl(long id, String newUrl) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET url=:newUrl WHERE id=:id");
        query.setParameter("newUrl", newUrl);
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public void removeAllResearcherUrls(String orcid) {
        Query query = entityManager.createQuery("delete from ResearcherUrlEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET assertion_origin_source_id = orcid where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NOT NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void revertUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET assertion_origin_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE assertion_origin_source_id IS NOT NULL");
        query.setMaxResults(max);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsOfResearcherUrlsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds) {
        Query query = entityManager.createNativeQuery("SELECT id FROM researcher_url WHERE source_id IN :ids");
        query.setParameter("ids", clientProfileOrcidIds);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean updateVisibility(String orcid, Visibility visibility) {
        Query query = entityManager.createNativeQuery("UPDATE researcher_url SET visibility = :visibility WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility.name());
        return query.executeUpdate() > 0;
    }

    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public void persist(ResearcherUrlEntity entity) {
        super.persist(entity);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public ResearcherUrlEntity merge(ResearcherUrlEntity entity) {
        return super.merge(entity);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public void remove(ResearcherUrlEntity entity) {
        super.remove(entity);
    }

}
