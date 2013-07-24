package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.WorkEntity;

public interface WorkManager {
    /**
     * Add a new work to the work table
     * 
     * @param work
     *          The work that will be persited
     * @return the work already persisted on database
     * */
    WorkEntity addWork(WorkEntity work); 
}
