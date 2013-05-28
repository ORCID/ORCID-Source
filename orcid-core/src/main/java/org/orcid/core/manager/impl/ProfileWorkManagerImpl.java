package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileWorkDao;

public class ProfileWorkManagerImpl implements ProfileWorkManager {

    @Resource
    private ProfileWorkDao profileWorkDao;
    
    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param clientOrcid
     *          The client orcid 
     * @param workId
     *          The id of the work that will be removed from the client profile     
     * @return true if the relationship was deleted
     * */
    @Override
    public boolean removeWork(String clientOrcid, String workId){
        return profileWorkDao.removeWork(clientOrcid, workId);
    }
    
    /**
     * Updates the visibility of an existing profile work relationship
     * 
     * @param clientOrcid
     *          The client orcid
     *          
     * @param workId
     *          The id of the work that will be updated
     *          
     * @param visibility
     *          The new visibility value for the profile work relationship         
     *                     
     * @return true if the relationship was updated
     * */
    public boolean updateWork(String clientOrcid, String workId, Visibility visibility){
        return profileWorkDao.updateWork(clientOrcid, workId, visibility);
    }
}
