package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Work;
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
