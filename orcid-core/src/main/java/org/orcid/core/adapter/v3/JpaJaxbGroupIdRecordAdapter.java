package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public interface JpaJaxbGroupIdRecordAdapter {

    GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity);

    GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord);

    List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities);
}
