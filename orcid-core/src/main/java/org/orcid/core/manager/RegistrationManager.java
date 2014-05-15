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

import java.net.URI;

import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface RegistrationManager {

    VerifyRegistrationToken parseEncyrptedParamsForVerification(String encryptedParams);

    void resetUserPassword(String toEmail, OrcidProfile orcidProfile, URI baseUri);

    OrcidProfile createMinimalRegistration(OrcidProfile orcidProfile);

    Long getCount();
}
