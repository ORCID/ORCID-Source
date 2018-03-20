package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ProfileKeywordDaoImpl extends GenericDaoImpl<ProfileKeywordEntity, Long> implements ProfileKeywordDao {

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
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id = :orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public List<ProfileKeywordEntity> getPublicProfileKeywords(String orcid, long lastModified) {
        return getProfileKeywords(orcid, Visibility.PUBLIC);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ProfileKeywordEntity> getProfileKeywords(String orcid, org.orcid.jaxb.model.common_v2.Visibility visibility) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id=:orcid AND visibility=:visibility order by displayIndex desc, dateCreated asc");
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
    public boolean deleteProfileKeyword(String orcid, String keyword) {
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE profile.id = :orcid AND keywordName = :keyword");
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
    public boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, org.orcid.jaxb.model.common_v2.Visibility visibility) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO profile_keyword (id, date_created, last_modified, profile_orcid, keywords_name, source_id, client_source_id, visibility) VALUES (nextval('keyword_seq'), now(), now(), :orcid, :keywords_name, :source_id, :client_source_id, :keywords_visibility)");
        query.setParameter("orcid", orcid);
        query.setParameter("keywords_name", keyword);
        query.setParameter("source_id", sourceId);
        query.setParameter("client_source_id", clientSourceId);
        query.setParameter("keywords_visibility", StringUtils.upperCase(visibility.value()));
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    public ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", putCode);
        return (ProfileKeywordEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean deleteProfileKeyword(ProfileKeywordEntity entity) {        
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE id=:id");
        query.setParameter("id", entity.getId());
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public void removeAllKeywords(String orcid) {
        Query query = entityManager.createQuery("delete from ProfileKeywordEntity where profile_orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

}
