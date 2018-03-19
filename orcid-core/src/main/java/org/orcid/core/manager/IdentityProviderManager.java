package org.orcid.core.manager;

import java.util.Locale;

import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.w3c.dom.Element;

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

    void loadIdentityProviders();

    String retrieveContactEmailByProviderid(String providerid);

    IdentityProviderEntity createEntityFromXml(Element idpElement);

    void incrementFailedCount(String shibIdentityProvider);

}
