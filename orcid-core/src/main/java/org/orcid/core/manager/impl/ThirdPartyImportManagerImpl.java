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
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;

public class ThirdPartyImportManagerImpl implements ThirdPartyImportManager {

    @Resource(name="clientRedirectDao")
    private ClientRedirectDao clientRedirectDao;

    @Override
    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeForImport() {
        

        List<OrcidClient> orcidClients = new ArrayList<OrcidClient>();
        List<ClientRedirectUriEntity> entitiesWithPredefinedScopes =
                clientRedirectDao.findClientDetailsWithRedirectScope();
        
        for (ClientRedirectUriEntity entity : entitiesWithPredefinedScopes) {
            
            ClientDetailsEntity clientDetails = entity.getClientDetailsEntity();
            RedirectUri redirectUri = new RedirectUri(entity.getRedirectUri());            
            String prefefinedScopes = entity.getPredefinedClientScope();
            redirectUri.setScope(new ArrayList<ScopePathType>(ScopePathType.getScopesFromSpaceSeparatedString(prefefinedScopes)));            
            OrcidClient minimalClientDetails = new OrcidClient();           
            minimalClientDetails.setDisplayName(clientDetails.getProfileEntity().getCreditName());
            minimalClientDetails.setShortDescription(clientDetails.getProfileEntity().getBiography());
            RedirectUris redirectUris = new RedirectUris();
            redirectUris.getRedirectUri().add(redirectUri);            
            minimalClientDetails.setClientId(clientDetails.getClientId());
            minimalClientDetails.setRedirectUris(redirectUris);
            orcidClients.add(minimalClientDetails);
            
        }
        return orcidClients;
    }

}
