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
package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.LegacyWorkEntity;

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
    public LegacyWorkEntity toWorkEntity(Work work) {
        if (work == null) {
            return null;
        }
        return mapperFacade.map(work, LegacyWorkEntity.class);
    }
    
    @Override
    public LegacyWorkEntity toWorkEntity(Work work, LegacyWorkEntity existing) {
        if (work == null) {
            return null;
        }
        mapperFacade.map(work, existing);
        return existing;
    }

    @Override
    public Work toWork(LegacyWorkEntity workEntity) {
        if (workEntity == null) {
            return null;
        }
        return mapperFacade.map(workEntity,Work.class);
    }

    @Override
    public WorkSummary toWorkSummary(LegacyWorkEntity workEntity) {
        if (workEntity == null) {
            return null;
        }
        return mapperFacade.map(workEntity, WorkSummary.class);
    }                    
    
    @Override
    public List<Work> toWork(Collection<LegacyWorkEntity> workEntities) {
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
    public List<WorkSummary> toWorkSummary(Collection<LegacyWorkEntity> workEntities) {
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
