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
package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.WorkContributorEntity;

public interface WorkContributorManager {
    /**
     * Add a work contributor to the work contributor table
     * 
     * @param workContributor
     *            The work contributor that will be persisted
     * @return the workContriburo already persisted on database
     * */
    public WorkContributorEntity addWorkContributor(WorkContributorEntity workContributor);
}
