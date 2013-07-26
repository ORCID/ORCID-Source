package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface WorkDao extends GenericDao<WorkEntity, Long> {

    
    /**
     * Add a new work to the work table
     * 
     * @param work
     *          The work that will be persisted
     * @return the work already persisted on database
     * */
    WorkEntity addWork(WorkEntity work);    
}
