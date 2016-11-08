/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface JpaJaxbWorkAdapter {

    WorkEntity toWorkEntity(Work work);

    Work toWork(WorkEntity workEntity);
    
    WorkSummary toWorkSummary(WorkEntity workEntity);

    List<Work> toWork(Collection<WorkEntity> workEntities);

    List<Work> toMinimizedWork(Collection<MinimizedWorkEntity> minimizedEntities);
    
    List<WorkSummary> toWorkSummary(Collection<WorkEntity> workEntities);
    
    List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities);
    
    WorkEntity toWorkEntity(Work work, WorkEntity existing);

}
