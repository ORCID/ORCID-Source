package org.orcid.core.adapter.v3;

import org.orcid.jaxb.model.v3.dev1.record.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public interface JpaJaxbNameAdapter {

    RecordNameEntity toRecordNameEntity(Name name);

    Name toName(RecordNameEntity entity);

    RecordNameEntity toRecordNameEntity(Name name, RecordNameEntity existing);
}
