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
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;

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
    public ProfileWorkEntity toProfileWorkEntity(Work work) {
        if (work == null) {
            return null;
        }
        return mapperFacade.map(work, ProfileWorkEntity.class);
    }
    
    @Override
    public ProfileWorkEntity toProfileWorkEntity(Work work, ProfileWorkEntity existing) {
        if (work == null) {
            return null;
        }
        mapperFacade.map(work, existing);
        return existing;
    }

    @Override
    public Work toWork(ProfileWorkEntity ProfileWorkEntity) {
        if (ProfileWorkEntity == null) {
            return null;
        }
        return mapperFacade.map(ProfileWorkEntity,Work.class);
    }

    @Override
    public WorkSummary toWorkSummary(ProfileWorkEntity ProfileWorkEntity) {
        if (ProfileWorkEntity == null) {
            return null;
        }
        return mapperFacade.map(ProfileWorkEntity, WorkSummary.class);
    }
    
    @Override
    public List<Work> toWork(Collection<ProfileWorkEntity> workEntities) {
        if (workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, Work.class);
    }
    
    @Override
    public List<WorkSummary> toWorkSummary(Collection<ProfileWorkEntity> workEntities) {
        if(workEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(workEntities, WorkSummary.class);
    }
}
