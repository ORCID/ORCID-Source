package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.WorkManager;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;

public class WorkManagerImpl implements WorkManager{

    @Resource
    private WorkDao workDao;
    
    /**
     * Add a new work to the work table
     * 
     * @param work
     *          The work that will be persited
     * @return the work already persisted on database
     * */
    public WorkEntity addWork(WorkEntity work){
        return workDao.addWork(work);
    } 
}
