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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.springframework.transaction.annotation.Transactional;

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements WorkDao {

    public WorkDaoImpl() {
        super(WorkEntity.class);
    }

    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persisted
     * @return the work already persisted on database
     * */
    @Override 
    @Transactional
    public WorkEntity addWork(WorkEntity work) {
        this.persist(work);
        this.flush();
        return work;
    }

    @Override
    @Transactional
    public WorkEntity editWork(WorkEntity updatedWork) {
        
        String query = "UPDATE work " + 
                "SET title=:title, translated_title=:translatedTitle, subtitle=:subtitle, " + 
                "description=:description, work_url=:workUrl, citation=:citation, journal_title=:journalTitle, " +
                "language_code=:languageCode, translated_title_language_code=:translatedTitleLanguageCode, " +
                "iso2_country=:iso2Country, citation_type=:citationType, work_type=:workType, " +
                "{0} {1} {2}" +
                "contributors_json=:contributorsJson, external_ids_json=:externalIdentifiersJson, last_modified=:lastModified " + 
                "WHERE work_id=:id";
        if(updatedWork.getPublicationDate() != null) {
            if(updatedWork.getPublicationDate().getDay() == null) {
                query = query.replace("{0}", "publication_day=null");
            } else {
                query = query.replace("{0}", "publication_day=:publicationDay");                
            }
            
            if(updatedWork.getPublicationDate().getMonth() == null) {
                query = query.replace("{1}", "publication_month=null,");
            } else {
                query = query.replace("{1}", "publication_month=:publicationMonth,");
            }
            
            if(updatedWork.getPublicationDate().getYear() == null) {
                query = query.replace("{2}", "publication_year=null,");
            } else {
                query = query.replace("{2}", "publication_year=:publicationDay,");
            }
        } else {
            query = query.replace("{0}", "publication_day=null,");
            query = query.replace("{1}", "publication_month=null,");
            query = query.replace("{2}", "publication_year=null,");
        } 
                
        Query updateQuery = entityManager
                .createNativeQuery(query);
        updateQuery.setParameter("title", updatedWork.getTitle());
        updateQuery.setParameter("translatedTitle", updatedWork.getTranslatedTitle());
        updateQuery.setParameter("subtitle", updatedWork.getSubtitle());
        updateQuery.setParameter("description", updatedWork.getDescription());
        updateQuery.setParameter("workUrl", updatedWork.getWorkUrl());
        updateQuery.setParameter("citation", updatedWork.getCitation());
        updateQuery.setParameter("journalTitle", updatedWork.getJournalTitle());
        updateQuery.setParameter("languageCode", updatedWork.getLanguageCode());
        updateQuery.setParameter("translatedTitleLanguageCode", updatedWork.getTranslatedTitleLanguageCode());
        updateQuery.setParameter("iso2Country", updatedWork.getIso2Country());
        updateQuery.setParameter("citationType", updatedWork.getCitationType());
        updateQuery.setParameter("workType", updatedWork.getWorkType());
        
        if(updatedWork.getPublicationDate() != null) {
            if(updatedWork.getPublicationDate().getDay() != null) {
                updateQuery.setParameter("publicationDay", updatedWork.getPublicationDate().getDay());
            } 
            
            if(updatedWork.getPublicationDate().getMonth() != null) {
                updateQuery.setParameter("publicationMonth", updatedWork.getPublicationDate().getMonth());
            } 
            
            if(updatedWork.getPublicationDate().getYear() != null) {
                updateQuery.setParameter("publicationYear", updatedWork.getPublicationDate().getYear());
            }
        }
        
        updateQuery.setParameter("contributorsJson", updatedWork.getContributorsJson());
        updateQuery.setParameter("externalIdentifiersJson", updatedWork.getExternalIdentifiersJson());
        updateQuery.setParameter("lastModified", new Date());
        updateQuery.setParameter("id",updatedWork.getId());
        
        updateQuery.executeUpdate();
        return updatedWork;
    }

    /**
     * Find works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @SuppressWarnings("unchecked")
    public List<MinimizedWorkEntity> findWorks(String orcid) {

        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility, w.externalIdentifiersJson, pw.displayIndex, pw.sourceProfile) "
                        + "from WorkEntity w, ProfileWorkEntity pw "
                        + "where pw.profile.id=:orcid and w.id=pw.work.id "
                        + "order by pw.displayIndex desc, pw.dateCreated asc");
        query.setParameter("orcid", orcid);

        return query.getResultList();
    }

    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    @SuppressWarnings("unchecked")
    public List<MinimizedWorkEntity> findPublicWorks(String orcid) {
        Query query = entityManager
                .createQuery("select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility, w.externalIdentifiersJson, pw.displayIndex, pw.sourceProfile) "
                        + "from WorkEntity w, ProfileWorkEntity pw "
                        + "where pw.visibility='PUBLIC' and pw.profile.id=:orcid and w.id=pw.work.id "
                        + "order by pw.displayIndex desc, pw.dateCreated asc");
        query.setParameter("orcid", orcid);

        return query.getResultList();
    }

}