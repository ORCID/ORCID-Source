package org.orcid.core.adapter;

import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public interface JpaJaxbNameAdapter {

    RecordNameEntity toRecordNameEntity(Name name);

    Name toName(RecordNameEntity entity);

    RecordNameEntity toRecordNameEntity(Name name, RecordNameEntity existing);
}
