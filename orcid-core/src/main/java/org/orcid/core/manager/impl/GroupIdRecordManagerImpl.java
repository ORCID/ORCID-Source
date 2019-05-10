package org.orcid.core.manager.impl;

import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidElementCantBeDeletedException;
import org.orcid.core.issn.IssnData;
import org.orcid.core.issn.client.IssnClient;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.GroupIdRecordManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.jaxb.model.common_v2.CreatedDate;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.utils.DateUtils;

public class GroupIdRecordManagerImpl extends GroupIdRecordManagerReadOnlyImpl implements GroupIdRecordManager {

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ActivityValidator activityValidator;
    
    @Resource
    private IssnClient issnClient;

    @Override
    public GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        activityValidator.validateGroupIdRecord(groupIdRecord, true, sourceEntity);
        validateDuplicate(groupIdRecord);
        GroupIdRecordEntity entity = jpaJaxbGroupIdRecordAdapter.toGroupIdRecordEntity(groupIdRecord);
        if (sourceEntity != null) {
            if (sourceEntity.getSourceClient() != null) {
                entity.setClientSourceId(sourceEntity.getSourceClient().getClientId());
            } else if (sourceEntity.getSourceProfile() != null) {
                entity.setSourceId(sourceEntity.getSourceProfile().getId());
            }
        }
        groupIdRecordDao.persist(entity);
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity);
    }

    @Override
    public GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord) {
        GroupIdRecordEntity existingEntity = groupIdRecordDao.find(putCode);

        if (existingEntity == null) {
            throw new GroupIdRecordNotFoundException();
        }

        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Save the original source
        String existingSourceId = existingEntity.getSourceId();
        String existingClientSourceId = existingEntity.getClientSourceId();

        activityValidator.validateGroupIdRecord(groupIdRecord, false, sourceEntity);
        validateDuplicate(groupIdRecord);

        orcidSecurityManager.checkSource(existingEntity);
        GroupIdRecordEntity updatedEntity = jpaJaxbGroupIdRecordAdapter.toGroupIdRecordEntity(groupIdRecord);
        updatedEntity.setDateCreated(existingEntity.getDateCreated());
        // Be sure it doesn't overwrite the source
        updatedEntity.setSourceId(existingSourceId);
        updatedEntity.setClientSourceId(existingClientSourceId);

        updatedEntity = groupIdRecordDao.merge(updatedEntity);
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(updatedEntity);
    }

    @Override
    public void deleteGroupIdRecord(Long putCode) {
        GroupIdRecordEntity existingEntity = groupIdRecordDao.find(putCode);
        if (existingEntity != null) {
            if (groupIdRecordDao.haveAnyPeerReview(existingEntity.getGroupId())) {
                throw new OrcidElementCantBeDeletedException("Unable to delete group id because there are peer reviews associated to it");
            }
            orcidSecurityManager.checkSource(existingEntity);
            groupIdRecordDao.remove(Long.valueOf(putCode));
        } else {
            throw new GroupIdRecordNotFoundException();
        }
    }
    
    @Override
    public GroupIdRecord createIssnGroupIdRecord(String groupId, String issn) {
        IssnData issnData = issnClient.getIssnData(issn);
        if (issnData != null) {
            GroupIdRecord record = new GroupIdRecord();
            record.setGroupId(groupId);
            GregorianCalendar cal = new GregorianCalendar();
            record.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
            record.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
            record.setName(issnData.getMainTitle());
            record.setType("journal");
            record.setSource(new Source()); // XXX ORCID - which client?
            return record;
        } else {
            return null;
        }
    }

    private void validateDuplicate(GroupIdRecord newGroupIdRecord) {
        if (groupIdRecordDao.duplicateExists(newGroupIdRecord.getPutCode(), newGroupIdRecord.getGroupId())) {
            throw new DuplicatedGroupIdRecordException();
        }
    }

}
