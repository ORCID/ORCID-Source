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

import org.orcid.persistence.jpa.entities.WorkEntity;

public interface WorkManager {
    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persited
     * @return the work already persisted on database
     * */
    WorkEntity addWork(WorkEntity work);
}
