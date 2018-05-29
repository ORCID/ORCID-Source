package org.orcid.core.manager.v3.read_only;

import java.util.Optional;

import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecords;

public interface GroupIdRecordManagerReadOnly {

    GroupIdRecord getGroupIdRecord(Long putCode);

    GroupIdRecords getGroupIdRecords(String pageSize, String pageNum);

    boolean exists(String groupId);
    
    Optional<GroupIdRecord> findByGroupId(String groupId);
    
    Optional<GroupIdRecord> findGroupIdRecordByName(String name);
}
