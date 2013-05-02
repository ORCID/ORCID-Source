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
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.springframework.transaction.annotation.Transactional;

public class ResearcherUrlDaoImpl extends GenericDaoImpl<ResearcherUrlEntity, Long> implements ResearcherUrlDao {

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
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid) {
        Query query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    /**
     * Deleted a researcher url from database
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    @Override
    @Transactional
    public boolean deleteResearcherUrl(long id) {
        Query query = entityManager.createQuery("DELETE FROM ResearcherUrlEntity WHERE id = :id");
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    @Override
    public ResearcherUrlEntity getResearcherUrl(long id) {
        TypedQuery<ResearcherUrlEntity> query = entityManager.createQuery("FROM ResearcherUrlEntity WHERE id = :id", ResearcherUrlEntity.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    /**
     * Adds a researcher url to a specific profile
     * @param orcid
     * @param url
     * @param urlName
     * @return true if the researcher url was successfully created on database
     * */
    @Override
    @Transactional
    public boolean addResearcherUrls(String orcid, String url, String urlName) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO researcher_url (date_created, last_modified, orcid, url, url_name) VALUES (now(), now(), :orcid, :url, :url_name)");
        query.setParameter("orcid", orcid);
        query.setParameter("url", url);
        query.setParameter("url_name", urlName);
        return query.executeUpdate() > 0 ? true : false;
    }

}
