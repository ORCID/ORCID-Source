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

import org.orcid.core.adapter.JpaJaxbNameAdapter;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbNameAdapterImpl implements JpaJaxbNameAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    @Override
    public RecordNameEntity toRecordNameEntity(Name name) {
        if(name == null){
            return null;
        }
        
        return mapperFacade.map(name, RecordNameEntity.class);
    }

    @Override
    public Name toName(RecordNameEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return mapperFacade.map(entity, Name.class);
    }

    @Override
    public RecordNameEntity toRecordNameEntity(Name name, RecordNameEntity existing) {
        if(name == null){
            return null;
        }
        mapperFacade.map(name, existing);
        return existing;
    }

}
