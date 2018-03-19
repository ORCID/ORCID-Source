package org.orcid.core.manager.read_only.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.adapter.JpaJaxbGroupIdRecordAdapter;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public class GroupIdRecordManagerReadOnlyImpl implements GroupIdRecordManagerReadOnly {

    @Resource
    protected JpaJaxbGroupIdRecordAdapter jpaJaxbGroupIdRecordAdapter;

    protected GroupIdRecordDao groupIdRecordDao;    
    
    public void setGroupIdRecordDao(GroupIdRecordDao groupIdRecordDao) {
		this.groupIdRecordDao = groupIdRecordDao;
	}

	@Override
    public GroupIdRecord getGroupIdRecord(Long putCode) {
        GroupIdRecordEntity groupIdRecordEntity = groupIdRecordDao.find(putCode);
        if (groupIdRecordEntity == null) {
            throw new GroupIdRecordNotFoundException();
        }
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(groupIdRecordEntity);
    }

	@Override
    public Optional<GroupIdRecord> findByGroupId(String groupId) {
        try {
            GroupIdRecordEntity entity = groupIdRecordDao.findByGroupId(groupId);
            return Optional.of(jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity));
        } catch(NoResultException nre) {
            return Optional.empty();
        }
    }
    
	@Override
    public Optional<GroupIdRecord> findGroupIdRecordByName(String name) {
        try {
            GroupIdRecordEntity entity = groupIdRecordDao.findByName(name);
            return Optional.of(jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity));
        } catch(NoResultException nre) {
            return Optional.empty();
        }
    }
	
    @Override
    public GroupIdRecords getGroupIdRecords(String pageSize, String pageNum) {
        int pageNumInt = convertToInteger(pageNum);
        int pageSizeInt = convertToInteger(pageSize);
        GroupIdRecords records = new GroupIdRecords();
        records.setPage(pageNumInt);
        records.setPageSize(pageSizeInt);
        List<GroupIdRecordEntity> recordEntities = groupIdRecordDao.getGroupIdRecords(pageSizeInt, pageNumInt);
        List<GroupIdRecord> recordsReturned = jpaJaxbGroupIdRecordAdapter.toGroupIdRecords(recordEntities);
        if (recordsReturned != null) {
            records.setTotal(recordsReturned.size());
            records.getGroupIdRecord().addAll(recordsReturned);
        } else {
            records.setTotal(0);
        }        
        return records;
    }

    @Override
    public boolean exists(String groupId) {
        return groupIdRecordDao.exists(groupId);
    }
    
    private int convertToInteger(String param) {
        int returnVal = 0;
        try {
            returnVal = Integer.valueOf(param);
        } catch (NumberFormatException e) {
            throw new OrcidValidationException();
        }
        return returnVal;
    }
}
