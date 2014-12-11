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
package org.orcid.api.common.jaxb;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidResourceResolver implements LSResourceResolver {

    private LSResourceResolver defaultResourceResolver;

    public OrcidResourceResolver(LSResourceResolver defaultResourceResolver) {
        this.defaultResourceResolver = defaultResourceResolver;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (shouldLoadFromClasspath(systemId)) {
            OrcidLsInput lsInput = new OrcidLsInput(publicId, systemId, baseURI);
            lsInput.setByteStream(getClass().getResourceAsStream("/" + systemId));
            return lsInput;
        }
        return defaultResourceResolver.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
    }

    private boolean shouldLoadFromClasspath(String systemId) {
        if (systemId != null) {
            if (systemId.startsWith("orcid-common-")) {
                return true;
            }
        }
        return false;
    }

}
