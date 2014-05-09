package org.orcid.persistence.dao.impl;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.CustomEmailDao;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;
import org.springframework.transaction.annotation.Transactional;

public class CustomEmailDaoImpl extends GenericDaoImpl<CustomEmailEntity, CustomEmailPk> implements CustomEmailDao {

    public CustomEmailDaoImpl() {
        super(CustomEmailEntity.class);
    }

    @Override
    public CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType) {
        TypedQuery<CustomEmailEntity> query = entityManager.createQuery("FROM CustomEmailEntity WHERE clientDetailsEntity=:clientDetailsId and emailType=:emailType", CustomEmailEntity.class);
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean createCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content) {
        Query query = entityManager.createNativeQuery("INSERT INTO custom_email(client_details_id, email_type, subject, content, date_created, last_modified) values(:clientDetailsId, :emailType, :subject, :content, now(), now())");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        query.setParameter("subject", content);
        query.setParameter("content", content);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content) {
        Query query = entityManager.createNativeQuery("UPDATE custom_email SET email_type=:emailType, subject=:subject, content=:content WHERE client_details_id=:client_details_id");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        query.setParameter("content", content);
        query.setParameter("subject", content);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean deleteCustomEmail(String clientDetailsId, EmailType emailType) {
        Query query = entityManager.createNativeQuery("DELETE FROM custom_email WHERE client_details_id=:clientDetailsId AND email_type=:emailType");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        return query.executeUpdate() > 0;
    }

    

}
