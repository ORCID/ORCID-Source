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
        OrcidLsInput lsInput = new OrcidLsInput(publicId, systemId, baseURI);
        lsInput.setByteStream(getClass().getResourceAsStream(buildResourcePath(systemId)));
        return lsInput;
    }

    private String buildResourcePath(String systemId) {
        if (systemId.startsWith("../")) {
            return systemId.substring(2, systemId.length());
        }
        // XXX Going to have to this properly at some point!
        else if (systemId.endsWith("-2.0_rc1.xsd")) {
            return "/record_2.0_rc1/" + systemId;
        } else if(systemId.endsWith("-2.0_rc2.xsd")) {
            return "/record_2.0_rc2/" + systemId;
        } else if(systemId.endsWith("-2.0_rc3.xsd")) {
            return "/record_2.0_rc3/" + systemId;
        } else if(systemId.endsWith("-2.0_rc4.xsd")) {
            return "/record_2.0_rc4/" + systemId;
        }
        return "/" + systemId;
    }

}
