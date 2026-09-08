package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.springframework.transaction.annotation.Transactional;

public interface GroupIdRecordDao extends GenericDao<GroupIdRecordEntity, Long> {
    @Transactional(readOnly = true)
    List<GroupIdRecordEntity> getGroupIdRecords(int pageSize, int page);

    @Transactional(readOnly = true)
    boolean exists(String groupId);

    @Transactional(readOnly = true)
    GroupIdRecordEntity findByGroupId(String groupId);

    @Transactional(readOnly = true)
    GroupIdRecordEntity findByName(String name);
    
    @Transactional(readOnly = true)
    boolean haveAnyPeerReview(String groupId);
    
    @Transactional(readOnly = true)
    boolean duplicateExists(Long putCode, String groupId);
    
    @Transactional(readOnly = true)
    List<GroupIdRecordEntity> getIssnRecordsSortedBySyncDate(int batchSize, Date syncTime);
}
