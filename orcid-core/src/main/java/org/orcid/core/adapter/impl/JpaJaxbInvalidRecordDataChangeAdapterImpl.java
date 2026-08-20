package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

public class JpaJaxbInvalidRecordDataChangeAdapterImpl implements JpaJaxbInvalidRecordDataChangeAdapter {

    public void setMapperFacade(Object mapperFacade) {
        // No-op: retained for backward-compatible Spring XML wiring during incremental Orika removal.
    }

    @Override
    public RecordCorrection toInvalidRecordDataChange(InvalidRecordDataChangeEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapEntity(entity);
    }

    @Override
    public List<RecordCorrection> toInvalidRecordDataChanges(Collection<InvalidRecordDataChangeEntity> entities) {
        if (entities == null) {
            return null;
        }
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::mapEntity).collect(Collectors.toList());
    }

    private RecordCorrection mapEntity(InvalidRecordDataChangeEntity entity) {
        RecordCorrection mapped = new RecordCorrection();
        mapped.setSequence(entity.getId());
        mapped.setSqlUsedToUpdate(entity.getSqlUsedToUpdate());
        mapped.setDescription(entity.getDescription());
        mapped.setNumChanged(entity.getNumChanged());
        mapped.setType(entity.getType());
        return mapped;
    }

}
