package org.orcid.core.manager;

import java.util.List;
import java.util.Locale;

import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;

/**
 * Manager to return all clients with a predefined OAuthscope 
 * @author jamesb
 *
 */
public interface ThirdPartyLinkManager {

    public static String CACHE_VERSION_KEY="import-wizard-cache-version";
    
    List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeWorksImport(Locale locale);
    
    List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeFundingImport(Locale locale);

    List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeReadAccess(Locale locale);
    
    List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopePeerReviewImport(Locale locale);

    void evictAll();

    long getLocalCacheVersion();

    void setLocalCacheVersion(long localCacheVersion);
    
    long getDatabaseCacheVersion(); 
    
    void updateDatabaseCacheVersion();
}
