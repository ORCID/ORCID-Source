package org.orcid.persistence.dao.impl;

import javax.persistence.Query;
import org.orcid.jaxb.model.message.SendEmailFrequency;
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
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendChangeNotifications = :frequency WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendAdministrativeChangeNotifications = :frequency WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendOrcidNews(String orcid, SendEmailFrequency frequency) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendOrcidNews = :frequency WHERE orcid = :orcid");
        query.setParameter("frequency", Float.valueOf(frequency.value()));
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateSendQuarterlyTips(String orcid, boolean enabled) {
        Query query = entityManager.createQuery("UPDATE EmailFrequencyEntity SET sendQuarterlyTips = :enabled WHERE orcid = :orcid");
        query.setParameter("enabled", enabled);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

}
