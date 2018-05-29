package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public interface GroupIdRecordDao extends GenericDao<GroupIdRecordEntity, Long> {
    List<GroupIdRecordEntity> getGroupIdRecords(int pageSize, int page);

    boolean exists(String groupId);
    GroupIdRecordEntity findByGroupId(String groupId);

    GroupIdRecordEntity findByName(String name);
    
    boolean haveAnyPeerReview(String groupId);
    
    boolean duplicateExists(Long putCode, String groupId);
}
