package org.orcid.listener.persistence.managers;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.orcid.listener.persistence.dao.Api30RecordStatusDao;
import org.orcid.listener.persistence.entities.Api30RecordStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Api30RecordStatusManager {

    @Autowired
    private Api30RecordStatusDao dao;
    
    private final List<ActivityType> all = Arrays.asList(ActivityType.values());

    @Transactional
    public void save(String orcid, Boolean summaryOk, List<ActivityType> failedElements) throws IllegalArgumentException, EntityExistsException{        
        if(dao.exists(orcid)) {
            dao.update(orcid, summaryOk, failedElements);
        } else {
            dao.create(orcid, summaryOk, failedElements);
        }
    }
    
    @Transactional
    public void allFailed(String orcid) {
        if(dao.exists(orcid)) {
            dao.update(orcid, false, all);
        } else {
            dao.create(orcid, false, all);
        }
    }
    
    @Transactional
    public void setSummaryFail(String orcid) {
        dao.setSummaryFail(orcid);
    }

    @Transactional
    public void setSummaryOk(String orcid) {
        dao.setSummaryOk(orcid);
    }
    
    @Transactional
    public void setActivityFail(String orcid, ActivityType type) {
        dao.setActivityFail(orcid, type);
    }
    
    @Transactional
    public void setActivityOk(String orcid, ActivityType type) {
        dao.setActivityOk(orcid, type);
    }
    
    public List<Api30RecordStatusEntity> getFailedElements(int batchSize) {
        return dao.getFailedElements(batchSize);
    }
}
