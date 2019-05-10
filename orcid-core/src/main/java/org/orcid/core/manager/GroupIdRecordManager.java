package org.orcid.core.manager;

import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;

public interface GroupIdRecordManager extends GroupIdRecordManagerReadOnly {

    GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord);

    GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord);

    void deleteGroupIdRecord(Long putCode);

    GroupIdRecord createIssnGroupIdRecord(String groupId, String group);    
}
