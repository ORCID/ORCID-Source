package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.persistence.dao.ProfileWorkDao;

public class ProfileWorkManagerImpl implements ProfileWorkManager {

    @Resource
    private ProfileWorkDao profileWorkDao;
    
    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param workId
     *          The id of the work that will be removed from the client profile
     * @param clientOrcid
     *          The client orcid 
     * @return true if the relationship was deleted
     * */
    @Override
    public boolean removeWork(String workId, String clientOrcid){
        return profileWorkDao.removeWork(workId, clientOrcid);
    }
}
