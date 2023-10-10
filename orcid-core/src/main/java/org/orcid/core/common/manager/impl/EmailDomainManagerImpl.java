package org.orcid.core.common.manager.impl;

import java.util.List;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.orcid.persistence.jpa.entities.EmailDomainEntity.DomainCategory;

public class EmailDomainManagerImpl implements EmailDomainManager {

    @Override
    public EmailDomainEntity createEmailDomain(String emailDomain, DomainCategory category) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean updateCategory(long id, DomainCategory category) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EmailDomainEntity findByEmailDoman(String emailDomain) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EmailDomainEntity> findByCategory(DomainCategory category) {
        // TODO Auto-generated method stub
        return null;
    }

}
