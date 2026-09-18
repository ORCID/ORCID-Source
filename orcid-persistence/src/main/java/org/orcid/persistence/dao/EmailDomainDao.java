package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface EmailDomainDao extends GenericDao<EmailDomainEntity, Long> {
    @Transactional(propagation = Propagation.REQUIRED)
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category);
    
    @Transactional(propagation = Propagation.REQUIRED)
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category, String rorId);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateCategory(long id, EmailDomainEntity.DomainCategory category);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateRorId(long id, String rorId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailDomainEntity>  findByEmailDomain(String emailDomain);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailDomainEntity> findByCategory(EmailDomainEntity.DomainCategory category);
}
