package org.orcid.persistence.dao.impl;

import jakarta.persistence.Query;

import org.orcid.persistence.aop.UpdateProfileLastModified;
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
    @UpdateProfileLastModified
    public boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity ef SET ef.sendChangeNotifications = :frequency, ef.lastModified=now() WHERE ef.orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity ef SET ef.sendAdministrativeChangeNotifications = :frequency, ef.lastModified=now() WHERE ef.orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity ef SET ef.sendMemberUpdateRequests = :frequency, ef.lastModified=now() WHERE ef.orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public boolean updateSendQuarterlyTips(String orcid, boolean enabled) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity ef SET ef.sendQuarterlyTips = :enabled, ef.lastModified=now() WHERE ef.orcid = :orcid");
        query.setParameter("enabled", enabled);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

}
