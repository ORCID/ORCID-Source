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

import javax.persistence.Query;

import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.keys.ProfileKeywordEntityPk;
import org.springframework.transaction.annotation.Transactional;

public class ProfileKeywordDaoImpl extends GenericDaoImpl<ProfileKeywordEntity, ProfileKeywordEntityPk> implements ProfileKeywordDao {

    public ProfileKeywordDaoImpl() {
        super(ProfileKeywordEntity.class);
    }
    
    /**
     * TODO
     * */
    @Override
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid) {
        Query query = entityManager.createQuery("FROM ProfileKeywordEntity WHERE profile.id = :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    /**
     * TODO
     * */
    @Override
    @Transactional
    public boolean deleteProfileKeyword(String orcid, String keyword) {
        Query query = entityManager.createQuery("DELETE FROM ProfileKeywordEntity WHERE profile.id = :orcid AND keyword = :keyword");
        query.setParameter("orcid", orcid);
        query.setParameter("keyword", keyword);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * TODO
     * */
    @Override
    @Transactional
    public void addProfileKeyword(String orcid, String keyword) {
        Query query = entityManager.createNativeQuery("INSERT INTO profile_keyword (date_created, last_modified, profile_orcid, keywords_name) VALUES (now(), now(), :orcid, :keywords_name)");
        query.setParameter("orcid", orcid);
        query.setParameter("keywords_name", keyword);
        query.executeUpdate();
    }

}
