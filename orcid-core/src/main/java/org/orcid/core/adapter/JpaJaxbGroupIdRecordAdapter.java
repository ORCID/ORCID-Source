package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public interface JpaJaxbGroupIdRecordAdapter {

    GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity);

    GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord);

    List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities);
}
