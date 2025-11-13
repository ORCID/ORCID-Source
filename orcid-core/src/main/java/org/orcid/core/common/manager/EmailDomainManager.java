package org.orcid.core.common.manager;

import java.util.List;

import org.orcid.core.common.manager.impl.EmailDomainManagerImpl.STATUS;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.pojo.EmailDomain;

public interface EmailDomainManager {
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category);
    
    boolean updateCategory(long id, EmailDomainEntity.DomainCategory category);

    List<EmailDomain>  findByEmailDomain(String emailDomain);

    List<EmailDomainEntity> findByCategory(EmailDomainEntity.DomainCategory category);
    
    STATUS createOrUpdateEmailDomain(String emailDomain, String rorId);
}
