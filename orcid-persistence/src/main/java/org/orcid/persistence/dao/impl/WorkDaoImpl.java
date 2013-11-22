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

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements
		WorkDao {

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
	 * Find the works for a specific user
	 * 
	 * @param orcid
	 *            the Id of the user
	 * @return the list of works associated to the specific user
	 * */
	public List<MinimizedWorkEntity> findWorks(String orcid) {
		Query query = entityManager
				.createQuery(
						"select NEW MinimizedWorkEntity (w.id, w.title, w.subtitle, w.description, w.publication_day, w.publication_month, w.publication_year, pw.visibility) " +
						"from work w, profile_work pw " +
						"where pw.orcid=:orcid and w.work_id=pw.work_id " +
						"order by w.publication_year desc, w.publication_month desc, w.publication_day desc, w.title desc, w.work_id desc",
						MinimizedWorkEntity.class);
		query.setParameter("orcid", orcid);
		List<MinimizedWorkEntity> results = (List<MinimizedWorkEntity>) query.getResultList();  
		return results;
	}
}
