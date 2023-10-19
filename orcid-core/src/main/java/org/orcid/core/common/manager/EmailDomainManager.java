package org.orcid.core.common.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.EmailDomainEntity;

public interface EmailDomainManager {
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category);
    
    boolean updateCategory(long id, EmailDomainEntity.DomainCategory category);

    EmailDomainEntity findByEmailDoman(String emailDomain);

    List<EmailDomainEntity> findByCategory(EmailDomainEntity.DomainCategory category);
    
    EmailDomainEntity createOrUpdateEmailDomain(String emailDomain, String rorId);
}
