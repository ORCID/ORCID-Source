package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.orcid.persistence.dao.EmailScheduleDao;
import org.orcid.persistence.jpa.entities.EmailScheduleEntity;

public class EmailScheduleDaoImpl extends GenericDaoImpl<EmailScheduleEntity, Long> implements EmailScheduleDao {
    
    public EmailScheduleDaoImpl() {
        super(EmailScheduleEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Long getValidScheduleId() {
        Query query = entityManager.createNativeQuery("SELECT id FROM email_schedule WHERE ((now() >= schedule_start AND schedule_end IS NULL) or (now() >= schedule_start AND now() < schedule_end)) AND latest_sent IS NULL OR EXTRACT(EPOCH FROM latest_sent) * 1000 + schedule_interval <= EXTRACT(EPOCH FROM now()) * 1000");
        List<BigInteger> results = query.getResultList();
        return results.isEmpty() ? -1L : results.get(0).longValue();
    }

    @Override
    @Transactional
    public void updateLatestSent(Long scheduleId, Date latestSentDate) {
        Query query = entityManager.createNativeQuery("UPDATE email_schedule SET latest_sent = :latestSentDate WHERE id = :id");
        query.setParameter("latestSentDate", latestSentDate);
        query.setParameter("id", scheduleId);
        query.executeUpdate();
    }

}
    