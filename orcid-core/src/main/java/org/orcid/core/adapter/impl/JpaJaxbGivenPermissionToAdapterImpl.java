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

import org.orcid.core.adapter.JpaJaxbGivenPermissionToAdapter;
import org.orcid.jaxb.model.record_rc2.DelegationDetails;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbGivenPermissionToAdapterImpl implements JpaJaxbGivenPermissionToAdapter {
    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    @Override
    public GivenPermissionToEntity toGivenPermissionTo(DelegationDetails details) {
        if(details == null) {
            return null;
        }        
        return mapperFacade.map(details, GivenPermissionToEntity.class);
    }

    @Override
    public DelegationDetails toDelegationDetails(GivenPermissionToEntity entity) {
        if(entity == null) {
            return null;
        }
        return mapperFacade.map(entity, DelegationDetails.class);        
    }

    @Override
    public List<DelegationDetails> toDelegationDetailsList(Collection<GivenPermissionToEntity> entities) {
        if(entities == null) {
            return null;
        }
        
        return mapperFacade.mapAsList(entities, DelegationDetails.class);
    }

}
