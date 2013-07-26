package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.springframework.transaction.annotation.Transactional;

public class WorkDaoImpl extends GenericDaoImpl<WorkEntity, Long> implements WorkDao {

    public WorkDaoImpl() {
        super(WorkEntity.class);
    }

    /**
     * Add a new work to the work table
     * 
     * @param work
     *          The work that will be persisted
     * @return the work already persisted on database
     * */
    @Override
    @Transactional
    public WorkEntity addWork(WorkEntity work) {
       this.persist(work);
       this.flush(); 
       return work;
    }
    
    
}
