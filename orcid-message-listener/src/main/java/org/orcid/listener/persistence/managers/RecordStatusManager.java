package org.orcid.listener.persistence.managers;

import javax.annotation.Resource;

import org.orcid.listener.persistence.dao.RecordStatusDao;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.springframework.stereotype.Component;

@Component
public class RecordStatusManager {

    private static Integer FIRST_FAIL = 1;
    
    @Resource
    private RecordStatusDao dao;
    
    public void markAsSent(String orcid, AvailableBroker broker) {
        if(dao.exists(orcid)) {
            dao.updateStatus(orcid, broker, 0);
        } else {
            dao.create(orcid, broker, 0);
        }
    }
    
    public void markAsFailed(String orcid, AvailableBroker broker) {
        if(dao.exists(orcid)) {
            dao.updateStatus(orcid, broker);
        } else {
            dao.create(orcid, broker, FIRST_FAIL);
        }
    }
}
