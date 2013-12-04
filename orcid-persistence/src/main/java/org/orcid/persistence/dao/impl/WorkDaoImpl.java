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
				.createQuery(
						"select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility) " +
						"from WorkEntity w, ProfileWorkEntity pw " +
						"where pw.profile.id=:orcid and w.id=pw.work.id " +
						"order by w.publicationDate.year desc, w.publicationDate.month desc, w.publicationDate.day desc, w.title asc, w.id desc");		
		query.setParameter("orcid", orcid);
		
		return query.getResultList();  
	}
	
	/**
     * Find the public works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    public List<MinimizedWorkEntity> findPublicWorks(String orcid){
    	Query query = entityManager
				.createQuery(
						"select NEW org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity(w.id, w.title, w.subtitle, w.description, w.publicationDate.day, w.publicationDate.month, w.publicationDate.year, pw.visibility) " +
						"from WorkEntity w, ProfileWorkEntity pw " +
						"where pw.visibility='PUBLIC' and pw.profile.id=:orcid and w.id=pw.work.id " +
						"order by w.publicationDate.year desc, w.publicationDate.month desc, w.publicationDate.day desc, w.title asc, w.id desc");		
		query.setParameter("orcid", orcid);
		
		return query.getResultList(); 
    }
}