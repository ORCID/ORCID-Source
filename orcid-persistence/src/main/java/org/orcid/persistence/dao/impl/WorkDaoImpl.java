package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements WorkDao {

    public WorkDaoImpl() {
        super(WorkEntity.class);
    }

    /**
     * Add a new work to the work table
     * 
     * @param work
     *          The work that will be persited
     * @return the work already persisted on database
     * */
    @Override
    public WorkEntity addWork(WorkEntity work) {
       this.persist(work);
       this.flush(); 
       return work;
    }
    
    
}
