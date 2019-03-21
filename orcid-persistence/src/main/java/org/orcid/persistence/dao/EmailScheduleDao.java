package org.orcid.persistence.dao;

import java.util.Date;

import org.orcid.persistence.jpa.entities.EmailScheduleEntity;

public interface EmailScheduleDao extends GenericDao<EmailScheduleEntity, Long> {

    Long currentScheduleAllows();

    void updateLatestSent(Long scheduleId, Date latestSentDate);

}
