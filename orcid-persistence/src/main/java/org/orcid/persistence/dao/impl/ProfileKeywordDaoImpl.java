package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ProfileKeywordDaoImpl extends GenericDaoImpl<ProfileKeywordEntity, Long> implements ProfileKeywordDao {

    private static final String PUBLIC_VISIBILITY = "PUBLIC";

    public ProfileKeywordDaoImpl() {
        super(ProfileKeywordEntity.class);
    }

    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "dao-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ProfileKeywordEntity> getProfileKeywords(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE orcid = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ProfileKeywordEntity> getPublicProfileKeywords(String orcid, long lastModified) {
        return getProfileKeywords(orcid, PUBLIC_VISIBILITY);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ProfileKeywordEntity> getProfileKeywords(String orcid, String visibility) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE orcid=:orcid AND visibility=:visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }
    
    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean deleteProfileKeyword(String orcid, String keyword) {
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE orcid = :orcid AND keywordName = :keyword");
        query.setParameter("orcid", orcid);
        query.setParameter("keyword", keyword);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, String visibility) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO profile_keyword (id, date_created, last_modified, profile_orcid, keywords_name, source_id, client_source_id, visibility) VALUES (nextval('keyword_seq'), now(), now(), :orcid, :keywords_name, :source_id, :client_source_id, :keywords_visibility)");
        query.setParameter("orcid", orcid);
        query.setParameter("keywords_name", keyword);
        query.setParameter("source_id", sourceId);
        query.setParameter("client_source_id", clientSourceId);
        query.setParameter("keywords_visibility", StringUtils.upperCase(visibility));
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    public ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE orcid=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", putCode);
        return (ProfileKeywordEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean deleteProfileKeyword(ProfileKeywordEntity entity) {        
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE id=:id");
        query.setParameter("id", entity.getId());
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void removeAllKeywords(String orcid) {
        Query query = entityManager.createQuery("delete from ProfileKeywordEntity where profile_orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE profile_keyword SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE profile_keyword SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE profile_keyword SET assertion_origin_source_id = profile_orcid where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NOT NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void revertUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE profile_keyword SET assertion_origin_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBORecords(int max) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE assertion_origin_source_id IS NOT NULL");
        query.setMaxResults(max);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsOfKeywordsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_keyword WHERE source_id IN :ids");
        query.setParameter("ids", clientProfileOrcidIds);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean updateVisibility(String orcid, Visibility visibility) {
        Query query = entityManager.createNativeQuery("UPDATE profile_keyword SET visibility = :visibility WHERE profile_orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility.name());
        return query.executeUpdate() > 0;
    }

    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public void persist(ProfileKeywordEntity entity) {
        super.persist(entity);
    }

    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public ProfileKeywordEntity merge(ProfileKeywordEntity entity) {
        return super.merge(entity);
    }
}
