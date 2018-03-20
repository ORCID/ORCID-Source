package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ShibbolethAccountEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface ShibbolethAccountDao extends GenericDao<ShibbolethAccountEntity, Long> {

    ShibbolethAccountEntity findByRemoteUserAndShibIdentityProvider(String remoteUser, String shibIdentityProvider);

    List<ShibbolethAccountEntity> findByOrcid(String orcid);

    void removeByIdAndOrcid(Long id, String orcid);
    
}
