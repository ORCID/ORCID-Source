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

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbOtherNameAdapterImpl implements JpaJaxbOtherNameAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public OtherNameEntity toOtherNameEntity(OtherName otherName) {
        if (otherName == null) {
            return null;
        }
        OtherNameEntity result = mapperFacade.map(otherName, OtherNameEntity.class);
        
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Override
    public OtherName toOtherName(OtherNameEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, OtherName.class);
    }

    @Override
    public OtherNames toOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<OtherName> otherNameList = mapperFacade.mapAsList(entities, OtherName.class);
        OtherNames otherNames = new OtherNames();        
        otherNames.setOtherNames(otherNameList);
        return otherNames;
    }

    @Override
    public OtherNames toMinimizedOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<OtherName> otherNameList = mapperFacade.mapAsList(entities, OtherName.class);
        
        for(OtherName otherName : otherNameList) {
            otherName.setCreatedDate(null);
            otherName.setSource(null);
        }
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(otherNameList);
        return otherNames;
    }
    
    @Override
    public OtherNameEntity toOtherNameEntity(OtherName otherName, OtherNameEntity existing) {
        if (otherName == null) {
            return null;
        }
        mapperFacade.map(otherName, existing);
        return existing;
    }

}
