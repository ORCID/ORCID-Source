package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.WorkContributorDao;
import org.orcid.persistence.jpa.entities.WorkContributorEntity;
import org.springframework.transaction.annotation.Transactional;

public class WorkContributorDaoImpl extends GenericDaoImpl<WorkContributorEntity, Long> implements WorkContributorDao {

    public WorkContributorDaoImpl() {
        super(WorkContributorEntity.class);
    }

    /**
     * Add a work contributor to the work contributor table
     * 
     * @param workContributor
     *          The work contributor that will be persisted
     * @return the workContriburo already persisted on database
     * */
    @Override
    @Transactional
    public WorkContributorEntity addWorkContributor(WorkContributorEntity workContributor) {
        this.persist(workContributor);
        this.flush();
        return workContributor;
    }

}
