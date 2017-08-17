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
package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.LegacyWorkEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface JpaJaxbWorkAdapter {

    LegacyWorkEntity toWorkEntity(Work work);

    Work toWork(LegacyWorkEntity workEntity);
    
    WorkSummary toWorkSummary(LegacyWorkEntity workEntity);

    List<Work> toWork(Collection<LegacyWorkEntity> workEntities);

    List<Work> toMinimizedWork(Collection<MinimizedWorkEntity> minimizedEntities);
    
    List<WorkSummary> toWorkSummary(Collection<LegacyWorkEntity> workEntities);
    
    List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities);
    
    LegacyWorkEntity toWorkEntity(Work work, LegacyWorkEntity existing);

}
