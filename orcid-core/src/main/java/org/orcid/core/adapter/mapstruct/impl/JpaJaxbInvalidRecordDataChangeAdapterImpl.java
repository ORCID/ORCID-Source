package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

@Mapper(componentModel = "spring")
public abstract class JpaJaxbInvalidRecordDataChangeAdapterImpl implements JpaJaxbInvalidRecordDataChangeAdapter {


    @Override
    @Mapping(source = "id", target = "sequence")
    // MapStruct natively maps sqlUsedToUpdate, description, numChanged, and type 
    // because the field names are identical in both classes.
    public abstract RecordCorrection toInvalidRecordDataChange(InvalidRecordDataChangeEntity entity);


    @Override
    public abstract List<RecordCorrection> toInvalidRecordDataChanges(Collection<InvalidRecordDataChangeEntity> entities);

}