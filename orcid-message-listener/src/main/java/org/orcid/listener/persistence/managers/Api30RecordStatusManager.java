package org.orcid.listener.persistence.managers;

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

    @Transactional
    public void save(String orcid, Boolean summaryOk, List<ActivityType> failedElements) throws IllegalArgumentException, EntityExistsException{        
        if(dao.exists(orcid)) {
            dao.update(orcid, summaryOk, failedElements);
        } else {
            dao.create(orcid, summaryOk, failedElements);
        }        
    }
    
        
    public List<Api30RecordStatusEntity> getFailedElements(int batchSize) {
        return dao.getFailedElements(batchSize);
    }
}
