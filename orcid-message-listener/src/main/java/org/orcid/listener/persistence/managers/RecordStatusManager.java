package org.orcid.listener.persistence.managers;

import java.util.List;

import org.orcid.listener.persistence.dao.RecordStatusDao;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RecordStatusManager {

    public static final Integer FIRST_FAIL = 1;

    public static final Integer OK = 0;

    @Autowired
    private RecordStatusDao dao;

    @Transactional
    public void markAsSent(String orcid, AvailableBroker broker) {
        if (dao.exists(orcid)) {
            dao.success(orcid, broker);
        } else {
            dao.create(orcid, broker, OK);
        }
    }

    @Transactional
    public void markAsFailed(String orcid, AvailableBroker broker) {
        if (dao.exists(orcid)) {
            dao.updateFailCount(orcid, broker);
        } else {
            dao.create(orcid, broker, FIRST_FAIL);
        }
    }
    
    public List<RecordStatusEntity> getFailedElements(int batchSize) {
        return dao.getFailedElements(batchSize);
    }
}
