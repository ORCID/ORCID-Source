package org.orcid.core.cli;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.orcid.core.issn.IssnData;
import org.orcid.core.issn.client.IssnClient;
import org.orcid.core.manager.validator.IssnValidator;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.InvalidIssnGroupIdRecordEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoadIssnData {

    private static final Logger LOG = LoggerFactory.getLogger(LoadIssnData.class);

    private static Pattern issnGroupTypePattern = Pattern.compile("^issn:(\\d{4}-\\d{3}[\\dXx])$");

    private static final int BATCH_SIZE = 30;

    private GroupIdRecordDao groupIdRecordDao;

    private GroupIdRecordDao groupIdRecordDaoReadOnly;
    
    private GenericDao<InvalidIssnGroupIdRecordEntity, Long> invalidIssnGroupIdRecordDao;

    private ClientDetailsEntity orcidSource;

    private IssnValidator issnValidator;

    private IssnClient issnClient;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Missing single arg for ORCID source client details id");
        }
        LoadIssnData loadIssnData = new LoadIssnData();
        loadIssnData.init(args[0]);
        loadIssnData.updateIssnGroupIdRecords();
    }

    private void updateIssnGroupIdRecords() {
        List<GroupIdRecordEntity> issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsNotSourcedBy(orcidSource.getId(), BATCH_SIZE);
        int count = 0;
        while (!issnEntities.isEmpty()) {
            for (GroupIdRecordEntity issnEntity : issnEntities) {
                String issn = getIssn(issnEntity);
                if (issn != null && issnValidator.issnValid(issn)) {
                    IssnData issnData = issnClient.getIssnData(issn);
                    if (issnData != null) {
                        updateIssnEntity(issnEntity, issnData);
                        count++;
                        try {
                            LOG.info("Updated group id record {} - {}, processed count now {}",
                                    new Object[] { issnEntity.getId(), issnEntity.getGroupId(), Integer.toString(count) });
                            Thread.sleep(10000l);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        LOG.warn("ISSN data not found for {}", issn);
                        recordFailure(issnEntity.getId(), "Data not found");
                    }
                } else {
                    LOG.info("Issn for group record {} not valid: {}", issnEntity.getId(), issnEntity.getGroupId());
                    recordFailure(issnEntity.getId(), "Invalid record");
                }
            }
            issnEntities = groupIdRecordDaoReadOnly.getIssnRecordsNotSourcedBy(orcidSource.getId(), BATCH_SIZE);
        }
    }

    private void recordFailure(Long id, String notes) {
        InvalidIssnGroupIdRecordEntity invalidIssn = new InvalidIssnGroupIdRecordEntity();
        invalidIssn.setId(id);
        invalidIssn.setNotes(notes);
        invalidIssnGroupIdRecordDao.persist(invalidIssn);
    }

    private void updateIssnEntity(GroupIdRecordEntity issnEntity, IssnData issnData) {
        issnEntity.setGroupName(issnData.getMainTitle());
        issnEntity.setClientSourceId(orcidSource.getId());
        groupIdRecordDao.merge(issnEntity);
    }

    private String getIssn(GroupIdRecordEntity issnEntity) {
        Matcher matcher = issnGroupTypePattern.matcher(issnEntity.getGroupId());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @SuppressWarnings({ "resource", "unchecked" })
    private void init(String orcidSourceClientDetailsId) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        groupIdRecordDaoReadOnly = (GroupIdRecordDao) context.getBean("groupIdRecordDaoReadOnly");
        groupIdRecordDao = (GroupIdRecordDao) context.getBean("groupIdRecordDao");
        issnValidator = context.getBean(IssnValidator.class);
        issnClient = context.getBean(IssnClient.class);
        invalidIssnGroupIdRecordDao = (GenericDao<InvalidIssnGroupIdRecordEntity, Long>) context.getBean("invalidIssnGroupIdRecordDao");

        ClientDetailsDao clientDetailsDaoReadOnly = (ClientDetailsDao) context.getBean("clientDetailsDaoReadOnly");
        orcidSource = clientDetailsDaoReadOnly.find(orcidSourceClientDetailsId);
        if (orcidSource == null) {
            throw new RuntimeException("Client details entity not found for id " + orcidSourceClientDetailsId);
        }
    }

}
