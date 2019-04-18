package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JpaJaxbWorkAdapterImpl implements JpaJaxbWorkAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public WorkEntity toWorkEntity(Work work) {
        if (work == null) {
            return null;
        }
        return mapperFacade.map(work, WorkEntity.class);
    }
    
    @Override
    public WorkEntity toWorkEntity(Work work, WorkEntity existing) {
        if (work == null) {
            return null;
        }
        mapperFacade.map(work, existing);
        return existing;
    }

    @Override
    public Work toWork(WorkEntity workEntity) {
        if (workEntity == null) {
            return null;
        }
        return mapperFacade.map(workEntity,Work.class);
    }

    @Override
    public WorkSummary toWorkSummary(WorkEntity workEntity) {
        if (workEntity == null) {
            return null;
        }
        return mapperFacade.map(workEntity, WorkSummary.class);
    }                    
    
    @Override
    public List<Work> toWork(Collection<WorkEntity> workEntities) {
        if (workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, Work.class);
    }
           
    @Override
    public List<Work> toMinimizedWork(Collection<MinimizedWorkEntity> minimizedEntities) {
        if(minimizedEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(minimizedEntities, Work.class);
    }
    
    @Override
    public List<WorkSummary> toWorkSummary(Collection<WorkEntity> workEntities) {
        if(workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, WorkSummary.class);
    }
    
    @Override
    public List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities) {
        if(workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, WorkSummary.class);
    }
}
