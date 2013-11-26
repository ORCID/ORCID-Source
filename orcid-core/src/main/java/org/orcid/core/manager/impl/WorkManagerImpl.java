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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.custom.WorkInfoEntity;

public class WorkManagerImpl implements WorkManager {

    @Resource
    private WorkDao workDao;

    @Resource
    private ProfileWorkDao profileWorkDao;
    
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
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    public List<MinimizedWorkEntity> findWorks(String orcid) {
    	return workDao.findWorks(orcid);
    }
    
    /**
     * Loads work information
     * 
     * @param workId
     * 		the Id of the work
     * @return a workInfo object with the work information
     * */
    public WorkInfoEntity loadWorkInfo(String orcid, String workId) {
    	WorkInfoEntity workInfo = new WorkInfoEntity();
    	ProfileWorkEntity profileWork = profileWorkDao.getProfileWork(orcid, workId);
    	loadWorkSourceInfo(profileWork.getSourceProfile(), workInfo);
    	return workInfo;
    }
    
    /**
     * TODO
     * */
    private void loadWorkSourceInfo(ProfileEntity sourceEntity, WorkInfoEntity workInfo) {
    	if(sourceEntity == null)
    		return;
    	Visibility sourceNameVisibility = (sourceEntity.getCreditNameVisibility() == null) ? OrcidVisibilityDefaults.CREDIT_NAME_DEFAULT.getVisibility() : sourceEntity.getCreditNameVisibility();
    	if(OrcidType.CLIENT.equals(sourceEntity.getOrcidType())){    		 
        	if(Visibility.PUBLIC.equals(sourceNameVisibility)) {
        		workInfo.setSourceName(sourceEntity.getCreditName());
        	}
    	} else {
    		//If it is a user, check if it have a credit name and is visible
            if(Visibility.PUBLIC.equals(sourceNameVisibility)){
            	workInfo.setSourceName(sourceEntity.getCreditName());
            } else {
                //If it doesnt, lets use the give name + family name
                String name = sourceEntity.getGivenNames() + (StringUtils.isEmpty(sourceEntity.getFamilyName()) ? "" : " " + sourceEntity.getFamilyName());
                workInfo.setSourceName(name);
            }
    	}    	
    }
    
    /**
     * TODO
     * */
    private void loadWorkInfo(WorkEntity workEntity, WorkInfoEntity workInfo){
    	if(workEntity == null)
    		return;
    	workInfo.setCitation();
    	workInfo.setCitationType();
    	workInfo.setContributorsJson();
    	workInfo.setDescription();
    	workInfo.setId();
    	workInfo.setIso2Country();
    	workInfo.setJournalTitle();
    	workInfo.setLanguageCode();
    	workInfo.setPublicationDay();
    	workInfo.setPublicationMonth();
    	workInfo.setPublicationYear();
    	workInfo.setSubtitle();
    	workInfo.setTitle();
    	workInfo.setTranslatedTitle();
    	workInfo.setTranslatedTitleLanguageCode();
    	workInfo.setWorkType();
    	workInfo.setWorkUrl();
    }
}
