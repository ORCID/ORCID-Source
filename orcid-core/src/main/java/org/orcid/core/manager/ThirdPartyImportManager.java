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
package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.clientgroup.OrcidClient;

/**
 * Manager to return all clients with a predefined OAuthscope 
 * @author jamesb
 *
 */
public interface ThirdPartyImportManager {

    public static String CACHE_VERSION_KEY="import-wizard-cache-version";
    
    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeWorksImport();
    
    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeFundingImport();

    public List<OrcidClient> findOrcidClientsWithPredefinedOauthScopeReadAccess();

    void evictAll();

    public long getLocalCacheVersion();

    public void setLocalCacheVersion(long localCacheVersion);
    
    public long getDatabaseCacheVersion(); 
    
    void updateDatabaseCacheVersion();
}
