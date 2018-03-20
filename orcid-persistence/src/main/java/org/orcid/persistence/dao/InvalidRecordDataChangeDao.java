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
