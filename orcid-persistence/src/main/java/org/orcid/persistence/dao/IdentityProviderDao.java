package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.IdentityProviderEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface IdentityProviderDao extends GenericDao<IdentityProviderEntity, Long> {

    IdentityProviderEntity findByProviderid(String providerid);

    void incrementFailedCount(String providerid);
}
