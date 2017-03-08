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

import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface InvalidRecordDataChangeDao {
    List<InvalidRecordDataChangeEntity> getByDateCreated(Long lastSequence, Long pageSize, boolean descendantOrder);
    boolean haveNext(Long sequence, boolean descendantOrder);
    boolean havePrevious(Long sequence, boolean descendantOrder);
}
