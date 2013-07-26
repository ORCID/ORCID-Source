package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.WorkContributorEntity;

public interface WorkContributorDao extends GenericDao<WorkContributorEntity, Long> {

    /**
     * Add a work contributor to the work contributor table
     * 
     * @param workContributor
     *          The work contributor that will be persisted
     * @return the workContriburo already persisted on database
     * */
    public WorkContributorEntity addWorkContributor(WorkContributorEntity workContributor);
}
