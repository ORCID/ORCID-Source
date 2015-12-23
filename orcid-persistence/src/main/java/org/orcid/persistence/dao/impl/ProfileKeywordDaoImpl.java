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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
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
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id = :orcid");
        query.setParameter("orcid", orcid);
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
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE profile.id = :orcid AND keyword = :keyword");
        query.setParameter("orcid", orcid);
        query.setParameter("keyword", keyword);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean updateKeywordsVisibility(String orcid, Visibility visibility) {
        Query query = entityManager
                .createNativeQuery("update profile set last_modified=now(), keywords_visibility=:keywords_visibility, indexing_status='PENDING' where orcid=:orcid");
        query.setParameter("keywords_visibility", StringUtils.upperCase(visibility.value()));
        query.setParameter("orcid", orcid);
        boolean result = query.executeUpdate() > 0 ? true : false;
        return result;
    }
    
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    @Override
    @Transactional
    public boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO profile_keyword (id, date_created, last_modified, profile_orcid, keywords_name, source_id, client_source_id) VALUES (nextval('keyword_seq'), now(), now(), :orcid, :keywords_name, :source_id, :client_source_id)");
        query.setParameter("orcid", orcid);
        query.setParameter("keywords_name", keyword);
        query.setParameter("source_id", sourceId);
        query.setParameter("client_source_id", clientSourceId);
        return query.executeUpdate() > 0 ? true : false;
    }

}
