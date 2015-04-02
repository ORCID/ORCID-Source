/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ShibbolethAccountDao;
import org.orcid.persistence.jpa.entities.ShibbolethAccountEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public class ShibbolethAccountDaoImpl extends GenericDaoImpl<ShibbolethAccountEntity, Long> implements ShibbolethAccountDao {

    public ShibbolethAccountDaoImpl() {
        super(ShibbolethAccountEntity.class);
    }

    @Override
    public ShibbolethAccountEntity findByRemoteUserAndShibIdentityProvider(String remoteUser, String shibIdentityProvider) {
        TypedQuery<ShibbolethAccountEntity> query = entityManager.createQuery(
                "from ShibbolethAccountEntity where remoteUser = :remoteUser and shibIdentityProvider = :shibIdentityProvider", ShibbolethAccountEntity.class);
        query.setParameter("remoteUser", remoteUser);
        query.setParameter("shibIdentityProvider", shibIdentityProvider);
        List<ShibbolethAccountEntity> results = query.getResultList();
        return results != null && !results.isEmpty() ? results.get(0) : null;
    }

}
