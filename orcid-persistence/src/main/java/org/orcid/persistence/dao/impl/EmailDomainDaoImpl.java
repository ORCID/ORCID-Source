package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class EmailDomainDaoImpl extends GenericDaoImpl<EmailDomainEntity, Long> implements EmailDomainDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(EmailDomainDaoImpl.class);
    
    public EmailDomainDaoImpl() {
        super(EmailDomainEntity.class);
    }
        
    @Override
    @Transactional
    public EmailDomainEntity createEmailDomain(String emailDomain, DomainCategory category) {
        LOG.debug("Creating domain {} with category {}", emailDomain, category);
        EmailDomainEntity e = new EmailDomainEntity();
        e.setEmailDomain(emailDomain);
        e.setCategory(category);
        entityManager.persist(e);
        return e;
    }
    
    @Override
    @Transactional
    public EmailDomainEntity createEmailDomain(String emailDomain, DomainCategory category, String rorId) {
        LOG.debug("Creating domain {} with category {} and ror Id {}", emailDomain, category, rorId);
        EmailDomainEntity e = new EmailDomainEntity();
        e.setEmailDomain(emailDomain);
        e.setCategory(category);
        e.setRorId(rorId);
        entityManager.persist(e);
        return e;
    }

    @Override
    @Transactional
    public boolean updateCategory(long id, DomainCategory category) {
        LOG.debug("Updating domain with id {} with category {}", id, category);
        Query query = entityManager.createNativeQuery("UPDATE email_domain SET category=:category WHERE id = :id");
        query.setParameter("id", id);        
        query.setParameter("category", category.toString());
        return query.executeUpdate() > 0; 
    }

    @Override
    @Transactional
    public boolean updateRorId(long id, String rorId) {
        LOG.debug("Updating domain with id {} with rorId {}", id, rorId);
        Query query = entityManager.createNativeQuery("UPDATE email_domain SET ror_id=:rorId WHERE id = :id");
        query.setParameter("id", id);        
        query.setParameter("rorId", rorId.toString());
        return query.executeUpdate() > 0; 
    }
    
    @Override
    public List<EmailDomainEntity>  findByEmailDomain(String emailDomain) {
        TypedQuery<EmailDomainEntity> query = entityManager.createQuery("from EmailDomainEntity where lower(trim(emailDomain)) = lower(trim(:emailDomain))", EmailDomainEntity.class);
        query.setParameter("emailDomain", emailDomain);
        try {
            return query.getResultList();
        } catch(NoResultException nre) {
            // Ignore this exception
        } catch(Exception e) {
            // Propagate any other exception
            throw e;
        }
        return null;
    }

    @Override
    public List<EmailDomainEntity> findByCategory(DomainCategory category) {
        TypedQuery<EmailDomainEntity> query = entityManager.createQuery("from EmailDomainEntity where category = :category", EmailDomainEntity.class);
        query.setParameter("category", category);
        List<EmailDomainEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }   
}
