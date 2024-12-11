package org.orcid.core.common.manager;

import java.util.List;

import org.orcid.core.common.manager.impl.EmailDomainManagerImpl.STATUS;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;

public interface EmailDomainManager {
    EmailDomainEntity createEmailDomain(String emailDomain, EmailDomainEntity.DomainCategory category);
    
    boolean updateCategory(long id, EmailDomainEntity.DomainCategory category);

    List<EmailDomainEntity>  findByEmailDomain(String emailDomain);

    List<EmailDomainEntity> findByCategory(EmailDomainEntity.DomainCategory category);
    
    STATUS createOrUpdateEmailDomain(String emailDomain, String rorId);

    void processProfessionalEmailsForV2API(org.orcid.jaxb.model.record_v2.Emails emails);

    void processProfessionalEmailsForV3API(org.orcid.jaxb.model.v3.release.record.Emails emails);
}
