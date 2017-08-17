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
package org.orcid.core.manager.v3.read_only;

import java.util.Optional;

import org.orcid.jaxb.model.v3.dev1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.dev1.groupid.GroupIdRecords;

public interface GroupIdRecordManagerReadOnly {

    GroupIdRecord getGroupIdRecord(Long putCode);

    GroupIdRecords getGroupIdRecords(String pageSize, String pageNum);

    boolean exists(String groupId);
    
    Optional<GroupIdRecord> findByGroupId(String groupId);
    
    Optional<GroupIdRecord> findGroupIdRecordByName(String name);
}
