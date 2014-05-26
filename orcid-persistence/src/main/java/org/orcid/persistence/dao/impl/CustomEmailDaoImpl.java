/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.NonUniqueResultException;
import org.orcid.persistence.dao.CustomEmailDao;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class CustomEmailDaoImpl extends GenericDaoImpl<CustomEmailEntity, CustomEmailPk> implements CustomEmailDao {

    public CustomEmailDaoImpl() {
        super(CustomEmailEntity.class);
    }

    /**
     * Get a list of all custom emails created by a specific client
     * @param clientDetailsId
     * @return a list containing all custom emails associated with a client
     * */
    @Override
    public List<CustomEmailEntity> getCustomEmails(String clientDetailsId) {
        TypedQuery<CustomEmailEntity> query = entityManager.createQuery("from CustomEmailEntity WHERE clientDetailsEntity.id=:clientDetailsId", CustomEmailEntity.class);
        query.setParameter("clientDetailsId", clientDetailsId);        
        return query.getResultList();
    }
    
    /**
     * Finds a custom email given his client id and the email type
     * @param clientDetailsId
     * @param emailType
     * @return a CustomEmailEntity object if the email is found, null otherwise
     * */
    @Override
    @Cacheable(value = "custom-email", key = "#clientDetailsId.concat('-').concat(#emailType).concat('-').concat(#lastModified)")
    public CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType, Date lastModified) {
        TypedQuery<CustomEmailEntity> query = entityManager.createQuery("FROM CustomEmailEntity WHERE clientDetailsEntity.id=:clientDetailsId and emailType=:emailType", CustomEmailEntity.class);
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType);
        try {
            return query.getSingleResult();
        } catch (NonUniqueResultException exception) {            
            return null;
        } catch (NoResultException exception) {
            return null;
        }        
    }
    
    /**
     * Creates a custom email on database
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @return true if it was able to create the custom email      
     * */
    @Override
    @Transactional
    public boolean createCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content, boolean isHtml) {
        Query query = entityManager.createNativeQuery("INSERT INTO custom_email(client_details_id, email_type, sender, subject, content, is_html, date_created, last_modified) values(:clientDetailsId, :emailType, :sender, :subject, :content, :isHtml, now(), now())");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        query.setParameter("sender", sender);
        query.setParameter("subject", subject);
        query.setParameter("content", content);
        query.setParameter("isHtml", isHtml);
        return query.executeUpdate() > 0;
    }

    /**
     * Updated an existing custom email
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @return true if it was able to update the custom email
     * */
    @Override
    @Transactional
    public boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content, boolean isHtml) {
        Query query = entityManager.createNativeQuery("UPDATE custom_email SET email_type=:emailType, sender=:sender, subject=:subject, content=:content, is_html=:isHtml, last_modified=now() WHERE client_details_id=:clientDetailsId");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        query.setParameter("sender", sender);
        query.setParameter("subject", subject);
        query.setParameter("content", content);
        query.setParameter("isHtml", isHtml);
        return query.executeUpdate() > 0;
    }

    /**
     * Deletes a custom email
     * @param clientDetailsId
     * @param emailType 
     * @return true if it was able to delete the custom email
     * */
    @Override
    @Transactional
    public boolean deleteCustomEmail(String clientDetailsId, EmailType emailType) {
        Query query = entityManager.createNativeQuery("DELETE FROM custom_email WHERE client_details_id=:clientDetailsId AND email_type=:emailType");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType.name());
        return query.executeUpdate() > 0;
    }

    /**
     * Checks if a custom email exists
     * @param clientDetailsId
     * @param emailType
     * @return true if a custom email with id=clientDetailsId and email type=emailType exists
     * */
    @Override
    public boolean exists(String clientDetailsId, EmailType emailType) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT count(*) FROM CustomEmailEntity WHERE clientDetailsEntity.id=:clientDetailsId and emailType=:emailType", Long.class);
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }
    
    /**
     * Get the last modified date of a custom email
     * @param clientDetailsId
     * @param emailType
     * @return the last modified date of the custom email, null in case the email doesn't exists
     * */
    @Override
    public Date getLastModified(String clientDetailsId, EmailType emailType) {
        TypedQuery<Date> query = entityManager.createQuery("SELECT lastModified FROM CustomEmailEntity WHERE clientDetailsEntity.id=:clientDetailsId and emailType=:emailType", Date.class);
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setParameter("emailType", emailType);
        Date result = null;
        try {
            result = query.getSingleResult();
        } catch(Exception e) {
            
        }
        return result;
    }

}
