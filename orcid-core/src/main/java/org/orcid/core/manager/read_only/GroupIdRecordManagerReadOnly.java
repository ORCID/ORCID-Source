package org.orcid.core.manager.read_only;

import java.util.Optional;

import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;

public interface GroupIdRecordManagerReadOnly {

    GroupIdRecord getGroupIdRecord(Long putCode);

    GroupIdRecords getGroupIdRecords(String pageSize, String pageNum);

    boolean exists(String groupId);
    
    Optional<GroupIdRecord> findByGroupId(String groupId);
    
    Optional<GroupIdRecord> findGroupIdRecordByName(String name);
}
