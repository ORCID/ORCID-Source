/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class ThirdPartyImportManagerImpl implements ThirdPartyImportManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyImportManagerImpl.class);


    @Resource(name = "clientRedirectDao")
    private ClientRedirectDao clientRedirectDao;

    @Cacheable("import-works-clients")
    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeWorksImport() {

        return getClients(RedirectUriType.IMPORT_WORKS_WIZARD);
    }

    @Override
    @Cacheable("read-access-clients")
    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeReadAccess() {
        return getClients(RedirectUriType.GRANT_READ_WIZARD);
    }
    
    @Override
    @CacheEvict(value = {"read-access-clients","import-works-clients"}, allEntries=true)    
    public void evictAll() {
        LOGGER.debug("read-access-clients and import-works-clients all keys  evicted");
    }

    private List<OrcidClient> getClients(RedirectUriType rut) {
        List<OrcidClient> orcidClients = new ArrayList<OrcidClient>();
        List<ClientRedirectUriEntity> entitiesWithPredefinedScopes = clientRedirectDao.findClientDetailsWithRedirectScope();

        for (ClientRedirectUriEntity entity : entitiesWithPredefinedScopes) {

            if (rut.value().equals(entity.getRedirectUriType())) {
                ClientDetailsEntity clientDetails = entity.getClientDetailsEntity();
                RedirectUri redirectUri = new RedirectUri(entity.getRedirectUri());
                String prefefinedScopes = entity.getPredefinedClientScope();
                redirectUri.setScope(new ArrayList<ScopePathType>(ScopePathType.getScopesFromSpaceSeparatedString(prefefinedScopes)));
                redirectUri.setType(RedirectUriType.fromValue(entity.getRedirectUriType()));
                RedirectUris redirectUris = new RedirectUris();
                redirectUris.getRedirectUri().add(redirectUri);

                OrcidClient minimalClientDetails = new OrcidClient();
                minimalClientDetails.setDisplayName(clientDetails.getProfileEntity().getCreditName());
                minimalClientDetails.setShortDescription(clientDetails.getProfileEntity().getBiography());
                minimalClientDetails.setClientId(clientDetails.getClientId());
                minimalClientDetails.setRedirectUris(redirectUris);
                orcidClients.add(minimalClientDetails);
            }

        }
        return orcidClients;
    }

}
