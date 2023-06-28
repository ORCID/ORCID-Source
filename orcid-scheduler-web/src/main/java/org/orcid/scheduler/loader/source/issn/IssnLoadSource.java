package org.orcid.scheduler.loader.source.issn;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.groupIds.issn.IssnClient;
import org.orcid.core.groupIds.issn.IssnData;
import org.orcid.core.groupIds.issn.IssnValidator;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.InvalidIssnGroupIdRecordEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IssnLoadSource {

    private static final Logger LOG = LoggerFactory.getLogger(IssnLoadSource.class);

    private static Pattern issnGroupTypePattern = Pattern.compile("^issn:(\\d{4}-\\d{3}[\\dXx])$");

    @Value("${org.orcid.scheduler.issnLoadSource.batchSize:50}")
    private int batchSize;

    @Value("${org.orcid.scheduler.issnLoadSource.waitBetweenBatches:30000}")
    private int waitBetweenBatches;
    
    @Resource
    private GroupIdRecordDao groupIdRecordDao;

    @Resource(name="groupIdRecordDaoReadOnly")
    private GroupIdRecordDao groupIdRecordDaoReadOnly;
    
    @Resource
    private GenericDao<InvalidIssnGroupIdRecordEntity, Long> invalidIssnGroupIdRecordDao;

    @Resource(name="clientDetailsDaoReadOnly")
    private ClientDetailsDao clientDetailsDaoReadOnly;
    
    private ClientDetailsEntity orcidSource;

    @Resource
    private IssnValidator issnValidator;

    @Resource
    private IssnClient issnClient;
    
    public void loadIssn(String issnSource) {
        
        if (issnSource == null) {
            throw new RuntimeException("Missing the  ORCID source client details id");
        }
        orcidSource = clientDetailsDaoReadOnly.find(issnSource);
        if (orcidSource == null) {
            throw new RuntimeException("Client details entity not found for id " + issnSource);
        }
        LOG.debug("Loading ISSN for ORCID source client details id: " + issnSource);
        this.updateIssnGroupIdRecords();
    }

    private void updateIssnGroupIdRecords() {
        Date start = new Date();
        List<GroupIdRecordEntity> issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsNotModifiedSince(batchSize, start);
        int count = 0;
        while (!issnEntities.isEmpty()) {
            for (GroupIdRecordEntity issnEntity : issnEntities) {
                String issn = getIssn(issnEntity);
                if (issn != null && issnValidator.issnValid(issn)) {
                    count++;
                    IssnData issnData = issnClient.getIssnData(issn);
                    if (issnData != null) {
                        updateIssnEntity(issnEntity, issnData);
                        LOG.info("Updated group id record {} - {}, processed count now {}",
                                new Object[] { issnEntity.getId(), issnEntity.getGroupId(), Integer.toString(count) });
                    } else {
                        LOG.warn("ISSN data not found for {}", issn);
                        recordFailure(issnEntity.getId(), "Data not found");
                    }
                } else {
                    LOG.info("Issn for group record {} not valid: {}", issnEntity.getId(), issnEntity.getGroupId());
                    recordFailure(issnEntity.getId(), "Invalid record");
                }                
                try {
                    // Lets sleep for 30 secs after processing one batch
                    if(count >= batchSize) {
                        Thread.sleep(waitBetweenBatches);
                        // Reset the count
                        count = 0;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsNotModifiedSince(batchSize, start);
        }
    }

    private void recordFailure(Long id, String notes) {
        InvalidIssnGroupIdRecordEntity invalidIssn = new InvalidIssnGroupIdRecordEntity();
        invalidIssn.setId(id);
        invalidIssn.setNotes(notes);
        invalidIssnGroupIdRecordDao.persist(invalidIssn);
    }

    private void updateIssnEntity(GroupIdRecordEntity issnEntity, IssnData issnData) {
        String currentGroupName = issnEntity.getGroupName();
        String updatedGroupName = issnData.getMainTitle(); 
        
        if(!StringUtils.equals(currentGroupName, updatedGroupName)) {
            issnEntity.setGroupName(updatedGroupName);
            issnEntity.setClientSourceId(orcidSource.getId());
            LOG.info("group id: " + issnEntity.getGroupId() +  " | current group name: " + currentGroupName +  " | group name  to be updated: " + issnEntity.getGroupName());
            groupIdRecordDao.merge(issnEntity);
        } else {
            LOG.info("Group id: " + issnEntity.getGroupId() + " is up to date");
        }
    }

    private String getIssn(GroupIdRecordEntity issnEntity) {
        Matcher matcher = issnGroupTypePattern.matcher(issnEntity.getGroupId());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
