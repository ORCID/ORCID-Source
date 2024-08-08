package org.orcid.core.manager.v3.read_only;


import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;

import java.util.List;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public interface ProfileEmailDomainManagerReadOnly {
    List<ProfileEmailDomainEntity> getEmailDomains(String orcid);
    List<ProfileEmailDomainEntity> getPublicEmailDomains(String orcid);
}
