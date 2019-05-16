package org.orcid.core.manager.v3.impl;

import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidIssnException;
import org.orcid.core.exception.OrcidElementCantBeDeletedException;
import org.orcid.core.issn.IssnData;
import org.orcid.core.issn.client.IssnClient;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.GroupIdRecordManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.core.manager.validator.IssnValidator;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;

public class GroupIdRecordManagerImpl extends GroupIdRecordManagerReadOnlyImpl implements GroupIdRecordManager {

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;
    
    @Value("${org.orcid.core.issn.source}")
    private String orcidSourceClientDetailsId;
    
    @Resource
    private IssnClient issnClient;
    
    @Resource
    private IssnValidator issnValidator;
    
    @Override
    public GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord) {
        SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();
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
    public GroupIdRecord createOrcidSourceIssnGroupIdRecord(String groupId, String issn) {
        GroupIdRecord issnRecord = createIssnGroupIdRecord(groupId, issn);
        GroupIdRecordEntity entity = jpaJaxbGroupIdRecordAdapter.toGroupIdRecordEntity(issnRecord);
        entity.setClientSourceId(orcidSourceClientDetailsId);
        groupIdRecordDao.persist(entity);
        return jpaJaxbGroupIdRecordAdapter.toGroupIdRecord(entity);
    }

    @Override
    public GroupIdRecord updateGroupIdRecord(Long putCode, GroupIdRecord groupIdRecord) {
        GroupIdRecordEntity existingEntity = groupIdRecordDao.find(putCode);

        if (existingEntity == null) {
            throw new GroupIdRecordNotFoundException();
        }

        SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();
        // Save the original source
        String existingSourceId = existingEntity.getSourceId();
        String existingClientSourceId = existingEntity.getClientSourceId();

        activityValidator.validateGroupIdRecord(groupIdRecord, false, sourceEntity);
        validateDuplicate(groupIdRecord);

        orcidSecurityManager.checkSourceAndThrow(existingEntity);
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
            orcidSecurityManager.checkSourceAndThrow(existingEntity);
            groupIdRecordDao.remove(Long.valueOf(putCode));
        } else {
            throw new GroupIdRecordNotFoundException();
        }
    }
    
    private GroupIdRecord createIssnGroupIdRecord(String groupId, String issn) {
        if (!issnValidator.issnValid(issn)) {
            throw new InvalidIssnException();
        }
        
        IssnData issnData = issnClient.getIssnData(issn);
        if (issnData == null) {
            throw new InvalidIssnException();
        }
        
        GroupIdRecord record = new GroupIdRecord();
        record.setGroupId(groupId);
        GregorianCalendar cal = new GregorianCalendar();
        record.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setName(issnData.getMainTitle());
        record.setType("journal");
        return record;
    }
    
    private void validateDuplicate(GroupIdRecord newGroupIdRecord) {
        if (groupIdRecordDao.duplicateExists(newGroupIdRecord.getPutCode(), newGroupIdRecord.getGroupId())) {
            throw new DuplicatedGroupIdRecordException();
        }
    }
    
}
