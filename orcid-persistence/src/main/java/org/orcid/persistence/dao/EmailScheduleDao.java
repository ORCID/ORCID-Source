package org.orcid.persistence.dao;

import java.util.Date;

import org.orcid.persistence.jpa.entities.EmailScheduleEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface EmailScheduleDao extends GenericDao<EmailScheduleEntity, Long> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Long getValidScheduleId();

    @Transactional(propagation = Propagation.REQUIRED)
    void updateLatestSent(Long scheduleId, Date latestSentDate);

}
