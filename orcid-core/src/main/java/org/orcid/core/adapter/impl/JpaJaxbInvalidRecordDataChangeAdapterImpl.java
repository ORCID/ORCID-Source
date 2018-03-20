package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbInvalidRecordDataChangeAdapterImpl implements JpaJaxbInvalidRecordDataChangeAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public RecordCorrection toInvalidRecordDataChange(InvalidRecordDataChangeEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, RecordCorrection.class);
    }

    @Override
    public List<RecordCorrection> toInvalidRecordDataChanges(Collection<InvalidRecordDataChangeEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, RecordCorrection.class);
    }

}
