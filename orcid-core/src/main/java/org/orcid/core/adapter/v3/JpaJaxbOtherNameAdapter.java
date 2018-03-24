package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public interface JpaJaxbOtherNameAdapter {

    OtherNameEntity toOtherNameEntity(OtherName otherName);

    OtherName toOtherName(OtherNameEntity entity);

    OtherNames toOtherNameList(Collection<OtherNameEntity> entities);
    
    OtherNames toMinimizedOtherNameList(Collection<OtherNameEntity> entities);

    OtherNameEntity toOtherNameEntity(OtherName otherName, OtherNameEntity existing);
}
