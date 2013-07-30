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
     *            The work contributor that will be persisted
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
