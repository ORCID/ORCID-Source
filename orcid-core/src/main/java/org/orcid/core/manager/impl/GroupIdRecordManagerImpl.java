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
package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.adapter.JpaJaxbGroupIdRecordAdapter;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.jaxb.model.common_rc2.Source;
import org.orcid.jaxb.model.common_rc2.SourceClientId;
import org.orcid.jaxb.model.common_rc2.SourceOrcid;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.groupid.GroupIdRecords;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class GroupIdRecordManagerImpl implements GroupIdRecordManager {

    @Resource
    private GroupIdRecordDao groupIdRecordDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private JpaJaxbGroupIdRecordAdapter jpaJaxbGroupIdRecordAdapter;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Override
    public GroupIdRecord getGroupIdRecord(Long putCode) {
        GroupIdRecordEntity groupIdRecordEntity = groupIdRecordDao.find(putCode);
        if (groupIdRecordEntity == null) {
            throw new GroupIdRecordNotFoundException();
        }
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(groupIdRecordEntity);
    }

    @Override
    public GroupIdRecord findByGroupId(String groupId) {
        try {
            GroupIdRecordEntity entity = groupIdRecordDao.findByGroupId(groupId);
            return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity);
        } catch(NoResultException nre) {
            return null;
        }
    }
    
    @Override
    public GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord) {
    	SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
    	ActivityValidator.validateCreateGroupRecord(groupIdRecord, sourceEntity);
        
        if (sourceEntity != null) {
            Source source = new Source();
            if (sourceEntity.getSourceClient() != null) {
                source.setSourceClientId(new SourceClientId(sourceEntity.getSourceClient().getClientId()));
            } else if (sourceEntity.getSourceProfile() != null) {
                source.setSourceOrcid(new SourceOrcid(sourceEntity.getSourceProfile().getId()));
            }
            groupIdRecord.setSource(source);
        }
        validateDuplicate(groupIdRecord);

        GroupIdRecordEntity entity = jpaJaxbGroupIdRecordAdapter.toGroupIdRecordEntity(groupIdRecord);
        groupIdRecordDao.persist(entity);
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity);
    }

    @Override
    public GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord) {
        GroupIdRecordEntity existingEntity = groupIdRecordDao.find(putCode);
        GroupIdRecordEntity updatedEntity = null;
        validateDuplicate(groupIdRecord);
        if (existingEntity != null) {
            SourceEntity existingSource = existingEntity.getSource();
            orcidSecurityManager.checkSource(existingSource);
            updatedEntity = jpaJaxbGroupIdRecordAdapter.toGroupIdRecordEntity(groupIdRecord);
            updatedEntity.setDateCreated(existingEntity.getDateCreated());
            updatedEntity.setSource(existingSource);
            updatedEntity = groupIdRecordDao.merge(updatedEntity);
        } else {
            throw new GroupIdRecordNotFoundException();
        }

        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(updatedEntity);
    }

    @Override
    public void deleteGroupIdRecord(Long putCode) {
        GroupIdRecordEntity existingEntity = groupIdRecordDao.find(putCode);
        if (existingEntity != null) {
            orcidSecurityManager.checkSource(existingEntity.getSource());
            groupIdRecordDao.remove(Long.valueOf(putCode));
        } else {
            throw new GroupIdRecordNotFoundException();
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

    private void validateDuplicate(GroupIdRecord groupIdRecord) {
        List<GroupIdRecordEntity> groupIdRecords = groupIdRecordDao.getAll();
        if (groupIdRecords != null) {
            for (GroupIdRecordEntity entity : groupIdRecords) {
                GroupIdRecord existing = jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity);
                if (existing.isDuplicated(groupIdRecord) && !existing.getPutCode().equals(groupIdRecord.getPutCode())) {
                    throw new DuplicatedGroupIdRecordException();
                }
            }
        }
    }
}
