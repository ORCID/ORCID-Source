package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;

public interface GroupIdRecordManager extends GroupIdRecordManagerReadOnly {

    GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord);

    GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord);

    void deleteGroupIdRecord(Long putCode);    
}
