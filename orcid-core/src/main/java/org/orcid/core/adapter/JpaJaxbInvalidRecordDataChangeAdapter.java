package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

public interface JpaJaxbInvalidRecordDataChangeAdapter {

    RecordCorrection toInvalidRecordDataChange(InvalidRecordDataChangeEntity entity);

    List<RecordCorrection> toInvalidRecordDataChanges(Collection<InvalidRecordDataChangeEntity> entities);
}
