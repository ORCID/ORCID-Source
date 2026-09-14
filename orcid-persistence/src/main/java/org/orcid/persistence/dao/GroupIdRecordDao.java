package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.springframework.transaction.annotation.Transactional;

public interface GroupIdRecordDao extends GenericDao<GroupIdRecordEntity, Long> {
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<GroupIdRecordEntity> getGroupIdRecords(int pageSize, int page);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(String groupId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    GroupIdRecordEntity findByGroupId(String groupId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    GroupIdRecordEntity findByName(String name);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean haveAnyPeerReview(String groupId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean duplicateExists(Long putCode, String groupId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<GroupIdRecordEntity> getIssnRecordsSortedBySyncDate(int batchSize, Date syncTime);
}
