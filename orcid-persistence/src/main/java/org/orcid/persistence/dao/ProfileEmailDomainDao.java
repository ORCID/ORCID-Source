package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProfileEmailDomainDao extends GenericDao<ProfileEmailDomainEntity, Long> {
    @Transactional(propagation = Propagation.REQUIRED)
    ProfileEmailDomainEntity addEmailDomain(String orcid, String emailDomain, String visibility);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeEmailDomain(String orcid, String emailDomain);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllEmailDomains(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, String emailDomain, String visibility);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileEmailDomainEntity> findByOrcid(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileEmailDomainEntity> findPublicEmailDomains(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    ProfileEmailDomainEntity findByEmailDomain(String orcid, String emailDomain);

    @Transactional(propagation = Propagation.REQUIRED)
    void moveEmailDomainToAnotherAccount(String emailDomain, String deprecatedOrcid, String primaryOrcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileEmailDomainEntity> findByEmailDomain(String emailDomain);
}