package org.orcid.core.manager;

import java.util.Locale;

/**
 * 
 * @author Will Simpson
 *
 */
public interface IdentityProviderManager {

    /**
     * 
     * @param providerid
     * @return The name for the current locale
     */
    String retrieveIdentitifyProviderName(String providerid);

    String retrieveIdentitifyProviderName(String providerid, Locale locale);

    String retrieveFreshIdentitifyProviderName(String providerid, Locale locale);

    String retrieveContactEmailByProviderid(String providerid);

    void incrementFailedCount(String shibIdentityProvider);

}
