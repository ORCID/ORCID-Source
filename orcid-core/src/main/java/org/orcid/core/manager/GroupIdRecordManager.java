/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;

public interface GroupIdRecordManager {

    GroupIdRecord getGroupIdRecord(Long putCode);

    GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord);

    GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord);

    void deleteGroupIdRecord(Long putCode);

    GroupIdRecords getGroupIdRecords(String pageSize, String pageNum);

    boolean exists(String groupId);
    
    GroupIdRecord findByGroupId(String groupId);
}
