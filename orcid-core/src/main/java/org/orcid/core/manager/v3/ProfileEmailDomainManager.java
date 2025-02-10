package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.ProfileEmailDomainManagerReadOnly;
import org.orcid.persistence.jpa.entities.EmailEntity;

import java.util.List;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public interface ProfileEmailDomainManager extends ProfileEmailDomainManagerReadOnly {
    void updateEmailDomains(String orcid, org.orcid.pojo.ajaxForm.Emails emails);
    void processDomain(String orcid, String email);
    void removeAllEmailDomains(String orcid);
    void moveEmailDomainToAnotherAccount(String emailDomain, String deprecatedOrcid, String primaryOrcid);
}
