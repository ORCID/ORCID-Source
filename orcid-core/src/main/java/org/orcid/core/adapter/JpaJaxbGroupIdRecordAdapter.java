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
package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public interface JpaJaxbGroupIdRecordAdapter {

    GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity);

    GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord);

    List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities);
}
