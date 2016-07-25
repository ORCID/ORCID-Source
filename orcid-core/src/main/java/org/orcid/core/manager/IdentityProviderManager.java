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
package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.w3c.dom.Element;

/**
 * 
 * @author Will Simpson
 *
 */
public interface IdentityProviderManager {

    void loadIdentityProviders();

    String retrieveContactEmailByProviderid(String providerid);

    IdentityProviderEntity createEntityFromXml(Element idpElement);

}
