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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.WorkContributorManager;
import org.orcid.core.manager.WorkExternalIdentifierManager;
import org.orcid.persistence.dao.WorkContributorDao;
import org.orcid.persistence.dao.WorkExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.WorkContributorEntity;
import org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity;

public class WorkExternalIdentifierManagerImpl implements WorkExternalIdentifierManager {

    @Resource
    WorkExternalIdentifierDao workExternalIdentifierDao;

    /**
     * Add a work contributor to the work contributor table
     * 
     * @param workContributor
     *            The work contributor that will be persisted
     * @return the workContriburo already persisted on database
     * */
    public WorkExternalIdentifierEntity addWorkExternalIdentifier(WorkExternalIdentifierEntity workExternalIdentifier) {
        return workExternalIdentifierDao.addWorkExternalIdentifier(workExternalIdentifier);
    }
}
