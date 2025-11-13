package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.EmailDomainEntity;

public interface EmailDomainDao extends GenericDao<EmailDomainEntity, Long> {
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category);
    
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category, String rorId);

    boolean updateCategory(long id, EmailDomainEntity.DomainCategory category);
    
    boolean updateRorId(long id, String rorId);

    List<EmailDomainEntity>  findByEmailDomain(String emailDomain);

    List<EmailDomainEntity> findByCategory(EmailDomainEntity.DomainCategory category);
}
