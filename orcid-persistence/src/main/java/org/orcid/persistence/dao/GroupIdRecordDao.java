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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public interface GroupIdRecordDao extends GenericDao<GroupIdRecordEntity, Long> {
    List<GroupIdRecordEntity> getGroupIdRecords(int pageSize, int page);

    boolean exists(String groupId);
    GroupIdRecordEntity findByGroupId(String groupId);

    GroupIdRecordEntity findByName(String name);
}
