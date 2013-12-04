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
package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.pojo.ajaxForm.Work;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;

public class WorkManagerImpl implements WorkManager {

	@Resource
	private WorkDao workDao;

	@Resource
	private ProfileWorkDao profileWorkDao;

	@Resource
	private Jpa2JaxbAdapter jpa2JaxbAdapter;

	/**
	 * Add a new work to the work table
	 * 
	 * @param work
	 *            The work that will be persited
	 * @return the work already persisted on database
	 * */
	public WorkEntity addWork(WorkEntity work) {
		return workDao.addWork(work);
	}

	/**
	 * Find the works for a specific user
	 * 
	 * @param orcid
	 *            the Id of the user
	 * @return the list of works associated to the specific user
	 * */
	public List<MinimizedWorkEntity> findWorks(String orcid) {
		return workDao.findWorks(orcid);
	}

	/**
     * Find the public works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    public List<MinimizedWorkEntity> findPublicWorks(String orcid) {
    	return workDao.findPublicWorks(orcid);
    }
	
	/**
	 * Loads work information
	 * 
	 * @param workId
	 *            the Id of the work
	 * @return a workInfo object with the work information
	 * */
	public Work loadWorkInfo(String orcid, String workId) {	 	
		ProfileWorkEntity profileWork = profileWorkDao.getProfileWork(orcid,
				workId);		
		WorkEntity workEntity = profileWork.getWork();
		WorkContributors workContributors = jpa2JaxbAdapter.getWorkContributors(profileWork);
		WorkExternalIdentifiers externalIdentifiers = jpa2JaxbAdapter.getWorkExternalIdentifiers(workEntity);
		return Work.valueOf(profileWork, workEntity, workContributors, externalIdentifiers);
	}
}