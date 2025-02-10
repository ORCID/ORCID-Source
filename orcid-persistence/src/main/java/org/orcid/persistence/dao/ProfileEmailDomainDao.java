package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;

import java.util.List;

public interface ProfileEmailDomainDao extends GenericDao<ProfileEmailDomainEntity, Long> {
    ProfileEmailDomainEntity addEmailDomain(String orcid, String emailDomain, String visibility);

    void removeEmailDomain(String orcid, String emailDomain);

    void removeAllEmailDomains(String orcid);

    boolean updateVisibility(String orcid, String emailDomain, String visibility);

    List<ProfileEmailDomainEntity> findByOrcid(String orcid);

    List<ProfileEmailDomainEntity> findPublicEmailDomains(String orcid);

    ProfileEmailDomainEntity findByEmailDomain(String orcid, String emailDomain);

    void moveEmailDomainToAnotherAccount(String emailDomain, String deprecatedOrcid, String primaryOrcid);
}