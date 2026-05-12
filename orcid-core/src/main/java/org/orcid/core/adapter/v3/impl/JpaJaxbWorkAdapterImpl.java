package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MapperFacade;

import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.WorkSummaryExtended;

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
    public WorkSummary toWorkSummary(MinimizedWorkEntity minimizedWorkEntity) {
        if (minimizedWorkEntity == null) {
            return null;
        }
        return mapperFacade.map(minimizedWorkEntity, WorkSummary.class);
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

    @Override
    public List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities, Map<String, ClientDetailsEntity> clientDetailsById) {
        if(workEntities == null) {
            return null;
        }
        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty(SourceEntityUtils.CLIENT_DETAILS_BY_ID_MAPPING_CONTEXT_KEY, clientDetailsById);
        return mapperFacade.mapAsList(workEntities, WorkSummary.class, context);
    }

    @Override
    public List<WorkSummaryExtended> toWorkSummaryExtendedFromMinimized(Collection<MinimizedExtendedWorkEntity> workEntities) {
        if(workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, WorkSummaryExtended.class);
    }

    @Override
    public WorkExtended toWorkExtended(WorkEntity workEntity) {
        if (workEntity == null) {
            return null;
        }
        return mapperFacade.map(workEntity, WorkExtended.class);
    }
}
