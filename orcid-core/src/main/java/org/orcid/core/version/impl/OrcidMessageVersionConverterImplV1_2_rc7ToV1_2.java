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
package org.orcid.core.version.impl;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc7ToV1_2 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc7";
    private static final String TO_VERSION = "1.2";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TO_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);
        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);
        return orcidMessage;
    }

}
