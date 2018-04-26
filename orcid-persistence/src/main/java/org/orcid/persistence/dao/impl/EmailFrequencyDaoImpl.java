package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.springframework.transaction.annotation.Transactional;

public class EmailFrequencyDaoImpl extends GenericDaoImpl<EmailFrequencyEntity, String> implements EmailFrequencyDao {

    public EmailFrequencyDaoImpl() {
        super(EmailFrequencyEntity.class);
    }
    
    @Override
    public EmailFrequencyEntity find(String id) {
        Query query = entityManager.createQuery("FROM EmailFrequencyEntity WHERE id = :id");
        query.setParameter("id", id);
        return (EmailFrequencyEntity) query.getSingleResult();
    }

    @Override
    public EmailFrequencyEntity findByOrcid(String orcid) {
        Query query = entityManager.createQuery("FROM EmailFrequencyEntity WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return (EmailFrequencyEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendChangeNotifications = :frequency, lastModified=now() WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendAdministrativeChangeNotifications = :frequency, lastModified=now() WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendMemberUpdateRequests = :frequency, lastModified=now() WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendQuarterlyTips(String orcid, boolean enabled) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendQuarterlyTips = :enabled, lastModified=now() WHERE orcid = :orcid");
        query.setParameter("enabled", enabled);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findOrcidsToProfess(int batchSize) {
        Query query = entityManager.createNativeQuery("SELECT orcid, send_email_frequency_days, send_change_notifications, send_administrative_change_notifications, send_orcid_news, send_member_update_requests FROM profile WHERE orcid NOT IN (SELECT orcid FROM email_frequency) LIMIT :batchSize");
        query.setParameter("batchSize", batchSize);
        return query.getResultList();
    }

}
